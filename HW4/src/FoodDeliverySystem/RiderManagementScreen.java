package FoodDeliverySystem;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
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

/**
 * Rider management screen (Assignment 4, Part ו).
 *
 * Shows every rider in a {@link TableView} and offers the full Part ו action set:
 *   - show all (the table itself);
 *   - add a new rider - the form's vehicle field is a ComboBox built directly
 *     from {@link VehicleType#values()}, which is the concrete "use the enum in
 *     the UI" requirement of Part ח;
 *   - search a rider by ID;
 *   - show all of a rider's orders;
 *   - update an order's status - reuses {@link DeliveryDataBase#markOrderOnTheWay}
 *     and {@link DeliveryDataBase#markOrderDelivered}, exactly like the rider's
 *     own screen;
 *   - show the rider who completed the most deliveries, via
 *     {@link DeliveryDataBase#getTopRider()}.
 *
 * Mirrors the other management screens: all logic lives in
 * {@link DeliveryDataBase}; the table is backed by an {@link ObservableList} so
 * one {@code data.setAll(system.getRiders())} redraws it after a change.
 */
public final class RiderManagementScreen {

    private RiderManagementScreen() {
    }

    public static Parent build(FoodDeliveryApp app) {
        DeliveryDataBase system = app.getSystem();

        // ---- the table ----
        ObservableList<Rider> data = FXCollections.observableArrayList(system.getRiders());
        TableView<Rider> table = new TableView<>(data);
        table.getColumns().add(column("ID",         "id",          110));
        table.getColumns().add(column("Name",       "fullName",    150));
        table.getColumns().add(column("Phone",      "phone",       120));
        table.getColumns().add(column("Vehicle",    "vehicle",     100));
        table.getColumns().add(column("Available",  "available",   80));
        table.getColumns().add(column("Deliveries", "ordersCount", 90));
        table.setPrefHeight(360);

        // ---- action buttons ----
        Button searchBtn = new Button("Search by ID");
        Button addBtn    = new Button("Add new");
        Button ordersBtn = new Button("Show rider's orders");
        Button statusBtn = new Button("Update order status");
        Button topBtn    = new Button("Top rider");
        Button backBtn   = new Button("\u2190 Back");

        searchBtn.setOnAction(e -> searchById(system, table));
        addBtn.setOnAction(e -> openAddForm(system, data));
        ordersBtn.setOnAction(e -> withSelected(table, r -> showRiderOrders(r)));
        statusBtn.setOnAction(e -> withSelected(table, r -> updateOrderStatus(system, data, r)));
        topBtn.setOnAction(e -> showTopRider(system));
        backBtn.setOnAction(e -> app.showAdminHub());

        HBox row1 = new HBox(10, searchBtn, addBtn);
        HBox row2 = new HBox(10, ordersBtn, statusBtn, topBtn);
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);

        Label title = new Label("Riders");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        VBox box = new VBox(14, title, table, row1, row2, backBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        return box;
    }

    // ===================================================================
    //  Operations
    // ===================================================================

    /** Asks for an ID and selects/scrolls to that rider, or reports not-found. */
    private static void searchById(DeliveryDataBase system, TableView<Rider> table) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search rider");
        dialog.setHeaderText(null);
        dialog.setContentText("Rider ID (9 digits):");
        dialog.showAndWait().ifPresent(input -> {
            Rider found = system.findRiderById(input.trim());
            if (found == null) {
                UiHelper.error("Not found", "No rider exists with ID " + input.trim() + ".");
                return;
            }
            table.getSelectionModel().select(found);
            table.scrollTo(found);
            UiHelper.info("Found", found.getFullName()
                    + "  (" + found.getVehicle() + ", "
                    + (found.isAvailable() ? "available" : "busy") + ")");
        });
    }

    /** Lists all orders currently assigned to the selected rider. */
    private static void showRiderOrders(Rider r) {
        List<String> lines = new ArrayList<>();
        for (Order o : r.getOrders()) {
            lines.add("#" + o.getOrderCode()
                    + "   restaurant " + o.getRestaurantCode()
                    + "   " + o.getFinalPrice() + " ILS"
                    + "   [" + o.getStatus() + "]");
        }
        UiHelper.popupList("Orders of " + r.getFullName(), lines);
    }

    /**
     * Advances one of the rider's orders to its next status. Lets the admin pick
     * an order, then applies the transition that fits its current status:
     * "sent" -> on the way, or "on the way" -> delivered (asking for a date).
     * Reuses the same DeliveryDataBase methods the rider's own screen uses.
     */
    private static void updateOrderStatus(DeliveryDataBase system, ObservableList<Rider> data, Rider r) {
        // Only non-delivered orders can still change status.
        List<Order> active = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (Order o : r.getOrders()) {
            if (!Order.STATUS_DELIVERED.equals(o.getStatus())) {
                active.add(o);
                labels.add("#" + o.getOrderCode() + "  [" + o.getStatus() + "]");
            }
        }
        if (active.isEmpty()) {
            UiHelper.error("No active orders", r.getFullName() + " has no orders to update.");
            return;
        }

        ChoiceDialog<String> pick = new ChoiceDialog<>(labels.get(0), labels);
        pick.setTitle("Update order status");
        pick.setHeaderText(null);
        pick.setContentText("Order:");
        Optional<String> chosen = pick.showAndWait();
        if (chosen.isEmpty()) {
            return;
        }
        Order order = active.get(labels.indexOf(chosen.get()));

        if (Order.STATUS_SENT.equals(order.getStatus())) {
            system.markOrderOnTheWay(order);
            data.setAll(system.getRiders());
            UiHelper.info("Updated", "Order #" + order.getOrderCode() + " is now on the way.");
        } else if (Order.STATUS_ON_THE_WAY.equals(order.getStatus())) {
            markDelivered(system, data, r, order);
        } else {
            UiHelper.info("No change", "Order #" + order.getOrderCode()
                    + " already has status '" + order.getStatus() + "'.");
        }
    }

    /** Asks for a delivery date and marks the order delivered. */
    private static void markDelivered(DeliveryDataBase system, ObservableList<Rider> data,
            Rider rider, Order order) {
        Integer d = askInt("Delivery date", "Day:");
        if (d == null) {
            return;
        }
        Integer m = askInt("Delivery date", "Month:");
        if (m == null) {
            return;
        }
        Integer y = askInt("Delivery date", "Year:");
        if (y == null) {
            return;
        }
        system.markOrderDelivered(order, rider, d, m, y);
        data.setAll(system.getRiders()); // availability / counts may have changed
        UiHelper.info("Delivered", "Order #" + order.getOrderCode() + " marked as delivered.");
    }

    /** Shows the rider with the most completed deliveries. */
    private static void showTopRider(DeliveryDataBase system) {
        Rider top = system.getTopRider();
        if (top == null) {
            UiHelper.info("No riders", "There are no riders in the system yet.");
            return;
        }
        int delivered = 0;
        for (Order o : top.getOrders()) {
            if (Order.STATUS_DELIVERED.equals(o.getStatus())) {
                delivered++;
            }
        }
        UiHelper.info("Top rider", top.getFullName()
                + "  (ID " + top.getId() + ")\ncompleted deliveries: " + delivered);
    }

    // ===================================================================
    //  Add form (VehicleType ComboBox - Part ח enum-in-UI)
    // ===================================================================

    private static void openAddForm(DeliveryDataBase system, ObservableList<Rider> data) {
        TextField id    = new TextField();
        TextField name  = new TextField();
        TextField phone = new TextField();

        ComboBox<VehicleType> vehicleBox = new ComboBox<>();
        vehicleBox.getItems().addAll(VehicleType.values()); // enum drives the choices
        vehicleBox.getSelectionModel().selectFirst();

        CheckBox availableBox = new CheckBox("Available for deliveries");
        availableBox.setSelected(true);

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(8);
        g.setPadding(new Insets(16));
        addRow(g, 0, "ID (9 digits):", id);
        addRow(g, 1, "Full name:", name);
        addRow(g, 2, "Phone:", phone);
        addRow(g, 3, "Vehicle:", vehicleBox);
        g.add(availableBox, 1, 4);

        Button save = new Button("Add rider");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox(10, save, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(12, new Label("New rider"), g, buttons);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add rider");
        stage.setScene(new Scene(box));
        cancel.setOnAction(e -> stage.close());

        save.setOnAction(e -> {
            String idText = id.getText().trim();
            if (!InputHelper.isValidNationalId(idText)) {
                UiHelper.error("Invalid ID", "Rider ID must be exactly 9 digits.");
                return;
            }
            if (!InputHelper.isLettersAndSpaces(name.getText().trim())) {
                UiHelper.error("Invalid name", "Name must contain letters only.");
                return;
            }
            if (!InputHelper.isValidPhone(phone.getText().trim())) {
                UiHelper.error("Invalid phone", "Phone number is not valid.");
                return;
            }
            VehicleType vehicle = vehicleBox.getValue();

            Rider rider = new Rider(idText, name.getText().trim(), phone.getText().trim(),
                    vehicle, availableBox.isSelected());
            if (system.addRider(rider)) {
                data.setAll(system.getRiders());
                stage.close();
                UiHelper.info("Added", "New rider added.");
            } else {
                UiHelper.error("Duplicate ID", "A rider with ID " + idText + " already exists.");
            }
        });

        stage.showAndWait();
    }

    // ===================================================================
    //  Small shared UI helpers (same pattern as the other screens)
    // ===================================================================

    private static <T> TableColumn<Rider, T> column(String title, String property, double width) {
        TableColumn<Rider, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }

    private static void addRow(GridPane g, int row, String label, javafx.scene.Node field) {
        g.add(new Label(label), 0, row);
        g.add(field, 1, row);
    }

    /** Small integer prompt; returns null if cancelled or not a whole number. */
    private static Integer askInt(String title, String prompt) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(prompt);
        Optional<String> res = dialog.showAndWait();
        if (res.isEmpty()) {
            return null;
        }
        try {
            return Integer.parseInt(res.get().trim());
        } catch (NumberFormatException ex) {
            UiHelper.error("Invalid number", prompt + " must be a whole number.");
            return null;
        }
    }

    /** Runs an action on the selected rider, or warns if none is selected. */
    private static void withSelected(TableView<Rider> table,
            java.util.function.Consumer<Rider> action) {
        Rider sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UiHelper.error("No selection", "Select a rider in the table first.");
        } else {
            action.accept(sel);
        }
    }
}