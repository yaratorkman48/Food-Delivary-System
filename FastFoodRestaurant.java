package FoodDeliverySystem;
/**
 * Represents a Fast-Food Restaurant in the Food Delivery System.
 * Extends the {@link Restaurant} class and adds two specialized fields:
 *   - average preparation time (in minutes)
 *   - extra surcharge added for fast delivery
 *
 * This class overrides {@link #calculateFinalPrice(double)} so that any
 * order from a FastFoodRestaurant automatically includes the fast-delivery
 * surcharge on top of the base delivery fee.
 */

public class FastFoodRestaurant extends Restaurant {

    private int avgPrepTimeMinutes;        // Average preparation time in minutes
    private double fastDeliverySurcharge;  // Extra cost added for fast delivery


    //Constructors
	public FastFoodRestaurant(int restaurantCode, String name, String cuisineType, double rating, boolean isOpen,
			double baseDeliveryFee, int avgPrepTimeMinutes, double fastDeliverySurcharge) {
		super(restaurantCode, name, cuisineType, rating, isOpen, baseDeliveryFee);
		this.avgPrepTimeMinutes = avgPrepTimeMinutes;
		this.fastDeliverySurcharge = fastDeliverySurcharge;
	}


	//Getters and Setters
	public int getAvgPrepTimeMinutes() {
		return avgPrepTimeMinutes;
	}

	public void setAvgPrepTimeMinutes(int avgPrepTimeMinutes) {
		this.avgPrepTimeMinutes = avgPrepTimeMinutes;
	}

	public double getFastDeliverySurcharge() {
		return fastDeliverySurcharge;
	}

	public void setFastDeliverySurcharge(double fastDeliverySurcharge) {
		this.fastDeliverySurcharge = fastDeliverySurcharge;
	}
	
	   /**
     * Calculates the final price for a fast-food order.
     *
     * Formula:
     *     finalPrice = basicAmount + baseDeliveryFee + fastDeliverySurcharge
     *
     * This overrides the parent's calculation to include the fast-delivery
     * surcharge. Java will dispatch to this version at runtime whenever the
     * underlying object is a FastFoodRestaurant, even if the variable type
     * is the more general Restaurant.
     *
     * @param basicAmount the basic amount of the order (before fees)
     * @return the final price including base delivery fee and fast-delivery surcharge
     */
    @Override
    public double calculateFinalPrice(double basicAmount) {
        if (basicAmount < 0) {
            return 0.0;
        }
        return basicAmount + baseDeliveryFee + fastDeliverySurcharge;
    }

	//toString
	@Override
    public String toString() {
        // Appends the parent string so you don't lose the restaurant's name, code, etc.
        return "FastFood " + super.toString() + 
               " [Prep Time: " + avgPrepTimeMinutes + " mins, Fast Surcharge: ₪" + fastDeliverySurcharge + "]";
    }
}
