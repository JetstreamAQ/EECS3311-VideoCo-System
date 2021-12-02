import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import storehook.CustomerStore;
import storehook.EmployeeStore;
import storehook.StoreHook;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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
            loginResult.setText((!result) ? "Invalid credentials.  Try again." : "Login Successful!");
        });
        grid.add(login, 1, 3);

        return new Scene(grid, 360, 360);
    }

    private Scene mainMenu(boolean employee) {
        GridPane grid = createGrid();

        Text text = new Text("Welcome," + hook.loggedUser().getFName() + " " + hook.loggedUser().getLName());
        text.setFont(Font.font("SansSerif", FontWeight.MEDIUM, 24));
        grid.add(text, 0, 0, 3, 1);

        return new Scene(grid, 360, 720);
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
