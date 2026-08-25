package FoodDeliverySystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * System-administrator login (Assignment 4, Part ב).
 *
 * Validates the typed credentials against the single {@link Admin} held by
 * {@link DeliveryDataBase}. On success it navigates to the admin control center;
 * on failure it shows an error alert and lets the user try again - the program
 * never crashes (Part יב).
 */
public final class AdminLoginScreen {

    private AdminLoginScreen() {
    }

    public static Parent build(FoodDeliveryApp app) {
        Label title = new Label("Administrator Login");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        TextField userField = new TextField();
        userField.setPromptText("username");
        PasswordField passField = new PasswordField();
        passField.setPromptText("password");

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(12);
        form.setAlignment(Pos.CENTER);
        form.add(new Label("Username:"), 0, 0);
        form.add(userField, 1, 0);
        form.add(new Label("Password:"), 0, 1);
        form.add(passField, 1, 1);

        Button loginBtn = new Button("Login");
        loginBtn.setPrefWidth(120);
        Button backBtn = new Button("Back");
        backBtn.setPrefWidth(120);

        loginBtn.setOnAction(e -> {
            Admin admin = app.getSystem().getSystemAdministrator();
            String user = userField.getText().trim();
            String pass = passField.getText();
            if (admin != null
                    && admin.getUsername().equals(user)
                    && admin.getPassword().equals(pass)) {
                app.showAdminHub();
            } else {
                UiHelper.error("Login failed", "Incorrect username or password.");
                passField.clear();
            }
        });
        backBtn.setOnAction(e -> app.showOpeningScreen());

        HBox buttons = new HBox(12, loginBtn, backBtn);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(20, title, form, buttons);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        return box;
    }
}
