package kawaiiklash;

import static kawaiiklash.Direction.NONE;

/**
 * A {@code Reactor} is a {@code Game} object that is {@code Updateable} and
 * {@code Cartesian}, but does not have a {@code Sprite} representation in
 * game nor can it collide with anything. Its width, height, and offsets
 * are all zero, so it is essentially a dimensionless object.
 *
 * @author Jeff Niu
 */
public abstract class Reactor implements Cartesian, Updateable {

    /**
     * The {@code Game} to which the {@code Reactor} belongs.
     */
    private final Game game;

    /**
     * Construct a {@code Reactor} and set the {@code Game} to which it
     * belongs.
     *
     * @param game the {@code Game}
     */
    public Reactor(Game game) {
        this.game = game;
    }

    /**
     * Get the {@code Game} to which this {@code Reactor} belongs.
     *
     * @return the {@code Game}
     */
    public Game getGame() {
        return game;
    }

    /**
     * A {@code Reactor} has no horizontal {@code Direction}.
     *
     * @return {@code Direction.NONE}
     */
    @Override
    public Direction getDirX() {
        return NONE;
    }

    /**
     * A {@code Reactor} has no vertical {@code Direction}.
     *
     * @return {@code Direction.NONE}
     */
    @Override
    public Direction getDirY() {
        return NONE;
    }

    /**
     * A {@code Reactor} does not have a horizontal offset.
     *
     * @return zero
     */
    @Override
    public double getOffsetX() {
        return 0.0;
    }

    /**
     * A {@code Reactor} does not have a vertical offset.
     *
     * @return zero
     */
    @Override
    public double getOffsetY() {
        return 0.0;
    }

    /**
     * A {@code Reactor} will be one pixel wide.
     *
     * @return one
     */
    @Override
    public double getWidth() {
        return 1.0;
    }

    /**
     * A {@code Reactor} will be one pixel high.
     *
     * @return one
     */
    @Override
    public double getHeight() {
        return 1.0;
    }

}
