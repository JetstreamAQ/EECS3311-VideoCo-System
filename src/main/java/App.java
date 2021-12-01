import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import storehook.CustomerStore;
import storehook.EmployeeStore;
import storehook.StoreHook;

public class App extends Application {
    private Stage focus;
    private StoreHook hook;

    public void launch() {
        Application.launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
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

        Button employeeLogin = new Button();
        employeeLogin.setText("Employee Login");
        employeeLogin.setOnAction(actionEvent -> focus.setScene(loginScreen(true)));
        grid.add(employeeLogin, 0, 4);

        Button customerLogin = new Button();
        customerLogin.setText("Customer Login");
        customerLogin.setOnAction(actionEvent -> focus.setScene(loginScreen(false)));
        grid.add(customerLogin, 1, 4);

        return new Scene(grid, 480, 240);
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
