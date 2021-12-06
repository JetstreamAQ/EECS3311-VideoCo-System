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

    public void performChecks() {
        ArrayList<User> users = DBUser.getINSTANCE().getUsers();
        OrderDB orderDB = OrderDB.getINSTANCE();

        System.out.println("[SYSTEM]: Performing Checks");

        /*
         * ##################################################################################
         * #####    FOR SIMULATION PURPOSES, ORDERS WHICH HAVE BEEN "Shipped" WILL      #####
         * #####    BE SET TO "Fulfilled" AUTOMATICALLY                                 #####
         * ##################################################################################
         * ~ Order State updating BEGIN ~
         */
        for (Order o : orderDB.getOrders()) {
            System.out.println("[SYSTEM]: Checking order state on order #" + o.getOrderID());
            if (o.getState().equals("Shipped")) {
                o.getOrderState().stateAction(false);
                orderDB.modOrder(o.getOrderID(), o);
                System.out.println("[SYSTEM]: Order #" + o.getOrderID() + " has arrived at the customer's location.");
            } else if (o.getState().equals("Await Payment")) {
                o.getOrderState().stateAction(false);
                orderDB.modOrder(o.getOrderID(), o);
                System.out.println("[SYSTEM]: Payment recieved for order #" + o.getOrderID());
            }
        }
        /*##### ORDER STATE UPDATING END #####*/

        /*##### CHECKING FOR OVERDUE RETURNS START #####*/
        //Grab every customer with a non-returned order
        //TODO: Reconsider algorithm if time permits
        Map<Long, Customer> ordersToCust = new HashMap<>();
        for (User u : users) {
            if (u instanceof Customer) {
                Customer cust = (Customer) u;
                System.out.println("[SYSTEM]: Checking orders for " + u.getEmail());
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
                System.out.println("[SYSTEM]: " + custFocus.getEmail() + " - Order #" + orderDB.getOrder(id).getOrderID() + " overdue! Customer has been charged $" + lateFee);
            }
        }
        /*##### CHECKING FOR OVERDUE RETURNS END #####*/
    }

    /**
     * Given a string date, calculate the days elapsed since then to today.
     *
     * @param date The date to compate to.
     * @return the days passed since the passed date.
     */
    public long daysSinceBorrowed(String date) {
        //Formatter for converting the string date into a form we can use to calculate the delta
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        //Getting today's date
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR)),
               month = Integer.toString(Calendar.getInstance().get(Calendar.MONTH) + 1),
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
