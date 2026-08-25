package FoodDeliverySystem;

/**
 * Thrown when a customer tries to place an order they cannot afford - their
 * credit balance is lower than the order's final price (HW3 Part F).
 * Checked exception (extends Exception).
 *
 * Modeling this as an exception (instead of a silent 'return false') means the
 * payment flow cannot accidentally proceed when funds are short: the throw
 * interrupts it, and a catch reports the problem to the user.
 */
public class InsufficientBalanceException extends Exception {

	/** Creates the exception with a specific, caller-supplied message. */
	public InsufficientBalanceException(String message) {
		super(message);
	}

	/** Creates the exception with a sensible default message. */
	public InsufficientBalanceException() {
		super("Insufficient balance to complete this order.");
	}
}