package FoodDeliverySystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Text-file persistence layer for the Food Delivery System (Assignment 4, Part י).
 *
 * Responsibilities:
 *   - {@link #saveAll(DeliveryDataBase)} writes every required collection, one
 *     object per line, into its own text file.
 *   - {@link #loadAll(DeliveryDataBase)} reads those files back, RE-CREATES the
 *     objects, and restores them into the system's collections.
 *
 * Design decisions (also documented in decisions.pdf):
 *   1. Fields are separated by '|' (a character that never appears in names,
 *      cities, emails or phone numbers in this data set).
 *   2. We store CODES, not objects. An order stores its restaurant's code; on
 *      load we look the restaurant up and re-link the live object, which keeps
 *      Order.calculateFinalPrice() polymorphic after a reload.
 *   3. finalPrice is NOT stored. Each order is rebuilt through the normal
 *      8-argument Order constructor, which recomputes finalPrice from the
 *      re-linked restaurant. The price reconstructs itself from the rules.
 *   4. Restaurants are polymorphic, so each line starts with a type tag
 *      (BASE / FASTFOOD / PREMIUM) used to pick the right constructor on load.
 *   5. Load = REPLACE: loadAll() clears the system first, so loading twice does
 *      not duplicate data.
 *
 * Uses File, FileReader, BufferedReader, FileWriter and BufferedWriter as
 * required. All readers/writers are opened with try-with-resources, so they are
 * always closed even if an exception is thrown mid-file.
 */
public class FileManager {

    // One file per collection (written to the project's working directory).
    private static final String CUSTOMERS_FILE   = "customers.txt";
    private static final String RESTAURANTS_FILE = "restaurants.txt";
    private static final String RIDERS_FILE      = "riders.txt";
    private static final String ORDERS_FILE      = "orders.txt";
    private static final String RESTADMINS_FILE  = "restAdmins.txt";

    // Field delimiter, and the regex form needed by String.split (| is special).
    private static final String DELIM       = "|";
    private static final String DELIM_REGEX = "\\|";
    // Sub-delimiter for the list of restaurant codes inside one RestAdmin line.
    private static final String SUB_DELIM   = ",";

    // ===================================================================
    //  PUBLIC API
    // ===================================================================

    /**
     * Saves all five collections, each to its own text file. The order of the
     * five calls does not matter for saving (each file is independent).
     *
     * @param db the live system whose data is written to disk
     * @throws IOException if any file cannot be written
     */
    public static void saveAll(DeliveryDataBase db) throws IOException {
        saveCustomers(db);
        saveRestaurants(db);
        saveRiders(db);
        saveOrders(db);
        saveRestAdmins(db);
    }

    /**
     * Clears the system, then loads all five collections back from disk. The
     * call order here IS significant: restaurants must exist before orders and
     * rest-admins (which link to them), and riders must exist before orders
     * (which re-attach to them).
     *
     * @param db the live system to repopulate
     * @throws IOException if any file is missing or cannot be read
     */
    public static void loadAll(DeliveryDataBase db) throws IOException {
        db.clearAllData();
        loadRestaurants(db); // 1) referenced by orders + rest-admins
        loadCustomers(db);   // 2) independent
        loadRiders(db);      // 3) referenced by orders
        loadOrders(db);      // 4) re-links restaurants + riders, rebuilds maps
        loadRestAdmins(db);  // 5) re-links managed restaurants
    }

    // ===================================================================
    //  CUSTOMERS
    //  Format: code|first|last|street|city|zip|phone|email|balance
    // ===================================================================

    private static void saveCustomers(DeliveryDataBase db) throws IOException {
        File file = new File(CUSTOMERS_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Customer c : db.getCustomers()) {
                bw.write(c.getCustomerCode() + DELIM + c.getFirstName() + DELIM + c.getLastName()
                        + DELIM + c.getStreet() + DELIM + c.getCity() + DELIM + c.getZipCode()
                        + DELIM + c.getPhone() + DELIM + c.getEmail() + DELIM + c.getCreditBalance());
                bw.newLine();
            }
        }
    }

    private static void loadCustomers(DeliveryDataBase db) throws IOException {
        File file = new File(CUSTOMERS_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(DELIM_REGEX, -1);
                Customer c = new Customer(
                        Integer.parseInt(p[0]), p[1], p[2], p[3], p[4], p[5], p[6], p[7],
                        Double.parseDouble(p[8]));
                db.addCustomer(c);
            }
        }
    }

    // ===================================================================
    //  RESTAURANTS (polymorphic)
    //  BASE|code|name|cuisine|rating|open|fee
    //  FASTFOOD|code|name|cuisine|rating|open|fee|avgPrepMinutes|fastSurcharge
    //  PREMIUM |code|name|cuisine|rating|open|fee|minOrderAmount|extraCommission
    // ===================================================================

    private static void saveRestaurants(DeliveryDataBase db) throws IOException {
        File file = new File(RESTAURANTS_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Restaurant r : db.getRestaurants()) {
                // Subtypes MUST be checked before the base case, because every
                // subtype is also an instanceof Restaurant.
                if (r instanceof FastFoodRestaurant) {
                    FastFoodRestaurant f = (FastFoodRestaurant) r;
                    bw.write("FASTFOOD" + DELIM + baseLine(f)
                            + DELIM + f.getAvgPrepTimeMinutes()
                            + DELIM + f.getFastDeliverySurcharge());
                } else if (r instanceof PremiumRestaurant) {
                    PremiumRestaurant pr = (PremiumRestaurant) r;
                    bw.write("PREMIUM" + DELIM + baseLine(pr)
                            + DELIM + pr.getMinimumOrderAmount()
                            + DELIM + pr.getExtraCommissionPercent());
                } else {
                    bw.write("BASE" + DELIM + baseLine(r));
                }
                bw.newLine();
            }
        }
    }

    /** The six shared base fields, used by all three restaurant lines. */
    private static String baseLine(Restaurant r) {
        return r.getRestaurantCode() + DELIM + r.getName() + DELIM + r.getCuisineType()
                + DELIM + r.getRating() + DELIM + r.isOpen() + DELIM + r.getBaseDeliveryFee();
    }

    private static void loadRestaurants(DeliveryDataBase db) throws IOException {
        File file = new File(RESTAURANTS_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(DELIM_REGEX, -1);
                String type   = p[0];
                int code      = Integer.parseInt(p[1]);
                String name   = p[2];
                String cuisine = p[3];
                double rating = Double.parseDouble(p[4]);
                boolean open  = Boolean.parseBoolean(p[5]);
                double fee    = Double.parseDouble(p[6]);

                Restaurant r;
                if (type.equals("FASTFOOD")) {
                    r = new FastFoodRestaurant(code, name, cuisine, rating, open, fee,
                            Integer.parseInt(p[7]), Double.parseDouble(p[8]));
                } else if (type.equals("PREMIUM")) {
                    r = new PremiumRestaurant(code, name, cuisine, rating, open, fee,
                            Double.parseDouble(p[7]), Double.parseDouble(p[8]));
                } else { // "BASE"
                    r = new Restaurant(code, name, cuisine, rating, open, fee);
                }
                db.addRestaurant(r);
            }
        }
    }

    // ===================================================================
    //  RIDERS
    //  Format: id|fullName|phone|VEHICLE_NAME|available
    //  (the rider's order list is NOT stored here - it is rebuilt from
    //   orders.txt via each order's riderCode)
    // ===================================================================

    private static void saveRiders(DeliveryDataBase db) throws IOException {
        File file = new File(RIDERS_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Rider r : db.getRiders()) {
                bw.write(r.getId() + DELIM + r.getFullName() + DELIM + r.getPhone()
                        + DELIM + r.getVehicle().name() + DELIM + r.isAvailable());
                bw.newLine();
            }
        }
    }

    private static void loadRiders(DeliveryDataBase db) throws IOException {
        File file = new File(RIDERS_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(DELIM_REGEX, -1);
                Rider r = new Rider(p[0], p[1], p[2],
                        VehicleType.fromString(p[3]), Boolean.parseBoolean(p[4]));
                db.addRider(r);
            }
        }
    }

    // ===================================================================
    //  ORDERS
    //  Format: code|custCode|restCode|riderCode|oDay|oMon|oYear
    //          |dDay|dMon|dYear|basicAmount|status
    //  (riderCode may be empty -> "||"; finalPrice is recomputed, not stored)
    // ===================================================================

    private static void saveOrders(DeliveryDataBase db) throws IOException {
        File file = new File(ORDERS_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (Order o : db.getOrders()) {
                bw.write(o.getOrderCode() + DELIM + o.getCustomerCode() + DELIM + o.getRestaurantCode()
                        + DELIM + o.getRiderCode()
                        + DELIM + o.getOrderDay() + DELIM + o.getOrderMonth() + DELIM + o.getOrderYear()
                        + DELIM + o.getDeliveryDay() + DELIM + o.getDeliveryMonth() + DELIM + o.getDeliveryYear()
                        + DELIM + o.getBasicAmount() + DELIM + o.getStatus());
                bw.newLine();
            }
        }
    }

    private static void loadOrders(DeliveryDataBase db) throws IOException {
        File file = new File(ORDERS_FILE);
        int maxCode = 1000; // empty file -> nextOrderCode stays 1001 (1000 + 1)

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(DELIM_REGEX, -1);
                int orderCode      = Integer.parseInt(p[0]);
                int customerCode   = Integer.parseInt(p[1]);
                int restaurantCode = Integer.parseInt(p[2]);
                String riderCode   = p[3];
                int oDay  = Integer.parseInt(p[4]);
                int oMon  = Integer.parseInt(p[5]);
                int oYear = Integer.parseInt(p[6]);
                int dDay  = Integer.parseInt(p[7]);
                int dMon  = Integer.parseInt(p[8]);
                int dYear = Integer.parseInt(p[9]);
                double basicAmount = Double.parseDouble(p[10]);
                String status      = p[11];

                // Re-link to the already-loaded restaurant object so that
                // calculateFinalPrice() dispatches polymorphically and the
                // 8-arg constructor recomputes finalPrice for us.
                Restaurant restaurant = db.findRestaurantByCode(restaurantCode);

                Order order = new Order(orderCode, customerCode, restaurant, restaurantCode,
                        oDay, oMon, oYear, basicAmount);

                // Restore the fields the constructor does not take.
                order.setRiderCode(riderCode);
                order.setDeliveryDay(dDay);
                order.setDeliveryMonth(dMon);
                order.setDeliveryYear(dYear);
                order.setStatus(status);

                // 1) master order list
                db.addOrder(order);

                // 2) re-attach to the assigned rider's personal list
                if (riderCode != null && !riderCode.isEmpty()) {
                    Rider rider = db.findRiderById(riderCode);
                    if (rider != null) {
                        rider.addOrder(order);
                    }
                }

                // 3) rebuild the three per-customer maps EXACTLY as order
                //    placement does (same four-step commit), so reports stay correct.
                db.addOrderToCustomer(customerCode, order);
                if (restaurant != null) {
                    db.addRestaurantToCustomer(customerCode, restaurant);
                }
                db.addPaymentForCustomer(customerCode, order.getFinalPrice());

                if (orderCode > maxCode) {
                    maxCode = orderCode;
                }
            }
        }
        // Continue generating order codes after the highest one we loaded.
        db.setNextOrderCode(maxCode + 1);
    }

    // ===================================================================
    //  REST-ADMINS
    //  Format: managerName|username|password|code1,code2,...
    //  (last field may be empty if the manager has no restaurants)
    // ===================================================================

    private static void saveRestAdmins(DeliveryDataBase db) throws IOException {
        File file = new File(RESTADMINS_FILE);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
            for (RestAdmin a : db.getRestAdmins()) {
                StringBuilder codes = new StringBuilder();
                ArrayList<Restaurant> managed = a.getManagedRestaurants();
                for (int i = 0; i < managed.size(); i++) {
                    if (i > 0) {
                        codes.append(SUB_DELIM);
                    }
                    codes.append(managed.get(i).getRestaurantCode());
                }
                bw.write(a.getManagerName() + DELIM + a.getUsername() + DELIM + a.getPassword()
                        + DELIM + codes.toString());
                bw.newLine();
            }
        }
    }

    private static void loadRestAdmins(DeliveryDataBase db) throws IOException {
        File file = new File(RESTADMINS_FILE);
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] p = line.split(DELIM_REGEX, -1);
                RestAdmin admin = new RestAdmin(p[0], p[1], p[2]);

                // p[3] holds the comma-separated managed codes (may be empty).
                if (p.length > 3 && !p[3].isEmpty()) {
                    String[] codes = p[3].split(SUB_DELIM);
                    for (String codeStr : codes) {
                        Restaurant r = db.findRestaurantByCode(Integer.parseInt(codeStr.trim()));
                        if (r != null) {
                            admin.addRestaurant(r); // re-link the live restaurant object
                        }
                    }
                }
                db.addRestAdmin(admin);
            }
        }
    }
}