package FoodDeliverySystem;
/**
 * Represents a Premium (high-end / luxury) Restaurant in the Food Delivery System.
 * Extends the {@link Restaurant} class and adds two specialized fields:
 *   - minimum order amount (orders below this amount are not allowed)
 *   - extra commission percent added to the order price
 *
 * This class overrides {@link #calculateFinalPrice(double)} so that any order
 * from a PremiumRestaurant:
 *   1) Is rejected (returns 0) if it does not meet the minimum order amount.
 *   2) Otherwise includes both the base delivery fee AND an extra commission
 *      calculated as a percentage of the basic amount.
 */
public class PremiumRestaurant extends Restaurant {
	   private double minimumOrderAmount;     // Minimum allowed order amount
	    private double extraCommissionPercent; // Extra commission as a percentage (e.g. 10.0 = 10%)
	    
	    //Constructors
		public PremiumRestaurant(int restaurantCode, String name, String cuisineType, double rating, boolean isOpen,
				double baseDeliveryFee, double minimumOrderAmount, double extraCommissionPercent) {
			super(restaurantCode, name, cuisineType, rating, isOpen, baseDeliveryFee);
			this.minimumOrderAmount = minimumOrderAmount;
			this.extraCommissionPercent = extraCommissionPercent;
		}
		
		//Getters and Setters
		public double getMinimumOrderAmount() {
			return minimumOrderAmount;
		}
		public void setMinimumOrderAmount(double minimumOrderAmount) {
			this.minimumOrderAmount = minimumOrderAmount;
		}
		public double getExtraCommissionPercent() {
			return extraCommissionPercent;
		}
		public void setExtraCommissionPercent(double extraCommissionPercent) {
			this.extraCommissionPercent = extraCommissionPercent;
		}
		
		   /**
	     * Calculates the final price for a premium-restaurant order.
	     *
	     * Formula:
	     *   - If basicAmount < minimumOrderAmount, return 0 (order is invalid).
	     *   - Otherwise:
	     *       commission = basicAmount * (extraCommissionPercent / 100)
	     *       finalPrice = basicAmount + baseDeliveryFee + commission
	     *
	     * This overrides the parent's calculation to enforce the minimum and
	     * apply the extra commission. Returning 0 for invalid orders signals
	     * to the caller that the minimum was not met (the caller should check).
	     *
	     * @param basicAmount the basic amount of the order (before fees)
	     * @return final price including delivery fee and commission, or 0 if below minimum
	     */
	    @Override
	    public double calculateFinalPrice(double basicAmount) {
	        if (basicAmount < 0) {
	            return 0.0;
	        }
	        if (basicAmount < minimumOrderAmount) {
	            return 0.0; // does not meet the minimum order requirement
	        }
	        double commission = basicAmount * (extraCommissionPercent / 100.0);
	        return basicAmount + baseDeliveryFee + commission;
	    }

		//toString
		@Override
		public String toString() {
		    return "Premium " + super.toString() + 
		           " [Minimum Order: ₪" + minimumOrderAmount + 
		           ", Commission: " + extraCommissionPercent + "%]";
		}
}
