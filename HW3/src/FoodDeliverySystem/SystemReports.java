package FoodDeliverySystem;

import java.util.ArrayList;

/**
 * Reporting utilities for the Food Delivery System (H_W3 Part D - Wildcards).
 *
 * This class exists to demonstrate bounded wildcards and a bounded generic
 * method. The guiding rule throughout is PECS - "Producer Extends, Consumer
 * Super":
 *   - A parameter we READ from is a producer  -> ? extends ...
 *   - A parameter we WRITE to  is a consumer  -> ? super ...
 *
 * Design decision: these are instance methods (called on a SystemReports
 * object) rather than static helpers, to keep the OOP "objects do work" style
 * the course emphasizes. The class holds no state, so a single shared instance
 * is enough.
 */

public class SystemReports {
	
	/**
     * Displays all the restaurants in the given collection.
     * * This method uses an upper-bounded wildcard (? extends Restaurant) to allow
     * flexibility, enabling it to accept an ArrayList of the base Restaurant class,
     * as well as ArrayLists of specific subclasses like FastFoodRestaurant or PremiumRestaurant.
     *
     * @param restaurants the ArrayList containing Restaurant objects (or any of its subclasses) to display.
     */
	
	
	public void displayAllRestaurants(ArrayList<? extends Restaurant> restaurants) {
		if (restaurants == null || restaurants.isEmpty()) {
			System.out.println("(no restaurants to display");
			return;
		}
		for (Restaurant r : restaurants) {
			System.out.println("  " + r);
		}
	}
	
	/**
	 * Returns the sum of the final prices of every order in the collection.
	 *
	 * Parameter is ? extends Order - another PRODUCER (we READ orders to add
	 * up their prices), so 'extends' again. The method works for any list
	 * whose element type is Order or a subtype of Order.
	 *
	 * @param orders a list of orders of any single order type
	 * @return the total of all final prices (0.0 if the list is null/empty)
	 */
	
	public  double sumFinalPrices(ArrayList<? extends Order> orders) {
		double total = 0.0;
		if (orders == null) {
			return total;
		}
		
		for (Order o : orders) {
			total+= o.getFinalPrice();
		}
		return total;
	}

	/**
	 * Adds a FastFoodRestaurant to the given collection.
	 *
	 * Parameter is ? super FastFoodRestaurant - "a list of FastFoodRestaurant
	 * OR any supertype" (Restaurant, Object). This is a CONSUMER: we WRITE a
	 * FastFoodRestaurant into it, so 'super' is correct (PECS). The add is
	 * type-safe because, whatever the list's real element type, a
	 * FastFoodRestaurant IS-A that type. This signature accepts:
	 *     ArrayList<FastFoodRestaurant>
	 *     ArrayList<Restaurant>
	 *     ArrayList<Object>
	 *
	 * @param list       a list that can hold FastFoodRestaurant (or a supertype)
	 * @param restaurant the FastFoodRestaurant to add
	 */
	public void addFastFoodRestaurant(ArrayList<? super FastFoodRestaurant> list,
	                                  FastFoodRestaurant restaurant) {
		if (list == null || restaurant == null) {
			return;
		}
		list.add(restaurant);
	}

	/**
	 * Returns the largest element of a collection, for ANY type that knows how
	 * to compare itself.
	 *
	 * This is a bounded GENERIC method, not a wildcard one. <T extends
	 * Comparable<T>> means "T may be any type, as long as it implements
	 * Comparable" - which guarantees every element has compareTo, so we can
	 * rank them. Returning T (not Object) means the caller gets back the exact
	 * type they passed in.
	 *
	 * "Largest" is defined purely by T's own compareTo: max is the element e
	 * for which no other element compares greater. The method itself is
	 * neutral about direction - it simply trusts each type's natural ordering.
	 *
	 * @param <T>  any type implementing Comparable<T>
	 * @param list the list to scan
	 * @return the maximum element, or null if the list is null/empty
	 */
	public <T extends Comparable<T>> T findMax(ArrayList<T> list) {
		if (list == null || list.isEmpty()) {
			return null;
		}
		T max = list.get(0);
		for (T element : list) {
			if (element.compareTo(max) > 0) {
				max = element;
			}
		}
		return max;
	}
	
}
