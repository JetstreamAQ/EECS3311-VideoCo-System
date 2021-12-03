package storehook;

import login.Login;
import movie.Movie;
import movie.MovieDB;
import order.OrderDB;
import user.data.Customer;
import user.data.User;

import java.util.ArrayList;

public class CustomerStore extends StoreHook {
    public CustomerStore() {}

    /**
     * A mid-tier hook for Login.login(...)
     *
     * @param username the username of the user to log in as
     * @param password the password of the user with the associated username
     * @return true if login was successful; false otherwise
     */
    @Override
    public boolean login(String username, String password) {
        User logged = Login.login(username, password);
        if (logged instanceof Customer)
            currentUser = logged;

        return currentUser != null;
    }

    /**
     * A mid-tier hook for ModifyUserDB.modify(...); modified for customers
     *
     * @param email the email of the associated user to modify
     * @param baseInfo an array containing base info shared among each user class
     * @param additionalInfo string array of the unique info between each class
     * @return  -1 - the password contains less than 8 characters
     *          -2 - the password is missing lowercase characters
     *          -3 - the password is missing uppcase characters
     *          -4 - the password is missing numerical characters
     *          -5 - the password is missing special characters
     *          -20 - the passed email is not that of the current user!
     *          0 - the user associated with the passed email was successfully modified
     *          1 - there is no user with the associated email/passed key is not an email/given email is invalid
     *          2 - the array sizes are incorrect; not enough information in baseInfo or additionalInfo
     *          3 - if the email/username is being changed and there already another user using the new email/username
     *          4 - passed province code is invalid
     *          5 - passed timezone is invalid
     *          6 - passed postal code is invalid
     *          7 - passed extension number is invalid
     */
    @Override
    public int editUser(String email, String[] baseInfo, String[] additionalInfo) {
        if (!super.currentUser.getEmail().equalsIgnoreCase(email))
            return -20;

        return super.editUser(email, baseInfo, additionalInfo);
    }

    /**
     * A mid-tier hook for Register objects.
     *
     * @param baseInfo An array of strings containing the basic information of a User; properties of User
     * @param additionalInfo An array of string containing information to the respective data type of the user
     * @param flag dictates what type of user is being registered
     * @return  Both RegisterCustomer and RegisterEmployee return different integers based on execution result.  Please
     *          refer to the relevant documentation for the respective object type.
     *          Result of -20 if the user tries to make an account besides an admin account.
     */
    @Override
    public int addUser(String[] baseInfo, String[] additionalInfo, String flag) {
        if (!flag.equals("Customer"))
            return -20;

        return super.addUser(baseInfo, additionalInfo, flag);
    }

    /**
     * A mid-tier hook for grabbing the ArrayList of orderIDs associated with the current user
     *
     * @return ArrayList of long order IDs
     */
    public ArrayList<Long> fetchCustOrders() {return ((Customer) currentUser).getCustOrders();}

    /**
     * A mid-tier hook for Customer.getAmtOwed()
     *
     * @return returns the amount of money owed by the customer
     */
    public double getAmtDue() {
        return ((Customer) super.currentUser).getAmtOwed();
    }

    /**
     * A mid-tier hook for Customer.getLoyaltyPoints()
     *
     * @return returns the number of loyalty points owned by the customer
     */
    public int getLoyaltyPoints() {
        return ((Customer) super.currentUser).getLoyaltyPoints();
    }

    /**
     * Process a payment for the current user making use of their available loyalty points
     *
     * @return true if the payment was successful; false otherwise
     */
    public boolean pointPayment() {
        int currentPoints = ((Customer) super.currentUser).getLoyaltyPoints();
        if (currentPoints < 10)
            return false;

        ((Customer) super.currentUser).setLoyaltyPoints(currentPoints - 10);
        return true;
    }
}
