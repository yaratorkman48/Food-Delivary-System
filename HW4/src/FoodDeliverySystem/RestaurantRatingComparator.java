package FoodDeliverySystem;
import java.util.Comparator;
/**
 * A comparator for the Restaurant class.
 * This class sorts restaurants based on their rating in descending order 
 * (from highest rating to lowest rating).
 */

public class RestaurantRatingComparator implements Comparator<Restaurant> {
	/**
	 * Compares two Restaurant objects based on their rating.
	 * The comparison is done in reverse order to achieve a descending sort.
	 *
	 * @param r1 the first restaurant to be compared.
	 * @param r2 the second restaurant to be compared.
	 * @return a negative integer, zero, or a positive integer as the second
	 * restaurant's rating is greater than, equal to, or less than
	 * the first restaurant's rating.
	 */
	@Override
	public int compare(Restaurant r1, Restaurant r2) {
		return Double.compare(r2.getRating(), r1.getRating());
	}
}