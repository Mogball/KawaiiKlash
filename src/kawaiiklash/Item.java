package kawaiiklash;

import static java.lang.Math.cos;
import java.util.List;
import org.newdawn.slick.Image;

/**
 * An item is something that is dropped on the screen and can be picked up
 * by another entity, usually the power.
 *
 * @author Jeff Niu
 */
public abstract class Item extends Entity {

    private static final int DROP = 0;
    private static final int IDLE = 1;
    private static final int PICKUP = 2;
    private static final List<Sound> SOUNDS = SoundLoader.get().loadSounds(Bank.getSoundRef("Item"));

    private int mode;
    /**
     * Used to make the item bob up and down when it is on the ground.
     */
    private Oscillator oscillator;

    public Item(Game game) {
        super(game);
        setX(0);
        setY(0);
        setDx(0);
        setDy(0);
        setDdx(0);
        setDdy(0);
        setMaxDx(UNLIMITED);
        setMaxDy(UNLIMITED);
        setActive(true);
        setDead(false);
        setState(0);
        mode = IDLE;
    }

    public Item(Game game, double x, double y) {
        super(game);
        setX(x);
        setY(y - getHeight() / 2);
        setDx(0);
        setDy(-400);
        setDdx(0);
        setDdy(GRAVITY / 2);
        setMaxDx(UNLIMITED);
        setMaxDy(GRAVITY);
        setActive(true);
        setDead(false);
        setState(0);
        mode = DROP;
        SOUNDS.get(0).playEffect(game.getPitch(), game.getGain(), false);
    }

    @Override
    public Rect getHitbox() {
        final SpriteSheet ss = getSprites().get(getState());
        final double x = getX() + ss.getOffsetX();
        final double y = getY() + ss.getOffsetY();
        return new Rect(x, y, ss.getWidth(), ss.getHeight());
    }

    /**
     * Indication that the player has picked up the item.
     *
     * @param player
     */
    public abstract void playerPickup(Player player);

    /**
     * The item has been picked up, so animate it by making it float
     * upwards, play the pick up sound, and remove it later.
     */
    public void pickedUp() {
        mode = PICKUP;
        setDy(-500);
        setDdy(1000);
        Image img = getSprites().get(getState()).get(0).getImage();
        img.setRotation(0);
        Effects.fade(Effects.Fade.OUT, getGame(), getSprites(), 500);
        Game game = getGame();
        SOUNDS.get(1).playEffect(game.getPitch(), game.getGain(), false);
    }

    /**
     * When it hits the ground, make it float up and down.
     *
     * @param tile
     */
    public void hitGround(Rect tile) {
        mode = IDLE;
        setY(tile.getY() - getHeight() - getOffsetY());
        setDy(0);
        setDdy(0);
        setMaxDy(UNLIMITED);
        Image img = getSprites().get(getState()).get(0).getImage();
        img.setRotation(0);
    }

    /**
     * Remove the item when it leaves the screen and falls off.
     *
     * s* @param dir
     */
    @Override
    public void isOutOfBounds(Direction dir) {
        if (dir == Direction.LEFT || dir == Direction.RIGHT) {
            setActive(false);
        }
        if (dir == Direction.DOWN) {
            died();
        }
    }

    @Override
    public void isInBounds() {
        setActive(true);
    }

    /**
     * Rotate the item if it is being dropped.
     *
     * @param dt
     */
    @Override
    public void updateState(int dt) {
        if (mode == IDLE) {
            if (oscillator == null) {
                oscillator = new Oscillator(getY(), 5, 0, 1500, (t) -> {
                    return -cos(t);
                });
            }
            setY(oscillator.get());
            oscillator.update(dt);
        }
        if (mode == DROP) {
            Image img = getSprites().get(getState()).get(0).getImage();
            img.rotate((float) (1080 * dt / MILLISECONDS));
        }
        if (mode == PICKUP && getDy() > 0) {
            getGame().remove(this);
        }
    }

    /**
     * Make sure to move the oscillator as well.
     *
     * @param dy
     */
    @Override
    public void moveY(double dy) {
        super.moveY(dy);
        oscillator.move(dy);
    }

    @Override
    public void collidedWith(Collideable other) {
        if (other instanceof Player && mode != PICKUP) {
            playerPickup((Player) other);
            pickedUp();
        }
        if (other instanceof Platform && mode == DROP) {
            Direction d = getCollideDir(other);
            if (d == Direction.UP && getHitbox().y < other.getHitbox().y) {
                hitGround(other.getHitbox());
            }
        }
    }

    @Override
    public void died() {
        getGame().remove(this);
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Parsable
    public void mode(String mode) {
        this.mode = Integer.parseInt(mode);
    }

    @Override
    public int getZ() {
        return Drawable.ITEM;
    }

}
