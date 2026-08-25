package FoodDeliverySystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

/**
 * Restaurant-manager personal screen (Assignment 4, Part טו - manager).
 *
 * The logged-in manager can: list managed restaurants, add a customer, place an
 * order for one of their restaurants, assign a rider to an order, view a
 * restaurant's orders, list open restaurants by cuisine, update a restaurant's
 * rating, open/close a restaurant, and see basic per-restaurant reports.
 * Everything operates through {@link DeliveryDataBase}.
 */
public final class RestAdminScreen {

    private RestAdminScreen() {
    }

    public static Parent build(FoodDeliveryApp app, RestAdmin admin) {
        Label title = new Label("Manager: " + admin.getManagerName());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");
        Label sub = new Label("Username: " + admin.getUsername() + "    Restaurants: " + admin.getCount());
        sub.setStyle("-fx-font-size: 14px;");

        Button myRests   = wide("My restaurants");
        Button addCust    = wide("Add customer");
        Button addOrder   = wide("Add order");
        Button assign     = wide("Assign rider to order");
        Button restOrders = wide("Orders of a restaurant");
        Button byCuisine  = wide("Open restaurants by cuisine");
        Button rating     = wide("Update rating");
        Button openClose  = wide("Open / close restaurant");
        Button reports    = wide("Basic reports");
        Button logout     = wide("Logout");

        myRests.setOnAction(e -> showMyRestaurants(admin));
        addCust.setOnAction(e -> addCustomer(app));
        addOrder.setOnAction(e -> addOrder(app, admin));
        assign.setOnAction(e -> assignRider(app));
        restOrders.setOnAction(e -> showRestaurantOrders(app, admin));
        byCuisine.setOnAction(e -> openByCuisine(app));
        rating.setOnAction(e -> updateRating(admin));
        openClose.setOnAction(e -> openClose(admin));
        reports.setOnAction(e -> basicReports(app, admin));
        logout.setOnAction(e -> app.showOpeningScreen());

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.add(myRests, 0, 0);    grid.add(addCust, 1, 0);
        grid.add(addOrder, 0, 1);   grid.add(assign, 1, 1);
        grid.add(restOrders, 0, 2); grid.add(byCuisine, 1, 2);
        grid.add(rating, 0, 3);     grid.add(openClose, 1, 3);
        grid.add(reports, 0, 4);

        VBox box = new VBox(16, title, sub, grid, logout);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(22));
        return box;
    }

    // ===================================================================
    //  Simple views
    // ===================================================================

    private static void showMyRestaurants(RestAdmin admin) {
        List<String> lines = new ArrayList<>();
        for (Restaurant r : admin.getManagedRestaurants()) {
            lines.add(r.getName() + "   (#" + r.getRestaurantCode() + ", " + r.getCuisineType()
                    + ", rating " + r.getRating() + ", " + (r.isOpen() ? "Open" : "Closed") + ")");
        }
        UiHelper.popupList("My restaurants", lines);
    }

    private static void showRestaurantOrders(FoodDeliveryApp app, RestAdmin admin) {
        Restaurant r = pickManaged(admin, "Orders of a restaurant");
        if (r == null) {
            return;
        }
        List<String> lines = new ArrayList<>();
        for (Order o : app.getSystem().getOrders()) {
            if (o.getRestaurantCode() == r.getRestaurantCode()) {
                lines.add("#" + o.getOrderCode() + "   customer " + o.getCustomerCode()
                        + "   " + o.getFinalPrice() + " ILS   [" + o.getStatus() + "]");
            }
        }
        UiHelper.popupList("Orders of " + r.getName(), lines);
    }

    private static void openByCuisine(FoodDeliveryApp app) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Open restaurants by cuisine");
        dialog.setContentText("Cuisine type:");
        dialog.showAndWait().ifPresent(input -> {
            ArrayList<Restaurant> found = app.getSystem().getOpenRestaurantsByCuisine(input.trim());
            List<String> lines = new ArrayList<>();
            for (Restaurant r : found) {
                lines.add(r.getName() + "   (#" + r.getRestaurantCode() + ", rating " + r.getRating() + ")");
            }
            UiHelper.popupList("Open " + input.trim() + " restaurants", lines);
        });
    }

    private static void basicReports(FoodDeliveryApp app, RestAdmin admin) {
        List<String> lines = new ArrayList<>();
        for (Restaurant r : admin.getManagedRestaurants()) {
            int count = 0;
            double revenue = 0.0;
            for (Order o : app.getSystem().getOrders()) {
                if (o.getRestaurantCode() == r.getRestaurantCode()) {
                    count++;
                    revenue += o.getFinalPrice();
                }
            }
            lines.add(r.getName() + ": " + count + " orders, revenue " + revenue
                    + " ILS, rating " + r.getRating() + ", " + (r.isOpen() ? "Open" : "Closed"));
        }
        UiHelper.popupList("Basic reports - my restaurants", lines);
    }

    // ===================================================================
    //  Rating / open-close (on a managed restaurant)
    // ===================================================================

    private static void updateRating(RestAdmin admin) {
        Restaurant r = pickManaged(admin, "Update rating");
        if (r == null) {
            return;
        }
        TextInputDialog dialog = new TextInputDialog(String.valueOf(r.getRating()));
        dialog.setHeaderText(null);
        dialog.setTitle("Update rating");
        dialog.setContentText("New rating for " + r.getName() + ":");
        dialog.showAndWait().ifPresent(input -> {
            double rating;
            try {
                rating = Double.parseDouble(input.trim());
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid rating", "Please enter a number.");
                return;
            }
            if (!InputHelper.isValidRating(rating)) {
                UiHelper.error("Invalid rating", "Rating must be between 0 and 5.");
                return;
            }
            r.setRating(rating);
            UiHelper.info("Updated", r.getName() + " rating is now " + rating + ".");
        });
    }

    private static void openClose(RestAdmin admin) {
        Restaurant r = pickManaged(admin, "Open / close restaurant");
        if (r == null) {
            return;
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Open", "Open", "Closed");
        dialog.setHeaderText(null);
        dialog.setTitle("Set status");
        dialog.setContentText("Set " + r.getName() + " to:");
        dialog.showAndWait().ifPresent(choice -> {
            r.setOpen("Open".equals(choice));
            UiHelper.info("Updated", r.getName() + " is now " + (r.isOpen() ? "open" : "closed") + ".");
        });
    }

    // ===================================================================
    //  Assign rider
    // ===================================================================

    private static void assignRider(FoodDeliveryApp app) {
        DeliveryDataBase system = app.getSystem();

        TextInputDialog codeDialog = new TextInputDialog();
        codeDialog.setHeaderText(null);
        codeDialog.setTitle("Assign rider");
        codeDialog.setContentText("Order code:");
        Optional<String> codeInput = codeDialog.showAndWait();
        if (codeInput.isEmpty()) {
            return;
        }
        int code;
        try {
            code = Integer.parseInt(codeInput.get().trim());
        } catch (NumberFormatException ex) {
            UiHelper.error("Invalid code", "Please enter a whole number.");
            return;
        }
        if (system.findOrderByCode(code) == null) {
            UiHelper.error("Not found", "No order with code " + code + ".");
            return;
        }

        // available riders only
        List<Rider> available = new ArrayList<>();
        List<String> labels = new ArrayList<>();
        for (Rider r : system.getRiders()) {
            if (r.isAvailable()) {
                available.add(r);
                labels.add(r.getId() + "  " + r.getFullName());
            }
        }
        if (available.isEmpty()) {
            UiHelper.error("No riders", "There are no available riders right now.");
            return;
        }
        ChoiceDialog<String> riderDialog = new ChoiceDialog<>(labels.get(0), labels);
        riderDialog.setHeaderText(null);
        riderDialog.setTitle("Choose rider");
        riderDialog.setContentText("Rider:");
        Optional<String> chosen = riderDialog.showAndWait();
        if (chosen.isEmpty()) {
            return;
        }
        Rider rider = available.get(labels.indexOf(chosen.get()));
        try {
            system.assignRiderToOrderChecked(rider.getId(), code);
            UiHelper.info("Assigned", rider.getFullName() + " assigned to order #" + code + ".");
        } catch (DeliveryPersonUnavailableException ex) {
            UiHelper.error("Assign failed", ex.getMessage());
        }
    }

    // ===================================================================
    //  Add order (managed restaurants only)
    // ===================================================================

    private static void addOrder(FoodDeliveryApp app, RestAdmin admin) {
        DeliveryDataBase system = app.getSystem();

        TextField custCode = new TextField();

        ComboBox<Restaurant> restBox = new ComboBox<>();
        for (Restaurant r : admin.getManagedRestaurants()) {
            if (r.isOpen()) {
                restBox.getItems().add(r);
            }
        }
        restBox.setConverter(new StringConverter<Restaurant>() {
            @Override public String toString(Restaurant r) {
                return r == null ? "" : "#" + r.getRestaurantCode() + "  " + r.getName();
            }
            @Override public Restaurant fromString(String s) { return null; }
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
                UiHelper.error("No restaurant", "You have no open restaurants to order from.");
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
    //  Add customer
    // ===================================================================

    private static void addCustomer(FoodDeliveryApp app) {
        TextField code    = new TextField();
        TextField first   = new TextField();
        TextField last    = new TextField();
        TextField street  = new TextField();
        TextField city    = new TextField();
        TextField zip     = new TextField();
        TextField phone   = new TextField();
        TextField email   = new TextField();
        TextField balance = new TextField("0.0");

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(8);
        g.setPadding(new Insets(16));
        int row = 0;
        g.add(new Label("Code:"), 0, row);       g.add(code, 1, row++);
        g.add(new Label("First name:"), 0, row); g.add(first, 1, row++);
        g.add(new Label("Last name:"), 0, row);  g.add(last, 1, row++);
        g.add(new Label("Street:"), 0, row);     g.add(street, 1, row++);
        g.add(new Label("City:"), 0, row);       g.add(city, 1, row++);
        g.add(new Label("Zip:"), 0, row);        g.add(zip, 1, row++);
        g.add(new Label("Phone:"), 0, row);      g.add(phone, 1, row++);
        g.add(new Label("Email:"), 0, row);      g.add(email, 1, row++);
        g.add(new Label("Balance:"), 0, row);    g.add(balance, 1, row++);

        Button save = new Button("Add customer");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox(10, save, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(12, new Label("New customer"), g, buttons);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add customer");
        stage.setScene(new Scene(box));
        cancel.setOnAction(e -> stage.close());

        save.setOnAction(e -> {
            int codeVal;
            double balVal;
            try {
                codeVal = Integer.parseInt(code.getText().trim());
                if (codeVal <= 0) {
                    UiHelper.error("Invalid input", "Code must be a positive whole number.");
                    return;
                }
                balVal = Double.parseDouble(balance.getText().trim());
                if (balVal < 0) {
                    UiHelper.error("Invalid input", "Balance cannot be negative.");
                    return;
                }
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid input", "Code and balance must be numbers.");
                return;
            }
            if (!InputHelper.isLettersAndSpaces(first.getText().trim())
                    || !InputHelper.isLettersAndSpaces(last.getText().trim())) {
                UiHelper.error("Invalid input", "Names must contain letters only.");
                return;
            }
            if (!InputHelper.isValidString(street.getText()) || !InputHelper.isValidString(city.getText())) {
                UiHelper.error("Invalid input", "Street and city cannot be empty.");
                return;
            }
            if (!InputHelper.isDigitsOnly(zip.getText().trim(), 4, 9)) {
                UiHelper.error("Invalid input", "Zip code must be 4-9 digits.");
                return;
            }
            if (!InputHelper.isValidPhone(phone.getText().trim())) {
                UiHelper.error("Invalid input", "Phone number is not valid.");
                return;
            }
            if (!InputHelper.isValidEmail(email.getText().trim())) {
                UiHelper.error("Invalid input", "Email address is not valid.");
                return;
            }
            Customer c = new Customer(codeVal, first.getText().trim(), last.getText().trim(),
                    street.getText().trim(), city.getText().trim(), zip.getText().trim(),
                    phone.getText().trim(), email.getText().trim(), balVal);
            if (app.getSystem().addCustomer(c)) {
                stage.close();
                UiHelper.info("Added", "New customer added.");
            } else {
                UiHelper.error("Duplicate code", "A customer with code " + codeVal + " already exists.");
            }
        });

        stage.showAndWait();
    }

    // ===================================================================
    //  Helpers
    // ===================================================================

    /** Lets the manager choose one of the restaurants they manage. */
    private static Restaurant pickManaged(RestAdmin admin, String titleText) {
        ArrayList<Restaurant> managed = admin.getManagedRestaurants();
        if (managed.isEmpty()) {
            UiHelper.error("No restaurants", "You manage no restaurants.");
            return null;
        }
        List<String> labels = new ArrayList<>();
        for (Restaurant r : managed) {
            labels.add("#" + r.getRestaurantCode() + "  " + r.getName());
        }
        ChoiceDialog<String> dialog = new ChoiceDialog<>(labels.get(0), labels);
        dialog.setHeaderText(null);
        dialog.setTitle(titleText);
        dialog.setContentText("Restaurant:");
        Optional<String> chosen = dialog.showAndWait();
        if (chosen.isEmpty()) {
            return null;
        }
        return managed.get(labels.indexOf(chosen.get()));
    }

    private static Button wide(String text) {
        Button b = new Button(text);
        b.setPrefWidth(220);
        b.setPrefHeight(40);
        b.setStyle("-fx-font-size: 13px;");
        return b;
    }
}