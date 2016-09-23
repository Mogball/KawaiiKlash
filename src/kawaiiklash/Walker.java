package kawaiiklash;

import static java.lang.Math.abs;
import static kawaiiklash.Direction.DOWN;
import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Direction.RIGHT;
import static kawaiiklash.Direction.UP;

/**
 * A {@code Walker} is a {@code Monster} that resides primarily on top of
 * platforms, following standard movement physics, and moves by running
 * along a platform.
 *
 * @author Jeff Niu
 */
public abstract class Walker extends Monster {

    /**
     * Create the {@code Walker}.
     *
     * @param game the {@code Game} to which the {@code Walker} belongs
     */
    public Walker(Game game) {
        super(game);
    }

    /**
     * When moving, the {@code Walker} will change its {@code Direction}
     * immediately upon encountering a wall. It will be able to stand on
 platforms and will hit ceilings.
     */
    @Override
    public void doLogic() {
        for (Platform platform : platforms) {
            Rect tile = platform.getHitbox();
            Direction dir = getCollideDir(tile);
            Direction dirX = getDirX();
            Direction dirY = getDirY();
            double x = getX();
            double y = getY();
            double dx = getDx();
            double dy = getDy();
            Rect box = getHitbox();
            if (getState() != HIT) {
                if (dir == LEFT && dirX == RIGHT) {
                    x = tile.getX() - box.getWidth() - getOffsetX();
                    dx = abs(dx) * dir.unit();
                    dirX = dir;
                }
                if (dir == RIGHT && dirX == LEFT) {
                    x = tile.getX() + tile.getWidth() - getOffsetX();
                    dx = abs(dx) * dir.unit();
                    dirX = dir;
                }
            } else {
                if (dir == LEFT && dirX == RIGHT) {
                    x = tile.getX() - box.getWidth() - getOffsetX();
                    dx = 0;
                    setDdx(0);
                }
                if (dir == RIGHT && dirX == LEFT) {
                    x = tile.getX() + tile.getWidth() - getOffsetX();
                    dx = 0;
                    setDdx(0);
                }
            }
            if (dir == UP && dirY == DOWN) {
                y = tile.getY() - box.getHeight() - getOffsetY();
                dy = 0;
            }
            if (dir == DOWN && dirY == UP) {
                y = tile.getY() + tile.getHeight() - getOffsetY();
                dy = 0;
            }
            setX(x);
            setY(y);
            setDx(dx);
            setDy(dy);
            setDirX(dirX);
            setDirY(dirY);
        }
    }

}
