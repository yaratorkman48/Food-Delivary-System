package FoodDeliverySystem;
import java.util.ArrayList;

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
	// A single ArrayList that manages its own size, so we don't need a separate count field	
    private ArrayList<Restaurant> managedRestaurants;
    //Constructors

    public RestAdmin(String managerName, String username, String password) {
    	super(managerName,username,password);
        // Initializing the dynamic array with a size of 0
        this.managedRestaurants = new ArrayList<>();
    }
                
     //Helper Methods


    /**
     * Adds a restaurant to this manager's portfolio.
     *
     * Refuses to add (returns false) when:
     *   - the parameter is null
     *   - a restaurant with the same restaurantCode is already in the list
     *     (no duplicates allowed - assignment requirement)
     * @param r the restaurant to add
     * @return true if added, false if null or duplicate
     */
        public boolean addRestaurant(Restaurant r) {
            if (r == null) {
                return false;
            }
         // Duplicate check by restaurant code
            for (Restaurant existing : managedRestaurants) {
				if (existing.getRestaurantCode() == r.getRestaurantCode()) {
					return false; // already managed by this admin
				}
			}
            managedRestaurants.add(r); // ArrayList grows automatically
			return true;
        }

        /**
         * Login verification for this manager.
         * Compares the supplied credentials against the manager's own.
         *
         * @param inputUsername the username entered by the user
         * @param inputPassword the password entered by the user
         * @return true if both match, false otherwise
         */
        public boolean login(String inputUsername, String inputPassword) {
            return this.getUsername().equals(inputUsername)
                && this.getPassword().equals(inputPassword);
        }

        
       //Getters and Setters

		public ArrayList<Restaurant> getManagedRestaurants() {
			return managedRestaurants;
		}
		public void setManagedRestaurants( ArrayList<Restaurant> managedRestaurants) {
			this.managedRestaurants = managedRestaurants;
		}

	    /**
	     * Returns how many restaurants this manager is responsible for.
	     *
	     * This is a DERIVED value: instead of storing a counter that has to be
	     * incremented in lock-step with the list, we ask the list for its size.
	     * One source of truth = no chance of the count and the actual contents
	     * drifting apart.
	     *
	     * @return the number of restaurants under this manager
	     */
	    public int getCount() {
	        return managedRestaurants.size();
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
		    for (Restaurant r : managedRestaurants) {
		        if (r.getRestaurantCode() == restaurantCode) {
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
	               ", managedRestaurants=" +managedRestaurants
	               + ", count=" + getCount() + "]";
	        
	        
	    }
}
