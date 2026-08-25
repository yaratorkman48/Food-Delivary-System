package FoodDeliverySystem;

/**
 * Thrown when a customer is looked up by code but does not exist in the system
 * (HW3 Part F). Checked exception (extends Exception) so the compiler forces
 * callers to handle or declare it - which is what lets us demonstrate 'throws'.
 *
 * Carries no new fields: it inherits message storage and the stack trace from
 * Exception. Its VALUE is its type - catching this specifically tells the
 * handler exactly what went wrong, separately from every other failure.
 */
public class CustomerNotFoundException extends Exception {

	/** Creates the exception with a specific, caller-supplied message. */
	public CustomerNotFoundException(String message) {
		super(message);
	}

	/** Creates the exception with a sensible default message. */
	public CustomerNotFoundException() {
		super("Customer not found in the system.");
	}
}