package FoodDeliverySystem;
import java.util.Arrays;

/**
 * Central container class for the Food Delivery System.
 *
 * Holds five dynamically-sized arrays:
 *   - customers   : all registered customers
 *   - restAdmins  : all restaurant managers
 *   - restaurants : all restaurants (polymorphic: Restaurant / FastFood / Premium)
 *   - riders      : all delivery riders
 *   - orders      : all orders ever placed
 *
 * Also maintains a 'nextOrderCode' counter to auto-generate unique order codes
 * (customers never input their own order code per course staff clarification).

 *   - This class does NOT hold an Admin field. The single Admin
 *     ("admin"/"12345") lives as a local variable in main(), not here.
 *   - All five arrays use Arrays.copyOf to grow dynamically by exactly +1
 *     each add. There is no fixed maximum size.
 *   - Even though arr.length always equals the corresponding count field,
 *     the count fields are maintained as separate state (required field
 *     per the assignment).
 *   - Helper methods like addX/findX live IN THIS CLASS. Other helper
 *     methods (like Rider.addOrder, RestAdmin.addRestaurant) live in their
 *     own entity classes - encouraged by course staff.
 *   - Duplicate prevention: addCustomer rejects same customerCode,
 *     addRider rejects same id, addRestaurant rejects same restaurantCode,
 *     addRestAdmin rejects same username, addOrder rejects same orderCode.
 *
 * Polymorphism note:
 *   The restaurants[] array is declared as Restaurant[], but can hold
 *   instances of Restaurant, FastFoodRestaurant, or PremiumRestaurant.
 *   This is exactly what enables Order.setBasicAmount() to dispatch
 *   polymorphically to the right calculateFinalPrice() at runtime.
 */
public class DeliverySystem {
	//Arrays and Counters
    private Customer[] customers;
    private int customersCount;
    
    private RestAdmin[] restAdmins;
    private int restAdminsCount;
    
    private Restaurant[] restaurants;
    private int restaurantsCount;
    
    private Rider[] riders;
    private int ridersCount;
    
    private Order[] orders;
    private int ordersCount;
    
    // Automatic Order Code Generator
    private int nextOrderCode;
  //Constructor
    /**
     * Creates an empty DeliverySystem with all arrays initialized to length 0
     * and the order code counter starting at 1001.
     */
    public DeliverySystem() {
        this.customers = new Customer[0];
        this.customersCount = 0;
        
        this.restAdmins = new RestAdmin[0];
        this.restAdminsCount = 0;
        
        this.restaurants = new Restaurant[0];
        this.restaurantsCount = 0;
        
        this.riders = new Rider[0];
        this.ridersCount = 0;
        
        this.orders = new Order[0]; 
        this.ordersCount = 0;
        
        this.nextOrderCode = 1001;  
    }
	// Getters for all arrays and counts

	public Customer[] getCustomers() {
		return customers;
	}


	public int getCustomersCount() {
		return customersCount;
	}


	public RestAdmin[] getRestAdmins() {
		return restAdmins;
	}


	public int getRestAdminsCount() {
		return restAdminsCount;
	}


	public Restaurant[] getRestaurants() {
		return restaurants;
	}


	public int getRestaurantsCount() {
		return restaurantsCount;
	}


	public Rider[] getRiders() {
		return riders;
	}


	public int getRidersCount() {
		return ridersCount;
	}


	public Order[] getOrders() {
		return orders;
	}


	public int getOrdersCount() {
		return ordersCount;
	}


	public int getNextOrderCode() {
		return nextOrderCode;
	}
	// ============================================================
    // Order code generator
    // ============================================================

    /**
     * Returns the next available order code and increments the internal
     * counter. Customers never input their own order code; this method
     * is used by menu code when creating a new order.
     *
     * @return a unique, auto-generated order code
     */
    public int generateOrderCode() {
        int code = nextOrderCode;
        nextOrderCode++;
        return code;
    }

    // ============================================================
    // ADD methods (each grows its array via Arrays.copyOf + count++)
    // ============================================================

    /**
     * Adds a customer to the system.
     * Refuses (returns false) if the customer is null or if a customer
     * with the same customerCode already exists.
     *
     * @param customer the customer to add
     * @return true if added, false otherwise
     */
    public boolean addCustomer(Customer customer) {
        if (customer == null) return false;
        // Duplicate check by code
        if (findCustomerByCode(customer.getCustomerCode()) != null) return false;
        // Grow array
        customers = Arrays.copyOf(customers, customers.length + 1);
        customers[customers.length - 1] = customer;
        customersCount++;
        return true;
    }

    /**
     * Adds a restaurant manager to the system.
     * Refuses if null or if username is already taken.
     *
     * @param restAdmin the restaurant manager to add
     * @return true if added, false otherwise
     */
    public boolean addRestAdmin(RestAdmin restAdmin) {
        if (restAdmin == null) return false;
        // Duplicate check by username
        if (findRestAdminByUsername(restAdmin.getUsername()) != null) return false;
        restAdmins = Arrays.copyOf(restAdmins, restAdmins.length + 1);
        restAdmins[restAdmins.length - 1] = restAdmin;
        restAdminsCount++;
        return true;
    }

    /**
     * Adds a restaurant to the system.
     * Accepts any subtype: Restaurant, FastFoodRestaurant, or PremiumRestaurant
     * - this is where the polymorphic array is built up.
     * Refuses if null or if a restaurant with the same restaurantCode exists.
     *
     * @param restaurant the restaurant to add (any subtype)
     * @return true if added, false otherwise
     */
    public boolean addRestaurant(Restaurant restaurant) {
        if (restaurant == null) return false;
        if (findRestaurantByCode(restaurant.getRestaurantCode()) != null) return false;
        restaurants = Arrays.copyOf(restaurants, restaurants.length + 1);
        restaurants[restaurants.length - 1] = restaurant;
        restaurantsCount++;
        return true;
    }

    /**
     * Adds a rider to the system.
     * Refuses if null or if a rider with the same id already exists.
     *
     * @param rider the rider to add
     * @return true if added, false otherwise
     */
    public boolean addRider(Rider rider) {
        if (rider == null) return false;
        if (findRiderById(rider.getId()) != null) return false;
        riders = Arrays.copyOf(riders, riders.length + 1);
        riders[riders.length - 1] = rider;
        ridersCount++;
        return true;
    }

    /**
     * Adds an order to the system.
     * Refuses if null or if an order with the same orderCode already exists.
     *
     * @param order the order to add
     * @return true if added, false otherwise
     */
    public boolean addOrder(Order order) {
        if (order == null) return false;
        if (findOrderByCode(order.getOrderCode()) != null) return false;
        orders = Arrays.copyOf(orders, orders.length + 1);
        orders[orders.length - 1] = order;
        ordersCount++;
        return true;
    }

    // ============================================================
    // FIND methods (linear search, return null if not found)
    // ============================================================

    /**
     * Finds a customer by customer code.
     * @param code the customer code to look up
     * @return the matching Customer, or null if not found
     */
    public Customer findCustomerByCode(int code) {
        for (int i = 0; i < customersCount; i++) {
            if (customers[i] != null && customers[i].getCustomerCode() == code) {
                return customers[i];
            }
        }
        return null;
    }

    /**
     * Finds a restaurant manager by username.
     * @param username the username to look up
     * @return the matching RestAdmin, or null if not found
     */
    public RestAdmin findRestAdminByUsername(String username) {
        if (username == null) return null;
        for (int i = 0; i < restAdminsCount; i++) {
            if (restAdmins[i] != null &&
                username.equals(restAdmins[i].getUsername())) {
                return restAdmins[i];
            }
        }
        return null;
    }

    /**
     * Finds a restaurant by restaurant code.
     * Returns the actual subtype (Restaurant / FastFood / Premium) -
     * the caller can use it polymorphically.
     *
     * @param code the restaurant code to look up
     * @return the matching Restaurant (or subtype), or null if not found
     */
    public Restaurant findRestaurantByCode(int code) {
        for (int i = 0; i < restaurantsCount; i++) {
            if (restaurants[i] != null && restaurants[i].getRestaurantCode() == code) {
                return restaurants[i];
            }
        }
        return null;
    }

    /**
     * Finds a rider by their national ID.
     * @param id the rider's national ID (תז)
     * @return the matching Rider, or null if not found
     */
    public Rider findRiderById(String id) {
        if (id == null) return null;
        for (int i = 0; i < ridersCount; i++) {
            if (riders[i] != null && id.equals(riders[i].getId())) {
                return riders[i];
            }
        }
        return null;
    }

    /**
     * Finds an order by order code.
     * @param code the order code to look up
     * @return the matching Order, or null if not found
     */
    public Order findOrderByCode(int code) {
        for (int i = 0; i < ordersCount; i++) {
            if (orders[i] != null && orders[i].getOrderCode() == code) {
                return orders[i];
            }
        }
        return null;
    }

    // ============================================================
    // Higher-level helper methods
    // ============================================================

    /**
     * Assigns a rider to an order. Used by Admin and RestAdmin menus.
     *
     * Checks:
     *   1. The rider exists (by ID).
     *   2. The order exists (by code).
     *   3. The rider is currently available.
     *
     * If all checks pass:
     *   - The rider's riderCode is recorded on the order.
     *   - The order is added to the rider's orders[] array.
     *   - The order status is updated to "on the way".
     *
     * Note: We intentionally do NOT mark the rider as unavailable here -
     *       a rider may be carrying multiple orders simultaneously.
     *       Availability is toggled by the rider themselves via the menu.
     *
     * @param riderId   the rider's national ID
     * @param orderCode the order code
     * @return true if successfully assigned, false if any check failed
     */
    public boolean assignRiderToOrder(String riderId, int orderCode) {
        Rider rider = findRiderById(riderId);
        if (rider == null)            return false;
        if (!rider.isAvailable())     return false;

        Order order = findOrderByCode(orderCode);
        if (order == null)            return false;

        // Link them both directions
        order.setRiderCode(riderId);
        order.setStatus(Order.STATUS_ON_THE_WAY);
        rider.addOrder(order);
        rider.setAvailable(false);   // ← rider is now busy with this active order
        return true;
    }
    

    /**
     * Returns all orders belonging to a specific customer.
     * Uses the "clean" approach per course staff: instead of storing
     * an Order[] inside Customer, we filter the main orders[] array.
     *
     * @param customerCode the customer's code
     * @return a fresh array containing only that customer's orders
     */
    public Order[] getOrdersForCustomer(int customerCode) {
        Order[] result = new Order[0];
        for (int i = 0; i < ordersCount; i++) {
            if (orders[i] != null && orders[i].getCustomerCode() == customerCode) {
                result = Arrays.copyOf(result, result.length + 1);
                result[result.length - 1] = orders[i];
            }
        }
        return result;
    }

    /**
     * Returns all orders assigned to a specific rider.
     * Could also just call rider.getOrders() - this method is a convenience
     * when you only have the rider's ID rather than the Rider object.
     *
     * @param riderId the rider's national ID
     * @return a fresh array of that rider's orders (empty if rider not found)
     */
    public Order[] getOrdersForRider(String riderId) {
        Rider rider = findRiderById(riderId);
        if (rider == null) return new Order[0];
        return rider.getOrders();
    }


	@Override
	public String toString() {
		return "DeliverySystem [customers=" + Arrays.toString(customers) + ", customersCount=" + customersCount
				+ ", restAdmins=" + Arrays.toString(restAdmins) + ", restAdminsCount=" + restAdminsCount
				+ ", restaurants=" + Arrays.toString(restaurants) + ", restaurantsCount=" + restaurantsCount
				+ ", riders=" + Arrays.toString(riders) + ", ridersCount=" + ridersCount + ", orders="
				+ Arrays.toString(orders) + ", ordersCount=" + ordersCount + ", nextOrderCode=" + nextOrderCode + "]";
	}
 
}
