package FoodDeliverySystem;
/**
 * Represents a customer in the Food Delivery System.
 * A customer can place orders to restaurants and have their orders delivered by riders.
 * Each customer has a unique customer code, contact details, a full address,
 * and a credit balance used for payments.
 * 
 */
public class Customer implements Comparable<Customer> {
	private int customerCode;
	private String firstName;
	private String lastName;
	private String street;
	private String city;
	private String zipCode;
	private String phone;
	private String email;
	private double creditBalance;
	
	
//Constructors
	public Customer(int customerCode, String firstName, String lastName, String street, String city, String zipCode,
			String phone,String email, double creditBalance) {
		this.customerCode = customerCode;
		this.firstName = firstName;
		this.lastName = lastName;
		this.street = street;
		this.city = city;
		this.zipCode = zipCode;
		this.phone = phone;
		this.email = email;
		this.creditBalance = creditBalance;
	}


//Getters and Setters 
	public int getCustomerCode() {
		return customerCode;
	}
	public void setCustomerCode(int customerCode) {
		this.customerCode = customerCode;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getZipCode() {
		return zipCode;
	}
	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public double getCreditBalance() {
		return creditBalance;
	}
	public void setCreditBalance(double creditBalance) {
		this.creditBalance = creditBalance;
	}

	/**
	 * Adds money to this customer's credit balance.
	 *
	 * Used by:
	 *   - The customer menu's "Load money to balance" option (Task 10).
	 *
	 * Rejects non-positive amounts (no zero or negative top-ups).
	 *
	 * @param amount the amount to add (must be &gt; 0)
	 * @return true on success, false if the amount is zero or negative
	 */
	public boolean topUp(double amount) {
		if (amount <= 0) return false;
		this.creditBalance += amount;
		return true;
	}

	/**
	 * Deducts money from this customer's credit balance, if sufficient
	 * funds are available.
	 *
	 * Used by:
	 *   - "Place order" flows (both RestAdmin Task 8 and Customer Task 10)
	 *     when the customer pays for the order's final price.
	 *   - The customer menu's "Withdraw money from balance" option (Task 10).
	 *
	 * Rejects non-positive amounts AND attempts that exceed the balance.
	 *
	 * @param amount the amount to remove (must be &gt; 0 and &lt;= balance)
	 * @return true on success, false if the amount is invalid or balance is too low
	 */
	public boolean withdraw(double amount) {
		if (amount <= 0)               return false;
		if (creditBalance < amount)    return false;
		this.creditBalance -= amount;
		return true;
	}
	/**
     * Compares this customer with another customer for ordering purposes.
     * The comparison is based on the customer's credit balance in descending order
     * (i.e., a customer with a higher credit balance will appear before a customer 
     * with a lower credit balance).
     *
     * @param other the other Customer object to be compared.
     * @return a negative integer, zero, or a positive integer as this customer's
     *         credit balance is greater than, equal to, or less than the specified
     *         customer's credit balance.
     */
	
	@Override
	public int compareTo(Customer other) {
		return Double.compare(other.creditBalance, this.creditBalance);
	}
//toString  
	
	@Override
	public String toString() {
		return "Customer [customerCode=" + customerCode + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", street=" + street + ", city=" + city + ", zipCode=" + zipCode + ", phone=" + phone + ", email="
				+ email + ", creditBalance=" + creditBalance + "]";
	}
	
}
