package FoodDeliverySystem;
import java.util.Arrays;

/**
 * Represents a Restaurant Manager (RestAdmin) in the Food Delivery System.
 *
 * Unlike the system Admin (who manages everything), a RestAdmin can only
 * manage specific restaurants assigned to them. 
 * * Key rules for this class:
 * - Inherits basic credentials (name, username, password) from the Admin class.
 * - Maintains a dynamic array of managed restaurants.
 * - A RestAdmin can manage multiple restaurants, but duplicate restaurants 
 * within the same manager's list are strictly prohibited.
 * - The managedRestaurants[] array grows dynamically using Arrays.copyOf.
 */

public class RestAdmin extends Admin{
	// Dynamic array to hold the restaurants this manager is responsible for
    private Restaurant[] managedRestaurants;
    private int count;
    public RestAdmin(String managerName, String username, String password) {
    	super(managerName,username,password);
        // Initializing the dynamic array with a size of 0
        this.managedRestaurants = new Restaurant[0];
        this.count = 0;
    }
        
        
     //Helper Methods

        // 1. Add a restaurant dynamically
        public boolean addRestaurant(Restaurant r) {
            if (r == null) {
                return false;
            }
         // Check for duplicates - Assignment requirement!
            for (int i = 0; i < count; i++) {
                if (this.managedRestaurants[i].getRestaurantCode() == r.getRestaurantCode()) {
                    System.out.println("Error: Restaurant already managed by this admin.");
                    return false; 
                }
            }
            
            // If the array is full, dynamically resize it by adding 1 extra slot
            if (this.count >= this.managedRestaurants.length) {
                int newSize = (this.managedRestaurants.length == 0) ? 1 : this.managedRestaurants.length + 1;
                this.managedRestaurants = Arrays.copyOf(this.managedRestaurants, newSize);
            }
            
            this.managedRestaurants[count] = r;
            this.count++;
            return true; 
        }

        // 3. Login verification
        public boolean login(String inputUsername, String inputPassword) {
            // Using Getters because the fields are inherited from Admin
            return this.getUsername().equals(inputUsername) && this.getPassword().equals(inputPassword);
        }
        
       //Getters and Setters

		public Restaurant[] getManagedRestaurants() {
			return managedRestaurants;
		}
		public void setManagedRestaurants(Restaurant[] managedRestaurants) {
			this.managedRestaurants = managedRestaurants;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		/**
		 * Checks whether this manager is responsible for the given restaurant.
		 * Used by the menu code to enforce the rule:
		 * "A RestAdmin can only manage restaurants assigned to them."
		 *
		 * @param restaurantCode the code of the restaurant to check
		 * @return true if this manager has this restaurant in their list
		 */
		public boolean isResponsibleFor(int restaurantCode) {
		    for (int i = 0; i < count; i++) {
		        if (managedRestaurants[i] != null &&
		            managedRestaurants[i].getRestaurantCode() == restaurantCode) {
		            return true;
		        }
		    }
		    return false;
		}
		//toString
		@Override
		public String toString() {
	        // We use Getters to retrieve the fields inherited from Admin
	        return "RestAdmin [managerName=" + getManagerName() + 
	               ", username=" + getUsername() + 
	               ", password=" + getPassword() +
	               ", managedRestaurants=" + Arrays.toString(managedRestaurants) + 
	               ", count=" + count + "]";
	    }
}
