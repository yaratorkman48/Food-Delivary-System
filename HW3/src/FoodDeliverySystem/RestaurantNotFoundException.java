package FoodDeliverySystem;

/**
 * Thrown when a restaurant is looked up by code but does not exist in the
 * system (HW3 Part F). Checked exception (extends Exception).
 *
 * Distinct type from the other lookups so a handler can react to a missing
 * restaurant differently from, say, a missing customer.
 */
public class RestaurantNotFoundException extends Exception {

	/** Creates the exception with a specific, caller-supplied message. */
	public RestaurantNotFoundException(String message) {
		super(message);
	}

	/** Creates the exception with a sensible default message. */
	public RestaurantNotFoundException() {
		super("Restaurant not found in the system.");
	}
}