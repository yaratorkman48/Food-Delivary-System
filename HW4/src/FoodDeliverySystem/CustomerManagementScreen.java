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
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
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
 * Customer management screen (Assignment 4, Part ג).
 *
 * Shows every customer in a {@link TableView} (the first of the three required
 * tables, Part יא) and offers: search by code, add, update, and the three
 * "history" views - a customer's orders, the restaurants they ordered from, and
 * the premium restaurants among those.
 *
 * All work goes through {@link DeliveryDataBase}; the screen only collects input
 * and shows results. The table is backed by an {@link ObservableList}, so a
 * single {@code data.setAll(...)} refresh redraws it after any change.
 */
public final class CustomerManagementScreen {

    private CustomerManagementScreen() {
    }

    public static Parent build(FoodDeliveryApp app) {
        DeliveryDataBase system = app.getSystem();

        // ---- the table (Part יא TableView #1) ----
        ObservableList<Customer> data = FXCollections.observableArrayList(system.getCustomers());
        TableView<Customer> table = new TableView<>(data);
        table.getColumns().add(column("Code",       "customerCode",  70));
        table.getColumns().add(column("First Name", "firstName",     110));
        table.getColumns().add(column("Last Name",  "lastName",      110));
        table.getColumns().add(column("City",       "city",          110));
        table.getColumns().add(column("Phone",      "phone",         120));
        table.getColumns().add(column("Email",      "email",         190));
        table.getColumns().add(column("Balance",    "creditBalance", 90));
        table.setPrefHeight(360);

        // ---- action buttons ----
        Button searchBtn   = new Button("Search by code");
        Button addBtn      = new Button("Add new");
        Button updateBtn   = new Button("Update selected");
        Button ordersBtn   = new Button("Show orders");
        Button restBtn     = new Button("Restaurants ordered from");
        Button premiumBtn  = new Button("Premium restaurants");
        Button backBtn     = new Button("\u2190 Back");

        searchBtn.setOnAction(e -> searchByCode(system, table));
        addBtn.setOnAction(e -> openForm(app, data, null));
        updateBtn.setOnAction(e -> {
            Customer sel = table.getSelectionModel().getSelectedItem();
            if (sel == null) {
                UiHelper.error("No selection", "Select a customer in the table first.");
            } else {
                openForm(app, data, sel);
            }
        });
        ordersBtn.setOnAction(e -> withSelected(table, c -> showOrders(system, c)));
        restBtn.setOnAction(e -> withSelected(table, c -> showRestaurants(system, c)));
        premiumBtn.setOnAction(e -> withSelected(table, c -> showPremium(system, c)));
        backBtn.setOnAction(e -> app.showAdminHub());

        HBox row1 = new HBox(10, searchBtn, addBtn, updateBtn);
        HBox row2 = new HBox(10, ordersBtn, restBtn, premiumBtn);
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);

        Label title = new Label("Customers");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        VBox box = new VBox(14, title, table, row1, row2, backBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        return box;
    }

    // ===================================================================
    //  Operations
    // ===================================================================

    /** Asks for a code and selects/scrolls to that customer, or reports not-found. */
    private static void searchByCode(DeliveryDataBase system, TableView<Customer> table) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search customer");
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
            // Uses the throwing finder so CustomerNotFoundException is handled in the UI (Part יב).
            try {
                Customer found = system.findCustomerByCodeOrThrow(code);
                table.getSelectionModel().select(found);
                table.scrollTo(found);
                UiHelper.info("Found", found.getFirstName() + " " + found.getLastName()
                        + "  (balance: " + found.getCreditBalance() + ")");
            } catch (CustomerNotFoundException ex) {
                UiHelper.error("Not found", ex.getMessage());
            }
        });
    }

    private static void showOrders(DeliveryDataBase system, Customer c) {
        ArrayList<Order> orders = system.getOrdersByCustomer().get(c.getCustomerCode());
        List<String> lines = new ArrayList<>();
        if (orders != null) {
            for (Order o : orders) {
                lines.add("#" + o.getOrderCode() + "   restaurant " + o.getRestaurantCode()
                        + "   " + o.getFinalPrice() + " ILS   [" + o.getStatus() + "]");
            }
        }
        popupList("Orders of " + c.getFirstName() + " " + c.getLastName(), lines);
    }

    private static void showRestaurants(DeliveryDataBase system, Customer c) {
        ArrayList<Restaurant> rests = system.getRestaurantsByCustomer().get(c.getCustomerCode());
        popupList("Restaurants " + c.getFirstName() + " ordered from", restaurantLines(rests));
    }

    private static void showPremium(DeliveryDataBase system, Customer c) {
        ArrayList<Restaurant> rests = system.getPremiumRestaurantsForCustomer(c);
        popupList("Premium restaurants " + c.getFirstName() + " ordered from", restaurantLines(rests));
    }

    private static List<String> restaurantLines(ArrayList<Restaurant> rests) {
        List<String> lines = new ArrayList<>();
        if (rests != null) {
            for (Restaurant r : rests) {
                lines.add(r.getName() + "   (#" + r.getRestaurantCode()
                        + ", " + r.getCuisineType() + ")");
            }
        }
        return lines;
    }

    // ===================================================================
    //  Add / Update form (one modal window used for both)
    // ===================================================================

    /**
     * Opens the add/edit form. When {@code existing} is null it adds a new
     * customer; otherwise it edits that customer in place (the code field is
     * locked, since the code is the customer's key).
     */
    private static void openForm(FoodDeliveryApp app, ObservableList<Customer> data, Customer existing) {
        boolean editing = existing != null;

        TextField code    = new TextField(editing ? String.valueOf(existing.getCustomerCode()) : "");
        TextField first   = new TextField(editing ? existing.getFirstName() : "");
        TextField last    = new TextField(editing ? existing.getLastName() : "");
        TextField street  = new TextField(editing ? existing.getStreet() : "");
        TextField city    = new TextField(editing ? existing.getCity() : "");
        TextField zip     = new TextField(editing ? existing.getZipCode() : "");
        TextField phone   = new TextField(editing ? existing.getPhone() : "");
        TextField email   = new TextField(editing ? existing.getEmail() : "");
        TextField balance = new TextField(editing ? String.valueOf(existing.getCreditBalance()) : "0.0");
        code.setDisable(editing); // never change a key

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(8);
        g.setPadding(new Insets(16));
        addRow(g, 0, "Code:", code);
        addRow(g, 1, "First name:", first);
        addRow(g, 2, "Last name:", last);
        addRow(g, 3, "Street:", street);
        addRow(g, 4, "City:", city);
        addRow(g, 5, "Zip code:", zip);
        addRow(g, 6, "Phone:", phone);
        addRow(g, 7, "Email:", email);
        addRow(g, 8, "Balance:", balance);

        Button save = new Button(editing ? "Save changes" : "Add customer");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox(10, save, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(12,
                new Label(editing ? "Update customer" : "New customer"), g, buttons);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(editing ? "Update customer" : "Add customer");
        stage.setScene(new Scene(box));
        cancel.setOnAction(e -> stage.close());

        save.setOnAction(e -> {
            // ---- validate ----
            String err = validate(editing, code.getText(), first.getText(), last.getText(),
                    street.getText(), city.getText(), zip.getText(), phone.getText(),
                    email.getText(), balance.getText());
            if (err != null) {
                UiHelper.error("Invalid input", err);
                return;
            }

            if (editing) {
                existing.setFirstName(first.getText().trim());
                existing.setLastName(last.getText().trim());
                existing.setStreet(street.getText().trim());
                existing.setCity(city.getText().trim());
                existing.setZipCode(zip.getText().trim());
                existing.setPhone(phone.getText().trim());
                existing.setEmail(email.getText().trim());
                existing.setCreditBalance(Double.parseDouble(balance.getText().trim()));
                data.setAll(app.getSystem().getCustomers()); // redraw
                stage.close();
                UiHelper.info("Updated", "Customer details were updated.");
            } else {
                Customer c = new Customer(
                        Integer.parseInt(code.getText().trim()),
                        first.getText().trim(), last.getText().trim(), street.getText().trim(),
                        city.getText().trim(), zip.getText().trim(), phone.getText().trim(),
                        email.getText().trim(), Double.parseDouble(balance.getText().trim()));
                if (app.getSystem().addCustomer(c)) {
                    data.setAll(app.getSystem().getCustomers());
                    stage.close();
                    UiHelper.info("Added", "New customer added.");
                } else {
                    UiHelper.error("Duplicate code",
                            "A customer with code " + code.getText().trim() + " already exists.");
                }
            }
        });

        stage.showAndWait();
    }

    /** Returns an error message, or null if every field is valid. */
    private static String validate(boolean editing, String code, String first, String last,
            String street, String city, String zip, String phone, String email, String balance) {
        if (!editing) {
            try {
                if (Integer.parseInt(code.trim()) <= 0) {
                    return "Code must be a positive whole number.";
                }
            } catch (NumberFormatException ex) {
                return "Code must be a whole number.";
            }
        }
        if (!InputHelper.isLettersAndSpaces(first.trim())) {
            return "First name must contain letters only.";
        }
        if (!InputHelper.isLettersAndSpaces(last.trim())) {
            return "Last name must contain letters only.";
        }
        if (!InputHelper.isValidString(street)) {
            return "Street cannot be empty.";
        }
        if (!InputHelper.isValidString(city)) {
            return "City cannot be empty.";
        }
        if (!InputHelper.isDigitsOnly(zip.trim(), 4, 9)) {
            return "Zip code must be 4-9 digits.";
        }
        if (!InputHelper.isValidPhone(phone.trim())) {
            return "Phone number is not valid.";
        }
        if (!InputHelper.isValidEmail(email.trim())) {
            return "Email address is not valid.";
        }
        try {
            if (Double.parseDouble(balance.trim()) < 0) {
                return "Balance cannot be negative.";
            }
        } catch (NumberFormatException ex) {
            return "Balance must be a number.";
        }
        return null;
    }

    // ===================================================================
    //  Small shared UI helpers
    // ===================================================================

    private static <T> TableColumn<Customer, T> column(String title, String property, double width) {
        TableColumn<Customer, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }

    private static void addRow(GridPane g, int row, String label, TextField field) {
        g.add(new Label(label), 0, row);
        g.add(field, 1, row);
    }

    /** Runs an action on the selected customer, or warns if none is selected. */
    private static void withSelected(TableView<Customer> table, java.util.function.Consumer<Customer> action) {
        Customer sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UiHelper.error("No selection", "Select a customer in the table first.");
        } else {
            action.accept(sel);
        }
    }

    /** Shows a list of text lines in a small modal window. */
    private static void popupList(String title, List<String> lines) {
        ListView<String> list = new ListView<>();
        if (lines.isEmpty()) {
            list.getItems().add("(none)");
        } else {
            list.getItems().addAll(lines);
        }
        Button close = new Button("Close");
        VBox box = new VBox(10, new Label(title), list, close);
        box.setPadding(new Insets(16));
        box.setAlignment(Pos.CENTER);

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle(title);
        stage.setScene(new Scene(box, 460, 380));
        close.setOnAction(e -> stage.close());
        stage.showAndWait();
    }
}