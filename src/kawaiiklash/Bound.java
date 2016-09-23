package kawaiiklash;

import static java.lang.Math.random;
import static java.lang.Math.round;

/**
 * A class which describes a range between and including two ends. Used
 * primarily for damage and knockback values.
 *
 * @author Jeff Niu
 */
public class Bound {

    private double lower;
    private double upper;

    /**
     * Create a {@code Bound} object with the upper and lower bounds zero.
     */
    public Bound() {
        lower = 0;
        upper = 0;
    }

    /**
     * Create a {@code Bound} with specified lower and upper bounds.
     *
     * @param lower
     * @param upper
     */
    public Bound(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }

    /**
     * Set the value of the lower bound.
     *
     * @param lower
     */
    public void setLowerBound(double lower) {
        this.lower = lower;
    }

    /**
     * Set the value of the upper bound.
     *
     * @param upper
     */
    public void setUpperBound(double upper) {
        this.upper = upper;
    }

    /**
     * Get the lower bound.
     *
     * @return
     */
    public double lower() {
        return lower;
    }

    /**
     * Get the upper bound.
     *
     * @return
     */
    public double upper() {
        return upper;
    }

    /**
     * Return a random double value between the upper and lower bounds.
     *
     * @return
     */
    public double rand() {
        return random() * (upper - lower) + lower;
    }

    /**
     * Return a random integer between the upper and lower bounds.
     *
     * @return
     */
    public int randInt() {
        return (int) (round(random() * (upper - lower) + lower));
    }
}
