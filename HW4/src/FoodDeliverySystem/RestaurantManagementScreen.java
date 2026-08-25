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
 * Restaurant management screen (Assignment 4, Part ד).
 *
 * Shows every restaurant in a {@link TableView} (the second of the three
 * required tables, Part י) and offers the full Part ד action set:
 *   - show all (the table itself);
 *   - search by code (uses the throwing finder so
 *     {@link RestaurantNotFoundException} is handled in the UI, Part יא);
 *   - add a new restaurant, POLYMORPHICALLY - a regular {@link Restaurant}, a
 *     {@link FastFoodRestaurant}, or a {@link PremiumRestaurant} - chosen from a
 *     ComboBox whose extra fields change with the type;
 *   - update the selected restaurant's rating;
 *   - open / close the selected restaurant;
 *   - show restaurants by type (regular / fast food / premium, via instanceof);
 *   - show open restaurants only.
 *
 * Mirrors {@link CustomerManagementScreen}: all real work goes through
 * {@link DeliveryDataBase}; the screen only collects input and shows results.
 * The table is backed by an {@link ObservableList}, so one
 * {@code data.setAll(system.getRestaurants())} redraws it after any change.
 */
public final class RestaurantManagementScreen {

    private RestaurantManagementScreen() {
    }

    public static Parent build(FoodDeliveryApp app) {
        DeliveryDataBase system = app.getSystem();

        // ---- the table (Part י TableView #2) ----
        ObservableList<Restaurant> data = FXCollections.observableArrayList(system.getRestaurants());
        TableView<Restaurant> table = new TableView<>(data);
        table.getColumns().add(column("Code",        "restaurantCode",  70));
        table.getColumns().add(column("Name",        "name",            150));
        table.getColumns().add(column("Cuisine",     "cuisineType",     120));
        table.getColumns().add(column("Rating",      "rating",          70));
        table.getColumns().add(column("Open",        "open",            60));
        table.getColumns().add(column("Delivery Fee", "baseDeliveryFee", 100));
        table.setPrefHeight(360);

        // ---- action buttons ----
        Button searchBtn   = new Button("Search by code");
        Button addBtn      = new Button("Add new");
        Button ratingBtn   = new Button("Update rating");
        Button openBtn     = new Button("Open");
        Button closeBtn    = new Button("Close");
        Button byTypeBtn   = new Button("Show by type");
        Button openOnlyBtn = new Button("Show open only");
        Button backBtn     = new Button("\u2190 Back");

        searchBtn.setOnAction(e -> searchByCode(system, table));
        addBtn.setOnAction(e -> openAddForm(system, data));
        ratingBtn.setOnAction(e -> withSelected(table, r -> updateRating(system, data, r)));
        openBtn.setOnAction(e -> withSelected(table, r -> setOpenState(system, data, r, true)));
        closeBtn.setOnAction(e -> withSelected(table, r -> setOpenState(system, data, r, false)));
        byTypeBtn.setOnAction(e -> showByType(system));
        openOnlyBtn.setOnAction(e -> showOpenOnly(system));
        backBtn.setOnAction(e -> app.showAdminHub());

        HBox row1 = new HBox(10, searchBtn, addBtn, ratingBtn);
        HBox row2 = new HBox(10, openBtn, closeBtn, byTypeBtn, openOnlyBtn);
        row1.setAlignment(Pos.CENTER);
        row2.setAlignment(Pos.CENTER);

        Label title = new Label("Restaurants");
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #1F3864;");

        VBox box = new VBox(14, title, table, row1, row2, backBtn);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        return box;
    }

    // ===================================================================
    //  Operations
    // ===================================================================

    /** Asks for a code and selects/scrolls to that restaurant, or reports not-found. */
    private static void searchByCode(DeliveryDataBase system, TableView<Restaurant> table) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Search restaurant");
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
            // Throwing finder -> RestaurantNotFoundException handled in the UI (Part יא).
            try {
                Restaurant found = system.findRestaurantByCodeOrThrow(code);
                table.getSelectionModel().select(found);
                table.scrollTo(found);
                UiHelper.info("Found", found.getName()
                        + "  (rating: " + found.getRating()
                        + ", " + (found.isOpen() ? "open" : "closed") + ")");
            } catch (RestaurantNotFoundException ex) {
                UiHelper.error("Not found", ex.getMessage());
            }
        });
    }

    /** Prompts for a new rating (0-5) for the selected restaurant and applies it. */
    private static void updateRating(DeliveryDataBase system, ObservableList<Restaurant> data, Restaurant r) {
        TextInputDialog dialog = new TextInputDialog(String.valueOf(r.getRating()));
        dialog.setTitle("Update rating");
        dialog.setHeaderText(r.getName());
        dialog.setContentText("New rating (0-5):");
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
            data.setAll(system.getRestaurants()); // redraw
            UiHelper.info("Updated", r.getName() + " now rated " + rating + ".");
        });
    }

    /** Opens or closes the selected restaurant and refreshes the table. */
    private static void setOpenState(DeliveryDataBase system, ObservableList<Restaurant> data,
            Restaurant r, boolean open) {
        if (r.isOpen() == open) {
            UiHelper.info("No change", r.getName() + " is already "
                    + (open ? "open." : "closed."));
            return;
        }
        r.setOpen(open);
        data.setAll(system.getRestaurants()); // redraw
        UiHelper.info(open ? "Opened" : "Closed",
                r.getName() + " is now " + (open ? "open." : "closed."));
    }

    /** Lets the user pick a restaurant type and lists every restaurant of that type. */
    private static void showByType(DeliveryDataBase system) {
        ChoiceDialog<String> dialog = new ChoiceDialog<>("Regular", "Regular", "Fast Food", "Premium");
        dialog.setTitle("Show by type");
        dialog.setHeaderText(null);
        dialog.setContentText("Restaurant type:");
        dialog.showAndWait().ifPresent(type -> {
            List<String> lines = new ArrayList<>();
            for (Restaurant r : system.getRestaurants()) {
                if (matchesType(r, type)) {
                    lines.add(restaurantLine(r));
                }
            }
            UiHelper.popupList(type + " restaurants", lines);
        });
    }

    /** Lists only the restaurants currently open. */
    private static void showOpenOnly(DeliveryDataBase system) {
        List<String> lines = new ArrayList<>();
        for (Restaurant r : system.getRestaurants()) {
            if (r.isOpen()) {
                lines.add(restaurantLine(r));
            }
        }
        UiHelper.popupList("Open restaurants", lines);
    }

    /** Maps the chosen label to the matching class in the restaurant hierarchy. */
    private static boolean matchesType(Restaurant r, String type) {
        switch (type) {
            case "Fast Food":
                return r instanceof FastFoodRestaurant;
            case "Premium":
                return r instanceof PremiumRestaurant;
            case "Regular":
                // A plain Restaurant that is neither of the two subtypes.
                return !(r instanceof FastFoodRestaurant) && !(r instanceof PremiumRestaurant);
            default:
                return false;
        }
    }

    private static String restaurantLine(Restaurant r) {
        return r.getName() + "   (#" + r.getRestaurantCode()
                + ", " + r.getCuisineType()
                + ", rating " + r.getRating()
                + ", " + (r.isOpen() ? "open" : "closed") + ")";
    }

    // ===================================================================
    //  Add form (polymorphic: Regular / Fast Food / Premium)
    // ===================================================================

    /**
     * Opens the modal add form. A ComboBox selects the restaurant type; the two
     * "extra" fields relabel themselves for the chosen subclass (prep-time and
     * surcharge for fast food; minimum order and commission for premium) and are
     * disabled for a regular restaurant. On save the correct subclass object is
     * constructed, demonstrating polymorphism at creation time.
     */
    private static void openAddForm(DeliveryDataBase system, ObservableList<Restaurant> data) {
        ComboBox<String> typeBox = new ComboBox<>();
        typeBox.getItems().addAll("Regular", "Fast Food", "Premium");
        typeBox.setValue("Regular");

        TextField code    = new TextField();
        TextField name    = new TextField();
        TextField cuisine = new TextField();
        TextField rating  = new TextField("0.0");
        TextField fee     = new TextField("0.0");
        CheckBox  openBox = new CheckBox("Open for orders");
        openBox.setSelected(true);

        Label extra1Label = new Label();
        Label extra2Label = new Label();
        TextField extra1  = new TextField();
        TextField extra2  = new TextField();

        // Relabel / enable the two extra fields to match the selected type.
        Runnable refreshExtras = () -> {
            String type = typeBox.getValue();
            if ("Fast Food".equals(type)) {
                extra1Label.setText("Avg prep time (min):");
                extra2Label.setText("Fast surcharge:");
                setExtrasEnabled(extra1, extra2, true, "0", "0.0");
            } else if ("Premium".equals(type)) {
                extra1Label.setText("Minimum order:");
                extra2Label.setText("Commission (%):");
                setExtrasEnabled(extra1, extra2, true, "0.0", "0.0");
            } else {
                extra1Label.setText("(not used)");
                extra2Label.setText("(not used)");
                setExtrasEnabled(extra1, extra2, false, "", "");
            }
        };
        typeBox.setOnAction(e -> refreshExtras.run());
        refreshExtras.run();

        GridPane g = new GridPane();
        g.setHgap(10);
        g.setVgap(8);
        g.setPadding(new Insets(16));
        addRow(g, 0, "Type:", typeBox);
        addRow(g, 1, "Code:", code);
        addRow(g, 2, "Name:", name);
        addRow(g, 3, "Cuisine:", cuisine);
        addRow(g, 4, "Rating (0-5):", rating);
        addRow(g, 5, "Delivery fee:", fee);
        g.add(openBox, 1, 6);
        g.add(extra1Label, 0, 7);
        g.add(extra1, 1, 7);
        g.add(extra2Label, 0, 8);
        g.add(extra2, 1, 8);

        Button save = new Button("Add restaurant");
        Button cancel = new Button("Cancel");
        HBox buttons = new HBox(10, save, cancel);
        buttons.setAlignment(Pos.CENTER);

        VBox box = new VBox(12, new Label("New restaurant"), g, buttons);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(16));

        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.setTitle("Add restaurant");
        stage.setScene(new Scene(box));
        cancel.setOnAction(e -> stage.close());

        save.setOnAction(e -> {
            Restaurant created = buildFromForm(typeBox.getValue(), code.getText(), name.getText(),
                    cuisine.getText(), rating.getText(), fee.getText(), openBox.isSelected(),
                    extra1.getText(), extra2.getText());
            if (created == null) {
                return; // buildFromForm already showed the specific error
            }
            if (system.addRestaurant(created)) {
                data.setAll(system.getRestaurants());
                stage.close();
                UiHelper.info("Added", "New " + typeBox.getValue().toLowerCase()
                        + " restaurant added.");
            } else {
                UiHelper.error("Duplicate code",
                        "A restaurant with code " + code.getText().trim() + " already exists.");
            }
        });

        stage.showAndWait();
    }

    /**
     * Validates the form fields and constructs the matching restaurant subclass,
     * or shows an error and returns null. Kept separate from the button handler
     * so the validation reads top-to-bottom.
     */
    private static Restaurant buildFromForm(String type, String codeText, String nameText,
            String cuisineText, String ratingText, String feeText, boolean open,
            String extra1Text, String extra2Text) {

        int code;
        try {
            code = Integer.parseInt(codeText.trim());
            if (code <= 0) {
                UiHelper.error("Invalid code", "Code must be a positive whole number.");
                return null;
            }
        } catch (NumberFormatException ex) {
            UiHelper.error("Invalid code", "Code must be a whole number.");
            return null;
        }
        if (!InputHelper.isValidString(nameText)) {
            UiHelper.error("Invalid name", "Name cannot be empty.");
            return null;
        }
        if (!InputHelper.isValidString(cuisineText)) {
            UiHelper.error("Invalid cuisine", "Cuisine cannot be empty.");
            return null;
        }
        double rating;
        try {
            rating = Double.parseDouble(ratingText.trim());
        } catch (NumberFormatException ex) {
            UiHelper.error("Invalid rating", "Rating must be a number.");
            return null;
        }
        if (!InputHelper.isValidRating(rating)) {
            UiHelper.error("Invalid rating", "Rating must be between 0 and 5.");
            return null;
        }
        double fee;
        try {
            fee = Double.parseDouble(feeText.trim());
            if (fee < 0) {
                UiHelper.error("Invalid fee", "Delivery fee cannot be negative.");
                return null;
            }
        } catch (NumberFormatException ex) {
            UiHelper.error("Invalid fee", "Delivery fee must be a number.");
            return null;
        }

        String nm = nameText.trim();
        String cz = cuisineText.trim();

        if ("Fast Food".equals(type)) {
            int prep;
            double surcharge;
            try {
                prep = Integer.parseInt(extra1Text.trim());
                if (prep < 0) {
                    UiHelper.error("Invalid prep time", "Prep time cannot be negative.");
                    return null;
                }
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid prep time", "Prep time must be a whole number.");
                return null;
            }
            try {
                surcharge = Double.parseDouble(extra2Text.trim());
                if (surcharge < 0) {
                    UiHelper.error("Invalid surcharge", "Surcharge cannot be negative.");
                    return null;
                }
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid surcharge", "Surcharge must be a number.");
                return null;
            }
            return new FastFoodRestaurant(code, nm, cz, rating, open, fee, prep, surcharge);
        }

        if ("Premium".equals(type)) {
            double minOrder;
            double commission;
            try {
                minOrder = Double.parseDouble(extra1Text.trim());
                if (minOrder < 0) {
                    UiHelper.error("Invalid minimum", "Minimum order cannot be negative.");
                    return null;
                }
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid minimum", "Minimum order must be a number.");
                return null;
            }
            try {
                commission = Double.parseDouble(extra2Text.trim());
                if (commission < 0) {
                    UiHelper.error("Invalid commission", "Commission cannot be negative.");
                    return null;
                }
            } catch (NumberFormatException ex) {
                UiHelper.error("Invalid commission", "Commission must be a number.");
                return null;
            }
            return new PremiumRestaurant(code, nm, cz, rating, open, fee, minOrder, commission);
        }

        // Regular restaurant - no extra fields.
        return new Restaurant(code, nm, cz, rating, open, fee);
    }

    // ===================================================================
    //  Small shared UI helpers (same pattern as CustomerManagementScreen)
    // ===================================================================

    private static <T> TableColumn<Restaurant, T> column(String title, String property, double width) {
        TableColumn<Restaurant, T> c = new TableColumn<>(title);
        c.setCellValueFactory(new PropertyValueFactory<>(property));
        c.setPrefWidth(width);
        return c;
    }

    private static void addRow(GridPane g, int row, String label, javafx.scene.Node field) {
        g.add(new Label(label), 0, row);
        g.add(field, 1, row);
    }

    private static void setExtrasEnabled(TextField e1, TextField e2, boolean enabled,
            String d1, String d2) {
        e1.setDisable(!enabled);
        e2.setDisable(!enabled);
        e1.setText(d1);
        e2.setText(d2);
    }

    /** Runs an action on the selected restaurant, or warns if none is selected. */
    private static void withSelected(TableView<Restaurant> table,
            java.util.function.Consumer<Restaurant> action) {
        Restaurant sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) {
            UiHelper.error("No selection", "Select a restaurant in the table first.");
        } else {
            action.accept(sel);
        }
    }
}