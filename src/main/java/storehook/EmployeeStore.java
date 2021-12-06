package storehook;

import login.Login;
import movie.Movie;
import movie.MovieDB;
import order.Order;
import order.OrderDB;
import user.DBUser;
import user.ModifyUserDB;
import user.data.*;

import java.util.ArrayList;
import java.util.Arrays;

public class EmployeeStore extends StoreHook {
    public EmployeeStore() {}

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
        if (logged instanceof Employee)
            currentUser = logged;

        return currentUser != null;
    }

    /**
     * A modified mid-tier hook for ModifyUserDB.modify(...) for employees.  Other accounts can only be modified if
     * the current user is an admin.  Otherwise, the current user may modify their own account.
     *
     * @param email the email of the associated user to modify
     * @param baseInfo an array containing base info shared among each user class
     * @param additionalInfo string array of the unique info between each class
     * @return -80 if the current user is not an admin nor are they editing their own account; refer to non-overridden method in StoreHook for additional int codes.
     */
    @Override
    public int editUser(String email, String[] baseInfo, String[] additionalInfo) {
        if (!email.equalsIgnoreCase("VideoCoSystem@VideoCo.org") && (currentUser instanceof Admin || currentUser.getEmail().equalsIgnoreCase(email)))
            return super.editUser(email, baseInfo, additionalInfo);

        return -80;
    }

    /**
     * A mid-tier hook for ModifyUserDB.removeUser(...)
     *
     * @param email the email of the associated user to remove
     * @return true if the user was successfully removed; false if a user is trying to delete itself
     */
    public boolean deleteUser(String email) {
        if (currentUser instanceof Admin && !currentUser.getEmail().equalsIgnoreCase(email) && !email.equalsIgnoreCase("VideoCoSystem@VideoCo.org"))
            return ModifyUserDB.removeUser(email);

        return false;
    }

    /**
     * A mid-tier hook for DBUser.getUsers()
     *
     * @return a list of every registered user---including test users.
     */
    public ArrayList<User> viewUsers() {return DBUser.getINSTANCE().getUsers();}

    /**
     * @param key the username/email of the customer to check
     * @return true if there is a customer with the given email/username in the DB.
     */
    public boolean probeCustomer(String key) {return DBUser.getINSTANCE().getUser(key) != null && DBUser.getINSTANCE().getUser(key) instanceof Customer;}

    /**
     * @param key the username/email of the customer to fetch
     * @return A customer object if there exists one with the given email/username; is null otherwise
     */
    public User fetchCustomer(String key) {
        if (probeCustomer(key))
            return DBUser.getINSTANCE().getUser(key);

        return null;
    }

    /**
     * @param key the associated username/email of the user to get
     * @return User object of the user with the given username/email
     */
    public User fetchUser(String key) {
        return DBUser.getINSTANCE().getUser(key);
    }

    /**
     * A mid-tier hook for OrderDB.getOrders()
     *
     * @return a list of every order in the system.
     */
    public ArrayList<Order> viewOrders() {return OrderDB.getINSTANCE().getOrders();}

    public ArrayList<Long> ordersToBeShipped() {
        ArrayList<Long> temp = new ArrayList<>();
        for (Order o : OrderDB.getINSTANCE().getOrders()) {
            if (o.getState().equals("Await Shipment"))
                temp.add(o.getOrderID());
        }

        return temp;
    }

    /**
     * a mid-tier hook for OrderDB.progOrderStatus(...)
     *
     * @param orderID the order ID of the order to progress the status of
     * @return a string representation of the orders status after execution
     */
    public String progOrder(long orderID) {return OrderDB.getINSTANCE().progOrderStatus(orderID);}

    /**
     * A mid-tier hook for MovieDB.addMovie(...);
     *
     * @param is the movie ID and stock count
     * @param price the price of the movie
     * @param trgd the Title, Release Date, Genre and Description
     * @param adc the Actor, Director and Category list
     * @return a movie object containing the passed properties
     * @return true if the movie was successfully added; false otherwise
     */
    public boolean addMovie(int[] is, double price, String[] trgd, String[][] adc) {
        //Abort the addition if there is a movie mapped to the given ID already
        if (MovieDB.getINSTANCE().getMovie(is[0]) != null)
            return false;
        Movie newMovie;

        //Creating the movie object to add
        try {
            newMovie = generateMovie(is, price, trgd, adc);
        } catch(IllegalArgumentException e) {
            return false;
        }

        return MovieDB.getINSTANCE().addMovie(newMovie);
    }

    /**
     * A mid-tier hook into MovieDB to modify a movie with the associated ID.
     *
     * @param is the movie ID and stock count
     * @param price the price of the movie
     * @param trgd the Title, Release Date, Genre and Description
     * @param adc the Actor, Director and Category list
     * @return true if the movie was successfully modified
     */
    public boolean modifyMovie(int[] is, double price, String[] trgd, String[][] adc) {
        MovieDB movieDB = MovieDB.getINSTANCE();
        //Abort the modification process if the passed ID does not map to an existing movie
        if (movieDB.getMovie(is[0]) == null)
            return false;
        Movie newMovie = null;

        //Creating the movie object to add
        //Q: Why do this when the created object is redundant?
        //A: To make use of the verification process already in place.  Costly for memory, yes.  Saves some time though.
        try {
            newMovie = generateMovie(is, price, trgd, adc);
        } catch(IllegalArgumentException e) {
            return false;
        }

        //Modifying the price and stock
        int[] res = {
                movieDB.setPrice(is[0], newMovie.getPrice()),
                movieDB.setStock(is[0], newMovie.getStock()),
                movieDB.setTRGD(is[0], 0, newMovie.getTitle()),
                movieDB.setTRGD(is[0], 1, newMovie.getReleaseDate()),
                movieDB.setTRGD(is[0], 2, newMovie.getGenre()),
                movieDB.setTRGD(is[0], 3, newMovie.getDescription()),
                movieDB.setADC(is[0], 0, (ArrayList<String>) newMovie.getActors()),
                movieDB.setADC(is[0], 1, (ArrayList<String>) newMovie.getDirectors()),
                movieDB.setADC(is[0], 2, (ArrayList<String>) newMovie.getCategories())
        };
        boolean modRes = true;
        for (int i : res)
            modRes = modRes && (i == 0 || i == 2);

        return modRes;
    }

    /**
     * Remove the movie with the given ID from the DB
     *
     * @param id the ID of the associated movie to remove
     * @return true if the movie was successfully removed; false otherwise
     */
    public boolean removeMovie(int id) {
        return MovieDB.getINSTANCE().removeMovie(id);
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
    @Override
    public ArrayList<Movie> searchMovies(String search, int flag) {
        ArrayList<Movie> matchingRes = MovieDB.getINSTANCE().getMovie(search, flag);

        if (!(currentUser instanceof Cashier))
            return matchingRes;

        ArrayList<Movie> filter = new ArrayList<>();
        Cashier cashier = (Cashier) currentUser;
        for (Movie m : matchingRes) {
            if (m.getCategories().contains(cashier.getLocation()))
                filter.add(m);
        }

        return filter;
    }

    /**
     *
     * @param is the movie ID and stock count
     * @param price the price of the movie
     * @param trgd the Title, Release Date, Genre and Description
     * @param adc the Actor, Director and Category list
     * @return a movie object containing the passed properties
     * @throws IllegalArgumentException when passed arguments meet any of the following...
     *          - invalid length
     *          - stock count is negative
     *          - price is negative
     *          - any elements of TRGD/ADC are Null or the empty string
     *          - release date is formatted incorrectly
     *          - categories does not contain "In-Store Location 1" or "In-Store Location 2"
     */
    private Movie generateMovie(int[] is, double price, String[] trgd, String[][] adc) {
        //verifying length
        if (is.length != 2 || trgd.length != 4 || adc.length != 3)
            throw new IllegalArgumentException();

        Movie newMovie = new Movie();

        newMovie.setId(is[0]);

        //Ensuring the new stock is a positive integer
        if (is[1] < 0)
            throw new IllegalArgumentException();
        newMovie.setStock(is[1]);

        //Ensuring the price is a positive number
        if (price < 0)
            throw new IllegalArgumentException();
        newMovie.setPrice(price);

        //Ensuring that each entry in 'trgd' and 'adc' are not null/empty
        for (String s : trgd) {
            if (s == null || s.equals(""))
                throw new IllegalArgumentException();
        }
        for (String[] c : adc) {
            for (String s : c) {
                if (s == null || s.equals(""))
                    throw new IllegalArgumentException();
            }
        }

        //Ensuring the date is formatted properly
        if (!trgd[1].matches("^\\d{4}-[01][1-9]-[0-3]\\d$"))
            throw new IllegalArgumentException();
        newMovie.setReleaseDate(trgd[1]);

        //Setting the rest of TRGD -> "Title, Genre and Description
        newMovie.setTitle(trgd[0]);
        newMovie.setGenre(trgd[2]);
        newMovie.setDescription(trgd[3]);

        /*Setting ADC -> "Actors, Directors and Categories"*/
        //Must first convert each array into a list
        ArrayList<String> actors = new ArrayList<>(Arrays.asList(adc[0]));
        ArrayList<String> directors = new ArrayList<>(Arrays.asList(adc[1]));
        ArrayList<String> categories = new ArrayList<>(Arrays.asList(adc[2]));

        //For categories, one of the entries MUST be "In-Store Location 1"/"In-Store Location 2"
        //Don't need this; some movies can just be in the warehouse
        /*if (!categories.contains("In-Store Location 1") && !categories.contains("In-Store Location 2"))
            throw new IllegalArgumentException();*/

        //Now setting ADC
        newMovie.setActors(actors);
        newMovie.setDirectors(directors);
        newMovie.setCategories(categories);

        //Returning the created movie
        return newMovie;
    }

    /**
     * Check if there exists a movie at the cashier's location
     *
     * @param movie the movie to check for
     * @param cashier the cashier to check
     * @return true if the movie is at the current location of the cashier; false otherwise
     */
    public boolean movieAtLocation(Movie movie, Cashier cashier) {
        return movie.getCategories().contains(cashier.getLocation());
    }
}
