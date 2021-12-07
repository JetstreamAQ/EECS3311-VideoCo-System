import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import movie.Movie;
import movie.MovieDB;
import order.Order;
import order.state.Fulfilled;
import order.state.OrderState;
import storehook.CustomerStore;
import storehook.EmployeeStore;
import storehook.StoreHook;
import system.VideoCoSys;
import user.data.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class App extends Application {
    private Stage focus;
    private StoreHook hook;

    public void launch() {
        Application.launch();
    }

    @Override
    public void start(Stage stage) {
        focus = stage;
        focus.setResizable(false);
        focus.setTitle("VideoCo Rental System");
        Scene current = openScreen();

        focus.setScene(current);
        focus.show();
    }

    /**
     * This is the main menu for prompting the user to log in
     * @return the login select screen
     */
    private Scene openScreen() {
        GridPane grid = createGrid();
        Text greeting = new Text("VideoCo Rental System");
        greeting.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 24));
        grid.add(greeting, 0, 0, 2, 1);

        Button employeeLogin = new Button("Employee Login");
        employeeLogin.setOnAction(actionEvent -> focus.setScene(loginScreen(true)));
        grid.add(employeeLogin, 0, 4);

        Button customerLogin = new Button("Customer Login");
        customerLogin.setOnAction(actionEvent -> focus.setScene(loginScreen(false)));
        grid.add(customerLogin, 1, 4);

        Button register = new Button("Register");
        register.setOnAction(actionEvent -> focus.setScene(register(0, false)));
        HBox regBox = new HBox(10);
        regBox.setAlignment(Pos.CENTER);
        regBox.getChildren().add(register);
        grid.add(regBox, 0, 5, 2, 1);

        return new Scene(grid, 480, 240);
    }

    /**
     * The register screen scene.
     *
     * @param type integer flag representing the type of user account being created
     * @param logMenu whether we should return to the main menu for when the user already logged in
     * @return the register screen
     */
    private Scene register(int type, boolean logMenu) {
        GridPane grid = createGrid();

        //Provinces
        Map<String, String> provToCode = new LinkedHashMap<>();
        provToCode.put("Newfoundland & Labrador", "NL");
        provToCode.put("Prince Edward Island", "PE");
        provToCode.put("Nova Scotia", "NS");
        provToCode.put("New Brunswick", "NB");
        provToCode.put("Quebec", "QC");
        provToCode.put("Ontario", "ON");
        provToCode.put("Manitoba", "MB");
        provToCode.put("Saskatchewan", "SK");
        provToCode.put("Alberta", "AB");
        provToCode.put("British Columbia", "BC");
        provToCode.put("Yukon", "YT");
        provToCode.put("Northwest Territories", "NT");
        provToCode.put("Nunavut", "NU");
        ObservableList<String> provinces = FXCollections.observableArrayList(provToCode.keySet());

        //integer flag to account type
        Map<Integer, String> intToString = new HashMap<>();
        intToString.put(0, "Customer");
        intToString.put(1, "Admin");
        intToString.put(2, "Cashier");
        intToString.put(3, "Inventory Operator");
        intToString.put(4, "Warehouse Shipping");

        Text header = new Text("Register an account");
        header.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 24));
        grid.add(header, 0, 0, 2, 1);

        /*BASIC INFORMATION BEGIN*/
        String[] baseInfo = new String[5];

        Text fName = new Text("First Name:");
        grid.add(fName, 0, 1);
        TextField fNameField = new TextField();
        grid.add(fNameField, 1, 1);

        Text lName = new Text("Last Name:");
        grid.add(lName, 0, 2);
        TextField lNameField = new TextField();
        grid.add(lNameField, 1, 2);

        Text user = new Text("Username:");
        grid.add(user, 0, 3);
        TextField userField = new TextField();
        grid.add(userField, 1, 3);

        Text email = new Text("E-Mail:");
        grid.add(email, 0, 4);
        TextField emailField = new TextField();
        grid.add(emailField, 1, 4);

        Text pass = new Text("Password:");
        grid.add(pass, 0, 5);
        PasswordField passField = new PasswordField();
        grid.add(passField, 1, 5);

        Text passConf = new Text("Confirm Password:");
        grid.add(passConf, 0, 6);
        PasswordField passConfField = new PasswordField();
        grid.add(passConfField, 1, 6);
        /*BASIC INFORMATION END*/

        /*CUSTOMER*/
        Text address = new Text("Address:");
        TextField addressField = new TextField();
        Text postal = new Text("Postal Code:");
        TextField postalField = new TextField();
        Text cityTown = new Text("City/Town");
        TextField cityTownField = new TextField();
        Text province = new Text("Province:");
        ComboBox provinceMenu = new ComboBox(provinces);
        provinceMenu.getSelectionModel().selectFirst();

        /*ADMIN*/
        Text timeZone = new Text("Time Zone (GMT...):");
        TextField tzField = new TextField();

        /*CASHIER*/
        Text location = new Text("Store Location:");
        ObservableList<String> storeLocations = FXCollections.observableArrayList("In-Store Location 1", "In-Store Location 2");
        ComboBox locationMenu = new ComboBox(storeLocations);
        locationMenu.getSelectionModel().selectFirst();

        /*INVENTORY OPERATOR*/
        Text extensionNum = new Text("Ext. Num:");
        TextField extField = new TextField();

        /*WAREHOUSE SHIPPING TEAM*/
        Text warehouseLocation = new Text("Location:"); //reusing location menu for the dropdown
        RadioButton isShipping = new RadioButton("Shipping Team?");

        /*ADDITIONAL INFORMATION BEGIN*/
        int vertOffset = 6;
        switch (type) {
            case 0: //Customer
                grid.add(address, 0, ++vertOffset);
                grid.add(addressField, 1, vertOffset);

                grid.add(postal, 0, ++vertOffset);
                grid.add(postalField, 1, vertOffset);

                grid.add(cityTown, 0, ++vertOffset);
                grid.add(cityTownField, 1, vertOffset);

                grid.add(province, 0, ++vertOffset);
                grid.add(provinceMenu, 1, vertOffset);
                break;

            case 1: //Admin
                grid.add(timeZone, 0, ++vertOffset);
                grid.add(tzField, 1, vertOffset);
                break;

            case 2: //Cashier
                grid.add(location, 0, ++vertOffset);
                grid.add(locationMenu, 1, vertOffset);
                break;

            case 3: //Inventory Operator
                grid.add(extensionNum, 0, ++vertOffset);
                grid.add(extField, 1, vertOffset);
                break;

            case 4: //Warehouse shipping team
                grid.add(warehouseLocation, 0, ++vertOffset);
                grid.add(provinceMenu, 1, vertOffset);

                grid.add(isShipping, 1, ++vertOffset);
                break;
        }
        /*ADDITIONAL INFORMATION END*/

        Button back = new Button("Back");
        back.setOnAction(actionEvent -> focus.setScene((logMenu) ? mainMenu(true) : openScreen()));
        grid.add(back, 0, ++vertOffset);

        Text regResult = new Text();
        grid.add(regResult, 0, vertOffset + 1, 2, 1);

        Button register = new Button("Register");
        register.setOnAction(actionEvent -> {
            regResult.setFill(Color.RED);
            if (passField.getText().equals(passConfField.getText())) {
                StoreHook tempHook = (type == 0) ? new CustomerStore() : new EmployeeStore();

                baseInfo[0] = fNameField.getText();
                baseInfo[1] = lNameField.getText();
                baseInfo[2] = userField.getText();
                baseInfo[3] = emailField.getText();
                baseInfo[4] = passField.getText();

                String[] additionalInfo = new String[0];
                switch (type) {
                    case 0:
                        additionalInfo = new String[] {
                                addressField.getText(),
                                postalField.getText(),
                                cityTownField.getText(),
                                provToCode.get(provinceMenu.getValue().toString())
                        };
                        break;

                    case 1: additionalInfo = new String[]{tzField.getText()}; break;
                    case 2: additionalInfo = new String[]{locationMenu.getValue().toString()}; break;
                    case 3: additionalInfo = new String[]{extField.getText()}; break;

                    case 4:
                        additionalInfo = new String[] {
                                provToCode.get(provinceMenu.getValue().toString()),
                                (isShipping.isSelected()) ? "True" : "False"
                        };
                        break;
                }

                int res = tempHook.addUser(baseInfo, additionalInfo, intToString.get(type));
                System.out.println(res);
                regResult.setText(
                        (res == 0) ? "Registration successful" :
                        (1 <= res && res <= 5) ? "Password must:\n- Be 8 characters long\n- Contain at least one lowercase & uppercase letter\n- One symbol\n- One number\n- No spaces " :
                        (res == 7) ? "Postal code must be of the form:\n\"[CAPITAL][number][CAPITAL] [number][CAPITAL][number]\"" :
                        (res == 9) ? "Username/Email already in use" :
                        (res == 10) ? "Passed E-mail isn't a valid email address" : "There was an error with one or more of your inputs."
                );
            } else {
                regResult.setText("Passwords do not match.");
            }
        });
        grid.add(register, 1, vertOffset);

        return new Scene(grid, 360, 500);
    }

    /**
     * The login screen scene
     *
     * @param employee whether to modify function and appearance for the employee login
     * @return the login screen
     */
    private Scene loginScreen(boolean employee) {
        GridPane grid = createGrid();

        Text text = new Text((employee) ? "Employee Login" : "Login");
        text.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 24));
        grid.add(text, 0, 0, 2, 1);

        Label user = new Label("User/Email:");
        grid.add(user, 0, 1);
        TextField userField = new TextField();
        grid.add(userField, 1, 1);

        Label password = new Label("Password:");
        grid.add(password, 0, 2);
        PasswordField pwField = new PasswordField();
        grid.add(pwField, 1, 2);

        Button back = new Button("Back");
        back.setOnAction(actionEvent -> focus.setScene(openScreen()));
        grid.add(back, 0, 3);

        Text loginResult = new Text();
        grid.add(loginResult, 0, 4, 2, 1);

        Button login = new Button("Login");
        login.setOnAction(actionEvent -> {
            hook = (employee) ? new EmployeeStore() : new CustomerStore();
            boolean result = hook.login(userField.getText(), pwField.getText());
            loginResult.setFill(Color.RED);
            loginResult.setText((!result) ? "Invalid credentials.  Try again." : "Login Successful.\nPlease Wait as we log you in.");

            if (result)
                focus.setScene(mainMenu(employee));
        });
        grid.add(login, 1, 3);

        return new Scene(grid, 360, 360);
    }

    /**
     * @param employee whether the currently logged user is an employee or not
     * @return a scene for viewing the main menu of the application after logging in.
     */
    private Scene mainMenu(boolean employee) {
        GridPane grid = createGrid();

        Text text = new Text("Welcome, " + hook.loggedUser().getFName() + " " + hook.loggedUser().getLName() + ".");
        text.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 18));
        grid.add(text, 0, 0, 3, 1);

        Button logout = new Button("Logout");
        logout.setOnAction(actionEvent -> {
            hook = null;
            focus.setScene(openScreen());
        });
        grid.add(logout, 0, 1);

        Button editAccount = new Button("Edit/View Account Details");
        editAccount.setOnAction(actionEvent -> focus.setScene(new Scene(viewProfile(hook.loggedUser()), 1280, 720)));
        grid.add(editAccount, 0, 2);

        Button viewOrders = new Button("View Orders");
        viewOrders.setOnAction(actionEvent -> focus.setScene(viewOrders()));
        grid.add(viewOrders, 0, 3);

        Button viewMovies = new Button("Browse Movies");
        viewMovies.setOnAction(actionEvent -> focus.setScene(browseMovies(employee)));
        grid.add(viewMovies, 0, 4);

        Button viewUsers = new Button("View/Edit User Accounts");
        viewUsers.setOnAction(actionEvent -> focus.setScene(viewUserList()));

        ObservableList<String> employeeType = FXCollections.observableArrayList();
        employeeType.add("Admin");
        employeeType.add("Cashier");
        employeeType.add("Inventory Operator");
        employeeType.add("Warehouse Shipping Team");
        ComboBox employeeVar = new ComboBox(employeeType);
        employeeVar.getSelectionModel().selectFirst();

        Button regEmployee = new Button("Register Employee");
        regEmployee.setOnAction(actionEvent -> focus.setScene(register(employeeVar.getSelectionModel().getSelectedIndex() + 1, true)));

        if (hook.loggedUser() instanceof Admin) {
            grid.add(viewUsers, 0, 5);
            grid.add(regEmployee, 0, 6);
            grid.add(employeeVar, 1, 6);
        }

        /*System Exclusive Stuff*/
        Button performChecks = new Button("Perform Check");
        performChecks.setOnAction(actionEvent -> {
            VideoCoSys system = new VideoCoSys();
            system.performChecks();
        });
        if (hook.loggedUser().getEmail().equalsIgnoreCase("VideoCoSystem@VideoCo.org"))
            grid.add(performChecks, 0, 7);

        return new Scene(grid, 400, 720);
    }

    /**
     * @param user The user whose information will be displayed and potentially modified
     * @return a scene for viewing user profile information
     */
    private GridPane viewProfile(User user) {
        GridPane grid = createGrid();

        String employeeInfo = (user instanceof Employee) ? (" [Employee ID#" + ((Employee) user).getId() + "]") : (" [Loyalty Points: " + ((Customer) user).getLoyaltyPoints() + "]");
        Text text = new Text("User Profile" + employeeInfo);
        text.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 20));
        grid.add(text, 0, 0, 3, 1);

        Separator s1 = new Separator();
        s1.setMinWidth(400);
        s1.setMaxWidth(400);
        grid.add(s1, 0, 1, 3, 1);

        //Provinces
        Map<String, String> provToCode = new LinkedHashMap<>();
        provToCode.put("Newfoundland & Labrador", "NL");
        provToCode.put("Prince Edward Island", "PE");
        provToCode.put("Nova Scotia", "NS");
        provToCode.put("New Brunswick", "NB");
        provToCode.put("Quebec", "QC");
        provToCode.put("Ontario", "ON");
        provToCode.put("Manitoba", "MB");
        provToCode.put("Saskatchewan", "SK");
        provToCode.put("Alberta", "AB");
        provToCode.put("British Columbia", "BC");
        provToCode.put("Yukon", "YT");
        provToCode.put("Northwest Territories", "NT");
        provToCode.put("Nunavut", "NU");
        ObservableList<String> provinces = FXCollections.observableArrayList(provToCode.keySet());

        /*### SHARED INFO BEGIN ###*/
        Text fName = new Text("First Name");
        TextField fNameField = new TextField(user.getFName());
        grid.add(fName, 0, 2);
        grid.add(fNameField, 0, 3);

        Text lName = new Text("Last Name");
        TextField lNameField = new TextField(user.getLName());
        grid.add(lName, 1, 2);
        grid.add(lNameField, 1, 3);

        Text username = new Text("Username");
        TextField unField = new TextField(user.getUsername());
        grid.add(username, 0, 4);
        grid.add(unField, 0, 5);

        Text email = new Text("E-Mail");
        TextField emailField = new TextField(user.getEmail());
        grid.add(email, 0, 6);
        grid.add(emailField, 0, 7);

        Text newPass = new Text("New Password");
        PasswordField newPassField = new PasswordField();
        grid.add(newPass, 0, 8);
        grid.add(newPassField, 0, 9);

        Text confirmPass = new Text("Confirm Password");
        PasswordField confField = new PasswordField();
        grid.add(confirmPass, 1, 8);
        grid.add(confField, 1, 9);
        /*### SHARED INFO END ###*/

        /*CUSTOMER*/
        Text address = new Text("Address:");
        TextField addressField = new TextField();
        Text postal = new Text("Postal Code:");
        TextField postalField = new TextField();
        Text cityTown = new Text("City/Town");
        TextField cityTownField = new TextField();
        Text province = new Text("Province:");
        ComboBox provinceMenu = new ComboBox(provinces);
        double due = (user instanceof Customer) ? ((Customer) user).getAmtOwed() : 0.0;
        Text amtOwed = new Text("Amount Owed: $" + String.format("%,.2f", due)+ "\nThis amount will automatically be paid for in your next purchase.");

        /*ADMIN*/
        Text timeZone = new Text("Time Zone (GMT...):");
        TextField tzField = new TextField();

        /*CASHIER*/
        Text location = new Text("Store Location:");
        ObservableList<String> storeLocations = FXCollections.observableArrayList("In-Store Location 1", "In-Store Location 2");
        ComboBox locationMenu = new ComboBox(storeLocations);

        /*INVENTORY OPERATOR*/
        Text extensionNum = new Text("Ext. Num:");
        TextField extField = new TextField();

        /*WAREHOUSE SHIPPING TEAM*/
        Text warehouseLocation = new Text("Location:"); //reusing location menu for the dropdown
        RadioButton isShipping = new RadioButton("Shipping Team?");

        Separator s2 = new Separator();
        s2.setMinWidth(400);
        s2.setMaxWidth(400);
        grid.add(s2, 0, 10, 3, 1);

        int vertOffset = 10, type = 0;
        if (user instanceof Customer) {
            Customer cust = (Customer) user;
            addressField.setText(cust.getStreet());
            postalField.setText(cust.getPostalCode());
            cityTownField.setText(cust.getCityTown());

            //Province code to full string
            String provName = "";
            for (Map.Entry<String, String> e : provToCode.entrySet())
                provName = (e.getValue().equals(cust.getProvince())) ? e.getKey() : provName;
            provinceMenu.setValue(provName);

            grid.add(address, 0, ++vertOffset);
            grid.add(addressField, 1, vertOffset);

            grid.add(postal, 0, ++vertOffset);
            grid.add(postalField, 1, vertOffset);

            grid.add(cityTown, 0, ++vertOffset);
            grid.add(cityTownField, 1, vertOffset);

            grid.add(province, 0, ++vertOffset);
            grid.add(provinceMenu, 1, vertOffset);

            grid.add(amtOwed, 0, ++vertOffset, 3, 1);
        } else if (user instanceof Admin) {
            type = 1;
            tzField.setText(((Admin) user).getTimeZone());

            grid.add(timeZone, 0, ++vertOffset);
            grid.add(tzField, 1, vertOffset);
        } else if (user instanceof Cashier) {
            type = 2;
            locationMenu.getSelectionModel().select(((Cashier) user).getLocation());

            grid.add(location, 0, ++vertOffset);
            grid.add(locationMenu, 1, vertOffset);
        } else if (user instanceof InventoryOperator) {
            type = 3;
            extField.setText(((InventoryOperator) user).getExtensionNum());

            grid.add(extensionNum, 0, ++vertOffset);
            grid.add(extField, 1, vertOffset);
        } else if (user instanceof WarehouseShippingTeam) {
            type = 4;
            //Province code to full string
            String provName = "";
            for (Map.Entry<String, String> e : provToCode.entrySet())
                provName = (e.getValue().equals(((WarehouseShippingTeam) user).getWarehouseLocation())) ? e.getKey() : provName;
            provinceMenu.setValue(provName);

            isShipping.setSelected(((WarehouseShippingTeam) user).getIsShipping());

            grid.add(warehouseLocation, 0, ++vertOffset);
            grid.add(provinceMenu, 1, vertOffset);

            grid.add(isShipping, 1, ++vertOffset);
        }

        //back button
        Button viewMovies = new Button("Back");
        viewMovies.setOnAction(actionEvent -> focus.setScene(mainMenu(user instanceof Employee)));
        grid.add(viewMovies, 0, ++vertOffset);

        //Save edit
        Button save = new Button("Save Changes");
        int finalType = type;
        save.setOnAction(actionEvent -> {
            save.setTextFill(Color.RED);
            if (newPassField.getText().equals(confField.getText())) {
                String[] additionalInfo = new String[0];
                String[] baseInfo = {
                        fNameField.getText(),
                        lNameField.getText(),
                        unField.getText(),
                        emailField.getText(),
                        (newPassField.getText().equals("")) ? user.getPassword() : newPassField.getText()
                };
                switch (finalType) {
                    case 0:
                        additionalInfo = new String[]{
                                addressField.getText(),
                                postalField.getText(),
                                cityTownField.getText(),
                                provToCode.get(provinceMenu.getValue().toString())
                        };
                        break;

                    case 1:
                        additionalInfo = new String[]{tzField.getText()};
                        break;
                    case 2:
                        additionalInfo = new String[]{locationMenu.getValue().toString()};
                        break;
                    case 3:
                        additionalInfo = new String[]{extField.getText()};
                        break;

                    case 4:
                        additionalInfo = new String[]{
                                provToCode.get(provinceMenu.getValue().toString()),
                                (isShipping.isSelected()) ? "True" : "False"
                        };
                        break;
                }

                int editRes = hook.editUser(user.getEmail(), baseInfo, additionalInfo);
                switch(editRes) {
                    case -5: save.setText("Password missing special chars."); break;
                    case -4: save.setText("Password missing numbers"); break;
                    case -3: save.setText("Password missing upper-case chars."); break;
                    case -2: save.setText("Password missing lower-case chars."); break;
                    case -1: save.setText("Password too short!"); break;

                    case 0:
                        save.setTextFill(Color.BLUE);
                        save.setText("Changes Saved!");
                        break;

                    case 3: save.setText("The new username/email is in use!"); break;
                    case 5: save.setText("Invalid timezone!"); break;
                    case 6: save.setText("Invalid postal code!"); break;
                    case 7: save.setText("Invalid extension number!"); break;
                    case 8: save.setText("Invalid street!"); break;

                    default: save.setText("There's a problem with your input!");
                }
            } else {
                save.setText("Passwords do not match!");
            }
        });
        grid.add(save, 1, vertOffset);

        return grid;
    }

    private Scene viewUserList() {
        GridPane grid = createGrid();

        Text text = new Text("List of Registered Users");
        text.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 24));
        grid.add(text, 0, 0, 3, 1);

        Button viewUser = new Button("View User");
        Button deleteUser = new Button("Delete User");

        AtomicReference<ObservableList<String>> emails = new AtomicReference<>(FXCollections.observableArrayList());
        AtomicReference<ArrayList<User>> userList = new AtomicReference<>(((EmployeeStore) hook).viewUsers());
        for (User u : userList.get())
            emails.get().add(u.getEmail());
        ListView<String> orders = new ListView<>(emails.get());
        orders.setPrefSize(300, 500);
        orders.setEditable(false);
        grid.add(orders, 0, 1, 5, 1);

        ScrollPane userDetails = new ScrollPane();
        userDetails.setPrefSize(600, 500);
        userDetails.setContent(viewProfile(userList.get().get(0)));
        grid.add(userDetails, 5, 1, 5, 1);

        viewUser.setOnAction(actionEvent -> {
            if (orders.getSelectionModel().getSelectedIndex() >= 0) {
                User target = ((EmployeeStore) hook).fetchUser(orders.getSelectionModel().getSelectedItem());
                userDetails.setContent(viewProfile(target));

                emails.set(FXCollections.observableArrayList());
                userList.set(((EmployeeStore) hook).viewUsers());
                for (User u : userList.get())
                    emails.get().add(u.getEmail());
                orders.setItems(emails.get());

                deleteUser.setText("Delete User");
            }
        });
        grid.add(viewUser,0, 2);

        deleteUser.setOnAction(actionEvent -> {
            if (orders.getSelectionModel().getSelectedIndex() >= 0) {
                boolean succ = ((EmployeeStore) hook).deleteUser(orders.getSelectionModel().getSelectedItem());
                if (succ)
                    deleteUser.setText("User Removed!");
                else
                    deleteUser.setText("Unable to delete user!");

                emails.set(FXCollections.observableArrayList());
                userList.set(((EmployeeStore) hook).viewUsers());
                for (User u : userList.get())
                    emails.get().add(u.getEmail());
                orders.setItems(emails.get());
                userDetails.setContent(viewProfile(userList.get().get(0)));
            }
        });
        grid.add(deleteUser, 1, 2);

        return new Scene(grid, 1280, 720);
    }

    /**
     * @return Scene for viewing orders
     */
    private Scene viewOrders() {
        GridPane grid = createGrid();

        Text text = new Text("Placed Orders");
        text.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 24));
        grid.add(text, 0, 0, 3, 1);

        ScrollPane orderDetails = new ScrollPane();
        orderDetails.setPrefSize(600, 500);
        grid.add(orderDetails, 5, 1, 5, 1);

        ObservableList<String> ids = FXCollections.observableArrayList();
        if (hook instanceof CustomerStore) {
            ArrayList<Long> orderIds = ((CustomerStore) hook).fetchCustOrders();
            for (Long l : orderIds)
                ids.add("Order #" + l);
        } else if (hook instanceof EmployeeStore && (hook.loggedUser() instanceof Admin || hook.loggedUser() instanceof InventoryOperator)) {
            ArrayList<Order> admin = ((EmployeeStore) hook).viewOrders();
            for (Order o : admin)
                ids.add("Order #" + o.getOrderID());
        } else if (hook.loggedUser() instanceof WarehouseShippingTeam) {
            for (Long l : ((EmployeeStore) hook).ordersToBeShipped())
                ids.add("Order #" + l);
        }
        ListView<String> orders = new ListView<>(ids);
        orders.setPrefSize(300, 500);
        orders.setEditable(false);
        grid.add(orders, 0, 1, 5, 1);

        //View selected order button
        Button seeOrder = new Button("View Order");
        seeOrder.setOnAction(actionEvent -> {
            if (orders.getSelectionModel().getSelectedItem() != null) {
                GridPane temp = createGrid();

                Text header = new Text(orders.getSelectionModel().getSelectedItem()); //NB: Number begins @ 7
                header.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 18));
                temp.add(header, 0, 0, 3, 1);

                long orderID = Long.parseLong(orders.getSelectionModel().getSelectedItem().substring(7));

                Text orderDate = new Text("Date: " + hook.fetchOrder(orderID).getOrderDate());
                temp.add(orderDate, 0, 1, 3, 1);

                String returned = "";
                if (hook.fetchOrder(orderID).getOrderState() instanceof Fulfilled)
                    returned = (((Fulfilled) hook.fetchOrder(orderID).getOrderState()).getReturned()) ? " [ORDER RETURNED]" : "";
                Text orderStatus = new Text("Status: " + hook.fetchOrder(orderID).getState() + returned);
                OrderState orderState = hook.fetchOrder(orderID).getOrderState();
                VideoCoSys system = new VideoCoSys();
                if ((orderState instanceof Fulfilled) && ((Fulfilled) orderState).getReturned()) {
                    orderStatus.setText("Status: Order Returned");
                } else if ((orderState instanceof Fulfilled) && system.daysSinceBorrowed(((Fulfilled) orderState).getDateArrived()) >= 14) {
                    Order focus = hook.fetchOrder(orderID);
                    User target = (hook instanceof EmployeeStore) ? ((EmployeeStore) hook).fetchUser(focus.getEmail()) : hook.loggedUser();

                    if (target instanceof Customer)
                        orderStatus.setText("Status: ORDER OVERDUE! Fee/Day - " + ((((Customer) target).getProvince().equals("ON")) ? "$1:00" : "$9.99"));
                }
                temp.add(orderStatus, 0, 2, 3, 1);

                Text custEmail = new Text("Customer E-Mail: " + hook.fetchOrder(orderID).getEmail());
                temp.add(custEmail, 0, 3, 3, 1);

                Separator s1 = new Separator();
                s1.setMaxWidth(450);
                s1.setMinWidth(450);
                temp.add(s1, 0, 4, 3, 1);

                Button cancelButton = new Button("Cancel Order");
                cancelButton.setDisable(hook.fetchOrder(orderID).getState().equals("Fulfilled") || hook.fetchOrder(orderID).getState().equals("Cancelled"));
                cancelButton.setOnAction(actionEvent1 -> {
                    cancelButton.setDisable(true);
                    orderStatus.setText("Status: Cancelled");
                    hook.cancelOrder(orderID);
                });
                temp.add(cancelButton, 0, 5, 1, 1);

                Button editButton = new Button("Advance Order");
                editButton.setDisable(orderState instanceof Fulfilled && ((Fulfilled) orderState).getReturned());
                editButton.setOnAction(actionEvent2 -> {
                    ((EmployeeStore) hook).progOrder(orderID);
                    orderStatus.setText("Status: " + hook.fetchOrder(orderID).getState());
                });
                if (hook instanceof EmployeeStore)
                    temp.add(editButton, 1, 5, 1, 1);

                /*DISPLAYING MOVIE INFO START*/
                ArrayList<Movie> associatedMovies = new ArrayList<>();
                for (int id : hook.fetchOrder(orderID).getMovies())
                    associatedMovies.add(MovieDB.getINSTANCE().getMovie(id));

                ArrayList<Text> movies = new ArrayList<>();
                for (Movie m : associatedMovies)
                    movies.add(new Text(m.toString()));

                for (int i = 0, offset = 7; i < movies.size(); i++, offset++)
                    temp.add(movies.get(i), 0, offset, 3, 1);
                /*DISPLAYING MOVIE INFO END*/
                orderDetails.setContent(temp);

                //Overdue notice
                Text overdue = new Text();
                overdue.setFill(Color.RED);

                //Only displaying notice if the order has been fulfilled, not returned and is overdue
                Order target = hook.fetchOrder(orderID);
                boolean orderFulfilled = target.getState().equals("Fulfilled"),
                        orderReturned = orderFulfilled && ((Fulfilled) target.getOrderState()).getReturned();
                if (orderReturned && system.daysSinceBorrowed(hook.fetchOrder(orderID).getOrderDate()) >= 14)
                    overdue.setText("This order is overdue.");
            }
        });
        grid.add(seeOrder,0, 2);

        //Search for
        TextField search = new TextField();
        Button searchButton = new Button("Search");
        searchButton.setOnAction(actionEvent -> {
            ArrayList<Order> temp = hook.fetchOrders(search.getText(), (hook instanceof CustomerStore) ? hook.loggedUser().getEmail() : null);
            ObservableList<String> temp2 = FXCollections.observableArrayList();
            for (Order o : temp)
                temp2.add("Order #" + o.getOrderID());
            orders.setItems(temp2);
        });
        grid.add(search, 0, 4);
        grid.add(searchButton, 1, 4);

        //back button
        Button viewMovies = new Button("Back");
        viewMovies.setOnAction(actionEvent -> focus.setScene(mainMenu(hook instanceof EmployeeStore)));
        grid.add(viewMovies, 0, 5);

        return new Scene(grid, 1280, 720);
    }

    /**
     * Scene for browsing movies
     *
     * @param employee whether the current user is an employee or not
     * @return Movie browsing scene
     */
    private Scene browseMovies(boolean employee) {
        GridPane grid = createGrid();
        AtomicReference<ArrayList<Movie>> results = new AtomicReference<>(hook.searchMovies("", 0));

        Text text = new Text("Browse Movies");
        text.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 20));
        grid.add(text, 0, 0, 3, 1);

        //Movies
        ScrollPane movieWindow = new ScrollPane();
        movieWindow.setPrefSize(960, 480);
        movieWindow.setContent(createMovieView(results.get(), false));
        grid.add(movieWindow, 0, 2, 8, 1);

        //back button
        Button viewMovies = new Button("Back");
        viewMovies.setOnAction(actionEvent -> focus.setScene(mainMenu(employee)));
        grid.add(viewMovies, 0, 4, 1, 1);

        //Add button
        Button addMovie = new Button("Add Movie");
        addMovie.setOnAction(actionEvent -> focus.setScene(movieMod(null)));
        if (hook.loggedUser() instanceof Admin)
            grid.add(addMovie, 2, 3, 3, 1);

        //view cart button
        Button cart = new Button("View Cart");
        cart.setOnAction(actionEvent -> focus.setScene(viewCart()));
        cart.setDisable(!(hook.loggedUser() instanceof Admin) && !(hook.loggedUser() instanceof InventoryOperator) && !(hook.loggedUser() instanceof Customer) && !(hook.loggedUser() instanceof Cashier));
        grid.add(cart, 0, 3, 1, 1);

        //Search bar
        TextField searchBar = new TextField();
        grid.add(searchBar, 0, 1, 3, 1);

        //Search option dropdown
        Map<String, Integer> optionToFlag = new LinkedHashMap<>();
        optionToFlag.put("Title", 0);
        optionToFlag.put("Release Date", 1);
        optionToFlag.put("Genre", 2);
        optionToFlag.put("Description", 3);
        optionToFlag.put("Actors", 4);
        optionToFlag.put("Directors", 5);
        optionToFlag.put("Categories", 6);
        ObservableList<String> options = FXCollections.observableArrayList(optionToFlag.keySet());
        ComboBox optionMenu = new ComboBox(options);
        optionMenu.getSelectionModel().selectFirst();
        grid.add(optionMenu, 3, 1, 3, 1);

        //Search button
        Button searchButton = new Button("Search");
        searchButton.setOnAction(actionEvent -> {
            results.set(hook.searchMovies(searchBar.getText(), optionToFlag.get(optionMenu.getValue().toString())));
            movieWindow.setContent(createMovieView(results.get(), false));
        });
        grid.add(searchButton, 6, 1, 3 ,1);

        return new Scene(grid, 1280, 720);
    }

    /**
     * @return a scene for viewing the cart contents
     */
    private Scene viewCart() {
        GridPane grid = createGrid();

        Text text = new Text("Cart and Payment");
        text.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 20));
        grid.add(text, 0, 0, 3, 1);

        //Window for viewing cart contents
        ScrollPane movieWindow = new ScrollPane();
        movieWindow.setPrefSize(600, 480);
        movieWindow.setContent(createMovieView(hook.getCart(), true));
        grid.add(movieWindow, 0, 1, 5, 1);

        //scroll pane for payment window
        ScrollPane paymentWindow = new ScrollPane();
        paymentWindow.setPrefSize(500, 480);
        paymentWindow.setContent(paymentWindow());
        grid.add(paymentWindow, 5, 1, 5, 1);

        //back button
        Button viewMovies = new Button("Back");
        viewMovies.setOnAction(actionEvent -> focus.setScene(browseMovies(hook instanceof EmployeeStore)));
        grid.add(viewMovies, 0, 2);

        return new Scene(grid, 1280, 720);
    }

    /**
     * @return a shared scene between movie addition and modification
     * @param movie if editing a movie, this will be the movie that will be edited.  If parameter is null; a new movie is added instead
     */
    private Scene movieMod(Movie movie) {
        GridPane grid = createGrid();

        Text text = new Text("Add/Edit Movie");
        text.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 20));
        grid.add(text, 0, 0, 3, 1);

        Text id = new Text("Movie ID:");
        TextField idField = new TextField();
        grid.add(id, 0, 1);
        grid.add(idField, 1, 1);

        Text stock = new Text("Movie Stock:");
        TextField stockField = new TextField();
        grid.add(stock, 0, 2);
        grid.add(stockField, 1, 2);

        Text price = new Text("Price ($CAD):");
        TextField priceField = new TextField();
        grid.add(price, 0, 3);
        grid.add(priceField, 1, 3);

        Text title = new Text("Title:");
        TextField movieTitle = new TextField();
        grid.add(title, 0, 4);
        grid.add(movieTitle, 1, 4);

        Text releaseDate = new Text("Release Date:");
        TextField rdField = new TextField();
        grid.add(releaseDate, 0, 5);
        grid.add(rdField, 1, 5);

        Text genre = new Text("Genre:");
        TextField genreField = new TextField();
        grid.add(genre, 0, 6);
        grid.add(genreField, 1, 6);

        Text desc = new Text("Description:");
        TextField descField = new TextField();
        grid.add(desc, 0, 7);
        grid.add(descField, 1, 7);

        Text actors = new Text("Actors:");
        ObservableList<String> actorList = FXCollections.observableArrayList();
        actorList.add("New Entry...");
        ComboBox actorBox = new ComboBox(actorList);
        actorBox.getSelectionModel().selectFirst();
        actorBox.setMaxWidth(150);
        actorBox.setMinWidth(150);
        TextField actorField = new TextField();
        actorField.setOnAction(actionEvent -> {
            int focusIndex = actorBox.getSelectionModel().getSelectedIndex();
            String focusText = actorList.get(focusIndex),
                   fieldText = actorField.getText();

            if (!focusText.equals(fieldText)) {
                if (focusText.equals("New Entry...") && !fieldText.equals("")) {
                    actorList.set(focusIndex, fieldText);
                    actorList.add("New Entry...");
                    actorBox.getSelectionModel().select(focusIndex);
                } else if (!focusText.equals("New Entry...") && fieldText.equals("")) {
                    actorBox.getSelectionModel().selectLast();
                    actorList.remove(focusIndex);
                } else if (!focusText.equals("New Entry...")){
                    actorList.set(focusIndex, fieldText);
                }
            }
        });
        actorBox.setOnAction(actionEvent -> actorField.setText(actorBox.getSelectionModel().getSelectedItem().toString()));
        grid.add(actors, 0, 8);
        grid.add(actorBox, 1, 8);
        grid.add(actorField, 2, 8);

        Text directors = new Text("Directors:");
        ObservableList<String> directorList = FXCollections.observableArrayList();
        directorList.add("New Entry...");
        ComboBox directorBox = new ComboBox(directorList);
        directorBox.getSelectionModel().selectFirst();
        directorBox.setMaxWidth(150);
        directorBox.setMinWidth(150);
        TextField directorField = new TextField();
        directorField.setOnAction(actionEvent -> {
            int focusIndex = directorBox.getSelectionModel().getSelectedIndex();
            String focusText = directorList.get(focusIndex),
                    fieldText = directorField.getText();

            if (!focusText.equals(fieldText)) {
                if (focusText.equals("New Entry...") && !fieldText.equals("")) {
                    directorList.set(focusIndex, fieldText);
                    directorList.add("New Entry...");
                    directorBox.getSelectionModel().select(focusIndex);
                } else if (!focusText.equals("New Entry...") && fieldText.equals("")) {
                    directorBox.getSelectionModel().selectLast();
                    directorList.remove(focusIndex);
                } else if (!focusText.equals("New Entry...")){
                    directorList.set(focusIndex, fieldText);
                }
            }
        });
        directorBox.setOnAction(actionEvent -> directorField.setText(directorBox.getSelectionModel().getSelectedItem().toString()));
        grid.add(directors, 0, 9);
        grid.add(directorBox, 1, 9);
        grid.add(directorField, 2, 9);

        Text categories = new Text("Categories:");
        ObservableList<String> catList = FXCollections.observableArrayList();
        catList.add("New Entry...");
        ComboBox catBox = new ComboBox(catList);
        catBox.getSelectionModel().selectFirst();
        catBox.setMaxWidth(150);
        catBox.setMinWidth(150);
        TextField catField = new TextField();
        catField.setOnAction(actionEvent -> {
            int focusIndex = catBox.getSelectionModel().getSelectedIndex();
            String focusText = catList.get(focusIndex),
                    fieldText = catField.getText();

            if (!focusText.equals(fieldText)) {
                if (focusText.equals("New Entry...") && !fieldText.equals("")) {
                    catList.set(focusIndex, fieldText);
                    catList.add("New Entry...");
                    catBox.getSelectionModel().select(focusIndex);
                } else if (!focusText.equals("New Entry...") && fieldText.equals("")) {
                    catBox.getSelectionModel().selectLast();
                    catList.remove(focusIndex);
                } else if (!focusText.equals("New Entry...")){
                    catList.set(focusIndex, fieldText);
                }
            }
        });
        catBox.setOnAction(actionEvent -> catField.setText(catBox.getSelectionModel().getSelectedItem().toString()));
        grid.add(categories, 0, 10);
        grid.add(catBox, 1, 10);
        grid.add(catField, 2, 10);

        if (movie != null) {
            idField.setDisable(true);

            idField.setText(Integer.toString(movie.getId()));
            stockField.setText(Integer.toString(movie.getStock()));
            priceField.setText(Double.toString(movie.getPrice()));
            movieTitle.setText(movie.getTitle());
            rdField.setText(movie.getReleaseDate());
            genreField.setText(movie.getGenre());
            descField.setText(movie.getDescription());

            actorList.clear();
            actorList.addAll(movie.getActors());
            actorList.add("New Entry...");
            actorBox.getSelectionModel().selectFirst();
            actorField.setText(actorBox.getValue().toString());

            directorList.clear();
            directorList.addAll(movie.getDirectors());
            directorList.add("New Entry...");
            directorBox.getSelectionModel().selectFirst();
            directorField.setText(directorBox.getValue().toString());

            catList.clear();
            catList.addAll(movie.getCategories());
            catList.add("New Entry...");
            catBox.getSelectionModel().selectFirst();
            catField.setText(catBox.getValue().toString());
        }

        //back button
        Button viewMovies = new Button("Back");
        viewMovies.setOnAction(actionEvent -> focus.setScene(browseMovies(true)));
        grid.add(viewMovies, 0, 11);

        //Edit/Modify movie
        Button editModify = new Button((movie == null) ? "Add Movie" : "Save Edits");
        editModify.setOnAction(actionEvent -> {
            //parse textfields into useable input
            int[] is;
            double parsedPrice;
            try {
                is = new int[]{Integer.parseInt(idField.getText()), Integer.parseInt(stockField.getText())};
                parsedPrice = Double.parseDouble(priceField.getText());
            } catch (NumberFormatException e) {
                is = new int[]{-1, -1};
                parsedPrice = -1.0;
            }

            String[] trgd = {
                    movieTitle.getText(),
                    rdField.getText(),
                    genreField.getText(),
                    descField.getText()
            };

            String[][] adc = new String[3][];
            actorList.remove(actorList.size() - 1);
            adc[0] = actorList.toArray(new String[actorList.size()]);
            directorList.remove(directorList.size() - 1);
            adc[1] = directorList.toArray(new String[directorList.size()]);
            catList.remove(catList.size() - 1);
            adc[2] = catList.toArray(new String[catList.size()]);

            actorList.add("New Entry...");
            directorList.add("New Entry...");
            catList.add("New Entry...");

            if (movie != null) {
                if (((EmployeeStore) hook).modifyMovie(is, parsedPrice, trgd, adc)) {
                    editModify.setTextFill(Color.BLUE);
                    editModify.setText("Changes Saved!");
                } else {
                    editModify.setTextFill(Color.RED);
                    editModify.setText("Error with changes");
                }
            } else {
                if (((EmployeeStore) hook).addMovie(is, parsedPrice, trgd, adc)) {
                    editModify.setTextFill(Color.BLUE);
                    editModify.setText("Movie Saved!");
                    editModify.setDisable(true);
                } else {
                    editModify.setTextFill(Color.RED);
                    editModify.setText("Error. Movie not added.");
                }
            }
        });
        grid.add(editModify, 1, 11);

        return new Scene(grid, 1280, 720);
    }

    /**
     * Creates a GridPane containing text results and action buttons for movie results
     *
     * @param movies ArrayList of movies to parse
     * @param removeFlag whether the buttons will be for adding or removing a movie
     * @return a GridPane :)
     */
    private GridPane createMovieView(ArrayList<Movie> movies, boolean removeFlag) {
        GridPane textRes = createGrid();
        textRes.setVgap(10);

        Text header = new Text();
        header.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 20));
        if (removeFlag)
            textRes.add(header, 0, 0, 3, 1);

        if (!removeFlag && movies.size() <= 0) {
            Text msg = new Text("No movies found.");
            msg.setFill(Color.RED);
            textRes.add(msg, 1, 0);
        }

        double total = 0.0;
        for (int i = 0, pos = 1; i < movies.size(); i++, pos += 12) {
            Text movie = new Text(movies.get(i).toString());
            total += movies.get(i).getPrice();

            Button addToCart = new Button((removeFlag) ? "Remove" : "Add to Cart");
            addToCart.setDisable(movies.get(i).getStock() == 0 || hook.getCart().contains(movies.get(i)) && !removeFlag);
            int finalI = i;
            addToCart.setOnAction(actionEvent -> {
                if (removeFlag) {
                    hook.removeMovieFromCart(movies.get(finalI).getId());
                    focus.setScene(viewCart());
                } else if (hook.loggedUser() instanceof Cashier && !((EmployeeStore) hook).movieAtLocation(movies.get(finalI), (Cashier) hook.loggedUser())) { //Cashiers can only get items located in their store
                    addToCart.setTextFill(Color.RED);
                    addToCart.setText("This movie is not at this location!");
                    addToCart.setDisable(true);
                } else if (!hook.getCart().contains(movies.get(finalI)) && hook.addMovieToCart(movies.get(finalI).getId())) {
                    addToCart.setTextFill(Color.RED);
                    addToCart.setText("Movie Added");
                    addToCart.setDisable(true);
                } else {
                    addToCart.setTextFill(Color.RED);
                    addToCart.setText("You don't have a cart!");
                    addToCart.setDisable(true);
                }
            });

            Button editMovie = new Button("Edit Movie");
            editMovie.setOnAction(actionEvent -> focus.setScene(movieMod(movies.get(finalI))));

            Button delete = new Button("Delete");
            delete.setOnAction(actionEvent -> {
                ((EmployeeStore) hook).removeMovie(movies.get(finalI).getId());
                focus.setScene(browseMovies(hook instanceof EmployeeStore));
            });

            Separator separator = new Separator();
            separator.setMaxWidth(360);
            separator.setMinWidth(360);
            separator.setValignment(VPos.TOP);

            int offset = (hook.loggedUser() instanceof Admin && !removeFlag) ? 5 : 1;
            textRes.add(movie, 0, pos, offset, 10);
            textRes.add(addToCart, 0, pos + 10, 1, 1);
            if (hook.loggedUser() instanceof Admin && !removeFlag) {
                textRes.add(editMovie, 1, pos + 10, 1, 1);
                textRes.add(delete, 2, pos + 10, 1, 1);
            }
            textRes.add(separator, 0, pos + 11, offset, 1);
        }

        header.setText("Grand Total (Inc. Taxes): $" + String.format("%,.2f", (total * 1.13)));

        return textRes;
    }

    /**
     * @return GridPane for a payment window
     */
    private GridPane paymentWindow() {
        GridPane payment = createGrid();
        payment.setAlignment(Pos.TOP_LEFT);

        //Header
        Text text = new Text("Payment Information");
        text.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 20));
        payment.add(text, 0, 0, 3, 1);

        //seperator
        Separator separator1 = new Separator();
        separator1.setMaxWidth(500);
        separator1.setValignment(VPos.TOP);
        payment.add(separator1, 0, 1, 5, 1);

        //CC field
        Text card = new Text("Card Number");
        payment.add(card, 0, 2, 3, 1);
        TextField cardField = new TextField();
        payment.add(cardField, 0, 3, 3, 1);

        //CCV
        Text ccv = new Text("CCV #");
        payment.add(ccv, 3, 2, 1, 1);
        TextField ccvField = new TextField();
        ccvField.setMaxWidth(50);
        payment.add(ccvField, 3, 3, 1, 1);

        //Expiration
        Text expMonth = new Text("Exp. Month (MM)");
        Text expYear = new Text("Exp. Year (YY)");
        payment.add(expMonth, 0, 4);
        payment.add(expYear, 1, 4);
        TextField month = new TextField();
        month.setMaxWidth(150);
        TextField year = new TextField();
        year.setMaxWidth(150);
        payment.add(month, 0, 5, 1, 1);
        payment.add(year, 1, 5, 1, 1);

        //Use Loyalty Points
        int targetLPAmt = (hook.loggedUser() instanceof Customer) ? ((Customer) hook.loggedUser()).getLoyaltyPoints() : 0;
        RadioButton lpToggle = new RadioButton("Pay with 10 loyalty points instead. [Total: " + targetLPAmt + "]");
        lpToggle.setOnAction(actionEvent -> {
            cardField.setDisable(!cardField.isDisable());
            month.setDisable(!month.isDisable());
            year.setDisable(!year.isDisable());
            ccvField.setDisable(!ccvField.isDisable());
        });
        lpToggle.setDisable(targetLPAmt < 10);
        payment.add(lpToggle, 0, 6, 5, 1);

        //seperator between CC info and billing
        Separator separator2 = new Separator();
        separator2.setMaxWidth(500);
        separator2.setValignment(VPos.TOP);
        payment.add(separator2, 0, 7, 5, 1);

        //Address
        Text address = new Text("Address");
        payment.add(address, 0, 10, 1, 1);
        TextField addressField = new TextField();
        addressField.setMinWidth(300);
        payment.add(addressField, 0, 11, 3, 1);

        //Postal code
        Text postal = new Text("Postal Code");
        payment.add(postal, 0, 12, 1, 1);
        TextField postalField = new TextField();
        postalField.setMaxWidth(150);
        payment.add(postalField, 0, 13, 1, 1);

        //province code
        Text province = new Text("Province");
        payment.add(province, 1, 12, 1, 1);
        Map<String, String> provToCode = new LinkedHashMap<>();
        provToCode.put("Newfoundland & Labrador", "NL");
        provToCode.put("Prince Edward Island", "PE");
        provToCode.put("Nova Scotia", "NS");
        provToCode.put("New Brunswick", "NB");
        provToCode.put("Quebec", "QC");
        provToCode.put("Ontario", "ON");
        provToCode.put("Manitoba", "MB");
        provToCode.put("Saskatchewan", "SK");
        provToCode.put("Alberta", "AB");
        provToCode.put("British Columbia", "BC");
        provToCode.put("Yukon", "YT");
        provToCode.put("Northwest Territories", "NT");
        provToCode.put("Nunavut", "NU");
        ObservableList<String> provinces = FXCollections.observableArrayList(provToCode.keySet());
        ComboBox provinceBox = new ComboBox(provinces);
        provinceBox.getSelectionModel().selectFirst();
        payment.add(provinceBox, 1, 13, 1, 1);

        //town/city
        Text cityTown = new Text("City/Town");
        payment.add(cityTown,0, 14, 1, 1);
        TextField cityTownField = new TextField();
        payment.add(cityTownField, 0, 15, 1, 1);

        //for InventoryOperator/Admin use (placing orders over the phone)
        Text custEmail = new Text("Customer E-mail");
        TextField custEmailField = new TextField();
        custEmailField.setOnAction(actionEvent -> {
            if (((EmployeeStore) hook).probeCustomer(custEmailField.getText())) {
                Customer target = (Customer) ((EmployeeStore) hook).fetchCustomer(custEmailField.getText());

                lpToggle.setText("Pay with 10 loyalty points instead. [Total: " + target.getLoyaltyPoints() + "]");
                lpToggle.setDisable(target.getLoyaltyPoints() < 10);

                addressField.setText(target.getStreet());
                postalField.setText(target.getPostalCode());

                //Province code to full string
                String provName = "";
                for (Map.Entry<String, String> e : provToCode.entrySet())
                    provName = (e.getValue().equals(target.getProvince())) ? e.getKey() : provName;
                provinceBox.setValue(provName);

                cityTownField.setText(target.getCityTown());
            } else {
                addressField.setText("");
                postalField.setText("");
                provinceBox.getSelectionModel().selectFirst();
                cityTownField.setText("");
            }
        });
        if (hook.loggedUser() instanceof InventoryOperator || hook.loggedUser() instanceof Admin) {
            addressField.setDisable(true);
            postalField.setDisable(true);
            provinceBox.setDisable(true);
            cityTownField.setDisable(true);

            payment.add(custEmail, 1, 14);
            payment.add(custEmailField, 1, 15);
        }

        //Toggle for shipping = billing
        RadioButton shippingBilling = new RadioButton("Billing Address the same as Shipping Address");
        shippingBilling.setDisable(!(hook.loggedUser() instanceof Customer));
        shippingBilling.setOnAction(actionEvent -> {
            Customer current = (Customer) hook.loggedUser();

            if (addressField.isDisable()) {
                addressField.setDisable(false);
                postalField.setDisable(false);
                provinceBox.setDisable(false);
                cityTownField.setDisable(false);

                addressField.setText("");
                postalField.setText("");
                provinceBox.getSelectionModel().selectFirst();
                cityTownField.setText("");
            } else {
                addressField.setDisable(true);
                postalField.setDisable(true);
                provinceBox.setDisable(true);
                cityTownField.setDisable(true);

                addressField.setText(current.getStreet());
                postalField.setText(current.getPostalCode());

                //Province code to full string
                String provName = "";
                for (Map.Entry<String, String> e : provToCode.entrySet())
                    provName = (e.getValue().equals(current.getProvince())) ? e.getKey() : provName;
                provinceBox.setValue(provName);

                cityTownField.setText(current.getCityTown());
            }
        });
        payment.add(shippingBilling, 0, 8, 3, 1);

        //result text
        Text result = new Text();
        payment.add(result, 0, 18, 5, 1);

        //Make order
        Button placeOrder = new Button("Place Order");
        placeOrder.setDisable(hook.getCart().size() == 0);
        placeOrder.setOnAction(actionEvent -> {
            String[] billingInfo = {
                    cardField.getText(),
                    month.getText() + "/" + year.getText(),
                    ccvField.getText(),
                    addressField.getText(),
                    postalField.getText(),
                    cityTownField.getText(),
                    provToCode.get(provinceBox.getValue().toString())
            };

            User targetCust;
            if (hook instanceof EmployeeStore && ((EmployeeStore) hook).probeCustomer(custEmailField.getText())) {
                targetCust = ((EmployeeStore) hook).fetchCustomer(custEmailField.getText());
            } else {
                targetCust = hook.loggedUser();
            }

            boolean paymentSucc = (lpToggle.isSelected()) ? hook.pointPayment(targetCust) : hook.makePayment(targetCust, Double.MAX_VALUE, billingInfo);

            if (paymentSucc && hook.getCart().size() != 0) {
                try {
                    long orderID = hook.makeOrder(targetCust, hook.getCart());
                    result.setFill(Color.BLUE);
                    result.setText("Payment Successful; Order has been placed\nYour order ID is: #" + orderID);
                    placeOrder.setDisable(true);

                    if (hook.loggedUser() instanceof Cashier)
                        hook.fetchOrder(orderID).setState("Fulfilled");
                } catch (IllegalArgumentException e) { //TODO: WHEN TESTING -- CHECK IF EXCEPTION IS THROWN
                    result.setFill(Color.RED);
                    result.setText("One or more of your items are now out of stock.");
                    placeOrder.setDisable(true);
                }
            } else if (paymentSucc && hook.getCart().size() == 0) {
                result.setFill(Color.BLUE);
                result.setText("Your overdue fees have been paid off.");
            } else {
                result.setFill(Color.RED);
                result.setText((lpToggle.isSelected()) ? "You don't have enough loyalty points!" : "Error with payment.\nPlease check that the provided information is correct.\nEnsure that all information is formatted correctly.\nEg. Postal Codes are of the form \"A1A 1A1\".");
            }
        });
        payment.add(placeOrder, 0, 17);

        return payment;
    }

    /**
     * Creating the GridPane object to use on scene creation
     * @return base GridPane object
     */
    private GridPane createGrid() {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(15, 15, 15, 15));

        return grid;
    }
}
