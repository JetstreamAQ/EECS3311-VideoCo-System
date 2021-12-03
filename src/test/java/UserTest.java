import login.Login;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import register.Register;
import register.RegisterCustomer;
import register.RegisterEmployee;
import user.DBUser;
import user.ModifyUserDB;
import user.data.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UserTest {
    DBUser users = DBUser.getINSTANCE();
    User newAdmin = new Admin(),
         newCashier = new Cashier(),
         newCustomer = new Customer(),
         newInvOp = new InventoryOperator(),
         newWSTeam = new WarehouseShippingTeam();

    Map<Integer, String> invalidTz = new HashMap<>(),
                         invalidExt = new HashMap<>();

    @Before
    public void setup() {
        System.out.println("IMPORTANT");
        for (int i = 0; i < 10; i++)
            System.out.println("IF ANY OF THE TESTS FAILS RUN 'loadbkup.sh' IN src/resources/yaml BEFORE RUNNING TESTS AGAIN!");
        System.out.println();

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

    @Test //TODO: Consider the random testing you do here
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
}
