package kawaiiklash;

import java.lang.reflect.Field;
import java.util.Locale;
import static kawaiiklash.Utility.fail;

/**
 * A LevelConfiguration Object to store level input information.
 *
 * @author Jeff Niu
 * @version 1 March 2015
 */
public class LevelConfiguration {

    // The start position of the screen
    private double x;
    private double y;

    // Whether the screen can canScroll in a certain direction
    private final boolean[] scroll = new boolean[Direction.DIRECTIONS];

    /**
     * Create a level
     *
     * @param game
     */
    public LevelConfiguration(Game game) {
    }

    /**
     * Set of methods to set whether or not the screen can canScroll in a
     * direction. Scroll Direction set based on the calling method name.
     *
     * @param scroll
     */
    public void scroll(boolean scroll) {
        Direction direction = null;
        StackTraceElement[] tentatives = Thread.currentThread().getStackTrace();
        for (StackTraceElement tentative : tentatives) {
            try {
                direction = Direction.valueOf(tentative.getMethodName().toUpperCase(Locale.ENGLISH));
            } catch (IllegalArgumentException | NullPointerException ex) {
                continue;
            }
            break;
        }
        if (direction != null) {
            this.scroll[direction.index()] = scroll;
        } else {
            fail("Failure kawaiiklash.LevelConfiguration.scroll(boolean): calling method name does not match kawaiiklash.Direction data enumeration types");
        }
    }

    public void left(String scroll) {
        scroll(Boolean.parseBoolean(scroll));
    }

    public void right(String scroll) {
        scroll(Boolean.parseBoolean(scroll));
    }

    public void up(String scroll) {
        scroll(Boolean.parseBoolean(scroll));
    }

    public void down(String scroll) {
        scroll(Boolean.parseBoolean(scroll));
    }

    public boolean[] canScroll() {
        boolean[] dest = new boolean[scroll.length];
        System.arraycopy(scroll, 0, dest, 0, scroll.length);
        return dest;
    }

    public void x(String coord) {
        x = Double.parseDouble(coord);
    }

    public void y(String coord) {
        y = Double.parseDouble(coord);
    }

    public Rect getScreen() {
        return new Rect(new Vector(x, y));
    }
}
