package FoodDeliverySystem;
/**
 * Represents a System Administrator in the Food Delivery System.
 * The Admin has full management permissions over the entire system:
 * managing customers, restaurants, restaurant managers, riders,
 * and assigning riders to orders.
 *
 * Per the assignment, the system has exactly one Admin, with the
 * default credentials: username = "admin", password = "12345".
 */

public class Admin {
	protected String managerName;
	protected String username;
	protected String password;
	
	//Constructors
	public Admin(String managerName, String username, String password) {
		this.managerName = managerName;
		this.username = username;
		this.password = password;
	}
	
	//Helper Method for Menu Login
		public boolean login(String inputUsername, String inputPassword) {
			return this.username.equals(inputUsername) && this.password.equals(inputPassword);
		} 
		
	//Getters and Setters
	public String getManagerName() {
		return managerName;
	}
	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	//toString
	@Override
	public String toString() {
		return "Admin [managerName=" + managerName + ", username=" + username + ", password=" + password + "]";
	}
}
