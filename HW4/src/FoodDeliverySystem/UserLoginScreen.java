package FoodDeliverySystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * User login (Assignment 4, Part ג / Part טו entry).
 *
 * The three user types identify themselves and are routed to their personal
 * screen: a customer by code, a rider by id, and a restaurant manager by
 * username + password (validated with {@link RestAdmin#login}).
 */
public final class UserLoginScreen {

    private UserLoginScreen() {
    }

    public static Parent build(FoodDeliveryApp app) {
        Label title = new Label("User Login");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        Button customer = big("Customer");
        Button rider    = big("Rider");
        Button manager  = big("Restaurant Manager");
        Button back     = big("\u2190 Back");

        customer.setOnAction(e -> loginCustomer(app));
        rider.setOnAction(e -> loginRider(app));
        manager.setOnAction(e -> loginManager(app));
        back.setOnAction(e -> app.showOpeningScreen());

        VBox box = new VBox(16, title, customer, rider, manager, back);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        return box;
    }

    private static void loginCustomer(FoodDeliveryApp app) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Customer login");
        dialog.setContentText("Customer code:");
        dialog.showAndWait().ifPresent(input -> {
            int code;
            try {
                code = Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid code", "Please enter a whole number.");
                return;
            }
            Customer c = app.getSystem().findCustomerByCode(code);
            if (c == null) {
                UiHelper.error("Not found", "No customer with code " + code + ".");
            } else {
                app.showCustomerScreen(c);
            }
        });
    }

    private static void loginRider(FoodDeliveryApp app) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Rider login");
        dialog.setContentText("Rider ID:");
        dialog.showAndWait().ifPresent(input -> {
            Rider r = app.getSystem().findRiderById(input.trim());
            if (r == null) {
                UiHelper.error("Not found", "No rider with ID " + input.trim() + ".");
            } else {
                app.showRiderScreen(r);
            }
        });
    }

    private static void loginManager(FoodDeliveryApp app) {
        TextField user = new TextField();
        user.setPromptText("username");
        PasswordField pass = new PasswordField();
        pass.setPromptText("password");

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(12);
        g.setAlignment(Pos.CENTER);
        g.add(new Label("Username:"), 0, 0);
        g.add(user, 1, 0);
        g.add(new Label("Password:"), 0, 1);
        g.add(pass, 1, 1);

        Button login = new Button("Login");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox(10, login, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(16, new Label("Restaurant manager login"), g, buttons);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Manager login");
        stage.setScene(new Scene(box));
        cancel.setOnAction(e -> stage.close());

        login.setOnAction(e -> {
            RestAdmin admin = app.getSystem().findRestAdminByUsername(user.getText().trim());
            if (admin != null && admin.login(user.getText().trim(), pass.getText())) {
                stage.close();
                app.showRestAdminScreen(admin);
            } else {
                UiHelper.error("Login failed", "Incorrect username or password.");
                pass.clear();
            }
        });

        stage.showAndWait();
    }

    private static Button big(String text) {
        Button b = new Button(text);
        b.setPrefWidth(260);
        b.setPrefHeight(44);
        b.setStyle("-fx-font-size: 14px;");
        return b;
    }
}