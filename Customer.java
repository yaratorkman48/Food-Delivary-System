package FoodDeliverySystem;
/**
 * Represents a customer in the Food Delivery System.
 * A customer can place orders to restaurants and have their orders delivered by riders.
 * Each customer has a unique customer code, contact details, a full address,
 * and a credit balance used for payments.
 */
public class Customer {
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

//toString
	@Override
	public String toString() {
		return "Customer [customerCode=" + customerCode + ", firstName=" + firstName + ", lastName=" + lastName
				+ ", street=" + street + ", city=" + city + ", zipCode=" + zipCode + ", phone=" + phone + ", email="
				+ email + ", creditBalance=" + creditBalance + "]";
	}
	
}
