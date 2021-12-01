package system;

import order.Order;
import order.OrderDB;
import order.state.Fulfilled;
import order.state.OrderState;
import register.RegisterEmployee;
import user.DBUser;
import user.data.Admin;
import user.data.Customer;
import user.data.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class VideoCoSys {
    public VideoCoSys() {}

    private void performChecks() {
        ArrayList<User> users = DBUser.getINSTANCE().getUsers();
        OrderDB orderDB = OrderDB.getINSTANCE();

        /*
         * ##################################################################################
         * #####    FOR SIMULATION PURPOSES, ORDERS WHICH HAVE BEEN "Shipped" WILL      #####
         * #####    BE SET TO "Fulfilled" AUTOMATICALLY                                 #####
         * ##################################################################################
         * ~ Order State updating BEGIN ~
         */
        for (Order o : orderDB.getOrders()) {
            if (o.getState().equals("Shipped"))
                o.setState("Fulfilled");
        }
        /*##### ORDER STATE UPDATING END #####*/

        /*##### CHECKING FOR OVERDUE RETURNS START #####*/
        //Grab every customer with a non-returned order
        //TODO: Reconsider algorithm if time permits
        Map<Long, Customer> ordersToCust = new HashMap<>();
        for (User u : users) {
            if (u instanceof Customer) {
                Customer cust = (Customer) u;
                for (long id : cust.getCustOrders()) {
                    OrderState focus = orderDB.getOrder(id).getOrderState();
                    if (focus.toString().equals("Fulfilled") && !((Fulfilled) focus).getReturned())
                        ordersToCust.put(id, cust);
                }
            }
        }

        //Charging customers for late returns
        ArrayList<Long> orderIDs = new ArrayList<>(ordersToCust.keySet());
        for (long id : orderIDs) {
            Fulfilled focus = (Fulfilled) orderDB.getOrder(id).getOrderState();
            if (daysSinceBorrowed(focus.getDateArrived()) >= 14) {
                Customer custFocus = ordersToCust.get(id);
                double lateFee = (custFocus.getProvince().equals("ON")) ? 1.00 : 9.99;
                custFocus.setAmtOwed(custFocus.getAmtOwed() + lateFee);

                DBUser.getINSTANCE().modifyUser(custFocus.getEmail(), custFocus);
            }
        }
        /*##### CHECKING FOR OVERDUE RETURNS END #####*/
    }

    /**
     * A mid-tier hook for RegisterEmployee.registerUser(...) for only the System to use in the creation of Admin
     * accounts.
     *
     * @param baseInfo An array of strings containing the basic information of a User; properties of User
     * @param additionalInfo An array of string containing information to the respective data type of the user
     * @return  Please refer to the return integers for RegisterEmployee.registerUser(...)
     */
    public int createAdmin(String[] baseInfo, String[] additionalInfo) {
        RegisterEmployee reg = new RegisterEmployee();
        return reg.registerUser(baseInfo, additionalInfo, "Admin");
    }

    /**
     * A mid-tier hook for DBUser.removeUser(...) configured to only allow the removal of Admin user accounts.
     *
     * @param email the email of the admin account to remove
     * @return true if the admin account was removed; false otherwise (ie. no account with that email exists)
     */
    public boolean removeAdmin(String email) {
        if (DBUser.getINSTANCE().getUser(email) != null && DBUser.getINSTANCE().getUser(email) instanceof Admin)
            return DBUser.getINSTANCE().removeUser(email);

        return false;
    }

    /**
     * Given a string date, calculate the days elapsed since then to today.
     *
     * @param date The date to compate to.
     * @return the days passed since the passed date.
     */
    private long daysSinceBorrowed(String date) {
        //Formatter for converting the string date into a form we can use to calculate the delta
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //Getting today's date
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR)),
               month = Integer.toString(Calendar.getInstance().get(Calendar.MONTH)),
               day = Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH)),
               today = year + "-" + month + "-" + day;

        //Converting the dates for comparison
        Date orderDate, currentDate;
        try {
            orderDate = sdf.parse(date);
            currentDate = sdf.parse(today);
        } catch (ParseException e) {
            return -1;
        }

        long delta = currentDate.getTime() - orderDate.getTime();

        return (delta / (1000 * 60 * 60 * 24)) % 365;
    }


}
