package storehook;

import movie.Movie;
import movie.MovieDB;
import order.OrderDB;
import user.data.Customer;

import java.util.ArrayList;

public class CustomerStore extends StoreHook {
    /**
     * The customer's cart
     */
    ArrayList<Movie> cart = new ArrayList<>();

    public CustomerStore() {}

    /**
     * Adding a movie with the associated ID into the customer's cart.
     *
     * @param id the ID of the movie to be added to the cart.
     * @return true if the movies was successfully added; false otherise.
     */
    public boolean addMovieToCart(int id) {
        if (MovieDB.getINSTANCE().getMovie(id) == null)
            return false;

        cart.add(MovieDB.getINSTANCE().getMovie(id));
        return true;
    }

    /**
     * Removing the movie with the associated ID from the customer's cart
     *
     * @param id the ID of the movie to be removed from the cart.
     * @return true if the movie has been removed; false otherwise---especially if the movie is not in the cart.
     */
    public boolean removeMovieFromCart(int id) {
        if (!cart.contains(MovieDB.getINSTANCE().getMovie(id)))
            return false;

        cart.remove(MovieDB.getINSTANCE().getMovie(id));
        return true;
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
     * A mid-tier hook for OrderDB.removeOrder(...); additional check included to ensure customer's cannot
     * cancel an order if it has already been fulfilled.
     *
     * @param id the ID of the order to cancel
     * @return true if the order was cancelled; false otherwise.
     */
    public boolean cancelOrder(long id) {
        if (OrderDB.getINSTANCE().getOrder(id).getState().equals("Fulfilled"))
            return false;

        return OrderDB.getINSTANCE().removeOrder(id);
    }

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
     * Process a payment for the current customer
     *
     * ########################################################################
     * ###    FOR SIMULATION PURPOSES:  ASSUME CUSTOMER HAS ENOUGH MONEY    ###
     * ########################################################################
     *
     * @param amt the amount owed
     * @param billing customer billing information
     * @return true if the payment was successfully made; otherwise returns false
     */
    public boolean makePayment(double amt, boolean billingIsShipping, String[] billing) {
        //Ensuring that passed array sizes are correct
        if (billingIsShipping && billing.length != 3)
            return false;
        else if (!billingIsShipping && billing.length != 6)
            return false;

        //Ensuring that the array contains the right amount of information
        if (!billing[0].matches("\\d{16}")) //VERIFYING CARD NUMBER
            return false;

        if (!billing[1].matches("[01][0-9]/[0123][0-9]")) //VERIFYING EXPIRATION DATE
            return false;

        if (!billing[2].matches("\\d{3}")) //VERIFYING CCV NUMBER
            return false;

        if (!billingIsShipping && !billing[3].matches("\\d+ [a-z A-Z]+")) //VERIFYING STREET FORMAT
            return false;

        if (!billingIsShipping && !billing[4].matches("^[A-Z]\\d[A-Z][ ]?\\d[A-Z]\\d$")) //VERIFYING POSTAL CODE
            return false;

        ArrayList<String> provCodes = new ArrayList<>();
        provCodes.add("NL");
        provCodes.add("PE");
        provCodes.add("NS");
        provCodes.add("NB");
        provCodes.add("QC");
        provCodes.add("ON");
        provCodes.add("MB");
        provCodes.add("SK");
        provCodes.add("AB");
        provCodes.add("BC");
        provCodes.add("YT");
        provCodes.add("NT");
        provCodes.add("NU");
        if (!billingIsShipping && !provCodes.contains(billing[5])) //VERIFYING PROVINCE CODE
            return false;

        //Deducting the payment off of what the customer owes
        double currentOwed = ((Customer) super.currentUser).getAmtOwed(),
               newAmt = (currentOwed - amt > 0) ? currentOwed - amt : 0.0;
        ((Customer) super.currentUser).setAmtOwed(newAmt);
        return true;
    }

    /**
     * Process a payment for the current user making use of their available loyalty points
     *
     * @return true if the payment was successful; false otherwise
     */
    public boolean pointPayment() {
        int currentPoints = ((Customer) super.currentUser).getLoyaltyPoints();
        if (currentPoints > 10)
            return false;

        ((Customer) super.currentUser).setLoyaltyPoints(currentPoints - 10);
        return true;
    }
}
