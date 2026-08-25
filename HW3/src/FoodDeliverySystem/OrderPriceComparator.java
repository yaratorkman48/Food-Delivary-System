package FoodDeliverySystem;

import java.util.Comparator;
/**
 * A comparator for the Order class.
 * This class sorts orders based on their final price in descending order 
 * (from highest price to lowest price).
 */
public class OrderPriceComparator implements Comparator<Order> {

    /**
     * Compares two Order objects based on their final price.
     * The comparison is done in reverse order to achieve a descending sort.
     *
     * @param o1 the first order to be compared.
     * @param o2 the second order to be compared.
     * @return a negative integer, zero, or a positive integer as the second
     * order's final price is greater than, equal to, or less than
     * the first order's final price.
     */
    @Override
    public int compare(Order o1, Order o2) {
        // השוואה הפוכה (o2 לעומת o1) כדי למיין מהגבוה לנמוך
        return Double.compare(o2.getFinalPrice(), o1.getFinalPrice());
    }
}