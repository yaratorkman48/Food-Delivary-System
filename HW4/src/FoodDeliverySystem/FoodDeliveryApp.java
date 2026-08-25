package FoodDeliverySystem;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * JavaFX entry point for the Food Delivery System (Assignment 4, Parts א + ב).
 *
 * Owns the single application window (one {@link Stage}) and the single shared
 * data model (one {@link DeliveryDataBase}, seeded once at startup). Screens are
 * plain JavaFX nodes built by small builder classes (OpeningScreen,
 * AdminLoginScreen, ...). Navigation works by swapping the ROOT node of one
 * reused {@link Scene}, which keeps the window size stable between screens.
 *
 * This class REPLACES Main's console menu as the way the system is operated.
 * Main is kept only for its data-seeding helpers ({@link Main#seedData}) until
 * the Task 11 cleanup.
 */
public class FoodDeliveryApp extends Application {

    private DeliveryDataBase system; // the one shared model
    private Stage stage;             // the one application window

    @Override
    public void start(Stage primaryStage) {
        this.stage = primaryStage;

        // Build the in-memory data exactly as the console version did.
        this.system = new DeliveryDataBase();
        Main.seedData(system);

        stage.setTitle("Food Delivery System");
        stage.setScene(new Scene(OpeningScreen.build(this), 900, 600));
        stage.show();
    }

    // ---- navigation -------------------------------------------------

    /** Swaps the visible screen while keeping the same window and scene. */
    public void switchTo(Parent screen, String title) {
        stage.getScene().setRoot(screen);
        stage.setTitle(title);
    }

    public void showOpeningScreen() {
        switchTo(OpeningScreen.build(this), "Food Delivery System");
    }

    /** The admin control center (Task 5a). */
    public void showAdminHub() {
        switchTo(AdminHubScreen.build(this), "Admin Control Center");
    }

    // The destinations reachable from the admin hub, each built by its own screen class.
    public void showCustomerManagement() {
        switchTo(CustomerManagementScreen.build(this), "Manage Customers");
    }

    public void showRestaurantManagement() {
        switchTo(RestaurantManagementScreen.build(this), "Manage Restaurants");
    }

    public void showOrderManagement() {
        switchTo(OrderManagementScreen.build(this), "Manage Orders");
    }

    public void showRiderManagement() {
        switchTo(RiderManagementScreen.build(this), "Manage Riders");
    }

    public void showRestAdminManagement() {
        switchTo(RestAdminManagementScreen.build(this), "Manage Managers");
    }

    public void showReports() {
        switchTo(ReportsScreen.build(this), "Reports & Sorting");
    }

    public void showUserLogin() {
        switchTo(UserLoginScreen.build(this), "User Login");
    }

    public void showCustomerScreen(Customer customer) {
        switchTo(CustomerScreen.build(this, customer),
                "Welcome, " + customer.getFirstName() + " " + customer.getLastName());
    }

    public void showRiderScreen(Rider rider) {
        switchTo(RiderScreen.build(this, rider), "Rider: " + rider.getFullName());
    }

    public void showRestAdminScreen(RestAdmin admin) {
        switchTo(RestAdminScreen.build(this, admin), "Manager: " + admin.getManagerName());
    }

    // ---- shared accessors 
    public DeliveryDataBase getSystem() {
        return system;
    }

    public Stage getStage() {
        return stage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}