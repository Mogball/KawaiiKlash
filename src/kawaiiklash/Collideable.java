package kawaiiklash;

/**
 * A {@code Collideable} is an Object that can collide with others.
 *
 * @author Jeff Niu
 */
public interface Collideable {

    /**
     * Get the hitbox representing this {@code Collideable}.
     *
     * @return
     */
    Rect getHitbox();

    /**
     * Determines whether or not this {@code Collideable} is able to
     * function in collision detection at the moment.
     *
     * @return
     */
    boolean canCollide();

    /**
     * Determine whether two {@code Collideable}s are making a collision.
     *
     * @param other the other {@code Collideable}
     * @return true if they collide, false otherwise
     */
    default boolean collidesWith(final Collideable other) {
        if (canCollide() && other.canCollide()) {
            return getHitbox().intersects(other.getHitbox());
        } else {
            return false;
        }
    }

    /**
     * Tell the {@code Collideable} that it has collided with another.
     *
     * @param other
     */
    void collidedWith(final Collideable other);

    /**
     * Overload method to get the {@code Direction} in which this
     * {@code Collideable} collides with another.
     *
     * @param other
     * @return
     */
    default Direction getCollideDir(final Collideable other) {
        return getCollideDir(other.getHitbox());
    }

    /**
     * This method analyzes the intersection between two hitboxes of two
     * {@code Collideable} objects to determine the {@code Direction} in
     * which one collides with the other.
     *
     * @param o
     * @return
     */
    default Direction getCollideDir(final Rect o) {
        final Rect r = getHitbox();
        final Rect i = r.intersection(o);
        if (i.height / 2 <= i.width) {
            return getVerCollideDir(o, r, i);
        } else {
            return getHorCollideDir(o, r, i);
        }

    }

    default Direction getHorCollideDir(final Rect o) {
        final Rect r = getHitbox();
        final Rect i = r.intersection(o);
        return getHorCollideDir(o, r, i);
    }

    default Direction getHorCollideDir(final Rect o, final Rect r, final Rect i) {
        if (i.width == o.width) {
            double x1 = i.x - r.x;
            double x2 = r.x + r.width - i.x - i.width;
            if (x1 > x2) {
                return Direction.LEFT;
            } else {
                return Direction.RIGHT;
            }
        } else {
            if (i.x == o.x) {
                return Direction.LEFT;
            } else if (i.x + i.width == o.x + o.width) {
                return Direction.RIGHT;
            } else {
                double x1 = i.x - o.x;
                double x2 = o.x + o.width - i.x - i.width;
                if (x1 > x2) {
                    return Direction.LEFT;
                } else {
                    return Direction.RIGHT;
                }
            }
        }
    }

    default Direction getVerCollideDir(final Rect o, final Rect r, final Rect i) {
        if (i.height == o.height) {
            double y1 = i.y - r.y;
            double y2 = r.y + r.height - i.y - i.height;
            if (y1 > y2) {
                return Direction.UP;
            } else {
                return Direction.DOWN;
            }
        } else {
            if (i.y == o.y) {
                return Direction.UP;
            } else if (i.y + i.height == o.y + o.height) {
                return Direction.DOWN;
            } else {
                double y1 = i.y - o.y;
                double y2 = o.y + o.height - i.y - i.height;
                if (y1 > y2) {
                    return Direction.UP;
                } else {
                    return Direction.RIGHT;
                }
            }
        }
    }

}
