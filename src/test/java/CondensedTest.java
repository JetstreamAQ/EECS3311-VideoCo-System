import login.Login;
import movie.Movie;
import movie.MovieDB;
import order.Order;
import order.OrderDB;
import org.junit.Assert;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import register.Register;
import register.RegisterCustomer;
import register.RegisterEmployee;
import storehook.CustomerStore;
import storehook.EmployeeStore;
import storehook.StoreHook;
import system.VideoCoSys;
import user.DBUser;
import user.ModifyUserDB;
import user.data.*;

import java.util.*;

@FixMethodOrder(MethodSorters.JVM)
public class CondensedTest {
    StoreHook cust, emp;
    Map<Integer, String> invalidTz = new HashMap<>();
    Map<Integer, String> invalidExt = new HashMap<>();

    MovieDB movies = MovieDB.getINSTANCE();
    Movie newMovie = new Movie();

    OrderDB orders = OrderDB.getINSTANCE();
    Order newOrder = new Order();

    DBUser users = DBUser.getINSTANCE();
    User newAdmin = new Admin(),
            newCashier = new Cashier(),
            newCustomer = new Customer(),
            newInvOp = new InventoryOperator(),
            newWSTeam = new WarehouseShippingTeam();

    @Before
    public void setup() {
        System.out.println("############### [IMPORTANT] ###############");
        System.out.println("RUN 'loadbkup.sh' BEFORE AND AFTER EXECUTING THESE TESTS");
        System.out.println("ANY SIGNIFICANT CHANGE TO THE DB *WILL* CAUSE THESE TO FAIL!");
        System.out.println("############### [IMPORTANT] ###############");
        cust = new CustomerStore();
        emp = new EmployeeStore();

        invalidTz.put(0, "asd");
        invalidTz.put(1, "GTM-7:00");
        invalidTz.put(2, "GMT - 7:00");
        invalidTz.put(3, "GMT-712:00");
        invalidTz.put(4, "GMT-:00");
        invalidTz.put(5, "GMT-7:50");
        invalidTz.put(6, "GMT-7:03");
        invalidTz.put(7, "GMT-7:00  ");
        invalidTz.put(8, "   GMT-7:00");
        invalidTz.put(9, "  GMT-7:00   ");
        invalidTz.put(10, "GMT 7:00");
        invalidTz.put(11, "GMT-13:00");

        invalidExt.put(0, "12312");
        invalidExt.put(1, "a123");
        invalidExt.put(2, "abcd");
        invalidExt.put(3, "    ");
        invalidExt.put(4, "123");
        invalidExt.put(5, " 123");
        invalidExt.put(6, "123 ");
        invalidExt.put(7, "");

        newAdmin.setFName("Test");
        newAdmin.setLName("Name");
        newAdmin.setUsername("TestName");
        newAdmin.setEmail("TestName@company.com");
        newAdmin.setPassword("password");

        newCashier.setFName("Cashier");
        newCashier.setLName("Name");
        newCashier.setUsername("CashierName");
        newCashier.setEmail("Cashier@company.com");
        newCashier.setPassword("password");

        newCustomer.setFName("Customer");
        newCustomer.setLName("Name");
        newCustomer.setUsername("CustomerName");
        newCustomer.setEmail("Customer@company.com");
        newCustomer.setPassword("password");

        newInvOp.setFName("InvOp");
        newInvOp.setLName("Name");
        newInvOp.setUsername("InvOpName");
        newInvOp.setEmail("InvOp@company.com");
        newInvOp.setPassword("password");

        newWSTeam.setFName("WSTeam");
        newWSTeam.setLName("Name");
        newWSTeam.setUsername("WSTeamName");
        newWSTeam.setEmail("WSTeam@company.com");
        newWSTeam.setPassword("password");

        //Setting the movie properties!
        newMovie.setId(-495);
        newMovie.setStock(789);
        newMovie.setPrice(8.99);
        newMovie.setTitle("Through the Gates of the Silver Key");
        newMovie.setReleaseDate("2077-06-16");
        newMovie.setGenre("Fantasy");
        newMovie.setDescription("Movie adaptation of Lovecraft's work of the same name.  ngl this adaptation sucks");
        ArrayList<String> actors = new ArrayList<>();
        actors.add("A dude in a speedo");
        newMovie.setActors(actors);
        ArrayList<String> directors = new ArrayList<>();
        directors.add("Some person we found in the streets");
        directors.add("Bob");
        directors.add("An underpaid intern");
        newMovie.setDirectors(directors);
        ArrayList<String> categories = new ArrayList<>();
        categories.add("hand_holding");
        newMovie.setCategories(categories);

        newOrder.setOrderID(-4);
        newOrder.setMovies(new ArrayList<>(Arrays.asList(-1, -2, -3)));
        newOrder.setEmail("ICantBelieveItIsAll@boiler.plate");
        newOrder.setOrderDate("2021-01-01");
        newOrder.setState("Shipped");
    }

    @Test
    public void test_order_loaded() {
        Order order = orders.getOrder(-1);
        Assert.assertNotNull(order);

        //Testing if the correct properties were loaded for id: -1
        //Can't get longs working for some reason with assertEquals(...)
        Assert.assertTrue(order.getMovies().get(0) == -1);
        Assert.assertEquals(1, order.getMovies().size());
        Assert.assertEquals("RealPerson@RealDomain.gov", order.getEmail());
        Assert.assertEquals("2021-01-01", order.getOrderDate());
        Assert.assertEquals("Await Payment", order.getState());

        //Double check: Print out string
        System.out.println(order);
    }

    @Test
    public void test_order_load_and_delete() {
        long succ = orders.addOrder(newOrder);
        Assert.assertEquals(-4, succ);

        //Check that the order was added with the correct properties
        Order addedOrder = orders.getOrder(-4);
        Assert.assertEquals(addedOrder.getOrderID(), newOrder.getOrderID()); //should match anyway; but just making sure
        Assert.assertArrayEquals(addedOrder.getMovies().toArray(), newOrder.getMovies().toArray());
        Assert.assertEquals(addedOrder.getEmail(), newOrder.getEmail());
        Assert.assertEquals(addedOrder.getOrderDate(), newOrder.getOrderDate());
        Assert.assertEquals(addedOrder.getState(), newOrder.getState());

        //Double-checking visually
        System.out.println(addedOrder);

        //removing the order
        boolean removeSucc = orders.removeOrder(-4);
        Assert.assertTrue(removeSucc);

        //Checking that the order was successfully removed
        Order orderDNE = orders.getOrder(-4); //Also doubles as a test to see if we can get an order that DNE
        Assert.assertNull(orderDNE);
    }

    @Test
    public void test_mod_order() {
        Order order = orders.getOrder(-2);
        Assert.assertNotNull(order);

        Order modOrder = new Order();
        modOrder.setOrderID(order.getOrderID());
        modOrder.setEmail(order.getEmail());
        modOrder.setOrderDate("2020-01-03");
        ArrayList<Integer> movies = new ArrayList<>(order.getMovies());
        movies.add(-2);
        movies.add(-1); //this shouldn't be in the final result
        modOrder.setMovies(movies);
        modOrder.setState("Cancelled"); //Should be able to cancel order as well

        boolean shouldFail = orders.modOrder(-1, modOrder);
        Assert.assertFalse(shouldFail);

        boolean shouldPass = orders.modOrder(-2, modOrder);
        Assert.assertTrue(shouldPass);

        //double-check
        Assert.assertEquals(modOrder, orders.getOrder(-2));
        System.out.println(orders.getOrder(-2));

        //Reverting things back
        boolean revert = orders.modOrder(-2, order);
        Assert.assertTrue(revert);

        //double-check
        Assert.assertEquals(order, orders.getOrder(-2));
        System.out.println("\n" + orders.getOrder(-2));
    }

    @Test
    public void test_cancel_when_fulfilled() {
        Order toMod = orders.getOrder(-3);
        Order modOrder = new Order();
        modOrder.setOrderID(-3);
        modOrder.setMovies(toMod.getMovies());
        modOrder.setEmail(toMod.getEmail());
        modOrder.setOrderDate(toMod.getOrderDate());
        modOrder.setState("Cancelled");

        boolean shouldFail = orders.modOrder(-3, modOrder);
        Assert.assertFalse(shouldFail);
    }

    @Test
    public void test_cancel_remove_all_movies() {
        long succ = orders.addOrder(newOrder);
        Assert.assertEquals(-4, succ);

        Order modOrder = new Order();
        modOrder.setOrderID(-4);
        modOrder.setEmail(newOrder.getEmail());
        modOrder.setOrderDate(newOrder.getOrderDate());
        modOrder.setMovies(new ArrayList<>());
        modOrder.setState(newOrder.getState());

        boolean shouldPass = orders.modOrder(-4, modOrder);
        Assert.assertTrue(shouldPass);

        //double check
        System.out.println(orders.getOrder(-4));

        //removing the order
        boolean removeSucc = orders.removeOrder(-4);
        Assert.assertTrue(removeSucc);

        //Checking that the order was successfully removed
        Order orderDNE = orders.getOrder(-4); //Also doubles as a test to see if we can get an order that DNE
        Assert.assertNull(orderDNE);
    }

    @Test
    public void test_add_existing_id() {
        newOrder.setOrderID(-3);
        long succ = orders.addOrder(newOrder);
        Assert.assertEquals(0, succ);

        Order bonusOrder = new Order();
        bonusOrder.setOrderID(-3);
        bonusOrder.setEmail("ChefChef@yahoo.ca");
        bonusOrder.setOrderDate(newOrder.getOrderDate());
        bonusOrder.setMovies(new ArrayList<>());
        bonusOrder.setState(newOrder.getState());
        succ = orders.addOrder(bonusOrder);
        Assert.assertEquals(1, succ);

        boolean remove = orders.removeOrder(0);
        Assert.assertTrue(remove);
        newOrder.setOrderID(-4);

        remove = orders.removeOrder(1);
        Assert.assertTrue(remove);
    }

    @Test
    public void test_cancel_order() {
        newOrder.setOrderID(-3);
        long succ = orders.addOrder(newOrder);
        Assert.assertEquals(0, succ);

        boolean res = orders.cancelOrder(0);
        Assert.assertTrue(res);
        Assert.assertEquals("Cancelled", orders.getOrder(0).getState());

        boolean remove = orders.removeOrder(0);
        Assert.assertTrue(remove);
    }

    @Test
    public void test_movie_loaded() {
        //Won't check how many movies are loaded since this can change as development goes on.
        Movie movie = movies.getMovie(-1);

        //Checking each portion loaded properly
        Assert.assertEquals(-1, movie.getId()); //should obviously pass.
        Assert.assertEquals(100, movie.getStock());
        Assert.assertEquals(3.50, movie.getPrice(), 0);
        Assert.assertEquals("The Crinj", movie.getTitle());
        Assert.assertEquals("2020-04-20", movie.getReleaseDate());
        Assert.assertEquals("Testing", movie.getGenre());
        Assert.assertEquals("Movie about the crinj.", movie.getDescription());
        Assert.assertEquals(3, movie.getActors().size()); //Should only be 3 actors loaded in
        Assert.assertEquals("Crinj #21", movie.getActors().get(0));
        Assert.assertEquals("Crinj #495", movie.getActors().get(1));
        Assert.assertEquals(":smile:", movie.getActors().get(2));
        Assert.assertEquals(2, movie.getDirectors().size()); //Should only be 1 director loaded in
        Assert.assertEquals("Some guy; don't worry about it", movie.getDirectors().get(0));
        Assert.assertEquals("[redacted]", movie.getDirectors().get(1));
        Assert.assertEquals(4, movie.getCategories().size()); //Should only have 4 categories
        Assert.assertEquals("In-Store Location 1", movie.getCategories().get(0));
        Assert.assertEquals("Crinj Compilation", movie.getCategories().get(1));
        Assert.assertEquals("Cringe Compilation", movie.getCategories().get(2));
        Assert.assertEquals("\"Jerry, why do we have this?\"", movie.getCategories().get(3));
    }

    @Test
    public void test_invalid_movie_search() {
        Movie movie = movies.getMovie(-600);
        Assert.assertNull(movie); //Non-existent movies return null
    }

    @Test
    public void test_add_existing_movie() {
        Movie movie = movies.getMovie(-1);
        boolean failAdd = movies.addMovie(movie);
        Assert.assertFalse(failAdd);

        Movie existMovie = new Movie();
        existMovie.setId(-1);
        failAdd = movies.addMovie(existMovie);
        Assert.assertFalse(failAdd);

        //Ensuring nothing was changed as we did this
        Movie movieInDB = movies.getMovie(-1);
        Assert.assertEquals(movie, movieInDB);
    }

    @Test
    public void test_remove_nonexisting_movie() {
        boolean dneMovie = movies.removeMovie(-600);
        Assert.assertFalse(dneMovie);
    }

    @Test
    public void test_add_remove_movie() {
        //See if the movie added successfully
        boolean movieAdded = movies.addMovie(newMovie);
        Assert.assertTrue(movieAdded);

        //Double checking if the movie added successfully
        Movie addedMovie = movies.getMovie(-495);
        Assert.assertEquals(newMovie.getId(), addedMovie.getId());
        Assert.assertEquals(newMovie.getStock(), addedMovie.getStock());
        Assert.assertEquals(newMovie.getPrice(), addedMovie.getPrice(), 0);
        Assert.assertEquals(newMovie.getTitle(), addedMovie.getTitle());
        Assert.assertEquals(newMovie.getReleaseDate(), addedMovie.getReleaseDate());
        Assert.assertEquals(newMovie.getGenre(), addedMovie.getGenre());
        Assert.assertEquals(newMovie.getDescription(), addedMovie.getDescription());
        for (int i = 0; i < newMovie.getActors().size(); i++)
            Assert.assertEquals(newMovie.getActors().get(i), addedMovie.getActors().get(i));
        for (int i = 0; i < newMovie.getDirectors().size(); i++)
            Assert.assertEquals(newMovie.getDirectors().get(i), addedMovie.getDirectors().get(i));
        for (int i = 0; i < newMovie.getCategories().size(); i++)
            Assert.assertEquals(newMovie.getCategories().get(i), addedMovie.getCategories().get(i));

        //Triple-check: Print to manually confirm movie added correctly
        System.out.println(addedMovie);

        //Removing the movie and seeing if it's removed successfully
        boolean movieRemoved = movies.removeMovie(-495);
        Assert.assertTrue(movieRemoved);

        //Double checking to see if the movie was removed from the DB
        Movie delMovie = movies.getMovie(-495);
        Assert.assertNull(delMovie);
    }

    @Test
    public void test_get_movie_by_title() {
        ArrayList<Movie> matches = movies.getMovie("[[Original Movie Name]]", 0);

        //Should be two matches with the original DB
        Assert.assertEquals(2, matches.size());

        //We get #-2 and #-3
        Movie negTwo = movies.getMovie(-2);
        Movie negThree = movies.getMovie(-3);
        Assert.assertEquals(negTwo, matches.get(0));
        Assert.assertEquals(negThree, matches.get(1));

        //Printing out the results for manual triple check
        System.out.println("BY TITLE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
        System.out.println(matches.get(1).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_release_date() {
        ArrayList<Movie> matches = movies.getMovie("2020-04-20", 1);

        //Should be three matches with the original DB
        Assert.assertEquals(3, matches.size());

        //We get #-1, #-2 and #-3
        Movie negOne = movies.getMovie(-1);
        Movie negTwo = movies.getMovie(-2);
        Movie negThree = movies.getMovie(-3);
        Assert.assertEquals(negOne, matches.get(0));
        Assert.assertEquals(negTwo, matches.get(1));
        Assert.assertEquals(negThree, matches.get(2));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
        System.out.println(matches.get(1).toString() + "\n======");
        System.out.println(matches.get(2).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_genre() {
        ArrayList<Movie> matches = movies.getMovie("TESTING", 2);

        //Should be three matches with the original DB
        Assert.assertEquals(2, matches.size());

        //We get #-1 and #-2
        Movie negOne = movies.getMovie(-1);
        Movie negTwo = movies.getMovie(-2);
        Assert.assertEquals(negOne, matches.get(0));
        Assert.assertEquals(negTwo, matches.get(1));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
        System.out.println(matches.get(1).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_description() {
        //NB: "Crinj" in the actual description is all lowercase!  Doubles as a test to check case-insensitivity
        ArrayList<Movie> matches = movies.getMovie("Crinj", 3);

        //Should be one matches with the original DB
        Assert.assertEquals(1, matches.size());

        //We get #-1
        Movie negOne = movies.getMovie(-1);
        Assert.assertEquals(negOne, matches.get(0));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_full_description() {
        ArrayList<Movie> matches = movies.getMovie("Movie about the crinj.", 3);

        //Should be one matches with the original DB
        Assert.assertEquals(1, matches.size());

        //We get #-1
        Movie negOne = movies.getMovie(-1);
        Assert.assertEquals(negOne, matches.get(0));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_actors() {
        ArrayList<Movie> matches = movies.getMovie("Crinj #495; Gengetsu", 4);

        //Should be three matches with the original DB
        Assert.assertEquals(3, matches.size());

        //We get #-1, #-2 and #-3
        Movie negOne = movies.getMovie(-1);
        Movie negTwo = movies.getMovie(-2);
        Movie negThree = movies.getMovie(-3);
        Assert.assertEquals(negOne, matches.get(0));
        Assert.assertEquals(negTwo, matches.get(1));
        Assert.assertEquals(negThree, matches.get(2));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
        System.out.println(matches.get(1).toString() + "\n======");
        System.out.println(matches.get(2).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_directors() {
        ArrayList<Movie> matches = movies.getMovie("Shinki", 5);

        //Should be two matches with the original DB
        Assert.assertEquals(2, matches.size());

        //We get #-2 and #-3
        Movie negTwo = movies.getMovie(-2);
        Movie negThree = movies.getMovie(-3);
        Assert.assertEquals(negTwo, matches.get(0));
        Assert.assertEquals(negThree, matches.get(1));

        //Printing out the results for manual triple check
        System.out.println("BY TITLE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
        System.out.println(matches.get(1).toString() + "\n======");
    }

    @Test
    public void test_get_movie_by_categories() {
        ArrayList<Movie> matches = movies.getMovie("In-Store Location 1", 6);

        //Should be one matches with the original DB
        Assert.assertEquals(1, matches.size());

        //We get #-1
        Movie negOne = movies.getMovie(-1);
        Assert.assertEquals(negOne, matches.get(0));

        //Printing out the results for manual triple check
        System.out.println("BY RELEASE DATE");
        System.out.println("======\n" + matches.get(0).toString() + "\n======");
    }

    @Test
    public void test_modify_movie_properties() {
        //See if the movie added successfully
        boolean movieAdded = movies.addMovie(newMovie);
        Assert.assertTrue(movieAdded);

        //Double checking if the movie added successfully
        Movie addedMovie = movies.getMovie(-495);
        Assert.assertEquals(newMovie.getId(), addedMovie.getId());
        Assert.assertEquals(newMovie.getStock(), addedMovie.getStock());
        Assert.assertEquals(newMovie.getPrice(), addedMovie.getPrice(), 0);
        Assert.assertEquals(newMovie.getTitle(), addedMovie.getTitle());
        Assert.assertEquals(newMovie.getReleaseDate(), addedMovie.getReleaseDate());
        Assert.assertEquals(newMovie.getGenre(), addedMovie.getGenre());
        Assert.assertEquals(newMovie.getDescription(), addedMovie.getDescription());
        for (int i = 0; i < newMovie.getActors().size(); i++)
            Assert.assertEquals(newMovie.getActors().get(i), addedMovie.getActors().get(i));
        for (int i = 0; i < newMovie.getDirectors().size(); i++)
            Assert.assertEquals(newMovie.getDirectors().get(i), addedMovie.getDirectors().get(i));
        for (int i = 0; i < newMovie.getCategories().size(); i++)
            Assert.assertEquals(newMovie.getCategories().get(i), addedMovie.getCategories().get(i));

        //Modifying the movie via MovieDB
        int oldStock = movies.getMovie(-495).getStock();
        int stockSet = movies.setStock(-495, oldStock - 1);
        Assert.assertEquals(0, stockSet);
        Assert.assertEquals(oldStock - 1, movies.getMovie(-495).getStock()); //Checking stock #1
        Assert.assertEquals(oldStock - 1, addedMovie.getStock()); //Checking stock #2

        int priceSet = movies.setPrice(-495, 14.86);
        Assert.assertEquals(0, priceSet);
        Assert.assertEquals(14.86, movies.getMovie(-495).getPrice(), 0); //Check price #1
        Assert.assertEquals(14.86, addedMovie.getPrice(), 0); //Check price #1

        int titleSet = movies.setTRGD(-495, 0, "It's Time to Duel!");
        Assert.assertEquals(0, titleSet);

        int releaseDateSet = movies.setTRGD(-495, 1, "2077-12-31");
        Assert.assertEquals(0, releaseDateSet);

        int genreSet = movies.setTRGD(-495, 2, "Chain Nibiru.");
        Assert.assertEquals(0, genreSet);

        int descSet = movies.setTRGD(-495, 3, "The most powerful end board known to man.  Nib token, pass.");
        Assert.assertEquals(0, descSet);

        int actorsAdd = movies.setADC(-495, 0, movies.getMovie(-495).getActors().size(), "Chef"); //Adding an actor
        Assert.assertEquals(0, actorsAdd);

        int actorsSet = movies.setADC(-495, 0, 0, "Brique Wahl"); //modifying an actor
        Assert.assertEquals(0, actorsSet);

        int directorsAdd = movies.setADC(-495, 1, movies.getMovie(-495).getDirectors().size(), "Bob Bobert");
        Assert.assertEquals(0, directorsAdd);

        int directorsSet = movies.setADC(-495, 1, 0, "hackerman");
        Assert.assertEquals(0, directorsSet);

        int categoriesAdd = movies.setADC(-495, 2, movies.getMovie(-495).getCategories().size(), "I really don't know");
        Assert.assertEquals(0, categoriesAdd);

        int categoriesSet = movies.setADC(-495, 2, 0, "extreme_hand_holding");
        Assert.assertEquals(0, categoriesSet);

        //Double check: Print to manually confirm movie properties were changed
        System.out.println(addedMovie);

        //Removing the movie and seeing if it's removed successfully
        boolean movieRemoved = movies.removeMovie(-495);
        Assert.assertTrue(movieRemoved);

        //Double checking to see if the movie was removed from the DB
        Movie delMovie = movies.getMovie(-495);
        Assert.assertNull(delMovie);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_stock_movie() {
        int test = movies.setStock(-600, 100); //movie DNE
        Assert.assertEquals(1, test);

        test = movies.setStock(-1, movies.getMovie(-1).getStock()); //same price
        Assert.assertEquals(2, test);

        movies.setStock(-1, -1);
        Assert.fail("IllegalArgumentException should be thrown; stock can't be negative.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_price_movie() {
        int test = movies.setPrice(-600, 100); //movie DNE
        Assert.assertEquals(1, test);

        test = movies.setPrice(-1, movies.getMovie(-1).getPrice()); //same price
        Assert.assertEquals(2, test);

        movies.setPrice(-1, -1);
        Assert.fail("IllegalArgumentException should be thrown; price can't be negative.");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_TRGD_input() {
        //NB: Setting the title, release date, genre and description all use the same method; no need
        //    for excessive testing here.
        int test = movies.setTRGD(-600, 0, "bruh"); //movie DNE
        Assert.assertEquals(1, test);

        test = movies.setTRGD(-1, 1, movies.getMovie(-1).getReleaseDate()); //Similar String
        Assert.assertEquals(2, test);

        test = movies.setTRGD(-1, 1, "[RANDOM NOISE]2021-11-01[SOME RANDOM TRASH]"); //Invalid date
        Assert.assertEquals(3, test);

        movies.setTRGD(-1, 2, ""); //Empty string
        Assert.fail("Should throw IllegalArgumentException on getting an empty string or a null string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_TRGD_flag() {
        movies.setTRGD(-1, 1231234, "spaghetti");
        Assert.fail("Should throw IllegalArgumentException when flag is not 0-3");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_ADC_input() {
        //NB: Setting actors, directors and categories use the same method; can avoid excessive testing
        int test = movies.setADC(-600, 0, 0, "REISUB"); //movie DNE
        Assert.assertEquals(1, test);

        test = movies.setADC(-1, 1, 0, movies.getMovie(-1).getDirectors().get(0)); //Existing input
        Assert.assertEquals(2, test);

        movies.setADC(-1, 2, 0, null);
        Assert.fail("Should throw IllegalArgumentException on getting an empty string or a null string");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_ADC_flag() {
        movies.setADC(-1, 123138, 0, "asofjiasgl");
        Assert.fail("Should throw IllegalArgumentException when flag is not 0-2");
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_invalid_ADC_propNum() {
        movies.setADC(-1, 0, movies.getMovie(-1).getActors().size() + 1, "Random person");
        Assert.fail("Should throw IllegalArgumentException when the propNum exceeds 1 more than the size of the property list.");
    }

    @Test
    public void test_users_loaded() {
        User admin = users.getUser("SamRodriguez@desperado.com");
        Assert.assertTrue(admin instanceof Admin);

        User cashier = users.getUser("JohnSmith@gmail.com");
        Assert.assertTrue(cashier instanceof Cashier);

        User customer = users.getUser("BlackBart@NotAPirate.ca");
        Assert.assertTrue(customer instanceof Customer);

        User invOp = users.getUser("ArroyoR@gmail.com");
        Assert.assertTrue(invOp instanceof InventoryOperator);

        User wst1 = users.getUser("BriqueWahl123@yahoo.ca");
        Assert.assertTrue(wst1 instanceof WarehouseShippingTeam);

        User wst2 = users.getUser("idek@company.ca");
        Assert.assertTrue(wst2 instanceof WarehouseShippingTeam);
    }

    @Test
    public void test_add_remove_users() {
        Admin admin = (Admin) newAdmin;
        admin.setId(-1);
        admin.setTimeZone("GMT-7");
        boolean adminRes = users.addUser(admin);
        Assert.assertTrue(adminRes);

        Cashier cashier = (Cashier) newCashier;
        cashier.setId(-2);
        cashier.setLocation("In-Store Location 2");
        boolean cashierRes = users.addUser(cashier);
        Assert.assertTrue(cashierRes);

        Customer customer = (Customer) newCustomer;
        customer.setAmtOwed(0.0);
        ArrayList<Long> movies = new ArrayList<>();
        movies.add((long) -1); movies.add((long) -2);
        customer.setCustOrders(movies);
        customer.setLoyaltyPoints(6);
        customer.setStreet("111 Real Street");
        customer.setPostalCode("B2B 2B2");
        customer.setProvince("MB");
        boolean customerRes = users.addUser(customer);
        Assert.assertTrue(customerRes);

        InventoryOperator invOp = (InventoryOperator) newInvOp;
        invOp.setExtensionNum("7890");
        boolean invOpRes = users.addUser(invOp);
        Assert.assertTrue(invOpRes);

        WarehouseShippingTeam wst = (WarehouseShippingTeam) newWSTeam;
        wst.setIsShipping(false);
        wst.setWarehouseLocation("MB");
        boolean wstRes = users.addUser(wst);
        Assert.assertTrue(wstRes);

        //removing the added entries
        boolean adminRem = users.removeUser(admin.getEmail());
        Assert.assertTrue(adminRem);

        boolean cashierRem = users.removeUser(cashier.getEmail());
        Assert.assertTrue(cashierRem);

        boolean customerRem = users.removeUser(customer.getEmail());
        Assert.assertTrue(customerRem);

        boolean invOpRem = users.removeUser(invOp.getEmail());
        Assert.assertTrue(invOpRem);

        boolean wstRem = users.removeUser(wst.getEmail());
        Assert.assertTrue(wstRem);
    }

    @Test
    public void test_modify_user() {
        Admin admin = (Admin) newAdmin;
        Admin oldAdmin = (Admin) users.getUser("SamRodriguez@desperado.com");
        admin.setId(-1);
        admin.setTimeZone("GMT-7");

        int failMod1 = users.modifyUser("FakeEmail@NotReal.bruh", admin);
        Assert.assertEquals(1, failMod1);

        int failMod2 = users.modifyUser("JohnSmith@gmail.com", admin);
        Assert.assertEquals(2, failMod2);

        int successMod = users.modifyUser("SamRodriguez@desperado.com", admin);
        Assert.assertEquals(0, successMod);
        Assert.assertNull(users.getUser("SamRodriguez@desperado.com"));

        Admin modAdmin = (Admin) users.getUser("TestName@company.com");
        Assert.assertEquals(admin.getId(), modAdmin.getId());
        Assert.assertEquals(admin.getEmail(), modAdmin.getEmail());
        Assert.assertEquals(admin.getFName(), modAdmin.getFName());
        Assert.assertEquals(admin.getLName(), modAdmin.getLName());
        Assert.assertEquals(admin.getUsername(), modAdmin.getUsername());
        Assert.assertEquals(admin.getPassword(), modAdmin.getPassword());
        Assert.assertEquals(admin.getTimeZone(), modAdmin.getTimeZone());

        int revertMod = users.modifyUser("TestName@company.com", oldAdmin);
        Assert.assertEquals(0, revertMod);
    }

    @Test
    public void test_register_customer() {
        Register reg = new RegisterCustomer();
        String[] basicInfo = {
                "Satori",
                "Komeiji",
                "MindReader1999",
                "SKomeiji11@google.com",
                ""
        };
        String[] additionalInfo = {
                "616 Former Hell Blvd.",
                "C3C 3C3",
                "Victoria",
                "BC"
        };

        //Mainly want to test the REGEX
        int empty = reg.registerUser(basicInfo, additionalInfo, null);
        Assert.assertEquals(1, empty);

        basicInfo[4] = "BADPASSWORD";
        int noLowerCase = reg.registerUser(basicInfo, additionalInfo, null);
        Assert.assertEquals(2, noLowerCase);

        basicInfo[4] = "badpassword";
        int noCaps = reg.registerUser(basicInfo, additionalInfo, null);
        Assert.assertEquals(3, noCaps);

        basicInfo[4] = "BadPassword";
        int noNum = reg.registerUser(basicInfo, additionalInfo, null);
        Assert.assertEquals(4, noNum);

        basicInfo[4] = "BadPassword123drowssaPdaB";
        int noSymbol = reg.registerUser(basicInfo, additionalInfo, null);
        Assert.assertEquals(5, noSymbol);

        basicInfo[4] = "Good_Password123drowssaP_dooG";
        int registerSucc = reg.registerUser(basicInfo, additionalInfo, null);
        Assert.assertEquals(0, registerSucc);

        //Cleaning up the test DB of clutter
        boolean remove = users.removeUser("SKomeiji11@google.com");
        Assert.assertTrue(remove);
    }

    @Test
    public void test_register_employee() {
        Register reg = new RegisterEmployee();
        String[] basicInfo = {
                "Billy",
                "G",
                "BillyG-8s",
                "info@gatesfoundation.org",
                "Password!@#123"
        };

        /*ADMIN REGISTRATION*/
        Random random = new Random();
        String[] adminInfo = {invalidTz.get(random.nextInt(invalidTz.size() - 1))};
        int badFlag = reg.registerUser(basicInfo, adminInfo, "BOI"); //Testing invalid flag
        Assert.assertEquals(6, badFlag);

        int badTz = reg.registerUser(basicInfo, adminInfo, "Admin");
        Assert.assertEquals(6, badTz);

        adminInfo[0] = "GMT-7:00";
        int adminTest = reg.registerUser(basicInfo, adminInfo, "Admin");
        Assert.assertEquals(0, adminTest);
        boolean remove = users.removeUser("info@gatesfoundation.org");
        Assert.assertTrue(remove);

        /*CASHIER REGISTRATION*/
        String[] cashierInfo = {"In-Store Location " + (random.nextInt(1) + 1)};
        int cashierTest = reg.registerUser(basicInfo, cashierInfo, "Cashier");
        Assert.assertEquals(0, cashierTest);
        remove = users.removeUser("info@gatesfoundation.org");
        Assert.assertTrue(remove);

        /*INVENTORY OP REGISTRATION*/
        String[] invOpInfo = {invalidExt.get(random.nextInt(invalidExt.size() - 1))};
        int invalidInvOp = reg.registerUser(basicInfo, invOpInfo, "Inventory Operator");
        Assert.assertEquals(6, invalidInvOp);
        invOpInfo[0] = "2222";
        int invOpTest = reg.registerUser(basicInfo, invOpInfo, "Inventory Operator");
        Assert.assertEquals(0, invOpTest);
        remove = users.removeUser("info@gatesfoundation.org");
        Assert.assertTrue(remove);

        /*WAREHOUSE SHIPPING TEAM REGISTRATION*/
        String[] wstInfo = {"YT", "FALSE"};
        int wstTest = reg.registerUser(basicInfo, wstInfo, "Warehouse Shipping");
        Assert.assertEquals(0, wstTest);
        remove = users.removeUser("info@gatesfoundation.org");
        Assert.assertTrue(remove);
    }

    @Test
    public void test_logins() {
        User customer = Login.login("BlackBart", "SomePasswordOrSomething");
        Assert.assertNotNull(customer);
        Assert.assertTrue(customer instanceof Customer);
        Assert.assertEquals(users.getUser("BlackBart"), customer);

        User employee = Login.login("Jetstream", "PasswordWithHighEntropy0!1@2#3$4%5^6&7*8(9)10");
        Assert.assertNotNull(employee);
        Assert.assertTrue(employee instanceof Employee);
        Assert.assertEquals(users.getUser("samrodriguez@desperado.com"), employee);

        User invalidUser = Login.login("Herobrine", "Spooky");
        Assert.assertNull(invalidUser);
    }

    @Test
    public void test_modifyUserDB() {
        String[] basicInfo = {
                "Some",
                "Person",
                "OriginalUsername",
                "SomeImportantPerson@gmail.com",
                "Password!@#123"
        };
        String[] additionalInfo = {"GMT+0:00"};
        Register register = new RegisterEmployee();
        int regInfo = register.registerUser(basicInfo, additionalInfo, "Admin");
        Assert.assertEquals(0, regInfo);
        User testAdmin = users.getUser("OriginalUsername");
        Assert.assertNotNull(testAdmin);

        int noUsr = ModifyUserDB.modify("bruh", basicInfo, additionalInfo);
        Assert.assertEquals(1, noUsr);

        //Testing additional info check
        additionalInfo[0] = "GMT-3:00";
        int modTime = ModifyUserDB.modify("SomeImportantPerson@gmail.com", basicInfo, additionalInfo);
        Assert.assertEquals(0, modTime);
        Admin fetched = (Admin) users.getUser("OriginalUsername");
        Assert.assertNotNull(fetched);
        Assert.assertEquals(additionalInfo[0], fetched.getTimeZone());

        //Checking if we can use an invalid email
        basicInfo[3] = "bruh";
        int invalidEmail = ModifyUserDB.modify("SomeImportantPerson@gmail.com", basicInfo, additionalInfo);
        Assert.assertEquals(1, invalidEmail);
        fetched = (Admin) users.getUser("OriginalUsername");
        Assert.assertNotNull(fetched);
        basicInfo[3] = "SomeImportantPerson@gmail.com";

        //Checking if we can change the username to an already existing one
        basicInfo[2] = "Jetstream";
        int existUser = ModifyUserDB.modify("SomeImportantPerson@gmail.com", basicInfo, additionalInfo);
        Assert.assertEquals(3, existUser);
        fetched = (Admin) users.getUser("OriginalUsername");
        Assert.assertNotNull(fetched);
        basicInfo[2] = "OriginalUsername";

        //Checking if we can change the email to an already existing one
        basicInfo[3] = "SamRodriguez@desperado.com";
        int existEmail = ModifyUserDB.modify("SomeImportantPerson@gmail.com", basicInfo, additionalInfo);
        Assert.assertEquals(3, existEmail);
        fetched = (Admin) users.getUser("SomeImportantPerson@gmail.com");
        Assert.assertNotNull(fetched);
        basicInfo[3] = "SomeImportantPerson@gmail.com";

        //Invalid password
        basicInfo[4] = "What is airflow?";
        int invalidPassword = ModifyUserDB.modify("SOMEIMPORTANTPERSON@GMAIL.COM", basicInfo, additionalInfo);
        Assert.assertEquals(-1, invalidPassword);
        basicInfo[4] = "Password!@#123";

        //Checking if we can change both email and username
        basicInfo[2] = "DifferentUsername";
        basicInfo[3] = "ATotallyImportantPerson@realdomain.org";
        int successful = ModifyUserDB.modify("SomeImportantPerson@gmail.com", basicInfo, additionalInfo);
        Assert.assertEquals(0, successful);
        fetched = (Admin) users.getUser("DifferentUsername");
        Assert.assertNotNull(fetched);
        fetched = (Admin) users.getUser("ATotallyImportantPerson@realdomain.org");
        Assert.assertNotNull(fetched);

        boolean remove = ModifyUserDB.removeUser("ATotallyImportantPerson@realdomain.org");
        Assert.assertTrue(remove);
    }

    @Test
    public void test_ModifyUserDB_otherEmployees() {
        String[] basicInfo = {
                "Some",
                "Person",
                "OriginalUsername",
                "SomeImportantPerson@gmail.com",
                "Password!@#123"
        };
        Register register = new RegisterEmployee();
        Register regCust = new RegisterCustomer();

        /*CASHIER TEST*/
        String[] cashierInfo = {"In-Store Location 1"};
        int regInfo = register.registerUser(basicInfo, cashierInfo, "Cashier");
        Assert.assertEquals(0, regInfo);
        User testCashier = users.getUser("OriginalUsername");
        Assert.assertNotNull(testCashier);
        cashierInfo[0] = "In-Store Location 2";
        int mod = ModifyUserDB.modify("SomeImportantPerson@gmail.com", basicInfo, cashierInfo);
        Assert.assertEquals(0, mod);
        boolean remove = ModifyUserDB.removeUser("SomeImportantPerson@gmail.com");
        Assert.assertTrue(remove);

        /*CUSTOMER TEST*/
        String[] customerInfo = {
                "123 bruh rd.",
                "D3D 3D3",
                "City",
                "NS"
        };
        regInfo = regCust.registerUser(basicInfo, customerInfo, "Customer");
        Assert.assertEquals(0, regInfo);
        User testCustomer = users.getUser("OriginalUsername");
        Assert.assertNotNull(testCustomer);
        customerInfo[0] = "1239 AAAAAAAAAAAAAAA";
        mod = ModifyUserDB.modify("SomeImportantPerson@gmail.com", basicInfo, customerInfo);
        Assert.assertEquals(0, mod);
        remove = ModifyUserDB.removeUser("SomeImportantPerson@gmail.com");
        Assert.assertTrue(remove);

        /*INVENTORY OPERATOR*/
        String[] invOpInfo = {"1234"};
        regInfo = register.registerUser(basicInfo, invOpInfo, "Inventory Operator");
        Assert.assertEquals(0, regInfo);
        User testOperator = users.getUser("OriginalUsername");
        Assert.assertNotNull(testOperator);
        invOpInfo[0] = "1239";
        mod = ModifyUserDB.modify("SomeImportantPerson@gmail.com", basicInfo, invOpInfo);
        Assert.assertEquals(0, mod);
        remove = ModifyUserDB.removeUser("SomeImportantPerson@gmail.com");
        Assert.assertTrue(remove);

        /*WAREHOUSE SHIPPING TEAM*/
        String[] wstInfo = {"ON", "true"};
        regInfo = register.registerUser(basicInfo, wstInfo, "Warehouse Shipping");
        Assert.assertEquals(0, regInfo);
        User testWst = users.getUser("OriginalUsername");
        Assert.assertNotNull(testWst);
        wstInfo[1] = "false";
        mod = ModifyUserDB.modify("SomeImportantPerson@gmail.com", basicInfo, wstInfo);
        Assert.assertEquals(0, mod);
        remove = ModifyUserDB.removeUser("SomeImportantPerson@gmail.com");
        Assert.assertTrue(remove);
    }

    @Test
    public void test_invalid_register_email() {
        String[] basicInfo = {
                "Doremy",
                "Sweet",
                "SweetDreamSheep",
                "Not an email",
                "ArbitraryPassword@!2345"
        };
        String[] additionalInfo = {"GMT+12:00"};
        Register reg = new RegisterEmployee();
        int invalidEmail = reg.registerUser(basicInfo, additionalInfo, "Admin");
        Assert.assertEquals(10, invalidEmail);
    }

    /**
     * REQ-1: Users must login to the system with valid and existing credentials. Invalid credentials
     * will result in a login error.
     */
    @Test
    public void test_login_fail() {
        /*LOGGING IN WITH NON EXISTANT USERS BEGIN*/
        boolean custFail = cust.login("Not A real user/email", "arbitrary");
        Assert.assertFalse(custFail);

        boolean empFail = emp.login("This shouldn't exist as well", "arbitrary");
        Assert.assertFalse(empFail);
        /*LOGGING IN WITH NON EXISTANT USER END*/

        /*SHOULD NOT BE ABLE TO LOG INTO EMPLOYEESTORE W/ CUSTOMER USER AND VICE VERSA*/
        custFail = cust.login("VideoCoSystem@VideoCo.org", "test");
        Assert.assertFalse(custFail);

        empFail = emp.login("test", "test");
        Assert.assertFalse(empFail);
    }

    @Test
    public void test_login_success() {
        boolean custSucc = cust.login("test", "test");
        Assert.assertTrue(custSucc);

        boolean empSucc = emp.login("SystemUser", "test");
        Assert.assertTrue(empSucc);
    }

    /**
     * REQ-2: Users must be able to register to the system with a unique username and email. Invalid
     * credentials will result in a registration error.
     */
    @Test
    public void test_customer_register() {
        /*REGISTRATION OF CUSTOMER ACCOUNT*/
        /*
         * registration is invalid when...
         *          - 1: if the password was less than 8 characters or contains a space
         *          - 2: if the password contained no lower-case characters
         *          - 3: if the password contained no upper-case characters
         *          - 4: if the password contained no digits 0-9
         *          - 5: if the password contained no special characters (eg. !@#$%^&*()_+...)
         *          - 6: if not enough information was passed through the arguments (array size too large/small)
         *          - 7: if an invalid postal code was passed (must be a canadian postal code)
         *          - 8: if the given street is of an incorrect format or not even a street
         *          - 9: if the user to register has their email in the DB already
         *          - 10: if the passed email "baseInfo[3]" is not an email
         * NB: Taken from JavaDoc on RegisterCustomer.java
         *
         * In practical usage, case 6 should never happen with a proper implementation of input parsing.
         * case 8 for invalid province codes should also never happen in actual usage if dropdown menus
         * (ie. ComboBox) are used in order to prevent odd input.
         */
        String[] basicInfo = {"Satori", "Komeiji", "MindReader1999", "not an email", ""};
        String[] additionalInfo = {"Not Matching regex", "FAKE POSTAL", "Victoria", "ACDC"};

        //trying to register a non-customer through CustomerStore
        //NB: with proper implementation, this should never happen
        int res = cust.addUser(basicInfo, additionalInfo, "BRUH");
        Assert.assertEquals(-20, res);

        //email not real
        res = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(10, res);

        //password too short
        basicInfo[3] = "test@domain.com";
        res = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(1, res);

        //no lower-case
        basicInfo[4] = "PASSWORD";
        res = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(2, res);

        //no upper-case
        basicInfo[4] = "password";
        res = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(3, res);

        //no digits
        basicInfo[4] = "Password";
        res = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(4, res);

        //no special chars
        basicInfo[4] = "Password1";
        res = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(5, res);

        //not enough info
        //NB: should never happen with proper parsing
        String[] tooShort = new String[1];
        res = cust.addUser(tooShort, additionalInfo, "Customer");
        Assert.assertEquals(6, res);
        res = cust.addUser(basicInfo, tooShort, "Customer");
        Assert.assertEquals(6, res);

        //invalid postal code
        basicInfo[4] = "Password!1";
        res = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(8, res);

        //invalid street
        additionalInfo[0] = "616 Former Hell Blvd.";
        res = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(7, res);

        //invalid province code
        additionalInfo[1] = "A1A 1A1";
        res = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(8, res);

        //Email in DB already
        additionalInfo[3] = "BC";
        res = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(9, res);

        //Successfully registering the customer
        basicInfo[3] = "SatoriKomeiji@gmail.com";
        res = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(0, res);

        //Clean up DB for future tests
        boolean removed = ModifyUserDB.removeUser(basicInfo[3]);
        Assert.assertTrue(removed);
    }

    @Test
    public void test_employee_register() {
        String[] basicInfo = {
                "Billy",
                "G",
                "BillyG-8s",
                "info@gatesfoundation.org",
                "Password!@#123"
        };

        /*ADMIN REGISTRATION*/
        Random random = new Random();
        String[] adminInfo = {invalidTz.get(random.nextInt(invalidTz.size() - 1))};
        int badFlag = emp.addUser(basicInfo, adminInfo, "BOI"); //Testing invalid flag
        Assert.assertEquals(6, badFlag);

        int badTz = emp.addUser(basicInfo, adminInfo, "Admin");
        Assert.assertEquals(6, badTz);

        adminInfo[0] = "GMT-7:00";
        int adminTest = emp.addUser(basicInfo, adminInfo, "Admin");
        Assert.assertEquals(0, adminTest);
        boolean remove = ModifyUserDB.removeUser("info@gatesfoundation.org");
        Assert.assertTrue(remove);

        /*CASHIER REGISTRATION*/
        String[] cashierInfo = {"In-Store Location " + (random.nextInt(1) + 1)};
        int cashierTest = emp.addUser(basicInfo, cashierInfo, "Cashier");
        Assert.assertEquals(0, cashierTest);
        remove = ModifyUserDB.removeUser("info@gatesfoundation.org");
        Assert.assertTrue(remove);

        /*INVENTORY OP REGISTRATION*/
        String[] invOpInfo = {invalidExt.get(random.nextInt(invalidExt.size() - 1))};
        int invalidInvOp = emp.addUser(basicInfo, invOpInfo, "Inventory Operator");
        Assert.assertEquals(6, invalidInvOp);
        invOpInfo[0] = "2222";
        int invOpTest = emp.addUser(basicInfo, invOpInfo, "Inventory Operator");
        Assert.assertEquals(0, invOpTest);
        remove = ModifyUserDB.removeUser("info@gatesfoundation.org");
        Assert.assertTrue(remove);

        /*WAREHOUSE SHIPPING TEAM REGISTRATION*/
        String[] wstInfo = {"YT", "FALSE"};
        int wstTest = emp.addUser(basicInfo, wstInfo, "Warehouse Shipping");
        Assert.assertEquals(0, wstTest);
        remove = ModifyUserDB.removeUser("info@gatesfoundation.org");
        Assert.assertTrue(remove);
    }

    /**
     * REQ-3: Users will be able to search for movies by name, category, or all movies. If no movies
     * match the selected search, a message is displayed indicating no movies match the desired
     * search.
     */
    @Test
    public void test_search_no_res() {
        //NB: The display message is handled in the GUI.
        //For no res, we check if the returned array is empty.
        //Which descendant of StoreHook we run doesn't matter as both inherit the same method from StoreHook without
        //changing what it does
        boolean login = cust.login("test", "test");
        Assert.assertTrue(login);

        ArrayList<Movie> results = cust.searchMovies("1234567890-=!@#$%^&*()", 0);
        Assert.assertEquals(0, results.size());
    }

    @Test
    public void test_search_res() {
        boolean login = cust.login("test", "test");
        Assert.assertTrue(login);

        ArrayList<Movie> results = cust.searchMovies("2020-04-20", 1);
        Assert.assertEquals(3, results.size());
    }

    //NB: Req 5 is purely visual, and is demonstrated in the demo.

    /**
     * REQ-6: Users must be able to pay for an order using a payment service or  loyalty points if
     * they  have  enough.  Invalid  shipping  or  payment  information  will  show  the  user  and  error
     * respective of the invalid credential.
     */
    @Test
    public void test_payment() {
        String[] basicInfo = {"Abdul", "Alhazred", "Necronomicon", "Alhazred@rlyeh.org", "Password!1"};
        String[] additionalInfo = {"123 Some Place", "P1A 9H7", "Quebec City", "QC"};

        int reg = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(0, reg);

        boolean logged = cust.login("Necronomicon", "Password!1");
        Assert.assertTrue(logged);

        //Customers have 0 points on registration
        boolean noPoints = cust.pointPayment(cust.loggedUser());
        Assert.assertFalse(noPoints);

        //Setting loyalty points to 10
        User temp = cust.loggedUser();
        ((Customer) temp).setLoyaltyPoints(10);
        DBUser.getINSTANCE().modifyUser(temp.getEmail(), temp);
        boolean pointsPaid = cust.pointPayment(cust.loggedUser());
        Assert.assertTrue(pointsPaid);

        //Paying with CC and billing info
        String[] billing = {
                "123412341234123",
                "1",
                "1",
                ((Customer) temp).getStreet(),
                ((Customer) temp).getPostalCode(),
                ((Customer) temp).getCityTown(), //<- Never checked but exists for simulation purposes
                ((Customer) temp).getProvince()
        };
        boolean invalidCC = cust.makePayment(cust.loggedUser(), 1.00, billing);
        Assert.assertFalse(invalidCC);

        billing[0] = "1234123412341234";
        boolean invalidExp = cust.makePayment(cust.loggedUser(), 1.00, billing);
        Assert.assertFalse(invalidExp);

        billing[1] = "01/22";
        boolean invalidCCV = cust.makePayment(cust.loggedUser(), 1.00, billing);
        Assert.assertFalse(invalidCCV);

        billing[2] = "133";
        boolean paymentSucc = cust.makePayment(cust.loggedUser(), 1.00, billing);
        Assert.assertTrue(paymentSucc);

        boolean removed = ModifyUserDB.removeUser(cust.loggedUser().getEmail());
        Assert.assertTrue(removed);
    }

    @Test
    public void test_point_payment() {
        String[] basicInfo = {"Abdul", "Alhazred", "Necronomicon", "Alhazred@rlyeh.org", "Password!1"};
        String[] additionalInfo = {"123 Some Place", "P1A 9B7", "Quebec City", "QC"};

        int reg = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(0, reg);

        boolean logged = cust.login("Necronomicon", "Password!1");
        Assert.assertTrue(logged);

        //Customers have 0 points on registration
        boolean noPoints = cust.pointPayment(cust.loggedUser());
        Assert.assertFalse(noPoints);

        //Setting loyalty points to 10
        User temp = cust.loggedUser();
        ((Customer) temp).setLoyaltyPoints(10);
        DBUser.getINSTANCE().modifyUser(temp.getEmail(), temp);
        boolean pointsPaid = cust.pointPayment(cust.loggedUser());
        Assert.assertTrue(pointsPaid);

        boolean removed = ModifyUserDB.removeUser("Alhazred@rlyeh.org");
        Assert.assertTrue(removed);
    }

    /**
     * REQ-4: Users must be able to create an order by adding a movie to their order. Users will not
     * be able to add a movie which is not in stock.
     *
     * REQ-7: Users will be rewarded 1 loyalty point per order. 10 loyalty points is applicable toward
     * a free movie rental.
     *
     * REQ-9: The system will update the available movie stock according to the corresponding user
     * action (placed order, cancelled order).
     *
     * REQ-10: Users must be able to dial in an operator and place an order for their movie rentals
     * via  the  operator  (after  providing  shipping/billing  address).  The  user  will  be  given  an  unique
     * order ID for their completed order.
     *
     * REQ-11: Users must be able to dial in an operator to check the status of their order by using
     * their unique order ID. Invalid order IDs will have no corresponding order.
     *
     * NB: For the entire order process to function, each of these requirements must be satisfied.  Writing up
     *     2 tests for each requirement will introduce redundant tests.
     *
     * NB: The method for the creation and retrieval of orders exists in StoreHook and as such can be used by both
     *     logged in Employees and Customers alike.
     */
    @Test
    public void test_create_order() {
        boolean login = cust.login("test", "test");
        Assert.assertTrue(login);

        //Adding non-existant movie to the cart
        boolean dne = cust.addMovieToCart(-200);
        Assert.assertFalse(dne);

        //Adding an existing movie to the cart
        boolean added = cust.addMovieToCart(-1);
        Assert.assertTrue(added);

        //Adding a completely different movie to the cart
        boolean dif = cust.addMovieToCart(-3);
        Assert.assertTrue(dif);

        //Making the order
        //NB: Each order has a unique ID assigned to it which is always returned once the order is made.
        //      THE INVENTORY OPERATOR WILL ALWAYS GET AN ID THEY CAN GIVE TO THE CUSTOMER!
        long orderID = cust.makeOrder(cust.loggedUser(), cust.getCart());
        Assert.assertEquals(0, orderID);

        //Whoops... Order DNE?
        Order none = OrderDB.getINSTANCE().getOrder(-1000);
        Assert.assertNull(none);

        //Ensuring order contents are right
        //NB: ORDERS CAN BE RETRIEVED USING THE ASSOCIATED ID
        ArrayList<Integer> movieIDs = OrderDB.getINSTANCE().getOrder(0).getMovies();
        Assert.assertTrue(movieIDs.contains(-1));
        Assert.assertTrue(movieIDs.contains(-3));

        //Cart should be empty
        Assert.assertEquals(0, cust.getCart().size());

        //Ensuring the customer got a loyalty point
        Assert.assertEquals(1, ((Customer) cust.loggedUser()).getLoyaltyPoints());

        //NB: OrderID = -3; by default is set to only have 1 copy in stock.  If the stock was updated
        //    then the user should be unable to add this to their cart to begin with in a new order.
        boolean notInStock = cust.addMovieToCart(-3);
        Assert.assertFalse(notInStock);

        //Cancelling the order will put the movie back in stock!
        boolean cancelled = cust.cancelOrder(0);
        Assert.assertTrue(cancelled);

        //If the movie is back in stock we should be able to add it now
        boolean inStock = cust.addMovieToCart(-3);
        Assert.assertTrue(inStock);

        //Removing the movie from the cart
        boolean outOfCart = cust.removeMovieFromCart(-3);
        Assert.assertTrue(outOfCart);
    }

    /**
     * REQ-12:  Users  must  be  able  to  rent  out  available  movies  from  the  two  store  locations  in
     * Toronto if they choose to walk into the store.
     *
     * NB: Cashiers can only place orders on movies which exist in the store they work in.  This is done by defaulting
     * the search for cashier results to being movies which can only be found in their store
     */
    @Test
    public void test_cashier_view() {
        boolean logged = emp.login("JoeSmith", "CompanyPassword");
        Assert.assertTrue(logged);

        ArrayList<Movie> res = emp.searchMovies("", 0);
        Assert.assertNotNull(res);
        for (Movie m : res)
            Assert.assertTrue(m.getCategories().contains(((Cashier) emp.loggedUser()).getLocation()));
    }

    /**
     * REQ-14:  Users must be able to manage their account/profile. This includes changing any of
     * the following: password, name, email.
     */
    @Test
    public void test_profile_manage_not_self() {
        String[] basicInfo = {"Testors", "Paint", "EnamelPaint", "idkman@domain.com", "Password!1"};
        String[] additionalInfo = {"123 Some Street idk", "L1L 1L1", "Toronto", "ON"};

        int reg = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(0, reg);

        boolean login = cust.login("EnamelPaint", "Password!1");
        Assert.assertTrue(login);

        //Trying to edit someone else as a Customer
        int notSelf = cust.editUser("test@domain.com", basicInfo, additionalInfo);
        Assert.assertEquals(-20, notSelf);

        //remove
        boolean remove = ModifyUserDB.removeUser("idkman@domain.com");
        Assert.assertTrue(remove);
    }

    @Test
    public void test_profile_manage() {
        String[] basicInfo = {"Testors", "Paint", "EnamelPaint", "idkman@domain.com", "Password!1"};
        String[] additionalInfo = {"123 Some Street idk", "L1L 1L1", "Toronto", "ON"};

        int reg = cust.addUser(basicInfo, additionalInfo, "Customer");
        Assert.assertEquals(0, reg);

        boolean login = cust.login("EnamelPaint", "Password!1");
        Assert.assertTrue(login);

        //Modifying self with no changes
        int noChange = cust.editUser(basicInfo[3], basicInfo, additionalInfo);
        Assert.assertEquals(0, noChange);

        String email = "idkman@domain.com";

        //Invalid email
        basicInfo[3] = "test@domain.com";
        int invalidEmail = cust.editUser(email, basicInfo, additionalInfo);
        Assert.assertEquals(3, invalidEmail);
        basicInfo[3] = email;

        //invalid usrName
        basicInfo[2] = "test";
        int invalidUsername = cust.editUser(email, basicInfo, additionalInfo);
        Assert.assertEquals(3, invalidUsername);
        basicInfo[2] = "EnamelPaint";

        //Invalid province code
        additionalInfo[3] = "test@domain.com";
        int badProvCode = cust.editUser(email, basicInfo, additionalInfo);
        Assert.assertEquals(4, badProvCode);
        additionalInfo[3] = "ON";

        //Invalid postal code
        additionalInfo[1] = "aaaaaaa";
        int badPost = cust.editUser(email, basicInfo, additionalInfo);
        Assert.assertEquals(6, badPost);
        additionalInfo[1] = "L1L 1L1";

        //Invalid address
        additionalInfo[0] = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        int invalidAddress = cust.editUser(email, basicInfo, additionalInfo);
        Assert.assertEquals(8, invalidAddress);
        additionalInfo[0] = "123 idk str.";

        //remove
        boolean remove = ModifyUserDB.removeUser("idkman@domain.com");
        Assert.assertTrue(remove);
    }

    /**
     * REQ-16: The System Admin is able to add/remove a movie from the system.
     */
    @Test
    public void test_admin_add_movie() {
        boolean logged = emp.login("SystemUser", "test");
        Assert.assertTrue(logged);

        int[] is = {0, -500};
        double price = -9.99;
        String[] trgd = {"Test Movie", "2020-031-01", "Test Case", "This is the description"};
        String[][] adc = {
                {"Person 1", "Person 2", "Person 3"},
                {"Director 1"},
                {"Category"}
        };

        //negative stock
        boolean badMovie = ((EmployeeStore) emp).addMovie(is, price, trgd, adc);
        Assert.assertFalse(badMovie);
        is[1] = 500;

        //negative price
        badMovie = ((EmployeeStore) emp).addMovie(is, price, trgd, adc);
        Assert.assertFalse(badMovie);
        price = 9.99;

        //invalid release date
        badMovie = ((EmployeeStore) emp).addMovie(is, price, trgd, adc);
        Assert.assertFalse(badMovie);
        trgd[1] = "2020-01-01";

        boolean addMovie = ((EmployeeStore) emp).addMovie(is, price, trgd, adc);
        Assert.assertTrue(addMovie);

        //Adding the duplicate movie
        boolean dupeMovie = ((EmployeeStore) emp).addMovie(is, price, trgd, adc);
        Assert.assertFalse(dupeMovie);

        //Clearing the movie
        boolean removed = MovieDB.getINSTANCE().removeMovie(0);
        Assert.assertTrue(removed);
    }

    @Test
    public void test_admin_remove_movie() {
        boolean logged = emp.login("SystemUser", "test");
        Assert.assertTrue(logged);

        int[] is = {0, 500};
        double price = 9.99;
        String[] trgd = {"Test Movie", "2020-01-01", "Test Case", "This is the description"};
        String[][] adc = {
                {"Person 1", "Person 2", "Person 3"},
                {"Director 1"},
                {"Category"}
        };

        boolean addMovie = ((EmployeeStore) emp).addMovie(is, price, trgd, adc);
        Assert.assertTrue(addMovie);

        //checking if movie added
        boolean exists = MovieDB.getINSTANCE().getMovie(0) != null;
        Assert.assertTrue(exists);

        boolean removeMovie = ((EmployeeStore) emp).removeMovie(0);
        Assert.assertTrue(removeMovie);

        boolean removeDNE = ((EmployeeStore) emp).removeMovie(-600);
        Assert.assertFalse(removeDNE);

        boolean removeRemoved = ((EmployeeStore) emp).removeMovie(0);
        Assert.assertFalse(removeRemoved);
    }

    /**
     * REQ-17: The System Admin is able to update movie information (title, actors, directors, date
     * of release, description).
     */
    @Test
    public void test_not_admin_mod_movie() {
        boolean logged = emp.login("GelPen", "Secure");
        Assert.assertTrue(logged);

        int[] is = {0, 500};
        double price = 9.99;
        String[] trgd = {"Test Movie", "2020-01-01", "Test Case", "This is the description"};
        String[][] adc = {
                {"Person 1", "Person 2", "Person 3"},
                {"Director 1"},
                {"Category"}
        };

        boolean mod = ((EmployeeStore) emp).modifyMovie(is, price, trgd, adc);
        Assert.assertFalse(mod);
    }

    @Test
    public void test_admin_mod_movie() {
        boolean logged = emp.login("SystemUser", "test");
        Assert.assertTrue(logged);

        int[] is = {0, 500};
        double price = 9.99;
        String[] trgd = {"Test Movie", "2020-01-01", "Test Case", "This is the description"};
        String[][] adc = {
                {"Person 1", "Person 2", "Person 3"},
                {"Director 1"},
                {"Category"}
        };

        boolean addMovie = ((EmployeeStore) emp).addMovie(is, price, trgd, adc);
        Assert.assertTrue(addMovie);

        //Modding the movie without any changes
        boolean noChange = ((EmployeeStore) emp).modifyMovie(is, price, trgd, adc);
        Assert.assertTrue(noChange);

        //negative stock
        is[1] = -500;
        boolean badMovie = ((EmployeeStore) emp).modifyMovie(is, price, trgd, adc);
        Assert.assertFalse(badMovie);
        is[1] = 500;

        //negative price
        price = -9.99;
        badMovie = ((EmployeeStore) emp).modifyMovie(is, price, trgd, adc);
        Assert.assertFalse(badMovie);
        price = 9.99;

        //invalid release date
        trgd[1] = "2020-021-01";
        badMovie = ((EmployeeStore) emp).modifyMovie(is, price, trgd, adc);
        Assert.assertFalse(badMovie);
        trgd[1] = "2020-01-01";

        boolean removeRemoved = ((EmployeeStore) emp).removeMovie(0);
        Assert.assertTrue(removeRemoved);
    }

    /**
     * REQ-18:  A  system  admin  will  be  able to  retrieve  and  update customer  account  information
     * including: name, email, password, username, order information, order status.
     *
     * NB: As per REQ-14, an admin is able to edit their own information
     */
    @Test
    public void test_admin_mod_system() {
        boolean logged = emp.login("SamRodriguez@desperado.com", "PasswordWithHighEntropy0!1@2#3$4%5^6&7*8(9)10");
        Assert.assertTrue(logged);

        String[] basicInfo = {"VideoCo", "System", "SystemUser", "VideoCoSystem@VideoCo.org", "test"};
        String[] additionalInfo = {"GMT-7:00"};

        int modSystem = emp.editUser(basicInfo[3], basicInfo, additionalInfo);
        Assert.assertEquals(-80, modSystem);
    }

    @Test
    public void test_admin_mod_user() {
        boolean logged = emp.login("SamRodriguez@desperado.com", "PasswordWithHighEntropy0!1@2#3$4%5^6&7*8(9)10");
        Assert.assertTrue(logged);

        String[] basicInfo = {"John", "Doe", "InvOp", "JohnDoe@VideoCo.org", "Password!1"};
        String[] additionalInfo = {"1234"};

        int added = emp.addUser(basicInfo, additionalInfo, "Inventory Operator");
        Assert.assertEquals(0, added);

        //admin can view all users
        Assert.assertNotEquals(0, ((EmployeeStore) emp).viewUsers());

        //Admin modding a user; no change
        int mod = emp.editUser(basicInfo[3], basicInfo, additionalInfo);
        Assert.assertEquals(0, mod);

        //removing the sample user
        boolean removed = ((EmployeeStore) emp).deleteUser("JohnDoe@VideoCo.org");
        Assert.assertTrue(removed);
    }

    /**
     * REQ-19: A system admin will be able to delete existing customer accounts.
     *
     * REQ-20: The system can add admin accounts to the system using the administrator's name
     * and email address. An admin must be an employee of the company.
     *
     * REQ-21: The system can remove admin accounts from the system.
     *
     * REQ-22: The system will be able to update the customer’s order shipping status.
     */
    @Test
    public void test_admin_delete_self() {
        boolean logged = emp.login("SystemUser", "test");
        Assert.assertTrue(logged);

        boolean removeSelf = ((EmployeeStore) emp).deleteUser(emp.loggedUser().getEmail());
        Assert.assertFalse(removeSelf);
    }

    @Test
    public void test_admin_delete() {
        boolean logged = emp.login("SystemUser", "test");
        Assert.assertTrue(logged);

        String[] basicInfo = {"John", "Doe", "InvOp", "JohnDoe@VideoCo.org", "Password!1"};
        String[] additionalInfo = {"GMT+3:00"};

        int added = emp.addUser(basicInfo, additionalInfo, "Admin");
        Assert.assertEquals(0, added);

        basicInfo[0] = "Real";
        basicInfo[1] = "Person";
        additionalInfo[0] = "GMT-3:00";
        int mod = emp.editUser(basicInfo[3], basicInfo, additionalInfo);
        Assert.assertEquals(0, mod);

        //removing the added admin
        boolean removed = ((EmployeeStore) emp).deleteUser(basicInfo[3]);
        Assert.assertTrue(removed);
    }

    /**
     * REQ-8: The User should be notified of a late fee based on their location. Canadian Users are
     * charged a late fee of $9.99 if they are located outside of Ontario. The company is not global,
     * it is Canada only.
     *
     * REQ-22: The system will be able to update the customer’s order shipping status.
     *
     * REQ-24: The system will charge customers a late fee of $1.00 CAD per day for movies which
     * are not returned within two weeks. The $1.00 CAD charge is a per movie charge.
     *
     * NB: For REQ-22, it's mainly done for when the status is "Await Payment" for simulation purposes
     */
    @Test
    public void test_system() {
        //Checking order updating
        Order focus = OrderDB.getINSTANCE().getOrder(-1);
        Assert.assertEquals("Await Payment", focus.getState());

        VideoCoSys system = new VideoCoSys();
        system.performChecks();

        //Checking order after system checks
        Assert.assertEquals("Await Shipment", focus.getState());

        //Assumption: Fee of $1.00 for users in Ontario, $9.99 outside of Ontario
        //Target customer initially is locationed outside of Ontario (New Brunswick)
        double fee1 = ((Customer) DBUser.getINSTANCE().getUser("test@domain.com")).getAmtOwed();
        Assert.assertEquals(9.99, fee1, 0.0);

        //Changing to Ontario
        User temp = DBUser.getINSTANCE().getUser("test@domain.com");
        ((Customer) temp).setProvince("ON");
        DBUser.getINSTANCE().modifyUser(temp.getEmail(), temp);

        system.performChecks();

        double fee2 = ((Customer) DBUser.getINSTANCE().getUser("test@domain.com")).getAmtOwed();
        Assert.assertEquals(fee1 + 1.00, fee2, 0.0);

        ((Customer) temp).setProvince("NB");
        DBUser.getINSTANCE().modifyUser(temp.getEmail(), temp);
    }

    /**
     * REQ-13: Users must be able to cancel their movie rental order given that the order status is
     * not “delivered”.
     *
     * Placed here because of how test_system affects the DB
     */
    @Test
    public void test_cancel_order_fulfilled() {
        boolean logged = cust.login("test", "test");
        Assert.assertTrue(logged);

        //getting the only order
        Order order = cust.fetchOrder(-40);
        Assert.assertNotNull(order);
        Assert.assertEquals(OrderDB.getINSTANCE().getOrder(-40), order);
        Assert.assertEquals("Fulfilled", order.getState());

        //Trying to cancel the only order (Fulfilled)
        boolean cancelled = cust.cancelOrder(-40);
        Assert.assertFalse(cancelled);
    }

    @Test
    public void test_cancel_order_notFulfilled() {
        boolean logged = cust.login("test", "test");
        Assert.assertTrue(logged);

        //getting the only order
        Order order = cust.fetchOrder(-40);
        Assert.assertNotNull(order);
        Assert.assertEquals(OrderDB.getINSTANCE().getOrder(-40), order);

        order.setState("Await Shipment");
        OrderDB.getINSTANCE().modOrder(order.getOrderID(), order);
        Assert.assertEquals("Await Shipment", order.getState());

        boolean cancelled = cust.cancelOrder(-40);
        Assert.assertTrue(cancelled);
    }
}
