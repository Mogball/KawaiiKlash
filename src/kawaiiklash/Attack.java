package kawaiiklash;

import java.util.ArrayList;
import java.util.List;
import static kawaiiklash.Utility.fail;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * The {@code Attack} is an {@code Entity} subclass used to handle the
 * various attacks that will be used by the {@code Player}. The
 * {@code Attack} will handle its own hit detection, such as when and how
 * to hit a {@code Monster}, and its animations.
 *
 *
 * @author Jeff Niu
 */
public abstract class Attack extends Entity {

    /**
     * A potential return value of the {@link #isDeadly() isDeadly()}
     * method that indicates that the {@code Attack} is currently not
     * deadly.
     */
    public static final int NOT_DEADLY = -1;

    /**
     * The state number that represents the index for the hitbox
     * {@code SpriteSheet}.
     */
    private final int HITBOX = getStateNumber("naked");

    /**
     * The {@code Attacker} that created this {@code Attack};
     */
    private final Attacker attacker;

    /**
     * A {@code List} containing various {@code List}s of {@code Monster}s
     * that have been hit by this {@code Attack}. The number of inner
     * {@code List}s is equal to the number of times the {@code Attack} may
     * potentially hit a {@code Monster} in its life. Each time a
     * {@code Monster} is hit by the {@code Attack} a particular time, it
     * is added to one of the {@code List}s. This ensures that the
     * {@code Attack} does not hit the same {@code Monster} several times
     * on each strike. The next time the {@code Attack} is deadly, if it
     * ever is, it will compare to a new {@code List}, so the same
     * {@code Monster} can be hit again.
     */
    protected final List<List<AttackTarget>> targets;

    /**
     * The number representing the state in which the {@code Attack} will
     * actually be able to damage a {@code Monster}. Since the hit
     * animation, effect animation, and other animations are all
     * incorporated into one {@code Object}, this number is crucial. For
     * example, the hit animation should not be deadly, but the strike
     * animation should.
     */
    private final int deadlyState;

    /**
     * This array contains the frame numbers on which the deadly state
     * animation would actually be deadly. These frame numbers correspond
     * to the frames in the animation in which the {@code Attack} actually
     * looks like it is striking. For simple {@code Attack}s, there may
     * only be one number inside this array, but other, more complex
     * {@code Attack}s may hit more than once. The number of {@code List}s
     * inside the {@link #targets list of monsters} is equal to the number
     * of frames in this array.
     */
    private final int[] deadlyFrames;

    /**
     * The {@code Class} of the specific {@Code AttackCible} that will take
     * damage from this {@code Attack}.
     */
    private final Class<?> cible;

    /**
     * A counter used for this {@code Attack}. One use is that it counts
     * towards the lifetime expiration of this {@code Attack}, at which
     * point it removes itself from the {@code Game}.
     *
     * @see #updateState()
     * @see #delay
     */
    private int count;

    /**
     * The amount of time for which the {@code Attack} is alive. After this
     * time, the {@code Attack} will remove itself from the {@code game}.
     *
     * @see #count
     */
    private int delay;

    /**
     * A {@code Bound} that describes the knockback range of this
     * {@code Attack} in pixels.
     */
    private Bound knockback;

    /**
     * A {@code Bound} that describes the damage range of this
     * {@code Attack} in health points.
     */
    private Bound damage;

    /**
     * A percentage that describes the amount of a {@code Monster}'s
     * defense that will be ignored when it is damaged.
     */
    private double breach;

    /**
     * Construct an {@code Attack}. Its position is set and its movement is
     * initialized to a standstill. It assumes the {@code Direction}
     * described by the arguments. Note that if the array of deadly frames
     * is {@code null}, it will be assumed that the {@code Attack} will be
     * deadly during all frames. However, it will only hit the
     * {@code Monster}s once per lifetime.
     *
     * @param game the game to which it belongs
     * @param x the x-position
     * @param y the y-position
     * @param dir the direction of the attack
     * @param attacker the {@code Attacker} that is creating this attack
     * @param deadlyState the state in which the attack is deadly
     * @param deadlyFrames the frames during which the attack is deadly
     * @param cible
     */
    public Attack(Game game, double x, double y, Direction dir, Attacker attacker, String deadlyState, int[] deadlyFrames, Class<?> cible) {
        super(game);

        // Initialize the position
        setX(x);
        setY(y);

        // Initialize the movement to still
        setDx(0);
        setDy(0);
        setDdx(0);
        setDdy(0);
        setMaxDx(UNLIMITED);
        setMaxDy(UNLIMITED);

        // Set the direction
        setDirX(dir);

        // Set the Player
        this.attacker = attacker;

        // Set the deadlyFrames state and frames
        this.deadlyState = getStateNumber(deadlyState);
        this.deadlyFrames = deadlyFrames;

        // Initialize the Lists of Monsters
        targets = new ArrayList<>(deadlyFrames.length);
        for (int i = 0; i < deadlyFrames.length; i++) {
            targets.add(new ArrayList<>(10));
        }

        // Set it to active immediately
        setActive(true);
        setDead(false);

        // Set the target class
        this.cible = cible;

        // Ensure that the specified target class is indeed an AttackCible
        if (!AttackTarget.class.isAssignableFrom(cible)) {
            fail(cible + " class does not implement " + AttackTarget.class + " and cannot be used as a target");
        }
    }

    /**
     * Since a single {@code Attack} subclass is actually an amalgamation
     * of various {@code Object}s sharing the same {@code Attack}
     * semantics, this method must be called immediately after an
     * {@code Attack}'s instantiation to specify which version of that
     * {@code Attack} is desired. In other words, the state of the
     * {@code Attack}. Since the state numbers are instance variables, they
     * cannot be passed to the constructor. As such, they must be set after
     * constructing the {@code Attack}.
     *
     * @param state
     */
    @Override
    public void setState(int state) {
        super.setState(state);
        count = 0;
        delay = getStateDelay(state);
        getSprites().get(state).cycleFrames(false);
    }

    /**
     * Get the sprite sheet that represents the hitbox for this attack.
     * Created so that this method could be overridden.
     *
     * @return the hitbox {@code SpriteSheet}.
     */
    public SpriteSheet getHitboxSheet() {
        return getSprites().get(HITBOX);
    }

    /**
     * Get the hitbox that represents this {@code Attack}.
     *
     * @return the hitbox
     */
    @Override
    public Rect getHitbox() {
        SpriteSheet ss = getHitboxSheet();
        SpriteSheet box = attacker.getHitboxSheet();
        double y = getY() + ss.getOffsetY();
        double height = ss.getHeight();
        double width = ss.getWidth();
        double x;
        if (getDirX() == DEFAULT_DIR) {
            x = getX() + ss.getOffsetX();
        } else {
            x = getX() - ss.getWidth() - ss.getOffsetX() + 2 * box.getOffsetX() + box.getWidth();
        }
        return new Rect(x, y, width, height);
    }

    /**
     * Update the {@code Attack}; add the passed time to its
     * {@link #count counter}.
     *
     * @param dt the amount of passed time
     */
    @Override
    public void update(int dt) {
        super.update(dt);
        count += dt;
    }

    /**
     * Checks whether or not this {@code Attack} is deadly. If it is not
     * deadly, then it will return the value
     * {@link #NOT_DEADLY NOT_DEADLY}. If it is, then it will return the
     * index of the deadly frame in {@link #deadlyFrames} to be used as a
     * reference for the new {@code List} in {@link #targets}.
     *
     * @return
     */
    public int isDeadly() {
        int frame = getStateFrame(getState());
        for (int i = 0; i < deadlyFrames.length; i++) {
            if (deadlyFrames[i] == frame) {
                return (getState() == deadlyState ? i : NOT_DEADLY);
            }
        }
        return NOT_DEADLY;
    }

    /**
     * If the {@code Attack} has collided with a {@code Monster} and it is
     * deadly, then damage that {@code Monster} and create the hit
     * animation. Also, add the {@code Monster} to the hit {@code List}.
     *
     * @param other the Entity that was hit
     */
    @Override
    public void collidedWith(Collideable other) {
        if (other instanceof AttackTarget && cible.isAssignableFrom(other.getClass())) {
            AttackTarget m = (AttackTarget) other;
            int index = isDeadly();
            if (m.isDamageable() && index != NOT_DEADLY && !targets.get(index).contains(m)) {
                hitTarget(m);
                targets.get(index).add(m);
            }

        }
    }

    /**
     * The lifetime of the {@code Attack} has expired and it will remove
     * itself from the {@code Game}.
     */
    @Override
    public void updateState(int dt) {
        if (count >= delay) {
            died();
        }
    }

    /**
     * Remove the {@code Attack} from the {@code Game} when it is
     * completed, or "dead".
     */
    @Override
    public void died() {
        getGame().remove(this);
    }

    /**
     * Notify the {@code Attack} that is has hit a particular
     * {@code Monster}.
     *
     * @param m
     */
    public abstract void hitTarget(AttackTarget m);

    @Override
    public void draw(Graphics g) {
        drawEntity(g);
        if (getGame().debugging()) {
            Rect r = getHitbox();
            float tx = (float) r.getX();
            float ty = (float) r.getY();
            float tw = (float) r.getWidth();
            float th = (float) r.getHeight();
            g.setColor(Color.blue);
            g.drawRect(tx, ty, tw, th);
        }
    }

    /**
     * Draw the {@code Attack}. Draws it with hitbox-centered reflection.
     *
     * @param g
     */
    @Override
    public void drawEntity(Graphics g) {
        SpriteSheet ss = getSprites().get(getState());
        SpriteSheet box = attacker.getHitboxSheet();
        Sprite s = ss.getSprite();
        int frame = getFrame();
        double sx;
        double sy = getY() + ss.getOffsetY(frame);
        if (getDirX() == DEFAULT_DIR) {
            sx = getX() + ss.getOffsetX(frame);
        } else {
            sx = getX() - ss.getOffsetX(frame) - ss.getWidth(frame) + 2 * box.getOffsetX() + box.getWidth();
            s = s.flipHorizontal();
        }
        s.draw((float) sx, (float) sy);
    }

    /**
     * Check collisions only if it is deadly.
     *
     * @return
     */
    @Override
    public boolean canCollide() {
        return super.canCollide() && getState() == deadlyState;
    }

    /**
     * @return the Player
     */
    public Attacker getAttacker() {
        return attacker;
    }

    /**
     * @return the knockback
     */
    public Bound getKnockback() {
        return knockback;
    }

    /**
     * @param knockback the knockback to set
     */
    public void setKnockback(Bound knockback) {
        this.knockback = knockback;
    }

    /**
     * @return the damage
     */
    public Bound getDamage() {
        return damage;
    }

    /**
     * @param damage the damage to set
     */
    public void setDamage(Bound damage) {
        this.damage = damage;
    }

    /**
     * @return the breach
     */
    public double getBreach() {
        return breach;
    }

    /**
     * @param breach the breach to set
     */
    public void setBreach(double breach) {
        this.breach = breach;
    }

    @Override
    public int getZ() {
        return Drawable.ATTACK;
    }

}
