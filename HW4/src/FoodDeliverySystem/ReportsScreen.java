package FoodDeliverySystem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

/**
 * Reports & sorting screen (Assignment 4, Part ח).
 *
 * Surfaces the HW3 ordering machinery in the GUI, each result shown in a JavaFX
 * list window. Every required technique is exercised here, unchanged from HW3:
 *   - Comparable via the method reference {@code Customer::compareTo}
 *     (customers by balance);
 *   - a lambda comparator (customers by first name; riders by deliveries);
 *   - the dedicated Comparator classes {@link RestaurantRatingComparator} and
 *     {@link OrderPriceComparator};
 *   - the generic/wildcard report method {@link SystemReports#sumFinalPrices}.
 *
 * Sorting is always done on a COPY of the collection, so the system's own order
 * is never disturbed.
 */
public final class ReportsScreen {

    private ReportsScreen() {
    }

    public static Parent build(FoodDeliveryApp app) {
        DeliveryDataBase system = app.getSystem();

        Button b1 = wide("Sort customers by balance");
        Button b2 = wide("Sort customers by first name");
        Button b3 = wide("Sort restaurants by rating");
        Button b4 = wide("Sort orders by final price");
        Button b5 = wide("Sort riders by deliveries");
        Button b6 = wide("Show open restaurants");
        Button b7 = wide("Show premium restaurants");
        Button b8 = wide("Show available riders");
        Button b9 = wide("Show total payments");
        Button back = wide("\u2190 Back");

        b1.setOnAction(e -> sortCustomersByBalance(system));
        b2.setOnAction(e -> sortCustomersByName(system));
        b3.setOnAction(e -> sortRestaurantsByRating(system));
        b4.setOnAction(e -> sortOrdersByPrice(system));
        b5.setOnAction(e -> sortRidersByDeliveries(system));
        b6.setOnAction(e -> showOpenRestaurants(system));
        b7.setOnAction(e -> showPremiumRestaurants(system));
        b8.setOnAction(e -> showAvailableRiders(system));
        b9.setOnAction(e -> showTotalPayments(system));
        back.setOnAction(e -> app.showAdminHub());

        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);
        grid.add(b1, 0, 0);
        grid.add(b2, 1, 0);
        grid.add(b3, 0, 1);
        grid.add(b4, 1, 1);
        grid.add(b5, 0, 2);
        grid.add(b6, 1, 2);
        grid.add(b7, 0, 3);
        grid.add(b8, 1, 3);
        grid.add(b9, 0, 4);

        Label title = new Label("Reports & Sorting");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        VBox box = new VBox(22, title, grid, back);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(30));
        return box;
    }

    // ===================================================================
    //  Sorting reports (each on a fresh copy)
    // ===================================================================

    /** Comparable, via the method reference Customer::compareTo (balance high to low). */
    private static void sortCustomersByBalance(DeliveryDataBase system) {
        ArrayList<Customer> list = new ArrayList<>(system.getCustomers());
        Collections.sort(list, Customer::compareTo);
        List<String> lines = new ArrayList<>();
        for (Customer c : list) {
            lines.add(c.getFirstName() + " " + c.getLastName() + "   | balance = " + c.getCreditBalance());
        }
        UiHelper.popupList("Customers by balance (high to low)", lines);
    }

    /** Lambda comparator delegating to String's natural order (A to Z). */
    private static void sortCustomersByName(DeliveryDataBase system) {
        ArrayList<Customer> list = new ArrayList<>(system.getCustomers());
        Collections.sort(list, (c1, c2) -> c1.getFirstName().compareTo(c2.getFirstName()));
        List<String> lines = new ArrayList<>();
        for (Customer c : list) {
            lines.add(c.getFirstName() + " " + c.getLastName());
        }
        UiHelper.popupList("Customers by first name (A to Z)", lines);
    }

    /** Dedicated Comparator class: RestaurantRatingComparator (rating high to low). */
    private static void sortRestaurantsByRating(DeliveryDataBase system) {
        ArrayList<Restaurant> list = new ArrayList<>(system.getRestaurants());
        Collections.sort(list, new RestaurantRatingComparator());
        List<String> lines = new ArrayList<>();
        for (Restaurant r : list) {
            lines.add(r.getName() + "   | rating = " + r.getRating());
        }
        UiHelper.popupList("Restaurants by rating (high to low)", lines);
    }

    /** Dedicated Comparator class: OrderPriceComparator (final price high to low). */
    private static void sortOrdersByPrice(DeliveryDataBase system) {
        ArrayList<Order> list = new ArrayList<>(system.getOrders());
        Collections.sort(list, new OrderPriceComparator());
        List<String> lines = new ArrayList<>();
        for (Order o : list) {
            lines.add("#" + o.getOrderCode() + "   | final = " + o.getFinalPrice() + " ILS   [" + o.getStatus() + "]");
        }
        UiHelper.popupList("Orders by final price (high to low)", lines);
    }

    /** Lambda comparator on the delivery count (high to low). */
    private static void sortRidersByDeliveries(DeliveryDataBase system) {
        ArrayList<Rider> list = new ArrayList<>(system.getRiders());
        Collections.sort(list, (r1, r2) -> Integer.compare(r2.getOrdersCount(), r1.getOrdersCount()));
        List<String> lines = new ArrayList<>();
        for (Rider r : list) {
            lines.add(r.getFullName() + "   | deliveries = " + r.getOrdersCount());
        }
        UiHelper.popupList("Riders by deliveries (high to low)", lines);
    }

    // ===================================================================
    //  Filter reports
    // ===================================================================

    private static void showOpenRestaurants(DeliveryDataBase system) {
        List<String> lines = new ArrayList<>();
        for (Restaurant r : system.getRestaurants()) {
            if (r.isOpen()) {
                lines.add(r.getName() + "   (#" + r.getRestaurantCode() + ", " + r.getCuisineType() + ")");
            }
        }
        UiHelper.popupList("Open restaurants", lines);
    }

    private static void showPremiumRestaurants(DeliveryDataBase system) {
        List<String> lines = new ArrayList<>();
        for (Restaurant r : system.getRestaurants()) {
            if (r instanceof PremiumRestaurant) {
                lines.add(r.getName() + "   (#" + r.getRestaurantCode() + ", " + r.getCuisineType() + ")");
            }
        }
        UiHelper.popupList("Premium restaurants", lines);
    }

    private static void showAvailableRiders(DeliveryDataBase system) {
        List<String> lines = new ArrayList<>();
        for (Rider r : system.getRiders()) {
            if (r.isAvailable()) {
                lines.add(r.getFullName() + "   (" + r.getVehicle() + ")");
            }
        }
        UiHelper.popupList("Available riders", lines);
    }

    /** Total of all final prices, via the generic/wildcard SystemReports method. */
    private static void showTotalPayments(DeliveryDataBase system) {
        SystemReports reports = new SystemReports();
        double total = reports.sumFinalPrices(system.getOrders());
        UiHelper.info("Total payments", "Sum of all order final prices: " + total + " ILS.");
    }

    // ===================================================================
    //  Helper
    // ===================================================================

    private static Button wide(String text) {
        Button b = new Button(text);
        b.setPrefWidth(240);
        b.setPrefHeight(40);
        b.setStyle("-fx-font-size: 13px;");
        return b;
    }
}
