package FoodDeliverySystem;

import java.util.ArrayList;
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
import javafx.scene.control.PasswordField;
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
 * Restaurant-manager (RestAdmin) management screen (Assignment 4, admin hub
 * item "ניהול מנהלי מסעדות").
 *
 * Shows every manager in a {@link TableView} and offers:
 *   - show all (the table itself);
 *   - add a new manager (name / username / password, unique username);
 *   - search a manager by username;
 *   - show the restaurants a manager is responsible for;
 *   - assign a restaurant to a manager - the core relationship this screen
 *     exists to manage, done through {@link RestAdmin#addRestaurant(Restaurant)}
 *     so the "no duplicate restaurant per manager" rule still holds.
 *
 * Mirrors the other management screens: all state lives in
 * {@link DeliveryDataBase} / {@link RestAdmin}; the table is backed by an
 * {@link ObservableList} so one {@code data.setAll(system.getRestAdmins())}
 * redraws it after a change.
 */
public final class RestAdminManagementScreen {

    private RestAdminManagementScreen() {
    }

    public static Parent build(FoodDeliveryApp app) {
        DeliveryDataBase system = app.getSystem();

        // ---- the table ----
        ObservableList<RestAdmin> data = FXCollections.observableArrayList(system.getRestAdmins());
        TableView<RestAdmin> table = new TableView<>(data);
        table.getColumns().add(column("Username",   "username",    150));
        table.getColumns().add(column("Manager",    "managerName", 180));
        table.getColumns().add(column("Restaurants", "count",       100));
        table.setPrefHeight(360);

        // ---- action buttons ----
        Button searchBtn = new Button("Search by username");
        Button addBtn    = new Button("Add new");
        Button showBtn   = new Button("Show restaurants");
        Button assignBtn = new Button("Assign restaurant");
        Button backBtn   = new Button("\u2190 Back");

        searchBtn.setOnAction(e -> searchByUsername(system, table));
        addBtn.setOnAction(e -> openAddForm(system, data));
        showBtn.setOnAction(e -> withSelected(table, RestAdminManagementScreen::showManaged));
        assignBtn.setOnAction(e -> withSelected(table, m -> assignRestaurant(system, data, m)));
        backBtn.setOnAction(e -> app.showAdminHub());

        HBox row1 = new HBox(10, searchBtn, addBtn);
        HBox row2 = new HBox(10, showBtn, assignBtn);
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);

        Label title = new Label("Restaurant Managers");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        VBox box = new VBox(14, title, table, row1, row2, backBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        return box;
    }

    // ===================================================================
    //  Operations
    // ===================================================================

    /** Asks for a username and selects/scrolls to that manager, or reports not-found. */
    private static void searchByUsername(DeliveryDataBase system, TableView<RestAdmin> table) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search manager");
        dialog.setHeaderText(null);
        dialog.setContentText("Username:");
        dialog.showAndWait().ifPresent(input -> {
            RestAdmin found = system.findRestAdminByUsername(input.trim());
            if (found == null) {
                UiHelper.error("Not found", "No manager exists with username '" + input.trim() + "'.");
                return;
            }
            table.getSelectionModel().select(found);
            table.scrollTo(found);
            UiHelper.info("Found", found.getManagerName()
                    + "  (" + found.getCount() + " restaurant(s))");
        });
    }

    /** Lists the restaurants the selected manager is responsible for. */
    private static void showManaged(RestAdmin m) {
        List<String> lines = new ArrayList<>();
        for (Restaurant r : m.getManagedRestaurants()) {
            lines.add(r.getName() + "   (#" + r.getRestaurantCode() + ", " + r.getCuisineType() + ")");
        }
        UiHelper.popupList("Restaurants managed by " + m.getManagerName(), lines);
    }

    /**
     * Assigns a restaurant to the selected manager. The admin picks a restaurant
     * from a ComboBox of all restaurants; {@link RestAdmin#addRestaurant} rejects
     * a duplicate, which is reported as a friendly message.
     */
    private static void assignRestaurant(DeliveryDataBase system, ObservableList<RestAdmin> data,
            RestAdmin manager) {
        if (system.getRestaurants().isEmpty()) {
            UiHelper.error("No restaurants", "There are no restaurants to assign.");
            return;
        }

        ComboBox<Restaurant> restBox = new ComboBox<>();
        restBox.getItems().addAll(system.getRestaurants());
        restBox.setConverter(new StringConverter<Restaurant>() {
            @Override public String toString(Restaurant r) {
                return r == null ? "" : "#" + r.getRestaurantCode() + "  " + r.getName();
            }
            @Override public Restaurant fromString(String s) {
                return null;
            }
        });
        restBox.getSelectionModel().selectFirst();

        Button assign = new Button("Assign");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox(10, assign, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(12,
                new Label("Assign a restaurant to " + manager.getManagerName()),
                restBox, buttons);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Assign restaurant");
        stage.setScene(new Scene(box));
        cancel.setOnAction(e -> stage.close());

        assign.setOnAction(e -> {
            Restaurant r = restBox.getValue();
            if (r == null) {
                UiHelper.error("No selection", "Please pick a restaurant.");
                return;
            }
            if (manager.addRestaurant(r)) {
                data.setAll(system.getRestAdmins()); // the count column changes
                stage.close();
                UiHelper.info("Assigned", r.getName() + " is now managed by "
                        + manager.getManagerName() + ".");
            } else {
                UiHelper.error("Already assigned",
                        manager.getManagerName() + " already manages " + r.getName() + ".");
            }
        });

        stage.showAndWait();
    }

    // ===================================================================
    //  Add form
    // ===================================================================

    private static void openAddForm(DeliveryDataBase system, ObservableList<RestAdmin> data) {
        TextField name     = new TextField();
        TextField username = new TextField();
        PasswordField password = new PasswordField();

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(8);
        g.setPadding(new Insets(16));
        addRow(g, 0, "Manager name:", name);
        addRow(g, 1, "Username:", username);
        addRow(g, 2, "Password:", password);

        Button save = new Button("Add manager");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox(10, save, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(12, new Label("New manager"), g, buttons);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add manager");
        stage.setScene(new Scene(box));
        cancel.setOnAction(e -> stage.close());

        save.setOnAction(e -> {
            if (!InputHelper.isLettersAndSpaces(name.getText().trim())) {
                UiHelper.error("Invalid name", "Manager name must contain letters only.");
                return;
            }
            if (!InputHelper.isValidString(username.getText())) {
                UiHelper.error("Invalid username", "Username cannot be empty.");
                return;
            }
            if (!InputHelper.isValidString(password.getText())) {
                UiHelper.error("Invalid password", "Password cannot be empty.");
                return;
            }
            RestAdmin manager = new RestAdmin(name.getText().trim(),
                    username.getText().trim(), password.getText());
            if (system.addRestAdmin(manager)) {
                data.setAll(system.getRestAdmins());
                stage.close();
                UiHelper.info("Added", "New manager added.");
            } else {
                UiHelper.error("Duplicate username",
                        "A manager with username '" + username.getText().trim() + "' already exists.");
            }
        });

        stage.showAndWait();
    }

    // ===================================================================
    //  Small shared UI helpers (same pattern as the other screens)
    // ===================================================================

    private static <T> TableColumn<RestAdmin, T> column(String title, String property, double width) {
        TableColumn<RestAdmin, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }

    private static void addRow(GridPane g, int row, String label, javafx.scene.Node field) {
        g.add(new Label(label), 0, row);
        g.add(field, 1, row);
    }

    /** Runs an action on the selected manager, or warns if none is selected. */
    private static void withSelected(TableView<RestAdmin> table,
            java.util.function.Consumer<RestAdmin> action) {
        RestAdmin sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UiHelper.error("No selection", "Select a manager in the table first.");
        } else {
            action.accept(sel);
        }
    }
}