package FoodDeliverySystem;

import java.io.IOException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Admin control center (Assignment 4, Part ב continued) - the hub the
 * administrator reaches after a successful login.
 *
 * One button per management area (filled in by Tasks 5b-5f and Task 7), plus
 * the Save / Load data actions (Part י) and Logout. Save and Load are wired to
 * {@link FileManager} here: each runs inside try/catch and reports the outcome
 * through {@link UiHelper}, so a file problem becomes a visible message instead
 * of a crash (Part יב).
 */
public final class AdminHubScreen {

    private AdminHubScreen() {
    }

    public static Parent build(FoodDeliveryApp app) {
        Label title = new Label("Admin Control Center");
        title.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        Button customers   = menuButton("Manage Customers");
        Button restaurants = menuButton("Manage Restaurants");
        Button orders      = menuButton("Manage Orders");
        Button riders      = menuButton("Manage Riders");
        Button managers    = menuButton("Manage Restaurant Managers");
        Button reports     = menuButton("Reports & Sorting");
        Button save        = menuButton("Save Data");
        Button load        = menuButton("Load Data");
        Button logout      = menuButton("Logout");

        customers.setOnAction(e -> app.showCustomerManagement());
        restaurants.setOnAction(e -> app.showRestaurantManagement());
        orders.setOnAction(e -> app.showOrderManagement());
        riders.setOnAction(e -> app.showRiderManagement());
        managers.setOnAction(e -> app.showRestAdminManagement());
        reports.setOnAction(e -> app.showReports());
        logout.setOnAction(e -> app.showOpeningScreen());

        save.setOnAction(e -> {
            try {
                FileManager.saveAll(app.getSystem());
                UiHelper.info("Data saved",
                        "All collections were written to the text files successfully.");
            } catch (IOException ex) {
                UiHelper.error("Save failed", "Could not save the data: " + ex.getMessage());
            }
        });

        load.setOnAction(e -> {
            try {
                FileManager.loadAll(app.getSystem());
                UiHelper.info("Data loaded",
                        "Data was restored from the text files.\n"
                        + app.getSystem().getCustomersCount()   + " customers, "
                        + app.getSystem().getRestaurantsCount() + " restaurants, "
                        + app.getSystem().getOrdersCount()      + " orders.");
            } catch (IOException ex) {
                UiHelper.error("Load failed",
                        "Could not load the data (has it been saved at least once?): "
                        + ex.getMessage());
            }
        });

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(14);
        grid.setAlignment(Pos.CENTER);
        grid.add(customers,   0, 0);
        grid.add(restaurants, 1, 0);
        grid.add(orders,      2, 0);
        grid.add(riders,      0, 1);
        grid.add(managers,    1, 1);
        grid.add(reports,     2, 1);
        grid.add(save,        0, 2);
        grid.add(load,        1, 2);
        grid.add(logout,      2, 2);

        VBox box = new VBox(24, title, grid);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(40));
        return box;
    }

    private static Button menuButton(String text) {
        Button b = new Button(text);
        b.setPrefWidth(220);
        b.setPrefHeight(48);
        b.setStyle("-fx-font-size: 13px;");
        return b;
    }
}
