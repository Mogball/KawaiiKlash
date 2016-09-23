package kawaiiklash;

/**
 * A {@code Brick} is a type of {@code Tile}. It is completely simplistic
 * in nature, as the actual hitbox corresponds exactly with the
 * {@code Sprite}, so no need for modification.
 *
 * @author Jeff Niu
 */
public class Brick extends Tile {

    /**
     * Create a {@code Brick}.
     *
     * @param game
     */
    public Brick(Game game) {
        super(game);
    }

    @Override
    public void updateState(int dt) {
    }

    @Override
    public boolean isSticky() {
        return false;
    }

    @Override
    public boolean isMoving() {
        return false;
    }

    @Override
    public double getDx() {
        return 0.0;
    }

    @Override
    public double getDy() {
        return 0.0;
    }

    @Override
    public void collidedWith(Collideable other) {
    }

    @Override
    public Direction getDirX() {
        return null;
    }

    @Override
    public Direction getDirY() {
        return null;
    }
}
