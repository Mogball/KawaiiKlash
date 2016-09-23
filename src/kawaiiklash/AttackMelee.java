package kawaiiklash;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import static kawaiiklash.Utility.fail;
import org.newdawn.slick.Graphics;

/**
 * A general class for handling any melee attacks. A melee attack is an
 * attack that creates a hitbox in an area to damage enemies. In that
 * sense, some magic attacks are classified as "melee" attacks.
 *
 * CONSIDER: limiting the number of enemies that can be hit, perhaps by
 * setting a size limit on the target list of lists in the superclass. Make
 * sure that it will hit the closest enemies.
 *
 * @author Jeff Niu
 */
public abstract class AttackMelee extends Attack implements Logicable {

    /**
     * A shorthand method for determining if a number exists in an array.
     * It is assumed that all the numbers in the array are unique. The
     * naming is suggestive of entity states and such.
     *
     * @param state
     * @param stateSet
     * @return
     */
    public static boolean stateSubsetOf(int state, int[] stateSet) {
        for (int i : stateSet) {
            if (state == i) {
                return true;
            }
        }
        return false;
    }

    /**
     * This state represents the main animation of the attack. For example,
     * if a sword swings, it would be this state. This state is responsible
     * for handling hitting enemies.
     */
    public final int EFFECT = getStateNumber("effect");
    /**
     * An animation state for the hit animation. For example, if a sword
     * hits an enemy, the hit animation would be a slash. No real
     * functionality associated with this state.
     */
    public final int[] HIT = getStateNumberSet("hit");

    /**
     * This scanner is used to scan horizontally for walls and get the
     * subregion of a hitbox if a wall sufficiently blocks the attack. It
     * essentially prevents hitting enemies though walls. However, hitting
     * enemies through roofs and floors still works. I'd consider adding a
     * second scanner to handle vertical scanning but recent performance
     * issues compel me to hold off on that until a better collision and
     * visibility system is implemented.
     */
    private final MeleeScanner hitboxFinder;

    /**
     * Additional non-functional animations to be added.
     */
    private final List<SpecialEffect> specials;

    /**
     * Create the melee attack.
     *
     * @param game the {@code Game} to which it belongs
     * @param x
     * @param y
     * @param dir the horizontal {@code Direction} of the attack
     * @param attacker whatever is creating the attack
     * @param deadlyState the state during which it is deadly
     * @param deadlyFrames the frames during which it is deadly
     * @param cible the class of the target
     */
    public AttackMelee(Game game, double x, double y, Direction dir, Attacker attacker, String deadlyState, int[] deadlyFrames, Class<?> cible) {
        super(game, x, y, dir, attacker, deadlyState, deadlyFrames, cible);
        specials = new ArrayList<>(2);
        hitboxFinder = new MeleeScanner();
    }

    /**
     * This method is usually called immediately after the melee attack is
     * initially created as a shorthand way to set the state.
     *
     * @return
     */
    public AttackMelee create() {
        setState(EFFECT);
        return this;
    }

    /**
     * Add an animation to the attack.
     *
     * @param specialState the state number
     * @param trace whether or not this animation should follow the
     * attacker when animating
     * @param startFrame the frame of the main state on which the special
     * effect is initiated
     */
    public void addSpecial(int specialState, boolean trace, int startFrame) {
        specials.add(new SpecialEffect(specialState, trace, startFrame));
    }

    @Override
    @SuppressWarnings("NoopMethodInAbstractClass")
    public void isOutOfBounds(Direction dir) {
    }

    @Override
    @SuppressWarnings("NoopMethodInAbstractClass")
    public void isInBounds() {
    }

    /**
     * Update the special effects and trace the attacker if needed.
     */
    @Override
    public void doLogic() {
        if (getState() == EFFECT) {
            setX(getAttacker().getX());
            setY(getAttacker().getY());
        }
        for (SpecialEffect spc : specials) {
            spc.doLogic();
        }
    }

    /**
     * Some melee attacks have multiple hit animations. Sometimes they are
     * chosen randomly or sometimes they are chosen in an order. Whatever
     * it may be, this method will retrieve it.
     *
     * @return
     */
    public abstract int getHitType();

    /**
     * A separate method for getting the horizontal position of the hit
     * animation so that it can be overridden if needed.
     *
     * @param m
     * @return
     */
    public double getHitX(AttackTarget m) {
        return m.getMidpoint().getX();
    }

    /**
     * @see kawaiiklash.AttackMelee#getHitX(kawaiiklash.AttackTarget)
     * @param m
     * @return
     */
    public double getHitY(AttackTarget m) {
        return m.getMidpoint().getY();
    }

    /**
     * Notification that the melee attack has hit a target. The melee
     * attack will damage the target and then create an instance of itself
     * in the "hit" state.
     *
     * @param m
     */
    @Override
    public void hitTarget(AttackTarget m) {
        m.takeDamage(getAttacker(), this);
        Class<?> c = getClass();
        Constructor<?> ctor = null;
        try {
            ctor = c.getConstructor(Game.class, double.class, double.class, Direction.class, Attacker.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            fail(ex);
        }
        if (ctor != null) {
            try {
                AttackMelee atk = (AttackMelee) ctor.newInstance(getGame(), getHitX(m), getHitY(m), getDirX(), getAttacker());
                atk.setState(getHitType());
                getGame().add(atk);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                fail(ex);
            }
        }
    }

    /**
     * The super class method always draws the hitbox if in debugging mode.
     * Here, we only want it to do so for the "effect" state.
     *
     * @param g
     */
    @Override
    public void draw(Graphics g) {
        if (getState() == EFFECT) {
            super.draw(g);
        } else {
            drawEntity(g);
        }
    }

    /**
     * If the attack state is currently a hit animation, do not draw it
     * with hitbox-centered reflection.
     *
     * @param g
     */
    @Override
    public void drawEntity(Graphics g) {
        if (stateSubsetOf(getState(), HIT)) {
            SpriteSheet ss = getSprites().get(getState());
            Sprite s = ss.getSprite();
            double sx;
            double sy = getY() + ss.getOffsetY();
            if (getDirX() == DEFAULT_DIR) {
                sx = getX() + ss.getOffsetX();
            } else {
                sx = getX() - ss.getWidth() - ss.getOffsetX();
                s = s.flipHorizontal();
            }
            s.draw((float) sx, (float) sy);
        } else {
            super.drawEntity(g);
        }
    }

    /**
     * Get the hitbox of the attack. If the state is in the effect state,
     * scan the region to prevent hitting through walls.
     *
     * @return
     */
    @Override
    public Rect getHitbox() {
        if (getState() == EFFECT) {
            return hitboxFinder.search();
        } else {
            return super.getHitbox();
        }
    }

    private class SpecialEffect implements Logicable {

        private AttackMelee special;
        private final int state;
        private final int frame;
        private final boolean trace;

        private SpecialEffect(int special, boolean trace, int frame) {
            this.special = null;
            this.frame = frame;
            this.trace = trace;
            state = special;
        }

        @Override
        public void doLogic() {
            if (special == null && getState() == EFFECT && getStateFrame(getState()) == frame) {
                Class<?> c = AttackMelee.this.getClass();
                Constructor<?> ctor = null;
                try {
                    ctor = c.getConstructor(Attacker.class);
                } catch (NoSuchMethodException | SecurityException ex) {
                    fail(ex);
                }
                if (ctor != null) {
                    try {
                        special = (AttackMelee) ctor.newInstance(getAttacker());
                        special.setState(state);
                        getGame().add(special);
                    } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                        fail(ex);
                    }
                }
            }
            if (special != null && trace) {
                special.setX(getAttacker().getX());
                special.setY(getAttacker().getY());
            }
        }
    }

    /**
     * Prevents hitting enemies through walls.
     */
    private class MeleeScanner {

        private final float MARGIN = 0.6f;

        private final ScannerImpl<?, Platform> s;

        private MeleeScanner() {
            s = new ScannerImpl<>(null, Platform.class);
        }

        public Rect search() {
            final List<Object> objs = getGame().getObjects();
            final Rect p = getAttacker().getHitbox();
            Rect a = s.search(getDirX(), objs, AttackMelee.super.getHitbox(), MARGIN);
            Rect upper;
            Rect lower;
            if (p.getY() >= a.getY()) {
                if (a.getY() + a.getHeight() >= p.getY()) {
                    upper = new Rect(a.getX(), a.getY(), a.getWidth(), p.getY() - a.getY());
                    lower = new Rect(a.getX(), p.getY(), a.getWidth(), a.getY() + a.getHeight() - p.getY());
                } else {
                    upper = a;
                    lower = new Rect();
                }
            } else if (a.getY() > p.getY() && a.getY() < p.getY() + p.getHeight()) {
                if (a.getY() + a.getHeight() <= p.getY() + p.getHeight()) {
                    return a;
                } else {
                    upper = new Rect();
                    lower = a;
                }
            } else {
                upper = new Rect();
                lower = a;
            }
            upper = s.search(Direction.UP, objs, upper, MARGIN);
            lower = s.search(Direction.DOWN, objs, lower, MARGIN);
            a = upper.union(lower);
            a = s.search(getDirX(), objs, a, MARGIN);
            return a;
        }

    }

}
