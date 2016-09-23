package kawaiiklash;

import static java.lang.Math.sqrt;
import static kawaiiklash.Direction.DOWN;
import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Direction.RIGHT;
import static kawaiiklash.Direction.UP;

/**
 * A {@code Jumper} is a {@code Monster} whose primary mode of movement is
 * jumping. It will rest on platforms but have the ability to
 * {@link #jump() jump}, as determined by its behavior.
 *
 * @author Jeff Niu
 */
public abstract class Jumper extends Monster {

    /**
     * The state of the {@code Jumper} when it is jumping.
     */
    protected final int JUMP = getStateNumber("jump");

    /**
     * The height that the {@code Jumper} will jump each time it
     * {@link #jump() jumps}.
     */
    private double height;

    /**
     * The jump length.
     */
    private double length;

    /**
     * The jump vertical speed.
     */
    private double jumpDy;

    /**
     * The jump horizontal speed.
     */
    private double jumpDx;

    /**
     * Construct a {@code Jumper}.
     *
     * @param game the {@code Game} to which it belongs
     */
    public Jumper(Game game) {
        super(game);
    }

    /**
     * @param height the height to set
     */
    public void setJumpHeight(double height) {
        this.height = height;
        jumpDy = -sqrt(2 * GRAVITY * height);
        jumpDx = -GRAVITY * length / jumpDy * 0.5;
    }

    /**
     * @param length the length to set
     */
    public void setJumpLength(double length) {
        this.length = length;
        jumpDy = -sqrt(2 * GRAVITY * height);
        jumpDx = -GRAVITY * length / jumpDy * 0.5;
    }

    /**
     * @return the jump height.
     */
    public double getJumpHeight() {
        return height;
    }

    /**
     * @return the jump length.
     */
    public double getJumpLength() {
        return length;
    }

    /**
     * Calculates, using the standard {@code Entity} acceleration
     * {@code GRAVITY = 2000p/s/s}, the speed required to achieve the jump
     * {@link #height height}. This assumes that the speed at the apex of
     * the jump is zero.
     *
     * @return
     */
    public double getJumpDy() {
        return jumpDy;
    }

    public double getJumpDx() {
        return jumpDx;
    }

    /**
     * While the {@code Jumper} is not doing its hit animation, hitting a
     * ceiling, wall, or ground will invoke a notification method that are
     * overridden by the {@code JumperImpl}. When it is doing its hit
     * animation, hitting the ground will cause it to stop moving
     * vertically, hitting a wall will cause it to stop moving entirely,
     * and hitting a ceiling will set its vertical speed to zero.
     */
    @Override
    public void doLogic() {
        for (Platform platform : platforms) {
            Rect tile = platform.getHitbox();
            Direction dir = getCollideDir(tile);
            Double x = null;
            Double y = null;
            Rect b = getHitbox();
            Direction dirX = getDirX();
            Direction dirY = getDirY();
            if (getState() != HIT) {
                if (dir == LEFT && dirX == RIGHT) {
                    x = tile.getX() - b.getWidth() - getOffsetX();
                    hitWall(tile);
                }
                if (dir == RIGHT && dirX == LEFT) {
                    x = tile.getX() + tile.getWidth() - getOffsetX();
                    hitWall(tile);
                }
                if (dir == UP && dirY == DOWN) {
                    y = tile.getY() - b.getHeight() - getOffsetY();
                    hitGround(tile);
                }
                if (dir == DOWN && dirY == UP) {
                    y = tile.getY() + tile.getHeight() - getOffsetY();
                    hitCeiling(tile);
                }
            } else {
                if (dir == LEFT && dirX == RIGHT) {
                    x = tile.getX() - b.getWidth() - getOffsetX();
                    setDx(0);
                    setDdx(0);
                }
                if (dir == RIGHT && dirX == LEFT) {
                    x = tile.getX() + tile.getWidth() - getOffsetX();
                    setDx(0);
                    setDdx(0);
                }
                if (dir == UP && dirY == DOWN) {
                    y = tile.getY() - b.getHeight() - getOffsetY();
                    setDy(0);
                }
                if (dir == DOWN && dirY == UP) {
                    y = tile.getY() + tile.getHeight() - getOffsetY();
                    setDy(0);
                }
            }
            if (x != null) {
                setX(x);
            }
            if (y != null) {
                setY(y);
            }
        }
    }

    /**
     * Tell the {@code Jumper} that should now jump.
     */
    public abstract void jump();

    /**
     * Tell the {@code Jumper} that is has hit the ground.
     *
     * @param ground the hitbox representing the ground hit
     */
    public abstract void hitGround(Rect ground);

    /**
     * Tell the {@code Jumper} that is has hit a ceiling.
     *
     * @param ceil
     */
    public abstract void hitCeiling(Rect ceil);

    /**
     * Tell the {@code Jumper} that is has hit a wall.
     *
     * @param wall the hitbox representing the wall hit
     */
    public abstract void hitWall(Rect wall);

}
