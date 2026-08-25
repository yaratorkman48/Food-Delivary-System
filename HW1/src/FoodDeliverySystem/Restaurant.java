package FoodDeliverySystem;
/**
 * Represents a generic Restaurant in the Food Delivery System.
 * This is the parent (super) class of the restaurant hierarchy.
 *
 * Two specialized restaurant types extend this class:
 *   - FastFoodRestaurant: adds preparation time and a fast-delivery surcharge.
 *   - PremiumRestaurant:  adds a minimum order requirement and an extra commission.
 *
 * Polymorphism is implemented through the {@link #calculateFinalPrice(double)} method,
 * which is overridden differently in each subclass. This means callers can hold a
 * generic Restaurant reference and Java will automatically dispatch to the correct
 * version at runtime.
 *
 * Fields are declared 'protected' (not 'private') so that the subclasses can access
 * them directly, which is the standard pattern for inheritance.
 */
public class Restaurant {

    protected int restaurantCode;       // Unique identifier for the restaurant
    protected String name;              // Restaurant's display name
    protected String cuisineType;       // Type of cuisine (e.g. "Italian", "Sushi")
    protected double rating;            
    protected boolean isOpen;           
    protected double baseDeliveryFee;
    
    // Constructors
	public Restaurant(int restaurantCode, String name, String cuisineType, double rating, boolean isOpen,
			double baseDeliveryFee) {
		this.restaurantCode = restaurantCode;
		this.name = name;
		this.cuisineType = cuisineType;
		this.rating = rating;
		this.isOpen = isOpen;
		this.baseDeliveryFee = baseDeliveryFee;
	}

	//Getters and Setters
	public int getRestaurantCode() {
		return restaurantCode;
	}

	public void setRestaurantCode(int restaurantCode) {
		this.restaurantCode = restaurantCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCuisineType() {
		return cuisineType;
	}

	public void setCuisineType(String cuisineType) {
		this.cuisineType = cuisineType;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public boolean isOpen() {
		return isOpen;
	}

	public void setOpen(boolean isOpen) {
		this.isOpen = isOpen;
	}

	public double getBaseDeliveryFee() {
		return baseDeliveryFee;
	}

	public void setBaseDeliveryFee(double baseDeliveryFee) {
		this.baseDeliveryFee = baseDeliveryFee;
	}
	

    /**
     * Calculates the final price for an order from this restaurant.
     *
     * For a regular Restaurant, the formula is simply:
     *     finalPrice = basicAmount + baseDeliveryFee
     *
     * Subclasses override this method to add their own price-calculation logic
     * (e.g. fast-delivery surcharge, premium commission, minimum-order check).
     *
     * @param basicAmount the basic amount of the order (before fees)
     * @return the final price including delivery fee
     */
    public double calculateFinalPrice(double basicAmount) {
        if (basicAmount < 0) {
            return 0.0; // defensive guard against negative input
        }
        return basicAmount + baseDeliveryFee;
    }

	//toString
	@Override
	public String toString() {
		return "Restaurant [restaurantCode=" + restaurantCode + ", name=" + name + ", cuisineType=" + cuisineType
				+ ", rating=" + rating + ", isOpen=" + isOpen + ", baseDeliveryFee=" + baseDeliveryFee + "]";
	} 
}
