package storehook;

import login.Login;
import movie.Movie;
import movie.MovieDB;
import order.Order;
import order.OrderDB;
import register.RegisterCustomer;
import register.RegisterEmployee;
import user.ModifyUserDB;
import user.data.User;

import java.util.ArrayList;
import java.util.Calendar;

public abstract class StoreHook {
    protected User currentUser;

    /**
     * The customer's cart
     */
    ArrayList<Movie> cart = new ArrayList<>();

    public StoreHook() {}

    /**
     * A mid-tier hook for Login.login(...)
     *
     * @param username the username of the user to log in as
     * @param password the password of the user with the associated username
     * @return true if login was successful; false otherwise
     */
    public boolean login(String username, String password) {
        User logged = Login.login(username, password);
        if (logged != null)
            currentUser = logged;

        return logged != null;
    }

    /**
     * @return the currently logged in user
     */
    public User loggedUser() {return currentUser;}

    /**
     * Given a user's cart---represented as an ArrayList of movies---create a new order containing their
     * added movies, and email.  Also assigns a unique order ID for them and an order state.
     *
     * @param movies ArrayList of movies the user has added
     * @return the order ID if it was successfully placed; otherwise returns -1
     */
    public long makeOrder(ArrayList<Movie> movies) {
        Order newOrder = new Order();
        newOrder.setOrderID(0);
        newOrder.setEmail(currentUser.getEmail());

        //Setting the order date
        String year = Integer.toString(Calendar.getInstance().get(Calendar.YEAR)),
               month = Integer.toString(Calendar.getInstance().get(Calendar.MONTH)),
               day = Integer.toString(Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        newOrder.setOrderDate(year + "-" + month + "-" + day);
        newOrder.setState("Await Payment");

        //Adding the movie IDs into order information
        ArrayList<Integer> addedIds = new ArrayList<>();
        MovieDB movieDB = MovieDB.getINSTANCE();
        for (Movie m : movies) {
            addedIds.add(m.getId());
            movieDB.setStock(m.getId(), m.getStock() - 1);
        }
        newOrder.setMovies(addedIds);

        long orderID = OrderDB.getINSTANCE().addOrder(newOrder);
        if (orderID == -1) { //If the order failed to be placed, restock the movies.
            for (Movie m : movies)
                movieDB.setStock(m.getId(), m.getStock() + 1);
        }

        cart = new ArrayList<>();
        return orderID;
    }

    /**
     * A mid-tier hook for ModifyUserDB.modify(...)
     *
     * @param email the email of the associated user to modify
     * @param baseInfo an array containing base info shared among each user class
     * @param additionalInfo string array of the unique info between each class
     * @return  -1 - the password contains less than 8 characters
     *          -2 - the password is missing lowercase characters
     *          -3 - the password is missing uppcase characters
     *          -4 - the password is missing numerical characters
     *          -5 - the password is missing special characters
     *          0 - the user associated with the passed email was successfully modified
     *          1 - there is no user with the associated email/passed key is not an email/given email is invalid
     *          2 - the array sizes are incorrect; not enough information in baseInfo or additionalInfo
     *          3 - if the email/username is being changed and there already another user using the new email/username
     *          4 - passed province code is invalid
     *          5 - passed timezone is invalid
     *          6 - passed postal code is invalid
     *          7 - passed extension number is invalid
     */
    public int editUser(String email, String[] baseInfo, String[] additionalInfo) {
        return ModifyUserDB.modify(email, baseInfo, additionalInfo);
    }

    /**
     * A mid-tier hook for MovieDB.getMovie(...)
     *
     * @param search the search term(s) to use.  Terms are split up by semi-colons, followed by a space, when
     *               searching by actors, directors and categories.
     * @param flag denotes the type of search to perform depending on the passed integers.
     *             - 0 = title
     *             - 1 = release date
     *             - 2 = genre
     *             - 3 = description
     *             - 4 = actors
     *             - 5 = directors
     *             - 6 = categories
     * @return an ArrayList containing all the movies which matched the given search string
     * @throws IllegalArgumentException when flag < 0 OR flag > 6
     */
    public ArrayList<Movie> searchMovies(String search, int flag) {
        return MovieDB.getINSTANCE().getMovie(search, flag);
    }

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
     * @return user's cart
     */
    public ArrayList<Movie> getCart() {return cart;}

    //TODO: create MakeOrder (see: InventoryOperator and Customer requirements)

    /**
     * A mid-tier hook for OrderDB.getOrder(...)
     *
     * @param id the ID for the order to get
     * @return the Order object with the associated ID
     */
    public Order fetchOrder(long id) {
        return OrderDB.getINSTANCE().getOrder(id);
    }

    /**
     * A mid-tier hook for Register objects.
     *
     * @param baseInfo An array of strings containing the basic information of a User; properties of User
     * @param additionalInfo An array of string containing information to the respective data type of the user
     * @param flag dictates what type of user is being registered
     * @return  Both RegisterCustomer and RegisterEmployee return different integers based on execution result.  Please
     *          refer to the relevant documentation for the respective object type.
     */
    public int addUser(String[] baseInfo, String[] additionalInfo, String flag) {
        if (flag.equalsIgnoreCase("Customer")) {
            RegisterCustomer newCust = new RegisterCustomer();
            return newCust.registerUser(baseInfo, additionalInfo, null);
        } else {
            RegisterEmployee newEmp = new RegisterEmployee();
            return newEmp.registerUser(baseInfo, additionalInfo, flag);
        }
    }
}
