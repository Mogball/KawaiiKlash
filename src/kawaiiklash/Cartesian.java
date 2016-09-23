package kawaiiklash;

/**
 * An {@code Object} that {@code Cartesian} is on that has a position
 * defined by a coordinate {@code (x,y)}. It also has dimensions and
 * offsets, if needed.
 *
 * @author Jeff Niu
 */
public interface Cartesian {

    /**
     * Set the x-value of the position of this {@code Cartesian}.
     *
     * @param x the new x-value
     */
    void setX(double x);

    /**
     * Set the y-value of this position of this {@code Cartesian}.
     *
     * @param y the new y-value
     */
    void setY(double y);

    /**
     * Set the position to a defined {@code Point}.
     *
     * @param p the point that defines the position
     */
    default void setPosition(Vector p) {
        setX(p.x);
        setY(p.y);
    }

    /**
     * Get the x-value of this position.
     *
     * @return the current x-value
     */
    double getX();

    /**
     * Get the y-value of this position.
     *
     * @return the current y-value
     */
    double getY();

    /**
     * Get the {@code Point} that represents the position.
     *
     * @return a {@code Point}
     */
    default Vector getPosition() {
        return new Vector(getX(), getY());
    }

    /**
     * Change the x-position.
     *
     * @param dx the change in x
     */
    default void moveX(double dx) {
        setX(getX() + dx);
    }

    /**
     * Change the y-position.
     *
     * @param dy the change in y
     */
    default void moveY(double dy) {
        setY(getY() + dy);
    }

    /**
     * Get the {@code Direction} of the {@code Cartesian} in the
     * horizontal.
     *
     * @return
     */
    Direction getDirX();

    /**
     * Get the {@code Direction} of the {@code Cartesian} in the vertical.
     *
     * @return
     */
    Direction getDirY();

    /**
     * Get the offset of the {@code Cartesian} in the horizontal, if there
     * is one.
     *
     * @return
     */
    double getOffsetX();

    /**
     * Get the offset of the {@code Cartesian} in the vertical, if there is
     * one.
     *
     * @return
     */
    double getOffsetY();

    /**
     * Get the width of this {@code Cartesian}.
     *
     * @return
     */
    double getWidth();

    /**
     * Get the height of this {@code Cartesian}.
     *
     * @return
     */
    double getHeight();

    default double distance(Cartesian o) {
        return distance(this, o);
    }

    default Vector getMidpoint() {
        double x = getX() + getOffsetX() + getWidth() / 2;
        double y = getY() + getOffsetY() + getHeight() / 2;
        return new Vector(x, y);
    }

    static double distance(Cartesian a, Cartesian b) {
        final double ax = a.getX() + a.getOffsetX();
        final double ay = a.getY() + a.getOffsetY();
        final double bx = b.getX() + b.getOffsetX();
        final double by = b.getY() + b.getOffsetY();
        Vector pa = new Vector(ax, ay);
        Vector pb = new Vector(bx, by);
        return pa.distance(pb);
    }

}
