package FoodDeliverySystem;

/**
 * Seed-data provider for the Food Delivery System.
 *
 * In Assignment 4 the system is operated entirely through the JavaFX GUI
 * (FoodDeliveryApp). The old console menu has been removed; what
 * remains is the initial data the GUI seeds once at startup via {@link #seedData}.
 */
public class Main {

    public static void seedData(DeliveryDataBase system) {
        seedRiders(system);
        seedCustomers(system);
        seedRestaurants(system);
        seedRestAdminsAndAssign(system);
        seedOrders(system);
    }


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
        system.addRider(new Rider("204050601", "Mohamad Saeed",   "0521234567", VehicleType.SCOOTER, true));
        system.addRider(new Rider("204050602", "Yara Mansour",     "0521234568", VehicleType.BICYCLE, true));
        system.addRider(new Rider("204050603", "Ahmad Haddad",     "0521234569", VehicleType.CAR,     true));
        system.addRider(new Rider("204050604", "Layla Saleh",      "0521234570", VehicleType.SCOOTER, true));
        system.addRider(new Rider("204050605", "Omar Zoabi",       "0521234571", VehicleType.BICYCLE, true));
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