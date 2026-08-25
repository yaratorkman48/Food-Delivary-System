package FoodDeliverySystem;

/**
 * Thrown when an attempt is made to assign a rider who is not currently
 * available to an order (HW3 Part F). Checked exception (extends Exception).
 *
 * A distinct type so the rider-assignment flow can report "rider unavailable"
 * specifically, separately from "rider not found" or any other failure.
 */
public class DeliveryPersonUnavailableException extends Exception {

	/** Creates the exception with a specific, caller-supplied message. */
	public DeliveryPersonUnavailableException(String message) {
		super(message);
	}

	/** Creates the exception with a sensible default message. */
	public DeliveryPersonUnavailableException() {
		super("The selected delivery person is not available.");
	}
}