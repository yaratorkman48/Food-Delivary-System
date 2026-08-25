package FoodDeliverySystem;
import java.util.ArrayList;
/**
 * Represents a delivery Rider in the Food Delivery System.
 * A rider is responsible for picking up orders from restaurants and delivering them to customers.
 * Each rider has a unique national ID (תז), a vehicle type, an availability flag,
 * and a dynamic list of orders they have been assigned to.
 *
 * NOTE: In Assignment 1, the rider's orders were stored in a manually-grown
 * {@code Order[]} array using {@code Arrays.copyOf}, plus a separate
 * {@code ordersCount} integer to track how many slots were filled. 
 * In Assignment 2, this is replaced by a single {@code ArrayList<Order>}.
 * The ArrayList grows automatically when we call {@code add()}, and it
 * always knows its own size via {@code size()} - so the separate counter
 * field is no longer needed (and keeping one would risk the two getting
 * out of sync, a classic bug).
 */

public class Rider {
    private String id;
    private String fullName;
    private String phone;
    private String vehicle;
    private boolean available;
    private ArrayList<Order> orders; // a  single ArrayList that manages its own size
    
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
        this.orders = new ArrayList<>();  //  
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
        this.orders = new ArrayList<>();  //
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

	public ArrayList<Order> getOrders() {
		return orders;
	}

	public void setOrders(ArrayList<Order> orders) {
		this.orders = orders;
	}


    /**
     * Returns how many orders are currently assigned to this rider.
     *
     * This is now a DERIVED value: instead of storing a counter that we
     * must remember to increment, we just ask the list for its size.
     * One source of truth = no chance of the count drifting away from
     * the actual number of orders.
     *
     * @return the number of orders assigned to this rider
     */
    public int getOrdersCount() {
        return orders.size();
    }

    /**
     * Adds an order to this rider's list of assigned orders.
     *
     * Refuses to add (returns false) if:
     *   - the parameter is null
     *   - an order with the same orderCode is already in the list (no duplicates)
     *
     * Compared to Assignment 1, the manual {@code Arrays.copyOf} grow step is
     * gone - {@code orders.add(order)} handles resizing internally. We still
     * keep an explicit duplicate check by order code, because two different
     * Order objects could share the same code, and the assignment forbids
     * duplicates.
     *
     * @param order the order to add
     * @return true if the order was successfully added, false otherwise
     */
    public boolean addOrder(Order order) {
        if (order == null) {
            return false;
        }
        // Duplicate check by order code (enhanced for-loop reads cleaner
        // than an index-based loop now that there is no separate counter)
        for (Order existing : orders) {
            if (existing.getOrderCode() == order.getOrderCode()) {
                return false; // duplicate - do nothing
            }
        }
        orders.add(order);  // ArrayList grows automatically
        return true;
    }
    
	 //toString
	@Override
	public String toString() {
		return "Rider [id=" + id + ", fullName=" + fullName + ", phone=" + phone + ", vehicle=" + vehicle
				+ ", available=" + available + ", ordersCount=" + orders.size() + "]";
	}
    
}
