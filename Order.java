package FoodDeliverySystem;
/**
 * Represents an Order in the Food Delivery System.
 *
 * An Order links three actors together:
 *   - A Customer (referenced by code only - clean approach, no duplicate data)
 *   - A Restaurant (held as both an instance reference AND a code,
 *                   per the explicit assignment requirement)
 *   - A Rider     (referenced by ID, may be empty until a rider is assigned)
 *
 * The Order's final price is calculated POLYMORPHICALLY:
 *   When setBasicAmount() is called, it automatically invokes the linked
 *   Restaurant's calculateFinalPrice() method. Because Restaurant has two
 *   subclasses (FastFoodRestaurant, PremiumRestaurant) that override
 *   calculateFinalPrice(), the price formula used depends on the actual
 *   restaurant type at runtime - the core OOP requirement of the assignment.
 */
public class Order {
	/** Initial status when an order is created and sent to the restaurant. */
    public static final String STATUS_SENT = "sent";

    /** Status when a rider has picked up the order and is in transit. */
    public static final String STATUS_ON_THE_WAY = "on the way";

    /** Final status when the order has been delivered to the customer. */
    public static final String STATUS_DELIVERED = "delivered";

    private int orderCode;          // Unique identifier for the order
    private int customerCode;       // Reference to the placing customer (by code only)

    private Restaurant restaurant;  // Polymorphic reference: can be Restaurant,
                                    // FastFoodRestaurant, or PremiumRestaurant
    private int restaurantCode;     // Same restaurant's code, stored explicitly
                                    // (assignment requires BOTH the reference and the code)

    private String riderCode;       // Rider id (matches Rider.id which is a String);
                                    // null/empty until DeliverySystem assigns a rider

    // Order date (set when the order is placed)
    private int orderDay;
    private int orderMonth;
    private int orderYear;

    // Delivery date (defaults to 0/0/0, filled in when status becomes DELIVERED)
    private int deliveryDay;
    private int deliveryMonth;
    private int deliveryYear;

    // Pricing
    private double basicAmount;     // Sum of menu items chosen by the customer
    private double finalPrice;      // Computed by restaurant.calculateFinalPrice()

    private String status;          // One of STATUS_SENT / STATUS_ON_THE_WAY / STATUS_DELIVERED

    
	 // Constructors

    /**
     * Default constructor.
     * Creates an empty Order. Mainly useful for testing.
     * Delivery date defaults to 0/0/0 as the assignment requires.
     */
    public Order() {
        this.orderCode = 0;
        this.customerCode = 0;
        this.restaurant = null;
        this.restaurantCode = 0;
        this.riderCode = "";
        this.orderDay = 0;
        this.orderMonth = 0;
        this.orderYear = 0;
        this.deliveryDay = 0;
        this.deliveryMonth = 0;
        this.deliveryYear = 0;
        this.basicAmount = 0.0;
        this.finalPrice = 0.0;
        this.status = STATUS_SENT;
    }
     public Order(int orderCode, int customerCode, Restaurant restaurant, int restaurantCode,
			int orderDay, int orderMonth, int orderYear,double basicAmount) {
		this.orderCode = orderCode;
		this.customerCode = customerCode;
		this.restaurant = restaurant;
		this.restaurantCode = restaurantCode;
		
		this.riderCode = "";//Empty string until assigned
		this.orderDay = orderDay;
		this.orderMonth = orderMonth;
		this.orderYear = orderYear;
		this.deliveryDay = 0;
		this.deliveryMonth = 0;
		this.deliveryYear = 0;
	
		this.status = STATUS_SENT;
		 // Set the basic amount THROUGH the setter so that finalPrice is
        // computed polymorphically. This is the heart of the assignment.
        setBasicAmount(basicAmount);
	}
     
     //Getters and Setters

     /**
      * Sets the basic amount AND automatically recomputes the final price
      * using the linked restaurant's polymorphic calculateFinalPrice() method.
      *
      * This is the ONLY way to update finalPrice. There is intentionally
      * no public setFinalPrice() method, because finalPrice must always
      * stay in sync with basicAmount AND the restaurant's pricing rules.
      *
      * If no restaurant is linked yet, finalPrice is set to 0 to avoid
      * stale data.
      *
      * @param basicAmount the basic amount entered by the customer
      */
     public void setBasicAmount(double basicAmount) {
    	    this.basicAmount = basicAmount;
    	    if (this.restaurant != null) {
    	        this.finalPrice = this.restaurant.calculateFinalPrice(basicAmount);
    	    } else {
    	        this.finalPrice = 0.0;
    	    }
    	}
     /**
      * Sets the linked restaurant.
      * If a basicAmount has already been set, also recomputes the finalPrice
      * using the new restaurant's polymorphic calculateFinalPrice().
      *
      * @param restaurant the Restaurant instance (any subtype allowed)
      */
     public void setRestaurant(Restaurant restaurant) {
         this.restaurant = restaurant;
         // Recompute finalPrice if we already have a basic amount
         if (restaurant != null && basicAmount > 0) {
             this.finalPrice = restaurant.calculateFinalPrice(basicAmount);
         }
     }
     /**
      * Marks this order as delivered and records the delivery date.
      * Combines three operations the rider must do at once:
      *   - update status to "delivered"
      *   - record day, month, year of delivery
      *
      * @param day   delivery day  (1-31)
      * @param month delivery month (1-12)
      * @param year  delivery year (e.g. 2026)
      */
     public void markAsDelivered(int day, int month, int year) {
         this.deliveryDay = day;
         this.deliveryMonth = month;
         this.deliveryYear = year;
         this.status = STATUS_DELIVERED;
     }

	public int getOrderCode() {
		return orderCode;
	}

	public void setOrderCode(int orderCode) {
		this.orderCode = orderCode;
	}

	public int getCustomerCode() {
		return customerCode;
	}

	public void setCustomerCode(int customerCode) {
		this.customerCode = customerCode;
	}

	public Restaurant getRestaurant() {
		return restaurant;
	}

	public int getRestaurantCode() {
		return restaurantCode;
	}

	public void setRestaurantCode(int restaurantCode) {
		this.restaurantCode = restaurantCode;
	}

	public String getRiderCode() {
		return riderCode;
	}

	public void setRiderCode(String riderCode) {
		this.riderCode = riderCode;
	}

	public int getOrderDay() {
		return orderDay;
	}

	public void setOrderDay(int orderDay) {
		this.orderDay = orderDay;
	}

	public int getOrderMonth() {
		return orderMonth;
	}

	public void setOrderMonth(int orderMonth) {
		this.orderMonth = orderMonth;
	}

	public int getOrderYear() {
		return orderYear;
	}

	public void setOrderYear(int orderYear) {
		this.orderYear = orderYear;
	}

	public int getDeliveryDay() {
		return deliveryDay;
	}

	public void setDeliveryDay(int deliveryDay) {
		this.deliveryDay = deliveryDay;
	}

	public int getDeliveryMonth() {
		return deliveryMonth;
	}

	public void setDeliveryMonth(int deliveryMonth) {
		this.deliveryMonth = deliveryMonth;
	}

	public int getDeliveryYear() {
		return deliveryYear;
	}

	public void setDeliveryYear(int deliveryYear) {
		this.deliveryYear = deliveryYear;
	}

	public double getBasicAmount() {
		return basicAmount;
	}

	public double getFinalPrice() {
		return finalPrice;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	//toString
	@Override
	public String toString() {
	    return "Order [orderCode=" + orderCode
	         + ", customerCode=" + customerCode
	         + ", restaurant=" + (restaurant != null ? restaurant.getName() : "N/A")
	         + ", restaurantCode=" + restaurantCode
	         + ", riderCode=" + (riderCode.isEmpty() ? "(unassigned)" : riderCode)
	         + ", orderDate=" + orderDay + "/" + orderMonth + "/" + orderYear
	         + ", deliveryDate=" + (deliveryYear == 0
	                ? "(not yet delivered)"
	                : deliveryDay + "/" + deliveryMonth + "/" + deliveryYear)
	         + ", basicAmount=" + basicAmount
	         + ", finalPrice=" + finalPrice
	         + ", status=" + status + "]";
	}
}
