package kawaiiklash;

import static kawaiiklash.Utility.fail;
import org.lwjgl.input.Keyboard;

/**
 * Keeps track of an Entity's unit and has to ability to convert the
 * Direction into a one-dimensional unit unit (+/- 1).
 *
 * @author Jeff Niu
 */
@SuppressWarnings("AccessingNonPublicFieldOfAnotherObject")
public enum Direction {

    // The tradition four Directions on the screen
    LEFT(-1),
    RIGHT(1),
    UP(-1),
    DOWN(1),
    // Extended Directions for three axes
    FRONT(1),
    BACK(-1),
    // A non-existing Diretion
    NONE(0),
    // Linear Directions
    POSITIVE(1),
    NEGATIVE(-1);

    // The total number of actual directions
    public static final int DIRECTIONS = 6;

    // The index of a Direction that is not one of the four traditionals
    private static final int NO_INDEX = -1;

    // The value of the unit unit in this Direction
    private final int unit;

    // The index of the Direction
    private int index;

    // Set the indices of the Direction
    static {
        // Incides of the standard Directions
        LEFT.index = 0;
        RIGHT.index = 1;
        UP.index = 2;
        DOWN.index = 3;
        FRONT.index = 4;
        BACK.index = 5;

        // Indices of all other directions
        NONE.index = NO_INDEX;
        POSITIVE.index = NO_INDEX;
        NEGATIVE.index = NO_INDEX;
    }

    /**
     * Create a Direction.
     *
     * @param vector
     * @param index
     */
    private Direction(int vector) {
        this.unit = vector;
    }

    /**
     * Converts the Direction into a unit vector.
     *
     * @return
     */
    public int unit() {
        return unit;
    }

    /**
     * Get the index of this Direction.
     *
     * @return
     */
    public int index() {
        if (index == NO_INDEX) {
            fail(new NoDirectionIndexException(this));
        }
        return index;
    }

    /**
     * Finds the linear Direction of a vector.
     *
     * @param vector
     * @return
     */
    public static Direction dirOf(double vector) {
        if (vector < 0) {
            return NEGATIVE;
        }
        if (vector > 0) {
            return POSITIVE;
        }
        return NONE;
    }

    /**
     * Finds the horizontal Direction of a vector.
     *
     * @param vector
     * @return
     */
    public static Direction xDirOf(double vector) {
        return vector <= 0 ? LEFT : RIGHT;
    }

    /**
     * Finds the vertical Direction of a vector.
     *
     * @param vector
     * @return
     */
    public static Direction yDirOf(double vector) {
        return vector <= 0 ? UP : DOWN;
    }

    public static int sgn(double vector) {
        return vector < 0 ? -1 : 1;
    }

    /**
     * Checks if this Direction is linear.
     *
     * @return
     */
    public boolean dir() {
        return this == POSITIVE || this == NEGATIVE;
    }

    /**
     * Checks if this Direction is horizontal.
     *
     * @return
     */
    public boolean xDir() {
        return this == RIGHT || this == LEFT;
    }

    /**
     * Checks if this Direction is vertical.
     *
     * @return
     */
    public boolean yDir() {
        return this == UP || this == DOWN;
    }

    /**
     * Returns the opposite Direction.
     *
     * @return
     */
    public Direction inverse() {
        if (dir()) {
            return dirOf(-unit);
        }
        if (xDir()) {
            return xDirOf(-unit);
        }
        if (yDir()) {
            return yDirOf(-unit);
        }
        return NONE;
    }

    /**
     * Returns the Direction of a keyboard key.
     *
     * @param keyCode
     * @return
     */
    public static Direction ofKey(int keyCode) {
        switch (keyCode) {
            case Keyboard.KEY_UP:
                return UP;
            case Keyboard.KEY_DOWN:
                return DOWN;
            case Keyboard.KEY_LEFT:
                return LEFT;
            case Keyboard.KEY_RIGHT:
                return RIGHT;
            default:
                return NONE;
        }
    }

    /**
     * Runtime Exception thrown when the program invokes a method calling
     * for the index of a Direction whose index does not exist.
     *
     * @author Jeff Niu
     */
    private static class NoDirectionIndexException extends RuntimeException {

        private static final long serialVersionUID = 1L;

        /**
         * The unique message of this Exception.
         */
        private static final String MESSAGE = "Attemping to get the index of a Direction whose index does not exist: ";

        /**
         * Creates a new Exception.
         *
         * @param message
         */
        NoDirectionIndexException(Direction direction) {
            super(MESSAGE + direction.toString());
        }

    }

}
