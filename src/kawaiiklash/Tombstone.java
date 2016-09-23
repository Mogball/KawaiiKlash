package kawaiiklash;

/**
 * The tombstone is something that is dropped in the game when the player
 * dies. This object resets the level when the player dies.
 *
 * @author Jeff Niu
 */
public class Tombstone extends Entity {

    private int count;

    private final int FALL = getStateNumber("fall");
    private final int GROUND = getStateNumber("ground");
    private final int STILL = getStateNumber("still");
    private final int HITBOX = getStateNumber("naked");

    public Tombstone(Game game, double x) {
        super(game);
        setX(x);
        setY(-getHeight());
        setDx(0);
        setDy(900);
        setDdx(0);
        setDdy(GRAVITY);
        setMaxDx(UNLIMITED);
        setMaxDy(UNLIMITED);
        setState(FALL);
        setActive(true);
        setDead(false);
        count = 0;
    }

    @Override
    public void update(int dt) {
        super.update(dt);
        count += dt;
    }

    @Override
    public void isOutOfBounds(Direction dir) {
        if (dir == Direction.DOWN) {
            changeToState(STILL);
        }
    }

    @Override
    public void isInBounds() {
    }

    @Override
    public void updateState(int dt) {
        if (getState() == GROUND && count >= getStateDelay(GROUND)) {
            changeToState(STILL);
            count = 0;
        }
        if (getState() == STILL && count >= 20 * getStateDelay(STILL)) {
            died();
        }
    }

    @Override
    public Rect getHitbox() {
        SpriteSheet ss = getSprites().get(HITBOX);
        Rect box = new Rect();
        box.x = getX() + ss.getOffsetX();
        box.y = getY() + ss.getOffsetY();
        box.width = ss.getWidth();
        box.height = ss.getHeight();
        return box;
    }

    @Override
    public void collidedWith(Collideable other) {
        if (other instanceof Platform && getState() == FALL && getCollideDir(other) == Direction.UP) {
            Rect tile = other.getHitbox();
            SpriteSheet ss = getSprites().get(HITBOX);
            setY(tile.getY() - ss.getHeight() - ss.getOffsetY());
            changeToState(GROUND);
            cycleFrames(GROUND, false);
            setDy(0);
            setDdy(0);
            count = 0;
        }
    }

    @Override
    public void died() {
        getGame().playerDead();
    }

    @Override
    public int getZ() {
        return Drawable.TILE;
    }

}
