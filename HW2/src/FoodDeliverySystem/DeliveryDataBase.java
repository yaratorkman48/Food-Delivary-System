package FoodDeliverySystem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

/**
 * Central database class for the Food Delivery System (Assignment 2).
 *
 * Renamed from {@code DeliverySystem} in Assignment 1 .
 * This class is now the single source of truth for every entity in the
 * system, including the system administrator.
 *
 * COLLECTIONS MIGRATION (Assignment 2):
 *   Assignment 1 used five {@code Type[]} arrays, each paired with an
 *   {@code int xCount} counter and grown with {@code Arrays.copyOf} on
 *   every add.
 *
 *   Assignment 2 replaces every array with an {@code ArrayList<Type>}.
 *   The list manages its own size, so the separate counter fields are gone
 *   - but the {@code getXxxCount()} GETTERS are preserved as derived
 *   methods that simply return {@code list.size()}. This keeps the public
 *   API stable for callers in {@code Main} while eliminating the field
 *   redundancy that could cause bugs.
 *
 * NEW IN ASSIGNMENT 2:
 *   - A {@code systemAdministrator} field of type {@link Admin}. The
 *     constructor creates this admin with credentials "admin" / "12345".
 *   - Three per-customer Maps for O(1) lookup by customer code:
 *       {@code HashMap<Integer, ArrayList<Order>>      ordersByCustomer}
 *       {@code Hashtable<Integer, ArrayList<Restaurant>> restaurantsByCustomer}
 *       {@code HashMap<Integer, Double>                customerPayments}
 *   -  helper "A": {@link #addOrderToCustomer(int, Order)}, plus the
 *     symmetric helpers {@link #addRestaurantToCustomer(int, Restaurant)}
 *     and {@link #addPaymentForCustomer(int, double)} for the other two
 *     maps. These keep the menu code out of the maps entirely.
 *   -  query functions B-F:
 *       B: {@link #getActiveOrdersForRider(String)} (status sent / on the way)
 *       C: {@link #getPremiumRestaurantsForCustomer(Customer)} (instanceof filter)
 *       D: {@link #getTopCustomer()} (max-by-orders scan of the map)
 *       E: {@link #getTopRider()} (max-by-deliveries scan of the list)
 *       F: {@link #getOpenRestaurantsByCuisine(String)} (open + cuisine filter)
 */
public class DeliveryDataBase {

    // The single system administrator. Created by the constructor below
    private Admin systemAdministrator;

    // Five ArrayLists - one for each entity type in the system.
    // No counter fields: the list always knows its own size.
    private ArrayList<Customer>   customers;
    private ArrayList<RestAdmin>  restAdmins;
    private ArrayList<Restaurant> restaurants;   // polymorphic - holds all 3 subtypes
    private ArrayList<Rider>      riders;
    private ArrayList<Order>      orders;

    // Three Maps keyed by customer code (Assignment 2 requirement).
    //
    // ordersByCustomer:
    //   key   = customer code
    //   value = ArrayList of THAT customer's orders
    //   purpose: O(1) lookup of "all orders for customer X" instead of
    //   scanning the master orders list every time.
    //
    // restaurantsByCustomer:
    //   key   = customer code
    //   value = ArrayList of restaurants this customer has ordered from
    //   (no duplicates - if they order from McDonald's twice, McDonald's
    //   appears once).
    //
    //   Why Hashtable here and not HashMap? The assignment specifies
    //   Hashtable for this one, to force you to learn both APIs. The
    //   public methods are nearly identical to HashMap; the deep
    //   differences (Hashtable is synchronized and refuses null keys/values)
    //   don't matter for this single-threaded program.
    //
    // customerPayments:
    //   key   = customer code
    //   value = running total of money this customer has paid in our system
    //   (the final price of every order, summed).
    private HashMap<Integer, ArrayList<Order>>      ordersByCustomer;
    private Hashtable<Integer, ArrayList<Restaurant>> restaurantsByCustomer;
    private HashMap<Integer, Double>                customerPayments;

    // Auto-generated order code counter (customers never enter their own).
    private int nextOrderCode;

    //  Constructor

    /**
     * Creates an empty database with:
     *   - All five lists initialized to empty {@code ArrayList}s
     *   - The order-code generator starting at 1001
     *   - A system administrator with username "admin" and password "12345"
     *
     * The display name "System Administrator" is internal and only shown
     * in the welcome message after a successful admin login.
     */
    public DeliveryDataBase() {
        // The admin is created HERE, not in Main - this is the headline
        // structural change introduced by Assignment 2.
        // (Display name "Shadi Asakla" is preserved from Part 1; only the
        //  username/password are constrained )
        this.systemAdministrator =
                new Admin("Shadi Asakla", "admin", "12345");

        this.customers   = new ArrayList<>();
        this.restAdmins  = new ArrayList<>();
        this.restaurants = new ArrayList<>();
        this.riders      = new ArrayList<>();
        this.orders      = new ArrayList<>();

        // Three per-customer maps - initially empty; entries are created
        // lazily by the helper methods (addOrderToCustomer, etc.) on
        // first use, so a customer who never ordered isn't stored here.
        this.ordersByCustomer      = new HashMap<>();
        this.restaurantsByCustomer = new Hashtable<>();
        this.customerPayments      = new HashMap<>();

        this.nextOrderCode = 1001;
    }

    //  Getters

    public Admin getSystemAdministrator() {
        return systemAdministrator;
    }

    public ArrayList<Customer> getCustomers() {
        return customers;
    }

    public ArrayList<RestAdmin> getRestAdmins() {
        return restAdmins;
    }

    public ArrayList<Restaurant> getRestaurants() {
        return restaurants;
    }

    public ArrayList<Rider> getRiders() {
        return riders;
    }

    public ArrayList<Order> getOrders() {
        return orders;
    }

    public HashMap<Integer, ArrayList<Order>> getOrdersByCustomer() {
        return ordersByCustomer;
    }

    public Hashtable<Integer, ArrayList<Restaurant>> getRestaurantsByCustomer() {
        return restaurantsByCustomer;
    }

    public HashMap<Integer, Double> getCustomerPayments() {
        return customerPayments;
    }

    public int getNextOrderCode() {
        return nextOrderCode;
    }

    // Derived count getters: kept so existing Main.java code keeps
    // working, and so we never have to maintain a "count" field in parallel.

    public int getCustomersCount()   { return customers.size();   }
    public int getRestAdminsCount()  { return restAdmins.size();  }
    public int getRestaurantsCount() { return restaurants.size(); }
    public int getRidersCount()      { return riders.size();      }
    public int getOrdersCount()      { return orders.size();      }

    //  Setters

    public void setSystemAdministrator(Admin systemAdministrator) {
        this.systemAdministrator = systemAdministrator;
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers = customers;
    }

    public void setRestAdmins(ArrayList<RestAdmin> restAdmins) {
        this.restAdmins = restAdmins;
    }

    public void setRestaurants(ArrayList<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public void setRiders(ArrayList<Rider> riders) {
        this.riders = riders;
    }

    public void setOrders(ArrayList<Order> orders) {
        this.orders = orders;
    }

    public void setOrdersByCustomer(HashMap<Integer, ArrayList<Order>> ordersByCustomer) {
        this.ordersByCustomer = ordersByCustomer;
    }

    public void setRestaurantsByCustomer(Hashtable<Integer, ArrayList<Restaurant>> restaurantsByCustomer) {
        this.restaurantsByCustomer = restaurantsByCustomer;
    }

    public void setCustomerPayments(HashMap<Integer, Double> customerPayments) {
        this.customerPayments = customerPayments;
    }

    public void setNextOrderCode(int nextOrderCode) {
        this.nextOrderCode = nextOrderCode;
    }

    //  Order code generator
    /**
     * Returns the next available order code and increments the internal
     * counter. Customers never enter their own order code.
     *
     * @return a unique, auto-generated order code
     */
    public int generateOrderCode() {
        int code = nextOrderCode;
        nextOrderCode++;
        return code;
    }

    //  ADD methods 

    /**
     * Adds a customer. Refuses (returns false) on null or duplicate code.
     */
    public boolean addCustomer(Customer customer) {
        if (customer == null) return false;
        if (findCustomerByCode(customer.getCustomerCode()) != null) return false;
        customers.add(customer);
        return true;
    }

    /**
     * Adds a restaurant manager. Refuses on null or duplicate username.
     */
    public boolean addRestAdmin(RestAdmin restAdmin) {
        if (restAdmin == null) return false;
        if (findRestAdminByUsername(restAdmin.getUsername()) != null) return false;
        restAdmins.add(restAdmin);
        return true;
    }

    /**
     * Adds a restaurant of ANY subtype (Restaurant / FastFood / Premium).
     * This is where the polymorphic list is populated. Refuses on null or
     * duplicate restaurantCode.
     */
    public boolean addRestaurant(Restaurant restaurant) {
        if (restaurant == null) return false;
        if (findRestaurantByCode(restaurant.getRestaurantCode()) != null) return false;
        restaurants.add(restaurant);
        return true;
    }

    /**
     * Adds a rider. Refuses on null or duplicate ID.
     */
    public boolean addRider(Rider rider) {
        if (rider == null) return false;
        if (findRiderById(rider.getId()) != null) return false;
        riders.add(rider);
        return true;
    }

    /**
     * Adds an order to the master list. Refuses on null or duplicate code.
     * Note: keeping the per-customer/per-restaurant maps in sync is the
     * caller's job (see Task 4's {@code addOrderToCustomer} helper).
     */
    public boolean addOrder(Order order) {
        if (order == null) return false;
        if (findOrderByCode(order.getOrderCode()) != null) return false;
        orders.add(order);
        return true;
    }

    //  Per-customer map helpers (Assignment 2)

    /**
     *  function "A": adds an order to the per-customer HashMap.
     *
     * Behaviour per the assignment:
     *   - If the order is already in this customer's list, do nothing
     *     (return false).
     *   - If the customer has no entry in the map yet, create a fresh
     *     empty {@code ArrayList<Order>} for them and add the order to it.
     *   - Otherwise just append the order to the existing list.
     *
     * Why this method exists at all (instead of letting menu code touch
     * the map directly): all "first-time vs returning customer" logic lives
     * in ONE place. The menus just call this and never see {@code put},
     * {@code get}, or {@code null}.
     *
     * @param customerCode the customer's code (used as the map key)
     * @param order        the order to add
     * @return true if added, false if null or duplicate
     */
    public boolean addOrderToCustomer(int customerCode, Order order) {
        if (order == null) return false;

        // Look up THIS customer's order list. Returns null if the customer
        // has never had an order before - that's the "first time" case.
        ArrayList<Order> customerOrders = ordersByCustomer.get(customerCode);

        if (customerOrders == null) {
            // First-time customer - create their list and register it in the map.
            customerOrders = new ArrayList<>();
            ordersByCustomer.put(customerCode, customerOrders);
        } else {
            // Returning customer - make sure this exact order isn't already there.
            for (Order existing : customerOrders) {
                if (existing.getOrderCode() == order.getOrderCode()) {
                    return false; // duplicate - do nothing per spec
                }
            }
        }

        customerOrders.add(order);
        return true;
    }

    /**
     * Symmetric helper for {@code restaurantsByCustomer}: records that the
     * given customer has ordered from the given restaurant.
     *
     * - If the customer has no entry yet, create a new list for them.
     * - If the restaurant is already in their list, do nothing (a customer
     *   who orders from McDonald's 5 times should see McDonald's listed once).
     *
     * Not required by name in the spec, but called from the order-creation
     * flow (Tasks 8 and 10) so the menu code never reaches into the
     * Hashtable directly. Mirrors the structure of {@link #addOrderToCustomer}.
     *
     * @param customerCode the customer's code (key)
     * @param restaurant   the restaurant they just ordered from
     * @return true if added, false if null or already present for this customer
     */
    public boolean addRestaurantToCustomer(int customerCode, Restaurant restaurant) {
        if (restaurant == null) return false;

        ArrayList<Restaurant> customerRestaurants = restaurantsByCustomer.get(customerCode);

        if (customerRestaurants == null) {
            customerRestaurants = new ArrayList<>();
            restaurantsByCustomer.put(customerCode, customerRestaurants);
        } else {
            for (Restaurant existing : customerRestaurants) {
                if (existing.getRestaurantCode() == restaurant.getRestaurantCode()) {
                    return false; // already recorded for this customer
                }
            }
        }

        customerRestaurants.add(restaurant);
        return true;
    }

    /**
     * Symmetric helper for {@code customerPayments}: adds {@code amount}
     * to the customer's running total in the map.
     *
     * - If the customer has no entry yet, the entry is created with this
     *   amount as the starting balance.
     * - Otherwise, the existing total is increased by {@code amount}.
     *
     * Negative amounts are rejected to keep the running total monotonically
     * increasing (this map tracks what was paid, not balance changes).
     *
     * @param customerCode the customer's code (key)
     * @param amount       the amount just paid (must be non-negative)
     * @return true on success, false if the amount is negative
     */
    public boolean addPaymentForCustomer(int customerCode, double amount) {
        if (amount < 0) return false;

        // Explicit "if absent treat as zero, else use existing" - the same
        // basic get + null-check pattern used by the other two helpers.
        Double existing = customerPayments.get(customerCode);
        double current = (existing == null) ? 0.0 : existing;
        customerPayments.put(customerCode, current + amount);
        return true;
    }

    //  FIND methods (linear search, return null if not found) 
    //
    // The enhanced for-loop replaces the index+count loop from Part 1:
    // shorter, clearer, and no risk of an off-by-one because the list
    // controls its own iteration bounds.

    public Customer findCustomerByCode(int code) {
        for (Customer c : customers) {
            if (c.getCustomerCode() == code) return c;
        }
        return null;
    }

    public RestAdmin findRestAdminByUsername(String username) {
        if (username == null) return null;
        for (RestAdmin a : restAdmins) {
            if (username.equals(a.getUsername())) return a;
        }
        return null;
    }

    public Restaurant findRestaurantByCode(int code) {
        for (Restaurant r : restaurants) {
            if (r.getRestaurantCode() == code) return r;
        }
        return null;
    }

    public Rider findRiderById(String id) {
        if (id == null) return null;
        for (Rider r : riders) {
            if (id.equals(r.getId())) return r;
        }
        return null;
    }

    public Order findOrderByCode(int code) {
        for (Order o : orders) {
            if (o.getOrderCode() == code) return o;
        }
        return null;
    }

    //  Required query functions B-F (Assignment 2 spec)
    //
    // These are the five "function ב..ו" the assignment requires. Each one
    // is read-only: it scans the data structures and returns a fresh result.
    // Together they showcase the three main techniques you get for free
    // once you commit to collections:
    //
    //   1. Filtering one list into another (B, C, F)
    //   2. Polymorphic filtering via {@code instanceof} (C)
    //   3. "Find the max" by scanning a Map / list (D, E)

    /**
     * function "B": all ACTIVE orders for a given rider.
     *
     * An order is "active" while it is in the rider's hands - i.e. its
     * status is either {@code STATUS_SENT} (at the restaurant, ready to
     * pick up) or {@code STATUS_ON_THE_WAY} (with the rider, en route).
     * Once an order is {@code STATUS_DELIVERED} it stops being active.
     *
     * Implementation note: we iterate the rider's OWN {@code orders} list
     * (built up in Task 1) rather than scanning the master {@code orders}
     * list, because the rider's list is already restricted to that rider -
     * fewer items to check, no need to filter by riderId.
     *
     * @param riderId the rider's national ID
     * @return that rider's active orders (empty list if the rider is unknown)
     */
    public ArrayList<Order> getActiveOrdersForRider(String riderId) {
        ArrayList<Order> result = new ArrayList<>();
        Rider rider = findRiderById(riderId);
        if (rider == null) return result;       // unknown rider -> empty list

        for (Order o : rider.getOrders()) {
            String status = o.getStatus();
            // Note: String.equals(otherString) - the constant must come first
            // to avoid a NullPointerException if status happens to be null.
            if (Order.STATUS_SENT.equals(status)
             || Order.STATUS_ON_THE_WAY.equals(status)) {
                result.add(o);
            }
        }
        return result;
    }

    /**
     * function "C": premium restaurants this customer has ordered from.
     *
     * Reads from the {@code restaurantsByCustomer} Hashtable (which already
     * deduplicates), then keeps only the entries that are actually of the
     * {@link PremiumRestaurant} subtype.
     *
     * This is the headline {@code instanceof} use case: the Hashtable holds
     * polymorphic {@code Restaurant} references; we filter by the actual
     * RUNTIME subtype to extract the premium ones.
     *
     * @param customer the customer (must not be null)
     * @return premium restaurants the customer has ordered from, or empty list
     */
    public ArrayList<Restaurant> getPremiumRestaurantsForCustomer(Customer customer) {
        ArrayList<Restaurant> result = new ArrayList<>();
        if (customer == null) return result;

        ArrayList<Restaurant> all = restaurantsByCustomer.get(customer.getCustomerCode());
        if (all == null) return result;          // customer never ordered

        for (Restaurant r : all) {
            if (r instanceof PremiumRestaurant) {
                // Runtime type check: only PremiumRestaurant subclass survives.
                // FastFoodRestaurant and the plain Restaurant base are filtered out.
                result.add(r);
            }
        }
        return result;
    }

    /**
     * function "D": the customer with the most orders in the system.
     *
     * Walks the {@code ordersByCustomer} map via keySet() and tracks the
     * running maximum. For each customer code we look up that customer's
     * order list and compare its size. One pass, O(n) in distinct customers.
     *
     * Assumption per the spec: there is only one customer with the maximum
     * count (so we don't have to handle ties).
     *
     * @return the top-ordering customer, or null if no orders exist yet
     */
    public Customer getTopCustomer() {
        if (ordersByCustomer.isEmpty()) return null;

        int maxCount = -1;
        int topCode  = -1;

        // Iterate the map keys (customer codes) and look up each list.
        // keySet() is the most basic way to walk a HashMap.
        for (Integer code : ordersByCustomer.keySet()) {
            int count = ordersByCustomer.get(code).size();
            if (count > maxCount) {
                maxCount = count;
                topCode  = code;
            }
        }
        return findCustomerByCode(topCode);
    }

    /**
     * function "E": the rider who has performed the most deliveries.
     *
     * Counts ONLY orders whose status is "delivered" for each rider.
     * Orders still in "sent" or "on the way" status are not counted, since
     * they have not yet been completed. One pass over the riders list; for
     * each rider we walk their orders list and tally delivered orders.
     *
     * Assumption per the spec: there is only one rider with the maximum
     * count (no ties to handle).
     *
     * @return the top-delivering rider, or null if no riders exist
     */
    public Rider getTopRider() {
        if (riders.isEmpty()) return null;

        Rider topRider      = null;
        int   maxDeliveries = -1;

        for (Rider r : riders) {
            // Count ONLY orders that have actually been delivered (status "delivered").
            // Orders still "sent" or "on the way" do not count as completed deliveries.
            int delivered = 0;
            for (Order o : r.getOrders()) {
                if (Order.STATUS_DELIVERED.equals(o.getStatus())) {
                    delivered++;
                }
            }
            if (delivered > maxDeliveries) {
                maxDeliveries = delivered;
                topRider      = r;
            }
        }
        return topRider;
    }
    /**
     * function "F": all OPEN restaurants matching the given cuisine.
     *
     * Two filters applied in one pass:
     *   1. The restaurant is currently open ({@code isOpen() == true}).
     *   2. Its cuisine type matches the requested one.
     *
     * Comparison is case-insensitive ({@code equalsIgnoreCase}) so the
     * caller can type "italian", "ITALIAN", or "Italian" and still match
     * restaurants stored as "Italian". This is the "pay attention to how
     * you defined cuisine type in Assignment 1" hint - cuisine is a String
     * (not an enum), so we need to be defensive about casing.
     *
     * @param cuisineType the cuisine to filter by (e.g. "Italian", "Sushi")
     * @return open restaurants of that cuisine (possibly empty, never null)
     */
    public ArrayList<Restaurant> getOpenRestaurantsByCuisine(String cuisineType) {
        ArrayList<Restaurant> result = new ArrayList<>();
        if (cuisineType == null) return result;

        for (Restaurant r : restaurants) {
            // r.isOpen() filters by status; equalsIgnoreCase filters by cuisine.
            // Putting cuisineType FIRST on the equalsIgnoreCase call would
            // NPE if cuisineType were null - which we already ruled out above.
            if (r.isOpen() && cuisineType.equalsIgnoreCase(r.getCuisineType())) {
                result.add(r);
            }
        }
        return result;
    }

    //  Higher-level helper methods 

    /**
     * Assigns a rider to an order. Used by Admin and RestAdmin menus.
     *
     * Checks (per the Assignment 2 ):
     *   1. The rider exists.
     *   2. The rider is available.
     *   3. The order exists.
     *   4. The order does not already have a rider assigned.
     *
     * Side effects on success:
     *   - Records the rider's ID on the order.
     *   - Adds the order to the rider's list.
     *   - Marks the rider as not available.
     *   - Sets the order status to {@link Order#STATUS_SENT} - i.e. it
     *     stays "sent" (= received at the restaurant). The rider will
     *     change it to "on the way" themselves when they pick it up.
     */
    public boolean assignRiderToOrder(String riderId, int orderCode) {
        Rider rider = findRiderById(riderId);
        if (rider == null)        return false;
        if (!rider.isAvailable()) return false;

        Order order = findOrderByCode(orderCode);
        if (order == null)        return false;

        // NEW Part 2 check: the order must not already have a rider.
        // Convention from Part 1: riderCode is "" (empty string) when unassigned.
        String currentRider = order.getRiderCode();
        if (currentRider != null && !currentRider.isEmpty()) return false;

        order.setRiderCode(riderId);
        order.setStatus(Order.STATUS_SENT);   // explicit per Part 2 spec - normally a no-op
        rider.addOrder(order);
        rider.setAvailable(false);
        return true;
    }

    @Override
    public String toString() {
        return "DeliveryDataBase ["
             + "systemAdministrator=" + systemAdministrator
             + ", customers="   + customers
             + ", restAdmins="  + restAdmins
             + ", restaurants=" + restaurants
             + ", riders="      + riders
             + ", orders="      + orders
             + ", ordersByCustomer="      + ordersByCustomer
             + ", restaurantsByCustomer=" + restaurantsByCustomer
             + ", customerPayments="      + customerPayments
             + ", nextOrderCode=" + nextOrderCode + "]";
    }
}
