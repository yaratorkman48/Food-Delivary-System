package FoodDeliverySystem;
import java.util.Arrays;

/**
 * Represents a delivery Rider in the Food Delivery System.
 * A rider is responsible for picking up orders from restaurants and delivering them to customers.
 * Each rider has a unique national ID (תז), a vehicle type, an availability flag,
 * and an array of orders they have been assigned to.
 *
 * NOTE: Per the assignment requirement, the orders are stored dynamically
 * using Arrays.copyOf to grow when needed. The "ordersCount" field tracks 
 * how many slots are actually filled.
 */
public class Rider {
    private String id;
    private String fullName;
    private String phone;
    private String vehicle;
    private boolean available;
    private Order[] orders;
    private int ordersCount;
    
    //Constructors

    /**
     * Default constructor.
     * Creates an empty Rider with default values and an empty orders array.
     */
    public Rider() {
        this.id = "";
        this.fullName = "";
        this.phone = "";
        this.vehicle = "";
        this.available = true;
        this.orders = new Order[0];  // dynamic - starts empty
        this.ordersCount = 0;
    } 
 
	  /**
     * Full constructor.
     * NOTE: All input validation should be done by the CALLER (using
     * InputHelper) BEFORE invoking this constructor. The constructor simply
     * assigns the provided values.
     *
     * @param id        national ID (תז)
     * @param fullName  full name of the rider
     * @param phone     phone number
     * @param vehicle   vehicle type (bicycle / scooter / car ...)
     * @param available whether the rider is currently available
     */
    public Rider(String id, String fullName, String phone,
                 String vehicle, boolean available) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.vehicle = vehicle;
        this.available = available;
        this.orders = new Order[0];  // dynamic - starts empty
        this.ordersCount = 0;
        }
	//Getters and Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getVehicle() {
		return vehicle;
	}

	public void setVehicle(String vehicle) {
		this.vehicle = vehicle;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public Order[] getOrders() {
		return orders;
	}

	public void setOrders(Order[] orders) {
		this.orders = orders;
	}

	public int getOrdersCount() {
		return ordersCount;
	}

	public void setOrdersCount(int ordersCount) {
		this.ordersCount = ordersCount;
	}


    /**
     * Adds an order to this rider's list of assigned orders.
     * Uses {@link Arrays#copyOf} to grow the array by exactly one slot.
     *
     * Refuses to add (returns false) if:
     *   - the parameter is null
     *   - an order with the same orderCode is already in the array (no duplicates)
     *
     * @param order the order to add
     * @return true if the order was successfully added, false otherwise
     */
    public boolean addOrder(Order order) {
        if (order == null) {
            return false;
        }
        // Check for duplicates by order code
        for (int i = 0; i < ordersCount; i++) {
            if (orders[i] != null && orders[i].getOrderCode() == order.getOrderCode()) {
                return false; // duplicate
            }
        }
        // Grow the array dynamically by one slot using Arrays.copyOf
        orders = Arrays.copyOf(orders, orders.length + 1);
        orders[orders.length - 1] = order;
        ordersCount++;
        return true;
    } 
    
	 //toString
	@Override
	public String toString() {
		return "Rider [id=" + id + ", fullName=" + fullName + ", phone=" + phone + ", vehicle=" + vehicle
				+ ", available=" + available + ", ordersCount=" + ordersCount + "]";
	}
    
}
