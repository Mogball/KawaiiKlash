package kawaiiklash;

import java.util.ArrayList;
import java.util.List;
import static kawaiiklash.Direction.DOWN;
import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Direction.NONE;
import static kawaiiklash.Direction.RIGHT;
import static kawaiiklash.Direction.UP;

/**
 * A {@code PlatformWalker} is a {@code Monster} that is capable of walking
 * back and forth along a single, continuous platform, which, by
 * definition, is a series of {@code Tile} objects.
 *
 * @author Jeff Niu
 */
public interface PlatformWalker extends Interactable {

    /**
     * A platform detection margin used in detecting platform edge in
     * {@link #edgeDirection(java.util.List) edgeDirection}.
     */
    static final int MARGIN = 5;

    /**
     * Request that the {@code Monster} pass over its {@code List} of all
     * the hitboxes of the {@code Tile} objects with which it has collided.
     *
     * @param tiles
     * @return
     */
    default List<Rect> getPlatforms(final List<Platform> tiles) {
        final List<Rect> platforms = new ArrayList<>(tiles.size());
        for (final Platform tile : tiles) {
            final Direction dir = getCollideDir(tile);
            if (dir == UP && getDirY() == DOWN) {
                platforms.add(tile.getHitbox());
            }
        }
        return platforms;
    }

    /**
     * This method determines whether or not the {@code PlatformWalker} has
     * reached the edge of a platform. It is then up to the
     * {@code PlatformWalker} to decide its action. If the
     * {@code PlatformWalker} has indeed reached the edge, then the method
     * will return either {@code Direction.LEFT} or
     * {@code Direction.RIGHT}. If it is not, then the method will return
     * {@code Direction.NONE}.
     *
     * @param platforms
     * @return
     */
    default Direction edgeDirection(final List<Rect> platforms) {
        if (platforms.isEmpty()) {
            return NONE;
        }
        final Rect hitbox = getHitbox();
        // The List of all the intersections with the Tiles
        final List<Rect> sects = new ArrayList<>(platforms.size());
        for (final Rect tile : platforms) {
            sects.add(hitbox.intersection(tile));
        }
        // The sum of all the collision intersections
        Rect c = sects.get(0);
        for (final Rect sect : sects) {
            c = c.union(sect);
        }
        final Direction dir;
        if (c.getWidth() + MARGIN >= hitbox.getWidth()) {
            dir = NONE;
        } else if (c.getX() > getX() && c.getX() + c.getWidth() < getX() + getWidth()) {

            // The first empty area
            final double x1 = getX() + getOffsetX();
            final double w1 = c.getX() - x1;

            // The second empty area
            final double x2 = c.getX() + c.getWidth();
            final double w2 = getWidth() - c.getWidth() - w1;

            if (w1 > w2) {
                dir = Direction.xDirOf(x1 - x2);
            } else if (w2 > w1) {
                dir = Direction.xDirOf(x2 - x1);
            } else {
                dir = NONE;
            }

        } else {
            if (c.getX() <= hitbox.getX()) {
                dir = RIGHT;
            } else if (c.getX() > hitbox.getX()) {
                dir = LEFT;
            } else {
                dir = NONE;
            }
        }
        return dir;
    }

}
