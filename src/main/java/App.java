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
import storehook.CustomerStore;
import storehook.EmployeeStore;
import storehook.StoreHook;
import user.data.Customer;

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
        register.setOnAction(actionEvent -> focus.setScene(register(0)));
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
     * @return the register screen
     */
    private Scene register(int type) {
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

            case 4:
                grid.add(warehouseLocation, 0, ++vertOffset);
                grid.add(provinceMenu, 1, vertOffset);

                grid.add(isShipping, 1, ++vertOffset);
                break;
        }
        /*ADDITIONAL INFORMATION END*/

        Button back = new Button("Back");
        back.setOnAction(actionEvent -> focus.setScene(openScreen()));
        grid.add(back, 0, ++vertOffset);

        Text regResult = new Text();
        grid.add(regResult, 0, vertOffset + 1, 2, 1);

        Button register = new Button("Register");
        register.setOnAction(actionEvent -> {
            regResult.setFill(Color.RED);
            if (passField.getText().equals(passConfField.getText())) {
                hook = (type == 0) ? new CustomerStore() : new EmployeeStore();

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

                int res = hook.addUser(baseInfo, additionalInfo, intToString.get(type));
                regResult.setText(
                        (res == 0) ? "Registration successful" :
                        (1 <= res && res <= 5) ? "Password must:\n- Be 8 characters long\n- Contain at least one lowercase & uppercase letter\n- One symbol\n- One number\n- No spaces " :
                        (res == 7) ? "Postal code must be of the form \"[CAPITAL][number][CAPITAL] [number][CAPITAL][number]\"" :
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
        grid.add(editAccount, 0, 2);

        Button viewMovies = new Button("Browse Movies");
        viewMovies.setOnAction(actionEvent -> focus.setScene(browseMovies(employee)));
        grid.add(viewMovies, 0, 3);

        return new Scene(grid, 400, 720);
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
        grid.add(viewMovies, 0, 4);

        //view cart button
        Button cart = new Button("View Cart");
        cart.setOnAction(actionEvent -> focus.setScene(viewCart()));
        grid.add(cart, 0, 3);

        //Search bar
        TextField searchBar = new TextField();
        grid.add(searchBar, 0, 1);

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
        grid.add(optionMenu, 1, 1);

        //Search button
        Button searchButton = new Button("Search");
        searchButton.setOnAction(actionEvent -> {
            results.set(hook.searchMovies(searchBar.getText(), optionToFlag.get(optionMenu.getValue().toString())));
            movieWindow.setContent(createMovieView(results.get(), false));
        });
        grid.add(searchButton, 2, 1);

        return new Scene(grid, 1280, 720);
    }

    private Scene viewCart() { //TODO: Allow for moving back to wherever we access our cart from
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
     * Creates a GridPane containing text results and action buttons for movie results
     *
     * @param movies ArrayList of movies to parse
     * @param removeFlag whether the buttons will be for adding or removing a movie
     * @return a GridPane :)
     */
    private GridPane createMovieView(ArrayList<Movie> movies, boolean removeFlag) {
        GridPane textRes = createGrid();
        textRes.setVgap(10);
        for (int i = 0, pos = 0; i < movies.size(); i++, pos += 12) {
            Text movie = new Text(movies.get(i).toString());

            Button addToCart = new Button((removeFlag) ? "Remove" : "Add to Cart");
            addToCart.setDisable(movies.get(i).getStock() == 0 || hook.getCart().contains(movies.get(i)) && !removeFlag);
            int finalI = i;
            addToCart.setOnAction(actionEvent -> {
                if (removeFlag) {
                    hook.removeMovieFromCart(movies.get(finalI).getId());
                    focus.setScene(viewCart());
                } else if (!hook.getCart().contains(movies.get(finalI))) {
                    hook.addMovieToCart(movies.get(finalI).getId());
                    addToCart.setTextFill(Color.RED);
                    addToCart.setText("Movie Added.");
                    addToCart.setDisable(true);
                }
            });

            Separator separator = new Separator();
            separator.setMaxWidth(360);
            separator.setValignment(VPos.TOP);

            textRes.add(movie, 0, pos, 1, 10);
            textRes.add(addToCart, 0, pos + 10);
            textRes.add(separator, 0, pos + 11);
        }

        return textRes;
    }

    /**
     * gridpane view for reviewing orders
     *
     * @param movies ArrayList of movies to display
     * @return GridPane view for reviewing orders
     */
    private GridPane createMovieView(ArrayList<Movie> movies) {
        GridPane textRes = createGrid();
        textRes.setVgap(10);
        for (int i = 0, pos = 0; i < movies.size(); i++, pos += 12) {
            Text movie = new Text(movies.get(i).toString());

            /*Button addToCart = new Button((removeFlag) ? "Remove" : "Add to Cart");
            addToCart.setDisable(movies.get(i).getStock() == 0 || hook.getCart().contains(movies.get(i)) && !removeFlag);
            int finalI = i;
            addToCart.setOnAction(actionEvent -> {
                if (removeFlag) {
                    hook.removeMovieFromCart(movies.get(finalI).getId());
                    focus.setScene(viewCart());
                } else if (!hook.getCart().contains(movies.get(finalI))) {
                    hook.addMovieToCart(movies.get(finalI).getId());
                    addToCart.setTextFill(Color.RED);
                    addToCart.setText("Movie Added.");
                    addToCart.setDisable(true);
                }
            });*/

            Separator separator = new Separator();
            separator.setMaxWidth(360);
            separator.setValignment(VPos.TOP);

            textRes.add(movie, 0, pos, 1, 10);
            //textRes.add(addToCart, 0, pos + 10);
            textRes.add(separator, 0, pos + 11);
        }

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
        Text expiration = new Text("Expiration (MM/YYYY)");
        payment.add(expiration, 0, 4);
        TextField month = new TextField();
        month.setMaxWidth(150);
        TextField year = new TextField();
        year.setMaxWidth(150);
        payment.add(month, 0, 5, 1, 1);
        payment.add(year, 1, 5, 1, 1);

        //seperator between CC info and billing
        Separator separator2 = new Separator();
        separator2.setMaxWidth(500);
        separator2.setValignment(VPos.TOP);
        payment.add(separator2, 0, 6, 5, 1);

        //Address
        Text address = new Text("Address");
        payment.add(address, 0, 9, 1, 1);
        TextField addressField = new TextField();
        addressField.setMinWidth(300);
        payment.add(addressField, 0, 10, 3, 1);

        //Postal code
        Text postal = new Text("Postal Code");
        payment.add(postal, 0, 11, 1, 1);
        TextField postalField = new TextField();
        postalField.setMaxWidth(150);
        payment.add(postalField, 0, 12, 1, 1);

        //province code
        Text province = new Text("Province");
        payment.add(province, 1, 11, 1, 1);
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
        payment.add(provinceBox, 1, 12, 1, 1);

        //town/city
        Text cityTown = new Text("City/Town");
        payment.add(cityTown,0, 13, 1, 1);
        TextField cityTownField = new TextField();
        payment.add(cityTownField, 0, 14, 1, 1);

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
                provinceBox.setValue("");
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
        payment.add(shippingBilling, 0, 7, 3, 1);

        //Make order //todo: move into window
        Button placeOrder = new Button("Place Order");
        placeOrder.setDisable(hook.getCart().size() == 0);
        placeOrder.setOnAction(actionEvent -> {
            hook.makeOrder(hook.getCart());
        });
        payment.add(placeOrder, 0, 10);

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
