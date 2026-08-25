package FoodDeliverySystem;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Small static helpers for the pop-up messages the assignment requires:
 * success / error feedback (Part י) and exception display (Part יב).
 *
 * Centralising them means every screen reports outcomes the same way, and a
 * caught exception becomes a visible message instead of a crash - the program
 * keeps running after any handled error.
 */
public final class UiHelper {

    private UiHelper() {
        // utility class - never instantiated
    }

    /** Green-path confirmation (e.g. "Data saved successfully"). */
    public static void info(String header, String content) {
        show(AlertType.INFORMATION, "Success", header, content);
    }

    /** Error message (e.g. a caught exception's getMessage()). */
    public static void error(String header, String content) {
        show(AlertType.ERROR, "Error", header, content);
    }

    private static void show(AlertType type, String title, String header, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Shows a list of text lines in a small modal window. Used by the
     * management screens for "show X" results (a customer's orders, open
     * restaurants, ...). An empty list shows "(none)" rather than a blank box.
     */
    public static void popupList(String title, List<String> lines) {
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