package FoodDeliverySystem;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;
import java.util.function.Function;
import java.util.List;
import java.util.stream.Collectors;
import java.util.function.Predicate;

/**
 * Entry point for the Food Delivery System.
 *
 * This class:
 *   1. Creates the DeliveryDataBase container (which creates the system
 *      Admin internally - per the Assignment 2 ).
 *   2. Seeds the system with the minimum required initial data, including
 *      at least 10 orders that update all four collections in parallel.
 *   3. Runs the main menu loop, routing the user to the appropriate
 *      sub-menu (Admin / RestAdmin / Rider / Customer).
 *
 * The Admin instance now lives INSIDE the DeliveryDataBase
 * In Assignment 1 it was a static field on this class -
 * that field is gone.
 */
public class Main {

    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        DeliveryDataBase system = new DeliveryDataBase();
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
    private static void runMainMenu(DeliveryDataBase system, Scanner sc) {
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

    private static void handleAdminLogin(DeliveryDataBase system, Scanner sc) {
        System.out.println("\n--- System Admin Login ---");
        String username = InputHelper.readNonEmptyString(sc, "Username: ");
        String password = InputHelper.readNonEmptyString(sc, "Password: ");

        // The Admin now lives inside DeliveryDataBase - we ask the DB
        // for it rather than reaching for a static field in this class.
        Admin admin = system.getSystemAdministrator();
        if (admin.login(username, password)) {
            System.out.println("Welcome, " + admin.getManagerName() + "!");
            showAdminMenu(system, sc);
        } else {
            System.out.println("Invalid credentials. Returning to main menu.");
        }
    }

    private static void handleRestAdminLogin(DeliveryDataBase system, Scanner sc) {
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

    private static void handleRiderLogin(DeliveryDataBase system, Scanner sc) {
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
    private static void handleCustomerLogin(DeliveryDataBase system, Scanner sc) {
        System.out.println("\n--- Customer Login ---");
        int code = InputHelper.readPositiveInt(sc, "Enter your customer code: ");

        // try / catch demonstration (HW3 Part F).
        // The throwing lookup forces us to handle CustomerNotFoundException;
        // a friendly message is shown and the program never crashes.
        try {
            Customer customer = system.findCustomerByCodeOrThrow(code);
            System.out.println("Welcome, " + customer.getFirstName() + "!");
            showCustomerMenu(system, customer, sc);
        } catch (CustomerNotFoundException e) {
            // Friendly message - the program does NOT crash (Part F requirement).
            System.out.println("Login failed: " + e.getMessage());
            System.out.println("Returning to main menu.");
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
     *   7. View all orders in the system          [Assignment 2]
     *   8. Show the customer with the most orders [Assignment 2]
     *   9. Show the rider with the most deliveries [Assignment 2]
     *  10. Toggle a restaurant's open/closed status [Assignment 2]
     *   0. Logout (return to main menu)
     *
     * NOTE: Admin does NOT add orders (per the assignment).
     */
    private static void showAdminMenu(DeliveryDataBase system, Scanner sc) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1.  Add a customer");
            System.out.println("2.  Add a restaurant manager");
            System.out.println("3.  Assign manager to a restaurant");
            System.out.println("4.  Add a restaurant");
            System.out.println("5.  Add a rider");
            System.out.println("6.  Assign rider to an order");
            System.out.println("7.  View all orders in the system");
            System.out.println("8.  Show customer with the most orders");
            System.out.println("9.  Show rider with the most deliveries");
            System.out.println("10. Toggle restaurant open/closed");
            System.out.println("11. Run all sorts (Comparable / Comparator)");
            System.out.println("12. Sort riders by number of deliveries (lambda)");
            System.out.println("13. Sort customers by first name (lambda)");
            System.out.println("14. Sort orders by date (lambda)");
            System.out.println("15. Print all customers (method ref: System.out::println)");
            System.out.println("16. Sort customers by balance (method ref: Customer::compareTo)");
            System.out.println("17. List customer first names (method ref: Customer::getFirstName)");
            System.out.println("18. Generate system reports (Wildcards)");
            System.out.println("19. [Stream] Show all open restaurants");
            System.out.println("20. [Stream] Show all premium restaurants");
            System.out.println("21. [Stream] Show all available riders");
            System.out.println("22. [Stream] Total money paid for all orders");
            System.out.println("23. [Predicate] Customers with balance over 100");
            System.out.println("0.  Logout");

            int choice = InputHelper.readIntInRange(sc, "Choose an option (0-23): ", 0,23);
            switch (choice) {
                case 1:  adminAddCustomer(system, sc);              break;
                case 2:  adminAddRestAdmin(system, sc);             break;
                case 3:  adminAssignManagerToRestaurant(system, sc);break;
                case 4:  adminAddRestaurant(system, sc);            break;
                case 5:  adminAddRider(system, sc);                 break;
                case 6:  adminAssignRiderToOrder(system, sc);       break;
                case 7:  adminViewAllOrders(system);                break;
                case 8:  adminShowTopCustomer(system);              break;
                case 9:  adminShowTopRider(system);                 break;
                case 10: adminToggleRestaurantStatus(system, sc);   break;
                case 11: adminRunAllSorts(system);                  break;
                case 12: adminSortRidersByDeliveries(system);       break;
                case 13: adminSortCustomersByFirstName(system);     break;  
                case 14: adminSortOrdersByDate(system);             break;
                case 15: adminPrintAllCustomers(system);            break;
                case 16: adminSortCustomersByBalanceRef(system);    break;
                case 17: adminListCustomerFirstNames(system);       break;
                case 18: adminGenerateReports(system);              break;
                case 19: adminStreamOpenRestaurants(system);        break;
                case 20: adminStreamPremiumRestaurants(system);     break;
                case 21: adminStreamAvailableRiders(system);        break;
                case 22: adminStreamTotalPaid(system);              break;
                case 23: adminPredicateRichCustomers(system);       break;
                case 0:  stay = false;                              break;
            }
        }
    }

    // Admin Option 1: Add a customer 
    private static void adminAddCustomer(DeliveryDataBase system, Scanner sc) {
        System.out.println("\n--- Add New Customer ---");
        int code = InputHelper.readPositiveInt(sc, "Customer code: ");
        if (system.findCustomerByCode(code) != null) {
            System.out.println("A customer with code " + code + " already exists. Operation cancelled.");
            return;
        }
        String firstName = InputHelper.readLettersOnly(sc, "First name: ");
        String lastName  = InputHelper.readLettersAndSpaces(sc, "Last name: ");
        String street    = InputHelper.readNonEmptyString(sc, "Street: ");       
        String city      = InputHelper.readLettersAndSpaces(sc, "City: ");
        String zipCode   = InputHelper.readDigitsOnly(sc, "Zip code: ", 5, 7);
        String phone     = InputHelper.readValidPhone(sc, "Phone: ");       
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
    private static void adminAddRestAdmin(DeliveryDataBase system, Scanner sc) {
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
    private static void adminAssignManagerToRestaurant(DeliveryDataBase system, Scanner sc) {
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
    private static void adminAddRestaurant(DeliveryDataBase system, Scanner sc) {
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
        // The restaurant is created, now we add it to the system. The system will add it to the master list and
        // also to the assigned manager's list if applicable (if no manager is assigned, it's just in the master list).

        if (system.addRestaurant(restaurant)) {
            System.out.println("Restaurant added successfully:");
            System.out.println("  " + restaurant);
        } else {
            System.out.println("Failed to add restaurant (unexpected error).");
        }
    }

    // Admin Option 5: Add a rider
    private static void adminAddRider(DeliveryDataBase system, Scanner sc) {
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
    private static void adminAssignRiderToOrder(DeliveryDataBase system, Scanner sc) {
        System.out.println("\n--- Assign Rider to Order ---");
        if (system.getOrdersCount() == 0) {
            System.out.println("There are no orders in the system yet. Customers or managers need to create orders first.");
            return;
        }
        String riderId = InputHelper.readValidNationalId(sc, "Rider national ID (9 digits): ");
        int orderCode = InputHelper.readPositiveInt(sc, "Order code: ");

        // Multiple catch blocks: each failure type gets its own friendly message,
        // and finally always runs. The program continues no matter what (Part F).
        try {
            system.assignRiderToOrderChecked(riderId, orderCode);
            System.out.println("Rider successfully assigned to order " + orderCode + ".");
            Order order = system.findOrderByCode(orderCode);
            System.out.println("  " + order);
        } catch (DeliveryPersonUnavailableException e) {
            System.out.println("Could not assign: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Could not assign: " + e.getMessage());
        } finally {
            // Consistency invariant (runs on EVERY exit path): a rider currently
            // carrying at least one ACTIVE order (sent / on the way - not yet
            // delivered) must be marked unavailable. We count only non-delivered
            // orders, so a rider who merely finished past deliveries stays available.
            Rider check = system.findRiderById(riderId);
            if (check != null) {
                int active = 0;
                for (Order o : check.getOrders()) {
                    if (!o.getStatus().equals(Order.STATUS_DELIVERED)) {
                        active++;
                    }
                }
                if (active > 0 && check.isAvailable()) {
                    check.setAvailable(false);
                    System.out.println("[consistency] Rider state corrected: marked unavailable.");
                }
            }
        }
    }
    // Admin Option 7: View all orders in the system

    /**
     * Prints every order in the master list. Uses the {@code orders}
     * ArrayList directly - no filtering. If the admin wants per-customer or
     * per-rider views, those are separate options.
     */
    private static void adminViewAllOrders(DeliveryDataBase system) {
        System.out.println("\n--- All Orders in the System ---");
        ArrayList<Order> all = system.getOrders();
        if (all.isEmpty()) {
            System.out.println("No orders in the system yet.");
            return;
        }
        System.out.println("Total: " + all.size() + " order(s)");
        for (int i = 0; i < all.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + all.get(i));
        }
    }

    // Admin Option 8: Show top customer

    /**
     * Asks the database for the customer with the most orders (function D)
     * and prints them along with their order count.
     */
    private static void adminShowTopCustomer(DeliveryDataBase system) {
        System.out.println("\n--- Customer with the Most Orders ---");
        Customer top = system.getTopCustomer();
        if (top == null) {
            System.out.println("No orders have been placed yet.");
            return;
        }
        // Pull the customer's order list out of the HashMap to report a count.
        ArrayList<Order> theirOrders = system.getOrdersByCustomer().get(top.getCustomerCode());
        int count = (theirOrders == null) ? 0 : theirOrders.size();

        System.out.println("Top customer: " + count + " order(s)");
        System.out.println("  " + top);
    }

    // Admin Option 9: Show top rider

    /**
     * Asks the database for the rider with the most deliveries (function E)
     * and prints them along with their delivered count.
     *
     * "Delivered" here counts only orders whose status is
     * {@code STATUS_DELIVERED} - matching the interpretation we chose in
     * Task 5 for the spec word "ביצע" (performed/completed).
     */
    private static void adminShowTopRider(DeliveryDataBase system) {
        System.out.println("\n--- Rider with the Most Deliveries ---");
        Rider top = system.getTopRider();
        if (top == null) {
            System.out.println("No riders in the system.");
            return;
        }
        int deliveries = 0;
        for (Order o : top.getOrders()) {
            if (Order.STATUS_DELIVERED.equals(o.getStatus())) {
                deliveries++;
            }
        }
        System.out.println("Top rider: " + deliveries + " delivery(ies)");
    }

    // Admin Option 10: Toggle restaurant open/closed

    /**
     * Flips a single restaurant's open/closed flag.
     *
     * Per the assignment, this option exists so the admin can mark a
     * restaurant as closed (e.g. holiday, system outage) or re-open it,
     * without going through an "edit restaurant" workflow.
     */
    private static void adminToggleRestaurantStatus(DeliveryDataBase system, Scanner sc) {
        System.out.println("\n--- Toggle Restaurant Open/Closed ---");
        int code = InputHelper.readPositiveInt(sc, "Restaurant code: ");
        Restaurant rest = system.findRestaurantByCode(code);
        if (rest == null) {
            System.out.println("Restaurant with code " + code + " not found. Operation cancelled.");
            return;
        }
        boolean wasOpen = rest.isOpen();
        rest.setOpen(!wasOpen);                         // flip the boolean
        String newState = rest.isOpen() ? "OPEN" : "CLOSED";
        System.out.println("Restaurant '" + rest.getName() + "' is now " + newState + ".");
    }
    
    /**
     * Admin Option 11: Run all three Part A sorts and display the results.
     *
     * Demonstrates BOTH ways of driving Collections.sort:
     *   - Customers   -> Collections.sort(list)            uses the class's
     *                    natural order (Customer implements Comparable).
     *   - Restaurants -> Collections.sort(list, comparator) uses an external
     *                    RestaurantRatingComparator.
     *   - Orders      -> Collections.sort(list, comparator) uses an external
     *                    OrderPriceComparator.
     *
     * IMPORTANT: each list is COPIED before sorting
     * (new ArrayList<>(system.getXxx())). The getters hand back the database's
     * real internal lists, and sorting one of those in place would permanently
     * reorder the live data. Sorting a copy reorders only our temporary view
     * and leaves the database untouched.
     */
    private static void adminRunAllSorts(DeliveryDataBase system) {
        System.out.println("\n RUN ALL SORTS ");

        // 1) Customers by credit balance (high -> low) via Comparable
        ArrayList<Customer> customersSorted = new ArrayList<>(system.getCustomers());
        Collections.sort(customersSorted);
        System.out.println("\n Customers by credit balance (high to low)");
        if (customersSorted.isEmpty()) {
            System.out.println("(no customers in the system)");
        } else {
            for (Customer c : customersSorted) {
                System.out.println("  " + c.getFirstName() + " " + c.getLastName()
                        + " | balance = " + c.getCreditBalance());
            }
        }

        // 2) Restaurants by rating (high -> low) via Comparator
        ArrayList<Restaurant> restaurantsSorted = new ArrayList<>(system.getRestaurants());
        Collections.sort(restaurantsSorted, new RestaurantRatingComparator());
        System.out.println("\n-- Restaurants by rating (high to low) --");
        if (restaurantsSorted.isEmpty()) {
            System.out.println("(no restaurants in the system)");
        } else {
            for (Restaurant r : restaurantsSorted) {
                System.out.println("  " + r.getName()
                        + " | rating = " + r.getRating());
            }
        }

        // 3) Orders by final price (high -> low) via Comparator
        ArrayList<Order> ordersSorted = new ArrayList<>(system.getOrders());
        Collections.sort(ordersSorted, new OrderPriceComparator());
        System.out.println("\n Orders by final price (high to low) ");
        if (ordersSorted.isEmpty()) {
            System.out.println("(no orders in the system)");
        } else {
            for (Order o : ordersSorted) {
                System.out.println("  Order #" + o.getOrderCode()
                        + " | finalPrice = " + o.getFinalPrice());
            }
        }
    }
    /**
     * Admin Option 12: Sort riders by number of deliveries, using a LAMBDA.
     *
     * Part B requirement: the comparison is written INLINE here, not by
     * reusing a named Comparator class. A Comparator has one abstract method,
     * compare(a, b); the lambda below IS that method's body. The compiler
     * infers the two parameters are Riders from the sort() call's list type.
     *
     * getOrdersCount() returns an int, so we use Integer.compare. We pass the
     * second rider's count first to get a HIGH-to-LOW order (most deliveries
     * first), the same argument-swap trick used in Part A.
     */
    private static void adminSortRidersByDeliveries(DeliveryDataBase system) {
        ArrayList<Rider> ridersSorted = new ArrayList<>(system.getRiders());
        Collections.sort(ridersSorted,
                (r1, r2) -> Integer.compare(r2.getOrdersCount(), r1.getOrdersCount()));

        System.out.println("\n-- Riders by number of deliveries (high to low) --");
        if (ridersSorted.isEmpty()) {
            System.out.println("(no riders in the system)");
        } else {
            for (Rider r : ridersSorted) {
                System.out.println("  " + r.getFullName()
                        + " | deliveries = " + r.getOrdersCount());
            }
        }
    }

    /**
     * Admin Option 13: Sort customers by first name (A-Z), using a LAMBDA.
     *
     * getFirstName() returns a String, and String already knows how to order
     * itself alphabetically via its own compareTo. So the lambda simply
     * delegates to it: a.getFirstName().compareTo(b.getFirstName()). Natural
     * (ascending) String order means A comes before Z — no argument swap here.
     */
    private static void adminSortCustomersByFirstName(DeliveryDataBase system) {
        ArrayList<Customer> customersSorted = new ArrayList<>(system.getCustomers());
        Collections.sort(customersSorted,
                (c1, c2) -> c1.getFirstName().compareTo(c2.getFirstName()));

        System.out.println("\n-- Customers by first name (A to Z) --");
        if (customersSorted.isEmpty()) {
            System.out.println("(no customers in the system)");
        } else {
            for (Customer c : customersSorted) {
                System.out.println("  " + c.getFirstName() + " " + c.getLastName());
            }
        }
    }

    /**
     * Admin Option 14: Sort orders by order date (oldest first), using a LAMBDA.
     *
     * A date is three fields, so the comparison is layered: compare the year
     * first; only if the years are equal does the month decide; only if the
     * months are also equal does the day decide. This "coarse unit first, fall
     * through to finer units on a tie" pattern is how all multi-field ordering
     * works. Ascending order here means earliest date first.
     */
    private static void adminSortOrdersByDate(DeliveryDataBase system) {
        ArrayList<Order> ordersSorted = new ArrayList<>(system.getOrders());
        Collections.sort(ordersSorted, (o1, o2) -> {
            int byYear = Integer.compare(o1.getOrderYear(), o2.getOrderYear());
            if (byYear != 0) {
                return byYear;
            }
            int byMonth = Integer.compare(o1.getOrderMonth(), o2.getOrderMonth());
            if (byMonth != 0) {
                return byMonth;
            }
            return Integer.compare(o1.getOrderDay(), o2.getOrderDay());
        });

        System.out.println("\n-- Orders by date (oldest first) --");
        if (ordersSorted.isEmpty()) {
            System.out.println("(no orders in the system)");
        } else {
            for (Order o : ordersSorted) {
                System.out.println("  Order #" + o.getOrderCode()
                        + " | date = " + o.getOrderDay() + "/"
                        + o.getOrderMonth() + "/" + o.getOrderYear());
            }
        }
    }
    /**
     * Admin Option 15: Print every customer using the method reference
     * System.out::println.
     *
     * forEach expects "something that consumes one element." The lambda
     * c -> System.out.println(c) does exactly one thing: call println on the
     * fixed object System.out. Because the body is a single call whose
     * argument is just the element, it collapses to the reference
     * System.out::println. This is the classic printing example from the
     * assignment.
     */
    private static void adminPrintAllCustomers(DeliveryDataBase system) {
        System.out.println("\n All customers (printed via System.out::println) ");
        if (system.getCustomers().isEmpty()) {
            System.out.println("(no customers in the system)");
        } else {
            system.getCustomers().forEach(System.out::println);
        }
    }

    /**
     * Admin Option 16: Sort customers by their NATURAL order using the
     * method reference Customer::compareTo.
     *
     * This is the "unbound instance method" flavor: Customer is the TYPE, not
     * an object. Used as a Comparator<Customer>, Java maps compare(a, b) onto
     * a.compareTo(b) — the first argument becomes the receiver, the second the
     * parameter. Since Task 1 made compareTo order by credit balance high to
     * low, this sort produces exactly that order, with no lambda body needed.
     */
    private static void adminSortCustomersByBalanceRef(DeliveryDataBase system) {
        ArrayList<Customer> customersSorted = new ArrayList<>(system.getCustomers());
        Collections.sort(customersSorted, Customer::compareTo);

        System.out.println("\n Customers by balance (Customer::compareTo, high to low) ");
        if (customersSorted.isEmpty()) {
            System.out.println("(no customers in the system)");
        } else {
            for (Customer c : customersSorted) {
                System.out.println("  " + c.getFirstName() + " " + c.getLastName()
                        + " | balance = " + c.getCreditBalance());
            }
        }
    }

    /**
     * Admin Option 17: List customers' first names using the method reference
     * Customer::getFirstName as a key extractor.
     *
     * A Function<Customer, String> means "given a Customer, produce a String."
     * The lambda c -> c.getFirstName() is a single call on the element, so it
     * collapses to Customer::getFirstName. We then apply that function to each
     * customer to print just the first name — a genuine key-extractor use that
     * does not rely on Comparator.comparing.
     */
    private static void adminListCustomerFirstNames(DeliveryDataBase system) {
        Function<Customer, String> nameOf = Customer::getFirstName;

        System.out.println("\n Customer first names (via Customer::getFirstName) ");
        if (system.getCustomers().isEmpty()) {
            System.out.println("(no customers in the system)");
        } else {
            for (Customer c : system.getCustomers()) {
                System.out.println("  " + nameOf.apply(c));
            }
        }
    }
    
    /**
     * Admin Option 18: Exercise all four SystemReports methods (Part D).
     *
     * Demonstrates each wildcard/generic signature against real system data,
     * so the feature is reachable from the menu (not buried in main).
     */
    private static void adminGenerateReports(DeliveryDataBase system) {
        SystemReports reports = new SystemReports();
        System.out.println("\n===== SYSTEM REPORTS (Wildcards) =====");

        // 1) ? extends Restaurant : pass the live ArrayList<Restaurant>.
        System.out.println("\n-- All restaurants (? extends Restaurant) --");
        reports.displayAllRestaurants(system.getRestaurants());

        // 2) ? extends Order : sum every order's final price.
        double total = reports.sumFinalPrices(system.getOrders());
        System.out.println("\n-- Sum of all final prices (? extends Order) --");
        System.out.println("  Total = " + total);

        // 3) ? super FastFoodRestaurant : add into a SUPERTYPE list to prove
        //    the wildcard accepts ArrayList<Restaurant>, not just
        //    ArrayList<FastFoodRestaurant>. We use a throwaway list so the real
        //    database is untouched.
        ArrayList<Restaurant> demoList = new ArrayList<>();
        System.out.println("\n Add FastFood into a supertype list (? super FastFoodRestaurant) ");
        System.out.println("  demo list size before = " + demoList.size());
        FastFoodRestaurant demoFF = new FastFoodRestaurant(
                9999, "Demo Burger", "American", 4.2, true, 10.0, 12, 5.0);
        reports.addFastFoodRestaurant(demoList, demoFF);
        System.out.println("  demo list size after  = " + demoList.size()
                + "  (added: " + demoFF.getName() + ")");

        // 4) Generic findMax over any Comparable. We use the customers' credit
        //    balances (Double implements Comparable<Double>), so "largest" is
        //    intuitive: the highest balance in the system.
        ArrayList<Double> balances = new ArrayList<>();
        for (Customer c : system.getCustomers()) {
            balances.add(c.getCreditBalance());
        }
        Double maxBalance = reports.findMax(balances);
        System.out.println("\n-- Highest credit balance (generic findMax) --");
        if (maxBalance == null) {
            System.out.println("  (no customers in the system)");
        } else {
            System.out.println("  Max balance = " + maxBalance);
        }
    }
    
    /**
     * Admin Option 19 [Stream]: show every OPEN restaurant.
     *
     * Pure Stream pipeline, no loops (Part E rule):
     *   stream()  -> turn the restaurant list into a stream
     *   filter()  -> keep only those where isOpen() is true (a Predicate)
     *   collect() -> gather the survivors into a List
     * The result is then printed with forEach + the System.out::println
     * method reference - another loop-free terminal operation.
     */
    private static void adminStreamOpenRestaurants(DeliveryDataBase system) {
        List<Restaurant> open = system.getRestaurants().stream()
                .filter(r -> r.isOpen())
                .collect(Collectors.toList());

        System.out.println("\n-- Open restaurants (Stream) --");
        if (open.isEmpty()) {
            System.out.println("(no open restaurants)");
        } else {
            open.forEach(System.out::println);
        }
    }

    /**
     * Admin Option 20 [Stream]: show every PREMIUM restaurant.
     *
     * Same shape, but the filter narrows by TYPE: r instanceof
     * PremiumRestaurant keeps only premium ones. Each element stays typed as
     * Restaurant, which is fine because we only display it (toString dispatches
     * polymorphically to the Premium version).
     */
    private static void adminStreamPremiumRestaurants(DeliveryDataBase system) {
        List<Restaurant> premium = system.getRestaurants().stream()
                .filter(r -> r instanceof PremiumRestaurant)
                .collect(Collectors.toList());

        System.out.println("\n-- Premium restaurants (Stream) --");
        if (premium.isEmpty()) {
            System.out.println("(no premium restaurants)");
        } else {
            premium.forEach(System.out::println);
        }
    }

    /**
     * Admin Option 21 [Stream]: show every AVAILABLE rider.
     *
     * filter() by isAvailable(); collect() into a List; print loop-free.
     */
    private static void adminStreamAvailableRiders(DeliveryDataBase system) {
        List<Rider> available = system.getRiders().stream()
                .filter(r -> r.isAvailable())
                .collect(Collectors.toList());

        System.out.println("\n-- Available riders (Stream) --");
        if (available.isEmpty()) {
            System.out.println("(no available riders)");
        } else {
            available.forEach(System.out::println);
        }
    }

    /**
     * Admin Option 22 [Stream]: total money paid across ALL orders.
     *
     * This pipeline ends in a numeric reduction instead of a collection:
     *   stream()        -> stream of Orders
     *   mapToDouble()   -> convert each Order to its finalPrice, giving a
     *                      DoubleStream (a stream of primitive doubles)
     *   sum()           -> terminal operation that adds them all up
     * No accumulator variable, no loop - the stream does the reduction.
     */
    private static void adminStreamTotalPaid(DeliveryDataBase system) {
        double total = system.getOrders().stream()
                .mapToDouble(o -> o.getFinalPrice())
                .sum();

        System.out.println("\n-- Total money paid for all orders (Stream) --");
        System.out.println("  Total = " + total);
    }
    /**
     * Admin Option 23 [Predicate]: split customers by a balance threshold,
     * using an explicit, reusable Predicate (HW3 Part G bonus).
     *
     * A Predicate<Customer> is a named boolean test: given a Customer, is the
     * condition true? Defining it once (instead of inlining the lambda) lets us
     * reuse it AND compose it:
     *   - richEnough            : balance is strictly greater than 100
     *   - richEnough.negate()   : the exact opposite test, derived for free
     *
     * We apply the predicate inside a stream filter, so the whole thing stays
     * loop-free, consistent with the Stream part.
     */
    private static void adminPredicateRichCustomers(DeliveryDataBase system) {
        // The reusable test required by Part G.
        Predicate<Customer> richEnough = c -> c.getCreditBalance() > 100;

        List<Customer> above = system.getCustomers().stream()
                .filter(richEnough)
                .collect(Collectors.toList());

        // Composition: negate() gives us the complementary test with no rewrite.
        List<Customer> belowOrEqual = system.getCustomers().stream()
                .filter(richEnough.negate())
                .collect(Collectors.toList());

        System.out.println("\n-- Customers with balance over 100 (Predicate) --");
        if (above.isEmpty()) {
            System.out.println("(none)");
        } else {
            above.forEach(c -> System.out.println("  " + c.getFirstName() + " "
                    + c.getLastName() + " | balance = " + c.getCreditBalance()));
        }

        System.out.println("\n-- Customers with balance 100 or below (Predicate.negate) --");
        if (belowOrEqual.isEmpty()) {
            System.out.println("(none)");
        } else {
            belowOrEqual.forEach(c -> System.out.println("  " + c.getFirstName() + " "
                    + c.getLastName() + " | balance = " + c.getCreditBalance()));
        }
    }
    
    /**
     * Restaurant Manager sub-menu loop. Per the assignment, a RestAdmin can:
     *   1. Add a customer
     *   2. Add an order (ONLY for restaurants they are responsible for;
     *      now also checks the customer's balance and deducts payment)
     *   3. Add a rider
     *   4. Assign a rider to an order
     *   5. Show all orders of a specific restaurant they manage
     *   6. Show all open restaurants matching a cuisine type
     *   0. Logout (return to main menu)
     *
     * Important rules:
     *   - The order code is AUTO-GENERATED by DeliveryDataBase.generateOrderCode().
     *     The user never enters their own order code.
     *   - When adding an order, only the restaurant CODE is asked (not the full
     *     restaurant). The system then looks up the restaurant and verifies
     *     that this specific manager is responsible for it.
     *   - Orders can be created without a rider; rider is assigned later via
     *     option 4 (or by the system admin).
     */
    private static void showRestAdminMenu(DeliveryDataBase system, RestAdmin manager, Scanner sc) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n===== RESTAURANT MANAGER MENU =====");
            System.out.println("Logged in as: " + manager.getManagerName()
                    + " (manages " + manager.getCount() + " restaurant(s))");
            System.out.println("1. Add a customer");
            System.out.println("2. Add an order");
            System.out.println("3. Add a rider");
            System.out.println("4. Assign rider to an order");
            System.out.println("5. Show all orders of one of my restaurants");
            System.out.println("6. Show open restaurants by cuisine");
            System.out.println("0. Logout");

            int choice = InputHelper.readIntInRange(sc, "Choose an option (0-6): ", 0, 6);
            switch (choice) {
                case 1: adminAddCustomer(system, sc);                          break;
                case 2: restAdminAddOrder(system, manager, sc);                break;
                case 3: adminAddRider(system, sc);                             break;
                case 4: adminAssignRiderToOrder(system, sc);                   break;
                case 5: restAdminShowOrdersForRestaurant(system, manager, sc); break;
                case 6: restAdminShowOpenRestaurantsByCuisine(system, manager, sc); break;
                case 0: stay = false;                                          break;
            }
        }
    }

    //RestAdmin Option 2: Add an order

    /**
     * Creates a new order on behalf of a customer.
     *
     * Validation flow (Assignment 2 ):
     *   1. Look up customer by code (must exist).
     *   2. Look up restaurant by code (must exist).
     *   3. Restaurant must currently be open.
     *   4. THIS manager must be responsible for that restaurant.
     *   5. {@link #placeOrder} additionally checks:
     *        - Premium minimum is met (if applicable).
     *        - Customer has enough balance.
     *      And on success it creates the order, updates ALL FOUR collections,
     *      and deducts the payment from the customer's balance.
     *
     * The order starts with status="sent", no rider, no delivery date.
     */
    private static void restAdminAddOrder(DeliveryDataBase system, RestAdmin manager, Scanner sc) {
        System.out.println("\n--- Add New Order ---");

        // Step 1: customer
        int customerCode = InputHelper.readPositiveInt(sc, "Customer code: ");
        Customer customer = system.findCustomerByCode(customerCode);
        if (customer == null) {
            System.out.println("Customer with code " + customerCode + " not found. Operation cancelled.");
            return;
        }

        // Step 2: restaurant
        int restaurantCode = InputHelper.readPositiveInt(sc, "Restaurant code: ");
        Restaurant restaurant = system.findRestaurantByCode(restaurantCode);
        if (restaurant == null) {
            System.out.println("Restaurant with code " + restaurantCode + " not found. Operation cancelled.");
            return;
        }

        // Step 3: restaurant open?
        if (!restaurant.isOpen()) {
            System.out.println("Restaurant '" + restaurant.getName()
                    + "' is currently CLOSED. Operation cancelled.");
            return;
        }

        // Step 4: manager responsible for this restaurant?
        if (!manager.isResponsibleFor(restaurantCode)) {
            System.out.println("You are NOT responsible for restaurant " + restaurantCode + ".");
            System.out.println("Operation cancelled.");
            return;
        }

        // Step 5: basic amount and order date
        double basicAmount = InputHelper.readPositiveDouble(sc, "Basic amount: ");
        System.out.println("Enter order date:");
        int day   = InputHelper.readIntInRange(sc, "  Day (1-31): ",    1, 31);
        int month = InputHelper.readIntInRange(sc, "  Month (1-12): ",  1, 12);
        int year  = InputHelper.readIntInRange(sc, "  Year (2000-2026): ", 2000, 2026);

        // Step 6: delegate to the shared placement helper.
        // placeOrder handles: premium minimum, balance check, order creation,
        // 4-collection update, balance deduction.
        Order order = placeOrder(system, customer, restaurant, day, month, year, basicAmount);
        if (order == null) {
            // placeOrder already printed the reason; nothing else to do.
            return;
        }

        System.out.println("Order created successfully!");
        System.out.println("  Order code (auto): " + order.getOrderCode());
        System.out.println("  Final price ("
                + restaurant.getClass().getSimpleName() + "): " + order.getFinalPrice());
        System.out.println("  Customer '" + customer.getFirstName()
                + "' balance after payment: " + customer.getCreditBalance());
        System.out.println("  " + order);
    }

    //  Shared placement helper

    /**
     * The complete "place an order" flow as defined by the Assignment 2 .
     * Called from {@link #restAdminAddOrder} AND from the customer's
     * own "Place new order" menu.
     *
     * Validation:
     *   - If the restaurant is a {@link PremiumRestaurant} and the basic
     *     amount is below its minimum, the final price comes back as 0 -
     *     we reject the order with a clear message rather than create a
     *     phantom "free" order.
     *   - If the customer's balance is below the final price, we reject
     *     with a clear message.
     *
     * Side effects on success:
     *   1. A new Order is created with an auto-generated code.
     *   2. {@code customer.withdraw(finalPrice)} - balance reduced.
     *   3. ALL FOUR collections updated in lock-step:
     *        - master orders ArrayList,
     *        - per-customer orders HashMap,
     *        - per-customer restaurants Hashtable,
     *        - per-customer payments HashMap.
     *
     * @return the newly created Order, or null if the order was rejected
     */
    private static Order placeOrder(DeliveryDataBase system, Customer customer,
                                    Restaurant restaurant, int day, int month, int year,
                                    double basicAmount) {
        // Preview the final price WITHOUT creating the order yet.
        // Restaurant.calculateFinalPrice is polymorphic - this same call
        // dispatches to FastFood / Premium overrides at runtime.
        double finalPrice = restaurant.calculateFinalPrice(basicAmount);

        // Reject "free" premium orders that fall below the minimum amount.
        if (finalPrice == 0.0 && restaurant instanceof PremiumRestaurant) {
            PremiumRestaurant pr = (PremiumRestaurant) restaurant;
            System.out.println("Restaurant '" + restaurant.getName()
                    + "' requires a minimum order of " + pr.getMinimumOrderAmount()
                    + ". You entered " + basicAmount + ". Operation cancelled.");
            return null;
        }

        // Insufficient balance.
        if (customer.getCreditBalance() < finalPrice) {
            System.out.println("Customer balance (" + customer.getCreditBalance()
                    + ") is less than the final price (" + finalPrice + "). Operation cancelled.");
            return null;
        }

        // All checks passed - create the order and commit changes.
        int orderCode = system.generateOrderCode();
        Order order = new Order(orderCode, customer.getCustomerCode(), restaurant,
                                restaurant.getRestaurantCode(), day, month, year, basicAmount);

        // Deduct the payment from the customer's balance.
        // withdraw() returns false if the amount is invalid, but we already
        // validated above - so this should always succeed here.
        customer.withdraw(finalPrice);

        // Update ALL FOUR collections - the data consistency discipline.
        // Same four-step sequence the seed code uses; if you ever change
        // this list of collections, update both places.
        system.addOrder(order);
        system.addOrderToCustomer(customer.getCustomerCode(), order);
        system.addRestaurantToCustomer(customer.getCustomerCode(), restaurant);
        system.addPaymentForCustomer(customer.getCustomerCode(), finalPrice);

        return order;
    }

    //  RestAdmin Option 5: Show all orders for one of my restaurants

    /**
     * Lists all orders for a given restaurant code. Restricted to restaurants
     * the manager is actually responsible for - a manager should not be able
     * to inspect another restaurant's orders.
     */
    private static void restAdminShowOrdersForRestaurant(DeliveryDataBase system, RestAdmin manager, Scanner sc) {
        System.out.println("\n--- Show All Orders for a Restaurant ---");
        int restCode = InputHelper.readPositiveInt(sc, "Restaurant code: ");
        Restaurant rest = system.findRestaurantByCode(restCode);
        if (rest == null) {
            System.out.println("Restaurant with code " + restCode + " not found.");
            return;
        }
        if (!manager.isResponsibleFor(restCode)) {
            System.out.println("You are NOT responsible for restaurant " + restCode
                    + ". You can only view orders for your own restaurants.");
            return;
        }

        // Filter the master orders list by restaurant code.
        ArrayList<Order> matching = new ArrayList<>();
        for (Order o : system.getOrders()) {
            if (o.getRestaurantCode() == restCode) matching.add(o);
        }

        if (matching.isEmpty()) {
            System.out.println("No orders found for restaurant '" + rest.getName() + "'.");
            return;
        }
        System.out.println("Total: " + matching.size() + " order(s) for '" + rest.getName() + "':");
        for (int i = 0; i < matching.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + matching.get(i));
        }
    }

    //  RestAdmin Option 6: Show open restaurants by cuisine 

    /**
     * Asks the database for all OPEN restaurants matching a given cuisine
     * type (case-insensitive). Delegates to query function F implemented
     * in Task 5.
     */
    private static void restAdminShowOpenRestaurantsByCuisine(DeliveryDataBase system,
                                                              RestAdmin manager, Scanner sc) {
        System.out.println("\n--- My Open Restaurants by Cuisine ---");
        String cuisine = InputHelper.readNonEmptyString(sc, "Cuisine type: ");

        // System-wide open restaurants of this cuisine (query F)...
        ArrayList<Restaurant> open = system.getOpenRestaurantsByCuisine(cuisine);
        // ...then keep ONLY those this manager is responsible for.
        ArrayList<Restaurant> mine = new ArrayList<>();
        for (Restaurant r : open) {
            if (manager.isResponsibleFor(r.getRestaurantCode())) {
                mine.add(r);
            }
        }

        if (mine.isEmpty()) {
            System.out.println("You have no open restaurants with cuisine '" + cuisine + "'.");
            return;
        }
        System.out.println("Total: " + mine.size() + " of your open restaurant(s) with cuisine '"
                + cuisine + "':");
        for (int i = 0; i < mine.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + mine.get(i));
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
     *
     *  ASSIGNMENT 2 ADDITION:
     *   - A new option 2 prints ONLY the rider's ACTIVE orders (status
     *     "sent" or "on the way"). The old "show all" option moves to
     *     option 3. Together they let the rider see "what do I need to
     *     do right now?" vs "what have I touched in total?"
     */
    private static void showRiderMenu(DeliveryDataBase system, Rider rider, Scanner sc) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n===== RIDER MENU =====");
            System.out.println("Logged in as: " + rider.getFullName()
                    + " (" + rider.getOrdersCount() + " assigned order(s))");
            System.out.println("1. Update order status");
            System.out.println("2. Show my active orders only");
            System.out.println("3. Show all of my orders (history)");
            System.out.println("0. Logout");

            int choice = InputHelper.readIntInRange(sc, "Choose an option (0-3): ", 0, 3);
            switch (choice) {
                case 1: riderUpdateOrderStatus(rider, sc);     break;
                case 2: riderShowActiveOrders(system, rider);  break;
                case 3: riderShowAllOrders(rider);             break;
                case 0: stay = false;                          break;
            }
        }
    }

    //  Rider Option 2: Show only active orders

    /**
     * Prints the rider's currently active orders - i.e. those still in
     * the system (status "sent" or "on the way", NOT yet delivered).
     *
     * Delegates to {@link DeliveryDataBase#getActiveOrdersForRider(String)}
     * which is the spec's required query function "B" (Task 5).
     *
     * Why a separate option from "show all": in the assignment's mental
     * model the rider has at most one active order at a time (they go
     * back to "available" the moment a delivery completes), so this view
     * answers the question "what's on my plate right now?" without
     * scrolling past 50 delivered orders from last month.
     */
    private static void riderShowActiveOrders(DeliveryDataBase system, Rider rider) {
        System.out.println("\n--- My Active Orders ---");
        ArrayList<Order> active = system.getActiveOrdersForRider(rider.getId());
        if (active.isEmpty()) {
            System.out.println("You have no active orders right now.");
            return;
        }
        System.out.println("Total: " + active.size() + " active order(s)");
        for (int i = 0; i < active.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + active.get(i));
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
     * Helper: searches the rider's orders list for the given order code.
     *
     * @return the Order if found in this rider's list, or null otherwise
     */
    private static Order findOrderInRider(Rider rider, int orderCode) {
        for (Order order : rider.getOrders()) {
            if (order.getOrderCode() == orderCode) {
                return order;
            }
        }
        return null;
    }

    //  Rider Option 2: Show all orders
    /**
     * Prints all orders currently assigned to this rider, using the rider's
     * own orders list. Each order is printed via toString().
     */
    private static void riderShowAllOrders(Rider rider) {
        System.out.println("\n--- My Orders ---");
        ArrayList<Order> orders = rider.getOrders();
        if (orders.isEmpty()) {
            System.out.println("You have no orders assigned yet.");
            return;
        }
        System.out.println("Total: " + orders.size() + " order(s)");
        for (int i = 0; i < orders.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + orders.get(i));
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
     *     DeliveryDataBase.generateOrderCode(). The customer never enters
     *     their own order code.
     *   - The customer enters only the basic amount. The final price is
     *     computed POLYMORPHICALLY inside Order.setBasicAmount() by calling
     *     the linked restaurant's calculateFinalPrice() method.
     *   - "View all of my orders" reads from the per-customer HashMap
     *     (Assignment 2 requirement), not by filtering the master list.
     *   - "Update personal info" exposes PHONE, EMAIL and ADDRESS - the
     *     three fields the Assignment 2  permits a customer to change.
     */
    private static void showCustomerMenu(DeliveryDataBase system, Customer customer, Scanner sc) {
        boolean stay = true;
        while (stay) {
            System.out.println("\n===== CUSTOMER MENU =====");
            System.out.println("Logged in as: " + customer.getFirstName()
                    + " " + customer.getLastName()
                    + " (code " + customer.getCustomerCode() + ")");
            System.out.println("1. Place a new order");
            System.out.println("2. View all of my orders");
            System.out.println("3. Update personal info (phone / email / address)");
            System.out.println("4. Show all restaurants I've ordered from");
            System.out.println("5. Show premium restaurants I've ordered from");
            System.out.println("6. Show my balance");
            System.out.println("7. Load money to my balance");
            System.out.println("8. Withdraw money from my balance");
            System.out.println("0. Logout");

            int choice = InputHelper.readIntInRange(sc, "Choose an option (0-8): ", 0, 8);
            switch (choice) {
                case 1: customerPlaceOrder(system, customer, sc);             break;
                case 2: customerViewOrders(system, customer);                 break;
                case 3: customerUpdatePersonalInfo(customer, sc);             break;
                case 4: customerShowRestaurantsOrderedFrom(system, customer); break;
                case 5: customerShowPremiumRestaurants(system, customer);     break;
                case 6: customerShowBalance(customer);                        break;
                case 7: customerTopUp(customer, sc);                          break;
                case 8: customerWithdraw(customer, sc);                       break;
                case 0: stay = false;                                         break;
            }
        }
    }

    // Customer Option 1: Place a new order

    /**
     * Creates a new order for this customer using the shared {@link #placeOrder}
     * helper - the SAME method the RestAdmin add-order flow uses (Task 8).
     *
     * The customer can order from ANY open restaurant (no "responsibility"
     * check - that restriction only applies to managers). placeOrder handles
     * the premium-minimum check, the balance check, order creation, the
     * four-collection update, and the balance deduction.
     */
    private static void customerPlaceOrder(DeliveryDataBase system, Customer customer, Scanner sc) {
        System.out.println("\n--- Place a New Order ---");

        int restaurantCode = InputHelper.readPositiveInt(sc, "Restaurant code: ");

        // try / catch for the whole order attempt (HW3 Part F).
        // Two custom exceptions can fire here: RestaurantNotFoundException from
        // the throwing lookup, and InsufficientBalanceException from the
        // pre-check. Either one is reported friendly-style; the program never
        // crashes and always returns cleanly to the customer menu.
        try {
            // Throws RestaurantNotFoundException if the code is unknown.
            Restaurant restaurant = system.findRestaurantByCodeOrThrow(restaurantCode);

            if (!restaurant.isOpen()) {
                System.out.println("Sorry - this restaurant is currently closed. Operation cancelled.");
                return;
            }
            System.out.println("Restaurant: " + restaurant.getName()
                    + " (" + restaurant.getClass().getSimpleName() + ")");
            System.out.println("Your current balance: " + customer.getCreditBalance());

            double basicAmount = InputHelper.readPositiveDouble(sc, "Basic amount: ");
            System.out.println("Enter order date:");
            int day   = InputHelper.readIntInRange(sc, "  Day (1-31): ",       1, 31);
            int month = InputHelper.readIntInRange(sc, "  Month (1-12): ",     1, 12);
            int year  = InputHelper.readIntInRange(sc, "  Year (2000-2026): ", 2000, 2026);

            // Preview the polymorphic final price so we can verify funds BEFORE
            // committing anything.
            double finalPrice = restaurant.calculateFinalPrice(basicAmount);

            // A premium order below its minimum prices to 0 - let the shared
            // helper handle that specific message (it already does), so we only
            // run the balance check for genuine, priced orders.
            if (!(finalPrice == 0.0 && restaurant instanceof PremiumRestaurant)) {
                // Throws InsufficientBalanceException if funds are short.
                system.verifyBalanceOrThrow(customer, finalPrice);
            }

            // Shared helper (unchanged): creates the order, deducts payment,
            // updates all four collections. Returns null only for the premium
            // minimum case, which it explains itself.
            Order order = placeOrder(system, customer, restaurant, day, month, year, basicAmount);
            if (order == null) {
                return;
            }

            System.out.println("\nYour order has been placed!");
            System.out.println("  Order code (auto): " + order.getOrderCode());
            System.out.println("  Final price (" + restaurant.getClass().getSimpleName()
                    + "): " + order.getFinalPrice());
            System.out.println("  Status: " + order.getStatus());
            System.out.println("  Balance remaining: " + customer.getCreditBalance());
            System.out.println("  Note: A rider will be assigned shortly.");

        } catch (RestaurantNotFoundException e) {
            System.out.println("Could not place order: " + e.getMessage());
        } catch (InsufficientBalanceException e) {
            System.out.println("Could not place order: " + e.getMessage());
        }
    }
    // Customer Option 2: View all my orders (FROM the HashMap)

    /**
     * Lists every order belonging to this customer.
     *
     * Assignment 2 requirement: the data is read DIRECTLY from the
     * per-customer HashMap ({@code ordersByCustomer}), not by filtering the
     * master orders list. This is the O(1) lookup the Map was built for.
     *
     * Because a customer who has never ordered has no entry in the map,
     * we must null-check the result before using it.
     */
    private static void customerViewOrders(DeliveryDataBase system, Customer customer) {
        System.out.println("\n--- My Orders ---");
        ArrayList<Order> myOrders = system.getOrdersByCustomer().get(customer.getCustomerCode());
        if (myOrders == null || myOrders.isEmpty()) {
            System.out.println("You have not placed any orders yet.");
            return;
        }
        System.out.println("Total: " + myOrders.size() + " order(s)");
        for (int i = 0; i < myOrders.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + myOrders.get(i));
        }
    }

    //  Customer Option 3: Update personal info (PHONE / EMAIL / ADDRESS)

    /**
     * Lets the customer update PHONE, EMAIL or ADDRESS - the three fields
     * the Assignment 2 specifically allows customers to change. The customer can update one 
     * field at a time, or cancel without changes. Each field is validated by the input helper
     * to ensure correct formats (e.g. phone must be digits, email must contain "@", etc).
     *
     * Other Customer setters exist but this menu never calls them, so the
     * customer cannot change their code, name, or balance from here.
     */
    private static void customerUpdatePersonalInfo(Customer customer, Scanner sc) {
        System.out.println("\n--- Update Personal Info ---");
        System.out.println("Current phone:   " + customer.getPhone());
        System.out.println("Current email:   " + customer.getEmail());
        System.out.println("Current address: " + customer.getStreet() + ", "
                + customer.getCity() + ", " + customer.getZipCode());
        System.out.println();
        System.out.println("What would you like to update?");
        System.out.println("  1. Phone");
        System.out.println("  2. Email");
        System.out.println("  3. Address (street, city, zip)");
        System.out.println("  0. Cancel");

        int choice = InputHelper.readIntInRange(sc, "Choice (0-3): ", 0, 3);
        switch (choice) {
            case 0:
                System.out.println("No changes made.");
                return;
            case 1:
                customer.setPhone(InputHelper.readValidPhone(sc, "New phone: "));
                System.out.println("Phone updated.");
                break;
            case 2:
                customer.setEmail(InputHelper.readValidEmail(sc, "New email: "));
                System.out.println("Email updated.");
                break;
            case 3:
                String newStreet  = InputHelper.readNonEmptyString(sc, "New street: ");
                String newCity    = InputHelper.readLettersAndSpaces(sc, "New city: ");
                String newZipCode = InputHelper.readDigitsOnly(sc, "New zip code: ", 5, 7);
                customer.setStreet(newStreet);
                customer.setCity(newCity);
                customer.setZipCode(newZipCode);
                System.out.println("Address updated.");
                break;
        }
        System.out.println("Final state of your record:");
        System.out.println("  " + customer);
    }

    //  Customer Option 4: Show all restaurants I've ordered from (FROM the Hashtable)

    /**
     * Lists every distinct restaurant this customer has ordered from.
     *
     * Assignment 2 requirement: the data is read from the per-customer
     * {@code restaurantsByCustomer} Hashtable. The Hashtable already
     * deduplicates (Task 4's addRestaurantToCustomer skips repeats), so a
     * customer who ordered from one place five times sees it once here.
     */
    private static void customerShowRestaurantsOrderedFrom(DeliveryDataBase system, Customer customer) {
        System.out.println("\n--- Restaurants I've Ordered From ---");
        ArrayList<Restaurant> mine = system.getRestaurantsByCustomer().get(customer.getCustomerCode());
        if (mine == null || mine.isEmpty()) {
            System.out.println("You have not ordered from any restaurant yet.");
            return;
        }
        System.out.println("Total: " + mine.size() + " restaurant(s)");
        for (int i = 0; i < mine.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + mine.get(i));
        }
    }

    //  Customer Option 5: Show ONLY premium restaurants I've ordered from

    /**
     * Lists only the PREMIUM restaurants this customer has ordered from.
     *
     * Assignment 2 explicitly requires using the dedicated DeliveryDataBase
     * function for this - that's query "C",
     * {@link DeliveryDataBase#getPremiumRestaurantsForCustomer(Customer)},
     * built in Task 5. The instanceof filtering lives inside that method;
     * this menu code just calls it and prints.
     */
    private static void customerShowPremiumRestaurants(DeliveryDataBase system, Customer customer) {
        System.out.println("\n--- Premium Restaurants I've Ordered From ---");
        ArrayList<Restaurant> premium = system.getPremiumRestaurantsForCustomer(customer);
        if (premium.isEmpty()) {
            System.out.println("You have not ordered from any premium restaurant yet.");
            return;
        }
        System.out.println("Total: " + premium.size() + " premium restaurant(s)");
        for (int i = 0; i < premium.size(); i++) {
            System.out.println("  " + (i + 1) + ". " + premium.get(i));
        }
    }

    //  Customer Option 6: Show balance

    /** Prints the customer's current credit balance. */
    private static void customerShowBalance(Customer customer) {
        System.out.println("\n--- My Balance ---");
        System.out.println("Current balance: " + customer.getCreditBalance());
    }

    //  Customer Option 7: Load money to balance

    /**
     * Adds money to the customer's balance via {@link Customer#topUp(double)}.
     * The amount is validated to be positive by both the input helper and
     * topUp itself (defence in depth).
     */
    private static void customerTopUp(Customer customer, Scanner sc) {
        System.out.println("\n--- Load Money to Balance ---");
        System.out.println("Current balance: " + customer.getCreditBalance());
        double amount = InputHelper.readPositiveDouble(sc, "Amount to load: ");
        if (customer.topUp(amount)) {
            System.out.println("Loaded " + amount + ". New balance: " + customer.getCreditBalance());
        } else {
            System.out.println("Invalid amount. No change made.");
        }
    }

    //  Customer Option 8: Withdraw money from balance

    /**
     * Removes money from the customer's balance via
     * {@link Customer#withdraw(double)}, which refuses to overdraw. We report
     * the outcome based on the boolean it returns.
     */
    private static void customerWithdraw(Customer customer, Scanner sc) {
        System.out.println("\n--- Withdraw Money from Balance ---");
        System.out.println("Current balance: " + customer.getCreditBalance());
        double amount = InputHelper.readPositiveDouble(sc, "Amount to withdraw: ");
        if (customer.withdraw(amount)) {
            System.out.println("Withdrew " + amount + ". New balance: " + customer.getCreditBalance());
        } else {
            System.out.println("Cannot withdraw " + amount
                    + " - insufficient balance or invalid amount. No change made.");
        }
    }


    private static void seedInitialData(DeliveryDataBase system) {
        seedRiders(system);
        seedCustomers(system);
        seedRestaurants(system);
        seedRestAdminsAndAssign(system);
        seedOrders(system);                 // NEW: Assignment 2 requires >=10 orders.
        System.out.println("System initialized successfully!");
        System.out.println("  - " + system.getCustomersCount() + " customers");
        System.out.println("  - " + system.getRestaurantsCount() + " restaurants");
        System.out.println("  - " + system.getRidersCount() + " riders");
        System.out.println("  - " + system.getRestAdminsCount() + " managers");
        System.out.println("  - " + system.getOrdersCount() + " orders");
    }

    /**
     * Builds 11 seed orders that exercise every collection at boot time:
     *   - the master orders list,
     *   - the per-customer HashMap,
     *   - the per-customer Hashtable of restaurants,
     *   - the per-customer payments HashMap,
     *   - each rider's own orders list.
     *
     * The orders are also deliberately distributed so the new queries
     * have meaningful answers from the start:
     *   - Customer 1001 (Mohamad) places 3 orders -> top customer.
     *   - Rider 204050601 (Mohamad Saeed) delivers 5 orders -> top rider.
     *   - Two orders sit "on the way" and two sit "sent" (unassigned),
     *     so the admin/manager menus have something to act on.
     *   - Premium, fast-food and regular restaurants all appear.
     */
    private static void seedOrders(DeliveryDataBase system) {
        // Riders we'll reuse below
        Rider r1 = system.findRiderById("204050601"); // Mohamad Saeed - top
        Rider r2 = system.findRiderById("204050602"); // Yara Mansour
        Rider r3 = system.findRiderById("204050603"); // Ahmad Haddad  - busy
        Rider r4 = system.findRiderById("204050604"); // Layla Saleh
        Rider r5 = system.findRiderById("204050605"); // Omar Zoabi    - busy

        // Customer 1001 - 3 orders (top customer)
        Order o1 = makeSeedOrder(system, 1001, 101, 20, 5, 2026,  80.0);
        assignAndDeliver(r1, o1, 21, 5, 2026);

        Order o2 = makeSeedOrder(system, 1001, 301, 22, 5, 2026, 150.0);
        assignAndDeliver(r1, o2, 23, 5, 2026);

        makeSeedOrder(system, 1001, 201,  1, 6, 2026,  50.0);
        // o3 stays as STATUS_SENT, unassigned - admin can assign it

        // Customer 1002 - 1 order
        Order o4 = makeSeedOrder(system, 1002, 103, 18, 5, 2026,  40.0);
        assignAndDeliver(r2, o4, 19, 5, 2026);

        // Customer 1004 - 2 orders
        Order o5 = makeSeedOrder(system, 1004, 304, 25, 5, 2026, 200.0);
        assignAndDeliver(r1, o5, 26, 5, 2026);

        Order o6 = makeSeedOrder(system, 1004, 201,  2, 6, 2026,  30.0);
        assignOnTheWay(r3, o6);

        // Customer 1005 - 2 orders
        Order o7 = makeSeedOrder(system, 1005, 207, 24, 5, 2026,  45.0);
        assignAndDeliver(r1, o7, 25, 5, 2026);

         makeSeedOrder(system, 1005, 302,  2, 6, 2026, 150.0);
        // o8 stays as STATUS_SENT, unassigned

        // Customer 1006 - 1 order
        Order o9 = makeSeedOrder(system, 1006, 203, 27, 5, 2026,  35.0);
        assignAndDeliver(r4, o9, 28, 5, 2026);

        // Customer 1009 - 2 orders (both PREMIUM - exercises function C)
        Order o10 = makeSeedOrder(system, 1009, 301, 28, 5, 2026, 180.0);
        assignAndDeliver(r1, o10, 29, 5, 2026);

        Order o11 = makeSeedOrder(system, 1009, 308,  2, 6, 2026, 120.0);
        assignOnTheWay(r5, o11);
    }

    /**
     * Helper: creates an Order, registers it in EVERY relevant collection,
     * and returns it for any caller-specific follow-up (status, rider).
     *
     * This is the canonical "place an order" sequence the  demands.
     * The customer placement flow (Task 10) and the RestAdmin add-order
     * flow (Task 8) will both do exactly the same four updates.
     * Centralising it here in the seed code makes the pattern obvious
     * and prevents one of the four collections from being forgotten.
     */
    private static Order makeSeedOrder(DeliveryDataBase system, int customerCode,
                                       int restaurantCode, int day, int month, int year,
                                       double basicAmount) {
        Restaurant rest = system.findRestaurantByCode(restaurantCode);
        int orderCode = system.generateOrderCode();
        Order order = new Order(orderCode, customerCode, rest, restaurantCode,
                                day, month, year, basicAmount);

        // Four-collection update - the "data consistency discipline" the
        // assignment is testing. Drop any one of these and the system
        // becomes inconsistent.
        system.addOrder(order);
        system.addOrderToCustomer(customerCode, order);
        system.addRestaurantToCustomer(customerCode, rest);
        system.addPaymentForCustomer(customerCode, order.getFinalPrice());
        return order;
    }

    /**
     * Helper: assigns the order to the rider AND marks it delivered on
     * the given date. The rider remains AVAILABLE because their job on
     * this order is finished.
     */
    private static void assignAndDeliver(Rider rider, Order order,
                                        int day, int month, int year) {
        rider.addOrder(order);
        order.setRiderCode(rider.getId());
        order.markAsDelivered(day, month, year);
        // rider.setAvailable stays true - delivered means done
    }

    /**
     * Helper: assigns the order to the rider and flags both as IN PROGRESS.
     * Rider is now UNAVAILABLE because they are physically busy with the
     * delivery.
     */
    private static void assignOnTheWay(Rider rider, Order order) {
        rider.addOrder(order);
        order.setRiderCode(rider.getId());
        order.setStatus(Order.STATUS_ON_THE_WAY);
        rider.setAvailable(false);
    }

    private static void seedRiders(DeliveryDataBase system) {
        system.addRider(new Rider("204050601", "Mohamad Saeed",   "0521234567", "scooter", true));
        system.addRider(new Rider("204050602", "Yara Mansour",     "0521234568", "bicycle", true));
        system.addRider(new Rider("204050603", "Ahmad Haddad",     "0521234569", "car",     true));
        system.addRider(new Rider("204050604", "Layla Saleh",      "0521234570", "scooter", true));
        system.addRider(new Rider("204050605", "Omar Zoabi",       "0521234571", "bicycle", true));
    }

    private static void seedCustomers(DeliveryDataBase system) {
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

    private static void seedRestaurants(DeliveryDataBase system) {
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

    private static void seedRestAdminsAndAssign(DeliveryDataBase system) {
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