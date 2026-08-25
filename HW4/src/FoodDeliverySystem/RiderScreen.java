package FoodDeliverySystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Rider personal screen (Assignment 4, Part טו - rider).
 *
 * The logged-in rider can: view their details, view their orders,
 * mark an order as "on the way", and mark an order as delivered.
 * Every operation goes through {@link DeliveryDataBase}.
 */
public final class RiderScreen {

    private RiderScreen() {}

    public static Parent build(FoodDeliveryApp app, Rider rider) {
        DeliveryDataBase system = app.getSystem();

        Label title = new Label("Rider: " + rider.getFullName());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        Label sub = new Label("ID: " + rider.getId()
                + "   Vehicle: " + rider.getVehicle().getDisplayName()
                + "   Status: " + (rider.isAvailable() ? "Available" : "On a delivery"));
        sub.setStyle("-fx-font-size: 14px;");

        Button details   = wide("My details");
        Button myOrders  = wide("My orders");
        Button onTheWay  = wide("Mark order: on the way");
        Button delivered = wide("Mark order: delivered");
        Button logout    = wide("Logout");

        details.setOnAction(e -> showDetails(rider));
        myOrders.setOnAction(e -> showOrders(rider));
        onTheWay.setOnAction(e -> markOnTheWay(app, rider));
        delivered.setOnAction(e -> markDelivered(app, rider));
        logout.setOnAction(e -> app.showOpeningScreen());

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.add(details,   0, 0);
        grid.add(myOrders,  1, 0);
        grid.add(onTheWay,  0, 1);
        grid.add(delivered, 1, 1);

        VBox box = new VBox(18, title, sub, grid, logout);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24));
        return box;
    }

    // ===================================================================
    //  Operations
    // ===================================================================

    private static void showDetails(Rider rider) {
        String text = "ID: " + rider.getId()
                + "\nName: " + rider.getFullName()
                + "\nPhone: " + rider.getPhone()
                + "\nVehicle: " + rider.getVehicle().getDisplayName()
                + "\nTotal orders: " + rider.getOrdersCount()
                + "\nStatus: " + (rider.isAvailable() ? "Available" : "On a delivery");
        UiHelper.info("My details", text);
    }

    private static void showOrders(Rider rider) {
        List<String> lines = new ArrayList<>();
        for (Order o : rider.getOrders()) {
            lines.add("#" + o.getOrderCode() + "   restaurant " + o.getRestaurantCode()
                    + "   " + o.getFinalPrice() + " ILS   [" + o.getStatus() + "]");
        }
        UiHelper.popupList("My orders", lines);
    }

    private static void markOnTheWay(FoodDeliveryApp app, Rider rider) {
        Order order = pickActive(rider, Order.STATUS_SENT, "Mark on the way");
        if (order == null) return;
        app.getSystem().markOrderOnTheWay(order);
        UiHelper.info("Updated", "Order #" + order.getOrderCode() + " is now on the way.");
    }

    private static void markDelivered(FoodDeliveryApp app, Rider rider) {
        Order order = pickActive(rider, Order.STATUS_ON_THE_WAY, "Mark delivered");
        if (order == null) return;

        TextInputDialog dayD = new TextInputDialog();
        dayD.setTitle("Delivery date");
        dayD.setHeaderText(null);
        dayD.setContentText("Day:");
        Optional<String> dayStr = dayD.showAndWait();
        if (dayStr.isEmpty()) return;

        TextInputDialog monD = new TextInputDialog();
        monD.setTitle("Delivery date");
        monD.setHeaderText(null);
        monD.setContentText("Month:");
        Optional<String> monStr = monD.showAndWait();
        if (monStr.isEmpty()) return;

        TextInputDialog yrD = new TextInputDialog();
        yrD.setTitle("Delivery date");
        yrD.setHeaderText(null);
        yrD.setContentText("Year:");
        Optional<String> yrStr = yrD.showAndWait();
        if (yrStr.isEmpty()) return;

        try {
            int d = Integer.parseInt(dayStr.get().trim());
            int m = Integer.parseInt(monStr.get().trim());
            int y = Integer.parseInt(yrStr.get().trim());
            app.getSystem().markOrderDelivered(order, rider, d, m, y);
            UiHelper.info("Delivered", "Order #" + order.getOrderCode() + " marked as delivered.");
        } catch (NumberFormatException ex) {
            UiHelper.error("Invalid date", "Day, month and year must be whole numbers.");
        }
    }

    /** Returns an order from the rider's list that has the given status, or null. */
    private static Order pickActive(Rider rider, String requiredStatus, String title) {
        List<Order> candidates = new ArrayList<>();
        List<String> labels    = new ArrayList<>();
        for (Order o : rider.getOrders()) {
            if (requiredStatus.equals(o.getStatus())) {
                candidates.add(o);
                labels.add("#" + o.getOrderCode() + "  restaurant " + o.getRestaurantCode());
            }
        }
        if (candidates.isEmpty()) {
            UiHelper.error("No orders", "No orders with status '" + requiredStatus + "'.");
            return null;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(labels.get(0), labels);
        dialog.setHeaderText(null);
        dialog.setTitle(title);
        dialog.setContentText("Order:");
        Optional<String> chosen = dialog.showAndWait();
        return chosen.isEmpty() ? null : candidates.get(labels.indexOf(chosen.get()));
    }

    private static Button wide(String text) {
        Button b = new Button(text);
        b.setPrefWidth(220);
        b.setPrefHeight(40);
        b.setStyle("-fx-font-size: 13px;");
        return b;
    }
}
