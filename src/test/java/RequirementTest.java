import movie.Movie;
import movie.MovieDB;
import order.Order;
import order.OrderDB;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import storehook.CustomerStore;
import storehook.EmployeeStore;
import storehook.StoreHook;
import system.VideoCoSys;
import user.DBUser;
import user.ModifyUserDB;
import user.data.Cashier;
import user.data.Customer;
import user.data.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RequirementTest {
    StoreHook cust, emp;
    Map<Integer, String> invalidTz = new HashMap<>();
    Map<Integer, String> invalidExt = new HashMap<>();

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
