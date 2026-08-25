package FoodDeliverySystem;
import java.util.Scanner;

/**
 * Entry point for the Food Delivery System.
 *
 * This class:
 *   1. Creates the DeliverySystem container.
 *   2. Creates the standalone Admin instance (lives HERE in main(),
 *      NOT inside DeliverySystem - per course staff clarification).
 *   3. Seeds the system with the minimum required initial data.
 *   4. Runs the main menu loop, routing the user to the appropriate
 *      sub-menu (Admin / RestAdmin / Rider / Customer).
 *
 * Tasks 7-10 implement each sub-menu in order. Task 7 (this version)
 * implements the Admin sub-menu in full; the other three remain stubs
 * until their respective tasks.
 */
public class Main {

    // The single Admin instance (per course staff clarification,
    // Admin is NOT stored inside DeliverySystem - it lives here)
    private static final Admin SYSTEM_ADMIN =
            new Admin("Shadi Asakla", "admin", "12345");

    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        DeliverySystem system = new DeliverySystem();
        seedInitialData(system);

        Scanner sc = new Scanner(System.in);
        printWelcome();
        runMainMenu(system, sc);

        System.out.println("\nGoodbye!");
        sc.close();
    }

    // Welcome banner

    private static void printWelcome() {
        System.out.println("    FOOD DELIVERY SYSTEM - University of Haifa");
        System.out.println("    Object-Oriented Programming Course");
    }

    // Main menu loop
    private static void runMainMenu(DeliverySystem system, Scanner sc) {
        boolean running = true;
        while (running) {
            System.out.println("\n MAIN MENU ");
            System.out.println("1. System Admin login");
            System.out.println("2. Restaurant Manager login");
            System.out.println("3. Rider login");
            System.out.println("4. Customer login");
            System.out.println("5. Exit");

            int choice = InputHelper.readIntInRange(sc, "Choose an option (1-5): ", 1, 5);

            switch (choice) {
                case 1: handleAdminLogin(system, sc);     break;
                case 2: handleRestAdminLogin(system, sc); break;
                case 3: handleRiderLogin(system, sc);     break;
                case 4: handleCustomerLogin(system, sc);  break;
                case 5: running = false;                  break;
            }
        }
    }

    // Login handlers

    private static void handleAdminLogin(DeliverySystem system, Scanner sc) {
        System.out.println("\n--- System Admin Login ---");
        String username = InputHelper.readNonEmptyString(sc, "Username: ");
        String password = InputHelper.readNonEmptyString(sc, "Password: ");

        if (SYSTEM_ADMIN.login(username, password)) {
            System.out.println("Welcome, " + SYSTEM_ADMIN.getManagerName() + "!");
            showAdminMenu(system, sc);
        } else {
            System.out.println("Invalid credentials. Returning to main menu.");
        }
    }

    private static void handleRestAdminLogin(DeliverySystem system, Scanner sc) {
        System.out.println("\n--- Restaurant Manager Login ---");
        String username = InputHelper.readNonEmptyString(sc, "Username: ");
        String password = InputHelper.readNonEmptyString(sc, "Password: ");

        RestAdmin manager = system.findRestAdminByUsername(username);
        if (manager != null && manager.login(username, password)) {
            System.out.println("Welcome, " + manager.getManagerName() + "!");
            showRestAdminMenu(system, manager, sc);
        } else {
            System.out.println("Invalid credentials. Returning to main menu.");
        }
    }

    private static void handleRiderLogin(DeliverySystem system, Scanner sc) {
        System.out.println("\n--- Rider Login ---");
        String id = InputHelper.readValidNationalId(sc, "Enter your national ID (9 digits): ");
        Rider rider = system.findRiderById(id);
        if (rider != null) {
            System.out.println("Welcome, " + rider.getFullName() + "!");
            showRiderMenu(system, rider, sc);
        } else {
            System.out.println("Rider not found. Returning to main menu.");
        }
    }

    private static void handleCustomerLogin(DeliverySystem system, Scanner sc) {
        System.out.println("\n--- Customer Login ---");
        int code = InputHelper.readPositiveInt(sc, "Enter your customer code: ");
        Customer customer = system.findCustomerByCode(code);
        if (customer != null) {
            System.out.println("Welcome, " + customer.getFirstName() + "!");
            showCustomerMenu(system, customer, sc);
        } else {
            System.out.println("Customer not found. Returning to main menu.");
        }
    }

    /**
     * Admin sub-menu loop. Per the assignment, the admin can:
     *   1. Add a customer
     *   2. Add a restaurant manager
     *   3. Assign a manager to a restaurant
     *   4. Add a restaurant (regular / fast-food / premium)
     *   5. Add a rider
     *   6. Assign a rider to an order
     *   0. Logout (return to main menu)
     *
     * NOTE: Admin does NOT add orders (per the assignment).
     */
    private static void showAdminMenu(DeliverySystem system, Scanner sc) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. Add a customer");
            System.out.println("2. Add a restaurant manager");
            System.out.println("3. Assign manager to a restaurant");
            System.out.println("4. Add a restaurant");
            System.out.println("5. Add a rider");
            System.out.println("6. Assign rider to an order");
            System.out.println("0. Logout");

            int choice = InputHelper.readIntInRange(sc, "Choose an option (0-6): ", 0, 6);
            switch (choice) {
                case 1: adminAddCustomer(system, sc);            break;
                case 2: adminAddRestAdmin(system, sc);           break;
                case 3: adminAssignManagerToRestaurant(system, sc); break;
                case 4: adminAddRestaurant(system, sc);          break;
                case 5: adminAddRider(system, sc);               break;
                case 6: adminAssignRiderToOrder(system, sc);     break;
                case 0: stay = false;                            break;
            }
        }
    }

    // Admin Option 1: Add a customer 
    private static void adminAddCustomer(DeliverySystem system, Scanner sc) {
        System.out.println("\n--- Add New Customer ---");
        int code = InputHelper.readPositiveInt(sc, "Customer code: ");
        if (system.findCustomerByCode(code) != null) {
            System.out.println("A customer with code " + code + " already exists. Operation cancelled.");
            return;
        }
        String firstName = InputHelper.readLettersOnly(sc, "First name: ");
        String lastName  = InputHelper.readLettersAndSpaces(sc, "Last name: ");
        String street    = InputHelper.readNonEmptyString(sc, "Street: ");        // unchanged
        String city      = InputHelper.readLettersAndSpaces(sc, "City: ");
        String zipCode   = InputHelper.readDigitsOnly(sc, "Zip code: ", 5, 7);
        String phone     = InputHelper.readValidPhone(sc, "Phone: ");        // ← this line was missing
        String email     = InputHelper.readValidEmail(sc, "Email: ");
        double balance   = InputHelper.readNonNegativeDouble(sc, "Initial credit balance: ");

        Customer c = new Customer(code, firstName, lastName, street, city, zipCode, phone, email, balance);
        if (system.addCustomer(c)) {
            System.out.println("Customer added successfully:");
            System.out.println("  " + c);
        } else {
            System.out.println("Failed to add customer (unexpected error).");
        }
    }

    // Admin Option 2: Add a restaurant manager 
    private static void adminAddRestAdmin(DeliverySystem system, Scanner sc) {
        System.out.println("\n--- Add New Restaurant Manager ---");
        String managerName = InputHelper.readLettersAndSpaces(sc, "Manager full name: ");       
        String username    = InputHelper.readNonEmptyString(sc, "Username: ");
        if (system.findRestAdminByUsername(username) != null) {
            System.out.println("A manager with username '" + username + "' already exists. Operation cancelled.");
            return;
        }
        String password    = InputHelper.readNonEmptyString(sc, "Password: ");

        RestAdmin ra = new RestAdmin(managerName, username, password);
        if (system.addRestAdmin(ra)) {
            System.out.println("Manager added successfully:");
            System.out.println("  " + ra);
        } else {
            System.out.println("Failed to add manager (unexpected error).");
        }
    }

    // Admin Option 3: Assign a manager to a restaurant 
    private static void adminAssignManagerToRestaurant(DeliverySystem system, Scanner sc) {
        System.out.println("\n--- Assign Manager to Restaurant ---");
        String username = InputHelper.readNonEmptyString(sc, "Manager username: ");
        RestAdmin manager = system.findRestAdminByUsername(username);
        if (manager == null) {
            System.out.println("Manager '" + username + "' not found. Operation cancelled.");
            return;
        }
        int restCode = InputHelper.readPositiveInt(sc, "Restaurant code: ");
        Restaurant restaurant = system.findRestaurantByCode(restCode);
        if (restaurant == null) {
            System.out.println("Restaurant with code " + restCode + " not found. Operation cancelled.");
            return;
        }

        if (manager.addRestaurant(restaurant)) {
            System.out.println("Manager '" + username + "' is now responsible for restaurant " + restCode + ".");
        } else {
            System.out.println("Manager '" + username + "' is already responsible for restaurant " + restCode + ". No change.");
        }
    }

    // Admin Option 4: Add a restaurant (with sub-prompt for type) 
    private static void adminAddRestaurant(DeliverySystem system, Scanner sc) {
        System.out.println("\n--- Add New Restaurant ---");
        System.out.println("Select restaurant type:");
        System.out.println("  1. Regular");
        System.out.println("  2. Fast Food");
        System.out.println("  3. Premium");
        int type = InputHelper.readIntInRange(sc, "Type (1-3): ", 1, 3);

        int code = InputHelper.readPositiveInt(sc, "Restaurant code: ");
        if (system.findRestaurantByCode(code) != null) {
            System.out.println("A restaurant with code " + code + " already exists. Operation cancelled.");
            return;
        }
        String name        = InputHelper.readNonEmptyString(sc, "Restaurant name: ");
        String cuisineType = InputHelper.readLettersAndSpaces(sc, "Cuisine type: ");
        double rating      = InputHelper.readRating(sc, "Rating (0.0 - 5.0): ");
        boolean isOpen     = InputHelper.readYesNo(sc, "Is the restaurant currently open?");
        double baseFee     = InputHelper.readNonNegativeDouble(sc, "Base delivery fee: ");

        Restaurant restaurant;
        switch (type) {
            case 1:
                restaurant = new Restaurant(code, name, cuisineType, rating, isOpen, baseFee);
                break;
            case 2:
                int    prepTime    = InputHelper.readPositiveInt   (sc, "Avg preparation time (minutes): ");
                double surcharge   = InputHelper.readNonNegativeDouble(sc, "Fast-delivery surcharge: ");
                restaurant = new FastFoodRestaurant(code, name, cuisineType, rating, isOpen,
                                                    baseFee, prepTime, surcharge);
                break;
            case 3:
                double minOrder    = InputHelper.readNonNegativeDouble(sc, "Minimum order amount: ");
                double commission  = InputHelper.readNonNegativeDouble(sc, "Extra commission percent (0-100): ");
                restaurant = new PremiumRestaurant(code, name, cuisineType, rating, isOpen,
                                                    baseFee, minOrder, commission);
                break;
            default:
                System.out.println("Unexpected error - operation cancelled.");
                return;
        }

        if (system.addRestaurant(restaurant)) {
            System.out.println("Restaurant added successfully:");
            System.out.println("  " + restaurant);
        } else {
            System.out.println("Failed to add restaurant (unexpected error).");
        }
    }

    // Admin Option 5: Add a rider
    private static void adminAddRider(DeliverySystem system, Scanner sc) {
        System.out.println("\n--- Add New Rider ---");
        String id = InputHelper.readValidNationalId(sc, "National ID (9 digits): ");
        if (system.findRiderById(id) != null) {
            System.out.println("A rider with ID '" + id + "' already exists. Operation cancelled.");
            return;
        }
        String fullName = InputHelper.readLettersAndSpaces(sc, "Full name: ");
        String phone    = InputHelper.readValidPhone     (sc, "Phone (10 digits): ");
        String vehicle  = InputHelper.readNonEmptyString(sc, "Vehicle (bicycle/scooter/car): ");
        boolean avail   = InputHelper.readYesNo(sc, "Is the rider currently available?");

        Rider r = new Rider(id, fullName, phone, vehicle, avail);
        if (system.addRider(r)) {
            System.out.println("Rider added successfully:");
            System.out.println("  " + r);
        } else {
            System.out.println("Failed to add rider (unexpected error).");
        }
    }

    // Admin Option 6: Assign rider to order 
    private static void adminAssignRiderToOrder(DeliverySystem system, Scanner sc) {
        System.out.println("\n--- Assign Rider to Order ---");
        if (system.getOrdersCount() == 0) {
            System.out.println("There are no orders in the system yet. Customers or managers need to create orders first.");
            return;
        }
        String riderId = InputHelper.readValidNationalId(sc, "Rider national ID (9 digits): ");
        Rider rider = system.findRiderById(riderId);
        if (rider == null) {
            System.out.println("Rider '" + riderId + "' not found. Operation cancelled.");
            return;
        }
        if (!rider.isAvailable()) {
            System.out.println("Rider '" + riderId + "' is currently unavailable. Operation cancelled.");
            return;
        }

        int orderCode = InputHelper.readPositiveInt(sc, "Order code: ");
        Order order = system.findOrderByCode(orderCode);
        if (order == null) {
            System.out.println("Order with code " + orderCode + " not found. Operation cancelled.");
            return;
        }

        if (system.assignRiderToOrder(riderId, orderCode)) {
            System.out.println("Rider successfully assigned to order " + orderCode + ".");
            System.out.println("  " + order);
        } else {
            System.out.println("Assignment failed.");
        }
    }

    /**
     * Restaurant Manager sub-menu loop. Per the assignment, a RestAdmin can:
     *   1. Add a customer
     *   2. Add an order (ONLY for restaurants they are responsible for)
     *   3. Add a rider
     *   4. Assign a rider to an order
     *   0. Logout (return to main menu)
     *
     * Important rules:
     *   - The order code is AUTO-GENERATED by DeliverySystem.generateOrderCode().
     *     The user never enters their own order code.
     *   - When adding an order, only the restaurant CODE is asked (not the full
     *     restaurant). The system then looks up the restaurant and verifies
     *     that this specific manager is responsible for it.
     *   - Orders can be created without a rider; rider is assigned later via
     *     option 4 (or by the system admin).
     */
    private static void showRestAdminMenu(DeliverySystem system, RestAdmin manager, Scanner sc) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n===== RESTAURANT MANAGER MENU =====");
            System.out.println("Logged in as: " + manager.getManagerName()
                    + " (manages " + manager.getCount() + " restaurant(s))");
            System.out.println("1. Add a customer");
            System.out.println("2. Add an order");
            System.out.println("3. Add a rider");
            System.out.println("4. Assign rider to an order");
            System.out.println("0. Logout");

            int choice = InputHelper.readIntInRange(sc, "Choose an option (0-4): ", 0, 4);
            switch (choice) {
                case 1: adminAddCustomer(system, sc);            break;
                case 2: restAdminAddOrder(system, manager, sc);  break;
                case 3: adminAddRider(system, sc);               break;
                case 4: adminAssignRiderToOrder(system, sc);     break;
                case 0: stay = false;                            break;
            }
        }
    }

    //RestAdmin Option 2: Add an order 
    /**
     * Creates a new order on behalf of a customer.
     *
     * Validation flow:
     *   1. Look up customer by code (must exist in the system).
     *   2. Look up restaurant by code (must exist in the system).
     *   3. Verify that THIS manager is responsible for that restaurant.
     *   4. Read the basic amount from the user.
     *   5. Read the order date from the user.
     *   6. Auto-generate an order code via system.generateOrderCode().
     *   7. Create the Order object (its constructor will polymorphically
     *      compute the finalPrice via restaurant.calculateFinalPrice()).
     *   8. Add to the system.
     *
     * The order starts with:
     *   - status = "sent"
     *   - delivery date = 0/0/0
     *   - rider code = "" (no rider assigned yet)
     */
    private static void restAdminAddOrder(DeliverySystem system, RestAdmin manager, Scanner sc) {
        System.out.println("\n--- Add New Order ---");

        // Step 1: customer
        int customerCode = InputHelper.readPositiveInt(sc, "Customer code: ");
        Customer customer = system.findCustomerByCode(customerCode);
        if (customer == null) {
            System.out.println("Customer with code " + customerCode + " not found. Operation cancelled.");
            return;
        }

        // Step 2: restaurant (input is just the code per staff rule)
        int restaurantCode = InputHelper.readPositiveInt(sc, "Restaurant code: ");
        Restaurant restaurant = system.findRestaurantByCode(restaurantCode);
        if (restaurant == null) {
            System.out.println("Restaurant with code " + restaurantCode + " not found. Operation cancelled.");
            return;
        }
     // Step 2.5: check restaurant is open 
        if (!restaurant.isOpen()) {
            System.out.println("Restaurant '" + restaurant.getName() 
                    + "' is currently CLOSED. Cannot create order. Operation cancelled.");
            return;
        }

        // Step 3: VERY IMPORTANT - this manager must actually manage this restaurant!
        if (!manager.isResponsibleFor(restaurantCode)) {
            System.out.println("You are NOT responsible for restaurant " + restaurantCode + ".");
            System.out.println("Operation cancelled.");
            return;
        }

        // Step 4: basic amount
        double basicAmount = InputHelper.readPositiveDouble(sc, "Basic amount: ");

        // Step 5: order date (validated component-by-component)
        System.out.println("Enter order date:");
        int day   = InputHelper.readIntInRange(sc, "  Day (1-31): ",    1, 31);
        int month = InputHelper.readIntInRange(sc, "  Month (1-12): ",  1, 12);
        int year  = InputHelper.readIntInRange(sc, "  Year (2000-2026): ", 2000, 2026);

        // Step 6: auto-generate order code (user never inputs it)
        int orderCode = system.generateOrderCode();

        // Step 7: create order - constructor will polymorphically compute finalPrice
        Order order = new Order(orderCode, customerCode, restaurant, restaurantCode,
                                day, month, year, basicAmount);

        // Step 8: add to system
        if (system.addOrder(order)) {
            System.out.println("Order created successfully!");
            System.out.println("  Auto-generated order code: " + orderCode);
            System.out.println("  Basic amount: " + basicAmount);
            System.out.println("  Final price (computed by " +
                    restaurant.getClass().getSimpleName() + "): " + order.getFinalPrice());
            // Special note for premium restaurants below minimum
            if (order.getFinalPrice() == 0.0 && restaurant instanceof PremiumRestaurant) {
                PremiumRestaurant pr = (PremiumRestaurant) restaurant;
                System.out.println("  WARNING: This premium restaurant requires a minimum order of "
                        + pr.getMinimumOrderAmount() + ". Final price was set to 0.");
            }
            System.out.println("  " + order);
        } else {
            System.out.println("Failed to add order (unexpected error).");
        }
    }

    /**
     * Rider sub-menu loop. Per the assignment, a Rider can:
     *   1. Update an order's status (advance it toward delivery)
     *   2. Show all of their assigned orders
     *   0. Logout (return to main menu)
     *
     * Important rules:
     *   - A rider can only operate on orders THEY are assigned to.
     *     We use the rider's own orders[] array as the source of truth.
     *   - Status transitions are strictly forward:
     *       sent -> on the way -> delivered
     *     A rider cannot move an order backward, and cannot change a
     *     delivered order.
     *   - When an order is marked as delivered, the rider must enter
     *     a delivery date (day/month/year), which is stored on the order.
     */
    private static void showRiderMenu(DeliverySystem system, Rider rider, Scanner sc) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n===== RIDER MENU =====");
            System.out.println("Logged in as: " + rider.getFullName()
                    + " (" + rider.getOrdersCount() + " assigned order(s))");
            System.out.println("1. Update order status");
            System.out.println("2. Show all of my orders");
            System.out.println("0. Logout");

            int choice = InputHelper.readIntInRange(sc, "Choose an option (0-2): ", 0, 2);
            switch (choice) {
                case 1: riderUpdateOrderStatus(rider, sc); break;
                case 2: riderShowAllOrders(rider);         break;
                case 0: stay = false;                      break;
            }
        }
    }

    //  Rider Option 1: Update order status 
    /**
     * Lets the rider advance an order along its status pipeline.
     *
     * Validation:
     *   - The rider must have orders.
     *   - The entered order code must belong to THIS rider.
     *   - Current status determines what transitions are allowed.
     *
     * Transitions:
     *   sent       -> on the way
     *   on the way -> delivered (asks for delivery date)
     *   delivered  -> no further changes allowed
     */
    private static void riderUpdateOrderStatus(Rider rider, Scanner sc) {
        System.out.println("\n--- Update Order Status ---");

        if (rider.getOrdersCount() == 0) {
            System.out.println("You have no orders assigned yet. Operation cancelled.");
            return;
        }

        int orderCode = InputHelper.readPositiveInt(sc, "Order code: ");
        Order order = findOrderInRider(rider, orderCode);
        if (order == null) {
            System.out.println("Order " + orderCode + " is not assigned to you. Operation cancelled.");
            return;
        }

        String currentStatus = order.getStatus();
        System.out.println("Current status: " + currentStatus);

        // Decide allowed transitions based on current status
        if (Order.STATUS_DELIVERED.equals(currentStatus)) {
            System.out.println("This order has already been delivered. No further changes allowed.");
            return;
        }

        if (Order.STATUS_SENT.equals(currentStatus)) {
            // Allow: sent -> on the way OR sent -> delivered (skip directly)
            System.out.println("Choose new status:");
            System.out.println("  1. On the way");
            System.out.println("  2. Delivered");
            int statusChoice = InputHelper.readIntInRange(sc, "Choice (1-2): ", 1, 2);
            if (statusChoice == 1) {
                order.setStatus(Order.STATUS_ON_THE_WAY);
                System.out.println("Status updated to '" + Order.STATUS_ON_THE_WAY + "'.");
            } else {
                deliverOrder(order, rider ,sc);
            }
            return;
        }

        if (Order.STATUS_ON_THE_WAY.equals(currentStatus)) {
            // Only one valid transition exists: -> delivered. Just confirm.
            boolean confirm = InputHelper.readYesNo(sc, "Mark this order as delivered?");
            if (confirm) {
                deliverOrder(order, rider, sc);
            } else {
                System.out.println("No change made.");
            }
            return;
        }

        // Defensive: unknown status (shouldn't happen)
        System.out.println("Unrecognized current status. Operation cancelled.");
    }

    /**
     * Helper: marks the order as delivered and asks for the delivery date.
     * Uses Order.markAsDelivered() which we built in Task 3.
     */
    private static void deliverOrder(Order order, Rider rider, Scanner sc) {
        System.out.println("Enter delivery date:");
        int day   = InputHelper.readIntInRange(sc, "  Day (1-31): ",       1, 31);
        int month = InputHelper.readIntInRange(sc, "  Month (1-12): ",     1, 12);
        int year  = InputHelper.readIntInRange(sc, "  Year (2000-2026): ", 2000, 2026);
        order.markAsDelivered(day, month, year);
        rider.setAvailable(true);   // ← rider is free for new orders
        System.out.println("Order " + order.getOrderCode() + " marked as DELIVERED on "
                + day + "/" + month + "/" + year + ".");
        System.out.println("You are now available for new orders.");
    }    /**
     * Helper: searches the rider's orders[] array for the given order code.
     *
     * @return the Order if found in this rider's list, or null otherwise
     */
    private static Order findOrderInRider(Rider rider, int orderCode) {
        Order[] orders = rider.getOrders();
        int count = rider.getOrdersCount();
        for (int i = 0; i < count; i++) {
            if (orders[i] != null && orders[i].getOrderCode() == orderCode) {
                return orders[i];
            }
        }
        return null;
    }

    //  Rider Option 2: Show all orders
    /**
     * Prints all orders currently assigned to this rider, using the rider's
     * own orders[] array. Each order is printed via toString().
     */
    private static void riderShowAllOrders(Rider rider) {
        System.out.println("\n--- My Orders ---");
        int count = rider.getOrdersCount();
        if (count == 0) {
            System.out.println("You have no orders assigned yet.");
            return;
        }
        Order[] orders = rider.getOrders();
        System.out.println("Total: " + count + " order(s)");
        for (int i = 0; i < count; i++) {
            System.out.println("  " + (i + 1) + ". " + orders[i]);
        }
    }

    /**
     * Customer sub-menu loop. Per the assignment, a Customer can:
     *   1. Place a new order
     *   2. View all of their orders
     *   3. Update personal info (PHONE and ADDRESS only)
     *   4. Display a restaurant's details by code
     *   0. Logout (return to main menu)
     *
     * Important rules:
     *   - When placing an order, the order code is AUTO-GENERATED by
     *     DeliverySystem.generateOrderCode(). The customer never enters
     *     their own order code.
     *   - The customer enters only the basic amount. The final price is
     *     computed POLYMORPHICALLY inside Order.setBasicAmount() by calling
     *     the linked restaurant's calculateFinalPrice() method.
     *   - "View all of my orders" uses the CLEAN APPROACH (per course staff):
     *     filter the master orders[] array in DeliverySystem by customerCode,
     *     rather than storing a duplicate Order[] inside Customer.
     *   - The "update personal info" menu only exposes PHONE and ADDRESS
     *     options. Other setters exist in the Customer class (per staff)
     *     but the menu code never calls them.
     */
    private static void showCustomerMenu(DeliverySystem system, Customer customer, Scanner sc) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n===== CUSTOMER MENU =====");
            System.out.println("Logged in as: " + customer.getFirstName()
                    + " " + customer.getLastName()
                    + " (code " + customer.getCustomerCode() + ")");
            System.out.println("1. Place a new order");
            System.out.println("2. View all of my orders");
            System.out.println("3. Update personal info (phone / address only)");
            System.out.println("4. Display restaurant details by code");
            System.out.println("0. Logout");

            int choice = InputHelper.readIntInRange(sc, "Choose an option (0-4): ", 0, 4);
            switch (choice) {
                case 1: customerPlaceOrder(system, customer, sc);  break;
                case 2: customerViewOrders(system, customer);      break;
                case 3: customerUpdatePersonalInfo(customer, sc);  break;
                case 4: customerShowRestaurant(system, sc);        break;
                case 0: stay = false;                              break;
            }
        }
    }

    // Customer Option 1: Place a new order 
    /**
     * Creates a new order for this customer.
     *
     * Flow:
     *   1. Read restaurant code from the user.
     *   2. Look it up; if not found, cancel.
     *   3. Read the basic amount.
     *   4. Read the order date.
     *   5. Auto-generate an order code (user never inputs it).
     *   6. Create the Order; the constructor will POLYMORPHICALLY compute
     *      the final price via restaurant.calculateFinalPrice().
     *   7. Add the order to the system.
     *
     * Note that a customer can order from ANY restaurant - there is no
     * "responsibility" check (only restaurant managers have that restriction).
     */
    private static void customerPlaceOrder(DeliverySystem system, Customer customer, Scanner sc) {
        System.out.println("\n--- Place a New Order ---");

        // Step 1-2: restaurant
        int restaurantCode = InputHelper.readPositiveInt(sc, "Restaurant code: ");
        Restaurant restaurant = system.findRestaurantByCode(restaurantCode);
        if (restaurant == null) {
            System.out.println("Restaurant with code " + restaurantCode + " not found. Operation cancelled.");
            return;
        }
        if (!restaurant.isOpen()) {
            System.out.println("Sorry - this restaurant is currently closed. Operation cancelled.");
            return;
        }
        System.out.println("Restaurant: " + restaurant.getName()
                + " (" + restaurant.getClass().getSimpleName() + ")");

        // Step 3: basic amount
        double basicAmount = InputHelper.readPositiveDouble(sc, "Basic amount: ");

        // Step 4: order date
        System.out.println("Enter order date:");
        int day   = InputHelper.readIntInRange(sc, "  Day (1-31): ",       1, 31);
        int month = InputHelper.readIntInRange(sc, "  Month (1-12): ",     1, 12);
        int year  = InputHelper.readIntInRange(sc, "  Year (2000-2026): ", 2000, 2026);

        // Step 5: auto-generate order code (user never inputs it)
        int orderCode = system.generateOrderCode();

        // Step 6: create order - constructor polymorphically computes finalPrice
        Order order = new Order(orderCode, customer.getCustomerCode(), restaurant,
                                restaurantCode, day, month, year, basicAmount);

        // Special handling: warn user if a premium minimum was not met
        if (order.getFinalPrice() == 0.0 && restaurant instanceof PremiumRestaurant) {
            PremiumRestaurant pr = (PremiumRestaurant) restaurant;
            System.out.println();
            System.out.println("Sorry - this premium restaurant requires a minimum order of "
                    + pr.getMinimumOrderAmount() + ".");
            System.out.println("Your basic amount of " + basicAmount + " is below the minimum.");
            System.out.println("Order NOT placed. Please try again with a larger amount.");
            return; // do NOT add the invalid order to the system
        }

        // Step 7: add to system
        if (system.addOrder(order)) {
            System.out.println();
            System.out.println("Your order has been placed!");
            System.out.println("  Auto-generated order code: " + orderCode);
            System.out.println("  Basic amount: " + basicAmount);
            System.out.println("  Final price (computed by " +
                    restaurant.getClass().getSimpleName() + "): " + order.getFinalPrice());
            System.out.println("  Status: " + order.getStatus());
            System.out.println("  Note: A rider will be assigned shortly.");
        } else {
            System.out.println("Failed to place order (unexpected error).");
        }
    }

    // Customer Option 2: View all orders
    /**
     * Lists every order belonging to this customer.
     *
     * Uses the "clean approach" per course staff: iterate through the
     * master orders[] array in DeliverySystem and filter by customerCode,
     * rather than storing a duplicate Order[] inside Customer.
     *
     * Each order is printed via its full toString().
     */
    private static void customerViewOrders(DeliverySystem system, Customer customer) {
        System.out.println("\n--- My Orders ---");
        Order[] myOrders = system.getOrdersForCustomer(customer.getCustomerCode());
        if (myOrders.length == 0) {
            System.out.println("You have not placed any orders yet.");
            return;
        }
        System.out.println("Total: " + myOrders.length + " order(s)");
        for (int i = 0; i < myOrders.length; i++) {
            System.out.println("  " + (i + 1) + ". " + myOrders[i]);
        }
    }

    //  Customer Option 3: Update personal info (PHONE and ADDRESS only) 
    /**
     * Lets the customer update PHONE or ADDRESS only.
     *
     * This is the menu-level enforcement of the assignment restriction:
     *   "A Customer can only update their address and phone number."
     *
     * Other Customer setters exist in the class (per the staff rule about
     * mandatory setters for all attributes), but THIS MENU never calls them.
     *
     * Only the field(s) the user actually chooses to change get updated,
     * preserving all other fields.
     */
    private static void customerUpdatePersonalInfo(Customer customer, Scanner sc) {
        System.out.println("\n--- Update Personal Info ---");
        System.out.println("Current phone:   " + customer.getPhone());
        System.out.println("Current address: " + customer.getStreet() + ", "
                + customer.getCity() + ", " + customer.getZipCode());
        System.out.println();
        System.out.println("What would you like to update?");
        System.out.println("  1. Phone");
        System.out.println("  2. Address (street, city, zip)");
        System.out.println("  3. Both");
        System.out.println("  0. Cancel");

        int choice = InputHelper.readIntInRange(sc, "Choice (0-3): ", 0, 3);
        if (choice == 0) {
            System.out.println("No changes made.");
            return;
        }

        if (choice == 1 || choice == 3) {
            String newPhone = InputHelper.readValidPhone(sc, "New phone (10 digits): ");
            customer.setPhone(newPhone);
            System.out.println("Phone updated to: " + newPhone);
        }
        if (choice == 2 || choice == 3) {
        	String newStreet  = InputHelper.readNonEmptyString(sc, "New street: ");   
        	String newCity    = InputHelper.readLettersAndSpaces(sc, "New city: ");
        	String newZipCode = InputHelper.readDigitsOnly(sc, "New zip code: ", 5, 7);
            customer.setStreet(newStreet);
            customer.setCity(newCity);
            customer.setZipCode(newZipCode);
            System.out.println("Address updated to: " + newStreet + ", " + newCity + ", " + newZipCode);
        }
        System.out.println();
        System.out.println("Final state of your record:");
        System.out.println("  " + customer);
    }

    // ----- Customer Option 4: Display restaurant by code 
    /**
     * Looks up a restaurant by code and prints its full toString().
     * Since restaurants[] is polymorphic, toString() automatically dispatches
     * to the correct subclass version (Restaurant / FastFood / Premium),
     * showing the type-specific fields.
     */
    private static void customerShowRestaurant(DeliverySystem system, Scanner sc) {
        System.out.println("\n--- Display Restaurant Details ---");
        int code = InputHelper.readPositiveInt(sc, "Restaurant code: ");
        Restaurant restaurant = system.findRestaurantByCode(code);
        if (restaurant == null) {
            System.out.println("Restaurant with code " + code + " not found.");
            return;
        }
        System.out.println("  " + restaurant);
    }


    private static void seedInitialData(DeliverySystem system) {
        seedRiders(system);
        seedCustomers(system);
        seedRestaurants(system);
        seedRestAdminsAndAssign(system);
        System.out.println("System initialized successfully!");
        System.out.println("  - " + system.getCustomersCount() + " customers");
        System.out.println("  - " + system.getRestaurantsCount() + " restaurants");
        System.out.println("  - " + system.getRidersCount() + " riders");
        System.out.println("  - " + system.getRestAdminsCount() + " managers");
    }

    private static void seedRiders(DeliverySystem system) {
        system.addRider(new Rider("204050601", "Mohamad Saeed",   "0521234567", "scooter", true));
        system.addRider(new Rider("204050602", "Yara Mansour",     "0521234568", "bicycle", true));
        system.addRider(new Rider("204050603", "Ahmad Haddad",     "0521234569", "car",     true));
        system.addRider(new Rider("204050604", "Layla Saleh",      "0521234570", "scooter", true));
        system.addRider(new Rider("204050605", "Omar Zoabi",       "0521234571", "bicycle", true));
    }

    private static void seedCustomers(DeliverySystem system) {
        // Updated cities and removed unwanted street names
        system.addCustomer(new Customer(1001, "Mohamad", "Abdo",    "Old City",       "Tel Aviv",     "3303220", "0501234567", "mohamad@gmail.com",  250.0));
        system.addCustomer(new Customer(1002, "Yara",    "Bsoul",   "Spring St 10",   "Jerusalem",    "6473812", "0501234568", "yara@huji.ac.il",    100.0));
        system.addCustomer(new Customer(1003, "Adi",     "Bakr",    "Champs-Elysees", "Paris",        "6618001", "0501234569", "adi@walla.co.il",     50.0));
        system.addCustomer(new Customer(1004, "Lina",    "Khoury",  "Oxford Street",  "London",       "9426408", "0501234570", "lina@gmail.com",     300.0));
        system.addCustomer(new Customer(1005, "Tamer",   "Mansour", "Carmel Center",  "Haifa",        "3464127", "0501234571", "tamer@hotmail.com",  150.0));
        system.addCustomer(new Customer(1006, "Maysa",   "Shami",   "Al-Bishara 100", "Nazareth",     "6433222", "0501234572", "maysa@gmail.com",    200.0));
        system.addCustomer(new Customer(1007, "Ibrahim", "Saleh",   "Sheikh Zayed 20","Dubai",        "6342211", "0501234573", "ibrahim@walla.co.il", 75.0));
        system.addCustomer(new Customer(1008, "Leen",    "Haddad",  "King Fahd Rd",   "Riyadh", "6678801", "0501234574", "leen@gmail.com",     120.0));
        system.addCustomer(new Customer(1009, "Karim",   "Zoabi",   "Marina 5",       "Milano",        "8810023", "0501234575", "karim@gmail.com",    400.0));
        system.addCustomer(new Customer(1010, "Salma",   "Awad",    "Center 7",       "Kfar Kana",    "8410001", "0501234576", "salma@gmail.com",     90.0));
    }

    private static void seedRestaurants(DeliverySystem system) {
        // 10 regular restaurants 
        system.addRestaurant(new Restaurant(101, "Tishreen",          "Fusion",          4.8, true,  15.0));
        system.addRestaurant(new Restaurant(102, "Shawatina",         "Seafood",         4.7, true,  18.0));
        system.addRestaurant(new Restaurant(103, "Little Italy",      "Italian",         4.5, true,  12.0));
        system.addRestaurant(new Restaurant(104, "Sushi Samurai",     "Japanese",        4.6, true,  14.0));
        system.addRestaurant(new Restaurant(105, "Taj Mahal",         "Indian",          4.2, true,  13.0));
        system.addRestaurant(new Restaurant(106, "El Sombrero",       "Mexican",         4.0, false, 11.0));
        system.addRestaurant(new Restaurant(107, "Peking Duck House", "Chinese",         4.4, true,  16.0));
        system.addRestaurant(new Restaurant(108, "Greek Taverna",     "Greek",           4.3, true,   8.0));
        system.addRestaurant(new Restaurant(109, "Texas BBQ",         "American",        4.1, true,  10.0));
        system.addRestaurant(new Restaurant(110, "Cafe de Paris",     "French",          4.5, true,  15.0));

        // 10 fast-food restaurants
        system.addRestaurant(new FastFoodRestaurant(201, "McDonald's",     "American",       4.0, true,  10.0, 15,  8.0));
        system.addRestaurant(new FastFoodRestaurant(202, "Burger King",    "American",       3.9, true,   9.0, 20,  7.0));
        system.addRestaurant(new FastFoodRestaurant(203, "KFC",            "American",       4.1, true,   8.0, 10,  5.0));
        system.addRestaurant(new FastFoodRestaurant(204, "Domino's Pizza", "Italian",        4.2, true,   9.0, 12,  6.0));
        system.addRestaurant(new FastFoodRestaurant(205, "Subway",         "Sandwiches",     3.8, true,  10.0, 18,  8.0));
        system.addRestaurant(new FastFoodRestaurant(206, "Taco Bell",      "Mexican",        3.7, true,   8.0, 12,  6.0));
        system.addRestaurant(new FastFoodRestaurant(207, "Pizza Hut",      "Italian",        4.0, true,   9.0, 15,  7.0));
        system.addRestaurant(new FastFoodRestaurant(208, "Shake Shack",    "American",       4.4, true,   8.0, 10,  5.0));
        system.addRestaurant(new FastFoodRestaurant(209, "Panda Express",  "Chinese",        3.9, true,   9.0, 15,  6.0));
        system.addRestaurant(new FastFoodRestaurant(210, "Five Guys",      "American",       4.5, true,  10.0, 18,  7.0));

        // 10 premium restaurants 
        system.addRestaurant(new PremiumRestaurant(301, "Nobu",               "Japanese",     4.9, true,  25.0, 100.0, 12.0));
        system.addRestaurant(new PremiumRestaurant(302, "Gordon Ramsay Steak","Steakhouse",   4.8, true,  30.0, 120.0, 15.0));
        system.addRestaurant(new PremiumRestaurant(303, "Nusr-Et (Salt Bae)", "Steakhouse",   4.7, true,  28.0, 110.0, 13.0));
        system.addRestaurant(new PremiumRestaurant(304, "Le Bernardin",       "French",       4.9, true,  35.0, 150.0, 15.0));
        system.addRestaurant(new PremiumRestaurant(305, "Magdalena",          "Upscale Local",4.8, true,  32.0, 130.0, 14.0));
        system.addRestaurant(new PremiumRestaurant(306, "Osteria Francescana","Italian",      4.9, true,  30.0, 140.0, 14.0));
        system.addRestaurant(new PremiumRestaurant(307, "Zuma",               "Contemporary", 4.7, true,  35.0, 160.0, 16.0));
        system.addRestaurant(new PremiumRestaurant(308, "Hakkasan",           "Chinese",      4.6, true,  25.0,  90.0, 10.0));
        system.addRestaurant(new PremiumRestaurant(309, "The French Laundry", "French",       4.9, true,  33.0, 130.0, 14.0));
        system.addRestaurant(new PremiumRestaurant(310, "Pujol",              "Mexican",      4.8, true,  40.0, 180.0, 18.0));
    }

    private static void seedRestAdminsAndAssign(DeliverySystem system) {
        RestAdmin yara    = new RestAdmin("Yara Torkman",    "yara",    "yara123");
        RestAdmin mohamad = new RestAdmin("Mohamad Saleh",  "mohamad", "mohamad123");
        RestAdmin layla   = new RestAdmin("Layla Mansour",  "layla",   "layla123");

        yara.addRestaurant(system.findRestaurantByCode(101));
        yara.addRestaurant(system.findRestaurantByCode(102));
        yara.addRestaurant(system.findRestaurantByCode(201));
        yara.addRestaurant(system.findRestaurantByCode(202));

        mohamad.addRestaurant(system.findRestaurantByCode(108));
        mohamad.addRestaurant(system.findRestaurantByCode(206));
        mohamad.addRestaurant(system.findRestaurantByCode(301));
        mohamad.addRestaurant(system.findRestaurantByCode(302));

        layla.addRestaurant(system.findRestaurantByCode(301));
        layla.addRestaurant(system.findRestaurantByCode(303));
        layla.addRestaurant(system.findRestaurantByCode(304));
        layla.addRestaurant(system.findRestaurantByCode(305));

        system.addRestAdmin(yara);
        system.addRestAdmin(mohamad);
        system.addRestAdmin(layla);
    }
}
