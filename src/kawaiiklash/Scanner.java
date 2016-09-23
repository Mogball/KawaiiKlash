package kawaiiklash;

import java.util.List;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * A {@code Scanner} is an interface that facilitates the use of
 * scanners.
 *
 * @author Jeff Niu
 */
public interface Scanner {

    /**
     * Get the {@code EntityScanner} used by this {@code Scanner}.
     *
     * @return
     */
    ScannerImpl<?, ?> getScanner();

    /**
     * Tell the {@code Scanner} to do its scanning.
     *
     * @return true if the scan succeeds, false otherwise
     */
    default boolean scan() {
        return getScanner().scan(getScanDir(), getScanObjects(), getScanArea(), getScanMargin());
    }

    /**
     * Really only makes use of the obstacle detecting features of the
     * scanner, but it returns the area that is not blocked.
     *
     * @return
     */
    default Rect search() {
        return getScanner().search(getScanDir(), getScanObjects(), getScanArea(), getScanMargin());
    }

    default void drawArea(Graphics g) {
        Rect r = search();
        float rx = (float) r.getX();
        float ry = (float) r.getY();
        float rw = (float) r.getWidth();
        float rh = (float) r.getHeight();
        g.setColor(Color.yellow);
        g.drawRect(rx, ry, rw, rh);
    }

    /**
     * Get the {@code Object}s through which to scan.
     *
     * @return
     */
    List<Object> getScanObjects();

    /**
     * Get the {@code Dimension} that represents the scan zone.
     *
     * @return the scan zone
     */
    Rect getScanArea();

    /**
     * Get the percent blockage that is needed for sight obstruction.
     *
     * @return the percent blockage
     */
    float getScanMargin();

    /**
     * Get the {@code Direction} in which the scan will be done.
     *
     * @return the scan {@code Direction}
     */
    Direction getScanDir();

}
