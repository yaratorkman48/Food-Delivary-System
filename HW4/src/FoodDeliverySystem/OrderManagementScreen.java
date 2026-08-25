package FoodDeliverySystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Order management screen (Assignment 4, Part ה).
 *
 * Shows every order in a {@link TableView} (the third of the three required
 * tables, Part י) and offers the full Part ה action set:
 *   - show all (the table itself);
 *   - search by code;
 *   - add a new order - reuses {@link DeliveryDataBase#placeOrder}, so the
 *     polymorphic price calculation, the premium minimum-order rule, and the
 *     balance check all apply, and both {@link InsufficientBalanceException}
 *     and the minimum-order {@link IllegalArgumentException} are handled in the
 *     UI (Part יא);
 *   - show orders by customer;
 *   - show orders by restaurant;
 *   - show the highest-priced order, found by reusing the HW3
 *     {@link OrderPriceComparator}.
 *
 * Mirrors {@link CustomerManagementScreen} / {@link RestaurantManagementScreen}:
 * the screen only collects input and shows results; all logic lives in
 * {@link DeliveryDataBase}. The table is backed by an {@link ObservableList},
 * so one {@code data.setAll(system.getOrders())} redraws it after a new order.
 */
public final class OrderManagementScreen {

    private OrderManagementScreen() {
    }

    public static Parent build(FoodDeliveryApp app) {
        DeliveryDataBase system = app.getSystem();

        // ---- the table (Part י TableView #3) ----
        ObservableList<Order> data = FXCollections.observableArrayList(system.getOrders());
        TableView<Order> table = new TableView<>(data);
        table.getColumns().add(column("Code",        "orderCode",      65));
        table.getColumns().add(column("Customer",    "customerCode",   85));
        table.getColumns().add(column("Restaurant",  "restaurantCode", 90));
        table.getColumns().add(column("Amount",      "basicAmount",    85));
        table.getColumns().add(column("Final Price", "finalPrice",     95));
        table.getColumns().add(column("Status",      "status",         100));
        table.getColumns().add(column("Rider",       "riderCode",      100));
        table.setPrefHeight(360);

        // ---- action buttons ----
        Button searchBtn     = new Button("Search by code");
        Button addBtn        = new Button("Add new");
        Button byCustomerBtn = new Button("Orders by customer");
        Button byRestBtn     = new Button("Orders by restaurant");
        Button highestBtn    = new Button("Highest-priced order");
        Button backBtn       = new Button("\u2190 Back");

        searchBtn.setOnAction(e -> searchByCode(system, table));
        addBtn.setOnAction(e -> openAddForm(system, data));
        byCustomerBtn.setOnAction(e -> showByCustomer(system));
        byRestBtn.setOnAction(e -> showByRestaurant(system));
        highestBtn.setOnAction(e -> showHighest(system));
        backBtn.setOnAction(e -> app.showAdminHub());

        HBox row1 = new HBox(10, searchBtn, addBtn);
        HBox row2 = new HBox(10, byCustomerBtn, byRestBtn, highestBtn);
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);

        Label title = new Label("Orders");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        VBox box = new VBox(14, title, table, row1, row2, backBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        return box;
    }

    // ===================================================================
    //  Operations
    // ===================================================================

    /** Asks for a code and selects/scrolls to that order, or reports not-found. */
    private static void searchByCode(DeliveryDataBase system, TableView<Order> table) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search order");
        dialog.setHeaderText(null);
        dialog.setContentText("Order code:");
        dialog.showAndWait().ifPresent(input -> {
            int code;
            try {
                code = Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid code", "Please enter a whole number.");
                return;
            }
            Order found = system.findOrderByCode(code);
            if (found == null) {
                UiHelper.error("Not found", "No order exists with code " + code + ".");
                return;
            }
            table.getSelectionModel().select(found);
            table.scrollTo(found);
            UiHelper.info("Found", "Order #" + found.getOrderCode()
                    + "  |  final price " + found.getFinalPrice()
                    + " ILS  |  " + found.getStatus());
        });
    }

    /** Lists all orders of a customer given by code. */
    private static void showByCustomer(DeliveryDataBase system) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Orders by customer");
        dialog.setHeaderText(null);
        dialog.setContentText("Customer code:");
        dialog.showAndWait().ifPresent(input -> {
            int code;
            try {
                code = Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid code", "Please enter a whole number.");
                return;
            }
            List<String> lines = new ArrayList<>();
            for (Order o : system.getOrders()) {
                if (o.getCustomerCode() == code) {
                    lines.add(orderLine(o));
                }
            }
            UiHelper.popupList("Orders of customer #" + code, lines);
        });
    }

    /** Lists all orders for a restaurant given by code. */
    private static void showByRestaurant(DeliveryDataBase system) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Orders by restaurant");
        dialog.setHeaderText(null);
        dialog.setContentText("Restaurant code:");
        dialog.showAndWait().ifPresent(input -> {
            int code;
            try {
                code = Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid code", "Please enter a whole number.");
                return;
            }
            List<String> lines = new ArrayList<>();
            for (Order o : system.getOrders()) {
                if (o.getRestaurantCode() == code) {
                    lines.add(orderLine(o));
                }
            }
            UiHelper.popupList("Orders for restaurant #" + code, lines);
        });
    }

    /**
     * Shows the single highest-priced order. Reuses the HW3
     * {@link OrderPriceComparator} (which orders high-to-low) on a COPY, then
     * takes the first element - so the ordering machinery from the previous
     * assignment is surfaced in the GUI, as Part ז requires.
     */
    private static void showHighest(DeliveryDataBase system) {
        ArrayList<Order> copy = new ArrayList<>(system.getOrders());
        if (copy.isEmpty()) {
            UiHelper.info("No orders", "There are no orders in the system yet.");
            return;
        }
        Collections.sort(copy, new OrderPriceComparator()); // high -> low
        Order highest = copy.get(0);

        List<String> lines = new ArrayList<>();
        lines.add("Order #" + highest.getOrderCode());
        lines.add("Customer: " + highest.getCustomerCode());
        lines.add("Restaurant: " + highest.getRestaurantCode()
                + (highest.getRestaurant() != null ? "  (" + highest.getRestaurant().getName() + ")" : ""));
        lines.add("Basic amount: " + highest.getBasicAmount() + " ILS");
        lines.add("Final price: " + highest.getFinalPrice() + " ILS");
        lines.add("Status: " + highest.getStatus());
        UiHelper.popupList("Highest-priced order", lines);
    }

    private static String orderLine(Order o) {
        return "#" + o.getOrderCode()
                + "   restaurant " + o.getRestaurantCode()
                + "   " + o.getFinalPrice() + " ILS"
                + "   [" + o.getStatus() + "]";
    }

    // ===================================================================
    //  Add order form (reuses DeliveryDataBase.placeOrder)
    // ===================================================================

    /**
     * Opens the modal "new order" form. The admin picks any OPEN restaurant, a
     * customer by code, an amount and a date. All the business rules live in
     * {@link DeliveryDataBase#placeOrder}: it computes the price polymorphically,
     * rejects a premium order below the minimum, and rejects an unaffordable
     * order - each surfaced here as a friendly message rather than a crash.
     */
    private static void openAddForm(DeliveryDataBase system, ObservableList<Order> data) {
        TextField custCode = new TextField();

        ComboBox<Restaurant> restBox = new ComboBox<>();
        for (Restaurant r : system.getRestaurants()) {
            if (r.isOpen()) {
                restBox.getItems().add(r);
            }
        }
        restBox.setConverter(new StringConverter<Restaurant>() {
            @Override public String toString(Restaurant r) {
                return r == null ? "" : "#" + r.getRestaurantCode() + "  " + r.getName();
            }
            @Override public Restaurant fromString(String s) {
                return null;
            }
        });
        if (!restBox.getItems().isEmpty()) {
            restBox.getSelectionModel().selectFirst();
        }

        TextField amount = new TextField();
        LocalDate today = LocalDate.now();
        TextField day   = new TextField(String.valueOf(today.getDayOfMonth()));
        TextField month = new TextField(String.valueOf(today.getMonthValue()));
        TextField year  = new TextField(String.valueOf(today.getYear()));

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(8);
        g.setPadding(new Insets(16));
        g.add(new Label("Customer code:"), 0, 0); g.add(custCode, 1, 0);
        g.add(new Label("Restaurant:"), 0, 1);    g.add(restBox, 1, 1);
        g.add(new Label("Order amount:"), 0, 2);  g.add(amount, 1, 2);
        g.add(new Label("Day:"), 0, 3);   g.add(day, 1, 3);
        g.add(new Label("Month:"), 0, 4); g.add(month, 1, 4);
        g.add(new Label("Year:"), 0, 5);  g.add(year, 1, 5);

        Button submit = new Button("Place order");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox(10, submit, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(12, new Label("New order"), g, buttons);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add order");
        stage.setScene(new Scene(box));
        cancel.setOnAction(e -> stage.close());

        submit.setOnAction(e -> {
            Restaurant r = restBox.getValue();
            if (r == null) {
                UiHelper.error("No restaurant", "There are no open restaurants to order from.");
                return;
            }
            Customer customer;
            double amt;
            int d;
            int m;
            int y;
            try {
                customer = system.findCustomerByCode(Integer.parseInt(custCode.getText().trim()));
                amt = Double.parseDouble(amount.getText().trim());
                d = Integer.parseInt(day.getText().trim());
                m = Integer.parseInt(month.getText().trim());
                y = Integer.parseInt(year.getText().trim());
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid input", "Customer code, amount and date must be numbers.");
                return;
            }
            if (customer == null) {
                UiHelper.error("Not found", "No customer with that code.");
                return;
            }
            if (amt <= 0) {
                UiHelper.error("Invalid amount", "Order amount must be positive.");
                return;
            }
            try {
                Order placed = system.placeOrder(customer, r, d, m, y, amt);
                data.setAll(system.getOrders()); // redraw with the new order
                stage.close();
                UiHelper.info("Order placed", "Order #" + placed.getOrderCode()
                        + " created. Final price: " + placed.getFinalPrice() + " ILS.");
            } catch (InsufficientBalanceException ex) {
                UiHelper.error("Order rejected", ex.getMessage());
            } catch (IllegalArgumentException ex) {
                UiHelper.error("Order rejected", ex.getMessage());
            }
        });

        stage.showAndWait();
    }

    // ===================================================================
    //  Small shared UI helper (same pattern as the other screens)
    // ===================================================================

    private static <T> TableColumn<Order, T> column(String title, String property, double width) {
        TableColumn<Order, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }
}