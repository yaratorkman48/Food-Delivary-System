package FoodDeliverySystem;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * The opening screen (Assignment 4, Part ב): the three top-level choices -
 * system-administrator login, user login, and exit.
 *
 * Built as a static factory that returns a JavaFX node, so the application can
 * drop it into the shared scene whenever the user comes "home".
 */
public final class OpeningScreen {

    private OpeningScreen() {
    }

    public static Parent build(FoodDeliveryApp app) {
        Label title = new Label("Food Delivery System");
        title.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        Label subtitle = new Label("Choose how you would like to sign in");
        subtitle.setStyle("-fx-font-size: 14px; -fx-text-fill: #555555;");

        Button adminBtn = bigButton("System Administrator Login");
        adminBtn.setOnAction(e -> app.switchTo(AdminLoginScreen.build(app), "Administrator Login"));

        Button userBtn = bigButton("User Login");
        userBtn.setOnAction(e -> app.showUserLogin());

        Button exitBtn = bigButton("Exit");
        exitBtn.setOnAction(e -> app.getStage().close());

        VBox box = new VBox(18, title, subtitle, adminBtn, userBtn, exitBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        return box;
    }

    /** Shared button style so every screen's primary buttons look the same. */
    static Button bigButton(String text) {
        Button b = new Button(text);
        b.setPrefWidth(280);
        b.setPrefHeight(44);
        b.setStyle("-fx-font-size: 14px;");
        return b;
    }
}