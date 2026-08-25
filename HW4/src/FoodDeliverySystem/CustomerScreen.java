package FoodDeliverySystem;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
 * Customer personal screen (Assignment 4, Part טו - customer).
 *
 * The logged-in customer can: view details, place an order, view their orders,
 * update contact info, load/withdraw money, see their balance, see the
 * restaurants (and premium restaurants) they ordered from, and look up a
 * restaurant by code. Ordering and money operations go through the model
 * ({@link DeliveryDataBase#placeOrder}, {@link Customer#topUp}/{@code withdraw})
 * and report success or a caught error via {@link UiHelper}.
 */
public final class CustomerScreen {

    private CustomerScreen() {
    }

    public static Parent build(FoodDeliveryApp app, Customer customer) {
        DeliveryDataBase system = app.getSystem();

        Label title = new Label("Welcome, " + customer.getFirstName() + " " + customer.getLastName());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        Label balanceLabel = new Label();
        balanceLabel.setStyle("-fx-font-size: 14px;");
        Runnable refreshBalance = () -> balanceLabel.setText(
                "Customer #" + customer.getCustomerCode() + "    Balance: " + customer.getCreditBalance() + " ILS");
        refreshBalance.run();

        Button details   = wide("View my details");
        Button order      = wide("Place new order");
        Button myOrders   = wide("View my orders");
        Button contact    = wide("Update contact info");
        Button load       = wide("Load money");
        Button withdraw   = wide("Withdraw money");
        Button balance    = wide("Show balance");
        Button restsFrom  = wide("Restaurants I ordered from");
        Button premium    = wide("Premium restaurants");
        Button searchRest = wide("Find restaurant by code");
        Button logout     = wide("Logout");

        details.setOnAction(e -> UiHelper.info("My details", detailsText(customer)));
        order.setOnAction(e -> placeOrder(app, customer, refreshBalance));
        myOrders.setOnAction(e -> showMyOrders(system, customer));
        contact.setOnAction(e -> updateContact(customer));
        load.setOnAction(e -> changeMoney(customer, true, refreshBalance));
        withdraw.setOnAction(e -> changeMoney(customer, false, refreshBalance));
        balance.setOnAction(e -> UiHelper.info("Balance",
                "Your current balance is " + customer.getCreditBalance() + " ILS."));
        restsFrom.setOnAction(e -> showRestaurants(system, customer, false));
        premium.setOnAction(e -> showRestaurants(system, customer, true));
        searchRest.setOnAction(e -> findRestaurant(system));
        logout.setOnAction(e -> app.showOpeningScreen());

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);
        grid.add(details, 0, 0);    grid.add(order, 1, 0);
        grid.add(myOrders, 0, 1);   grid.add(contact, 1, 1);
        grid.add(load, 0, 2);       grid.add(withdraw, 1, 2);
        grid.add(balance, 0, 3);    grid.add(searchRest, 1, 3);
        grid.add(restsFrom, 0, 4);  grid.add(premium, 1, 4);

        VBox box = new VBox(18, title, balanceLabel, grid, logout);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24));
        return box;
    }

    // ===================================================================
    //  Operations
    // ===================================================================

    private static String detailsText(Customer c) {
        return "Code: " + c.getCustomerCode()
                + "\nName: " + c.getFirstName() + " " + c.getLastName()
                + "\nAddress: " + c.getStreet() + ", " + c.getCity() + " " + c.getZipCode()
                + "\nPhone: " + c.getPhone()
                + "\nEmail: " + c.getEmail()
                + "\nBalance: " + c.getCreditBalance() + " ILS";
    }

    private static void showMyOrders(DeliveryDataBase system, Customer c) {
        ArrayList<Order> orders = system.getOrdersByCustomer().get(c.getCustomerCode());
        List<String> lines = new ArrayList<>();
        if (orders != null) {
            for (Order o : orders) {
                lines.add("#" + o.getOrderCode() + "   restaurant " + o.getRestaurantCode()
                        + "   " + o.getFinalPrice() + " ILS   [" + o.getStatus() + "]");
            }
        }
        UiHelper.popupList("My orders", lines);
    }

    private static void showRestaurants(DeliveryDataBase system, Customer c, boolean premiumOnly) {
        ArrayList<Restaurant> rests = premiumOnly
                ? system.getPremiumRestaurantsForCustomer(c)
                : system.getRestaurantsByCustomer().get(c.getCustomerCode());
        List<String> lines = new ArrayList<>();
        if (rests != null) {
            for (Restaurant r : rests) {
                lines.add(r.getName() + "   (#" + r.getRestaurantCode() + ", " + r.getCuisineType() + ")");
            }
        }
        UiHelper.popupList(premiumOnly ? "Premium restaurants I ordered from"
                                       : "Restaurants I ordered from", lines);
    }

    private static void findRestaurant(DeliveryDataBase system) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle("Find restaurant");
        dialog.setContentText("Restaurant code:");
        dialog.showAndWait().ifPresent(input -> {
            int code;
            try {
                code = Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid code", "Please enter a whole number.");
                return;
            }
            Restaurant r = system.findRestaurantByCode(code);
            if (r == null) {
                UiHelper.error("Not found", "No restaurant with code " + code + ".");
            } else {
                UiHelper.info("Restaurant #" + code, restaurantDetails(r));
            }
        });
    }

    private static String restaurantDetails(Restaurant r) {
        StringBuilder sb = new StringBuilder();
        sb.append("Name: ").append(r.getName())
          .append("\nCuisine: ").append(r.getCuisineType())
          .append("\nRating: ").append(r.getRating())
          .append("\nStatus: ").append(r.isOpen() ? "Open" : "Closed")
          .append("\nDelivery fee: ").append(r.getBaseDeliveryFee());
        if (r instanceof FastFoodRestaurant) {
            FastFoodRestaurant f = (FastFoodRestaurant) r;
            sb.append("\nType: Fast Food")
              .append("\nAvg prep time: ").append(f.getAvgPrepTimeMinutes()).append(" min")
              .append("\nFast delivery surcharge: ").append(f.getFastDeliverySurcharge());
        } else if (r instanceof PremiumRestaurant) {
            PremiumRestaurant p = (PremiumRestaurant) r;
            sb.append("\nType: Premium")
              .append("\nMinimum order: ").append(p.getMinimumOrderAmount())
              .append("\nExtra commission %: ").append(p.getExtraCommissionPercent());
        } else {
            sb.append("\nType: Basic");
        }
        return sb.toString();
    }

    private static void changeMoney(Customer c, boolean loading, Runnable refreshBalance) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setHeaderText(null);
        dialog.setTitle(loading ? "Load money" : "Withdraw money");
        dialog.setContentText("Amount:");
        dialog.showAndWait().ifPresent(input -> {
            double amount;
            try {
                amount = Double.parseDouble(input.trim());
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid amount", "Please enter a number.");
                return;
            }
            boolean ok = loading ? c.topUp(amount) : c.withdraw(amount);
            if (ok) {
                refreshBalance.run();
                UiHelper.info("Done", (loading ? "Loaded " : "Withdrew ") + amount
                        + " ILS. New balance: " + c.getCreditBalance() + " ILS.");
            } else {
                UiHelper.error("Rejected", loading
                        ? "Amount must be positive."
                        : "Amount must be positive and not exceed your balance.");
            }
        });
    }

    // ---- place order (modal form) ----

    private static void placeOrder(FoodDeliveryApp app, Customer customer, Runnable refreshBalance) {
        DeliveryDataBase system = app.getSystem();

        ComboBox<Restaurant> restBox = new ComboBox<>();
        for (Restaurant r : system.getRestaurants()) {
            if (r.isOpen()) {
                restBox.getItems().add(r); // only open restaurants take orders
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
        g.add(new Label("Restaurant:"), 0, 0);  g.add(restBox, 1, 0);
        g.add(new Label("Order amount:"), 0, 1); g.add(amount, 1, 1);
        g.add(new Label("Day:"), 0, 2);   g.add(day, 1, 2);
        g.add(new Label("Month:"), 0, 3); g.add(month, 1, 3);
        g.add(new Label("Year:"), 0, 4);  g.add(year, 1, 4);

        Button submit = new Button("Place order");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox(10, submit, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(12, new Label("New order"), g, buttons);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Place order");
        stage.setScene(new Scene(box));
        cancel.setOnAction(e -> stage.close());

        submit.setOnAction(e -> {
            Restaurant r = restBox.getValue();
            if (r == null) {
                UiHelper.error("No restaurant", "There are no open restaurants to order from.");
                return;
            }
            double amt;
            int d;
            int m;
            int y;
            try {
                amt = Double.parseDouble(amount.getText().trim());
                d = Integer.parseInt(day.getText().trim());
                m = Integer.parseInt(month.getText().trim());
                y = Integer.parseInt(year.getText().trim());
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid input", "Amount and date must be numbers.");
                return;
            }
            if (amt <= 0) {
                UiHelper.error("Invalid amount", "Order amount must be positive.");
                return;
            }
            // The model enforces premium-minimum and balance, throwing on failure.
            try {
                Order placed = system.placeOrder(customer, r, d, m, y, amt);
                refreshBalance.run();
                stage.close();
                UiHelper.info("Order placed",
                        "Order #" + placed.getOrderCode() + " created. Final price: "
                        + placed.getFinalPrice() + " ILS.");
            } catch (InsufficientBalanceException ex) {
                UiHelper.error("Order rejected", ex.getMessage());
            } catch (IllegalArgumentException ex) {
                UiHelper.error("Order rejected", ex.getMessage());
            }
        });

        stage.showAndWait();
    }

    // ---- update contact info (modal form) ----

    private static void updateContact(Customer c) {
        TextField street = new TextField(c.getStreet());
        TextField city   = new TextField(c.getCity());
        TextField zip    = new TextField(c.getZipCode());
        TextField phone  = new TextField(c.getPhone());
        TextField email  = new TextField(c.getEmail());

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(8);
        g.setPadding(new Insets(16));
        g.add(new Label("Street:"), 0, 0); g.add(street, 1, 0);
        g.add(new Label("City:"), 0, 1);   g.add(city, 1, 1);
        g.add(new Label("Zip:"), 0, 2);    g.add(zip, 1, 2);
        g.add(new Label("Phone:"), 0, 3);  g.add(phone, 1, 3);
        g.add(new Label("Email:"), 0, 4);  g.add(email, 1, 4);

        Button save = new Button("Save");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox(10, save, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(12, new Label("Update contact info"), g, buttons);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Update contact info");
        stage.setScene(new Scene(box));
        cancel.setOnAction(e -> stage.close());

        save.setOnAction(e -> {
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
            c.setStreet(street.getText().trim());
            c.setCity(city.getText().trim());
            c.setZipCode(zip.getText().trim());
            c.setPhone(phone.getText().trim());
            c.setEmail(email.getText().trim());
            stage.close();
            UiHelper.info("Updated", "Your contact info was updated.");
        });

        stage.showAndWait();
    }

    private static Button wide(String text) {
        Button b = new Button(text);
        b.setPrefWidth(220);
        b.setPrefHeight(40);
        b.setStyle("-fx-font-size: 13px;");
        return b;
    }
}