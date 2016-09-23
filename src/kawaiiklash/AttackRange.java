package kawaiiklash;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import static kawaiiklash.AttackMelee.stateSubsetOf;
import static kawaiiklash.Utility.fail;
import org.newdawn.slick.Graphics;

/**
 * A ranged attack is any attack that fires a projectile that damages a
 * target.
 *
 * CONSIDER: limiting the number of enemies hit. Granted, a much easier
 * implementation than the melee counterpart.
 *
 * @author Jeff Niu
 */
public abstract class AttackRange extends Attack implements Logicable {

    /**
     * Value to indicate that a ball should not be fired.
     */
    private static final int NO_BALL = -1;

    /**
     * The animation state whose only functionality is to act as a
     * reference point as to when the projectile is fired.
     */
    public final int EFFECT = getStateNumber("effect");
    /**
     * The state representing the projectile. This state will be
     * specialized as it will travel independently of the attacker and deal
     * damage.
     */
    public final int BALL = getStateNumber("ball");
    /**
     * Functionless animation state for when a target is hit.
     */
    public final int[] HIT = getStateNumberSet("hit");

    /**
     * The frames of the "effect" state during which a projectile should be
     * fired.
     */
    private final int[] shootFrames;
    /**
     * For each of the above frames, there is a corresponding boolean
     * representing whether or not a projectile has been fired on that
     * frame.
     */
    private final boolean[] fired;

    /**
     * Additional functionless animation effects.
     */
    private final List<SpecialEffect> specials;

    /**
     * Create the ranged attack.
     *
     * @param game
     * @param x
     * @param y
     * @param dir
     * @param attacker
     * @param shootFrames the frames of the "effect" state during which a
     * projectile should be fired
     * @param cible
     */
    public AttackRange(Game game, double x, double y, Direction dir, Attacker attacker, int[] shootFrames, Class<?> cible) {
        super(game, x, y, dir, attacker, "ball", new int[]{0}, cible);
        this.shootFrames = shootFrames;
        fired = new boolean[shootFrames.length];
        specials = new ArrayList<>(2);
    }

    /**
     * Shorthand method for when a ranged attack is first created.
     *
     * @return
     */
    public AttackRange create() {
        setState(EFFECT);
        return this;
    }

    /**
     * Add a special effect.
     *
     * @param specialState
     * @param trace
     * @param startFrame
     */
    public void addSpecial(int specialState, boolean trace, int startFrame) {
        specials.add(new SpecialEffect(specialState, trace, startFrame));
    }

    /**
     * This method will determine whether or not a projectile should be
     * fired on a particular frame. If it should, then the index of that
     * frame in {@link AttackRange#shootFrames} and
     * {@link AttackRange#fired} is returned. Otherwise, it will return
     * {@link #NO_BALL};
     *
     * @return
     */
    private int launchBall() {
        int frame = getStateFrame(EFFECT);
        for (int i = 0; i < shootFrames.length; i++) {
            if (frame == shootFrames[i]) {
                return i;
            }
        }
        return NO_BALL;
    }

    /**
     * Get the hitbox of the projectile. For now, it will be a one-by-one
     * pixel in the center of the actual hitbox. The choice to further
     * shrink the size of the hitbox even more than the actual projectile
     * is for overall graphical fidelity without making tedious code
     * alterations. That's either because I'm lazy or because the current
     * framework does not permit an elegant solution.
     *
     * @return
     */
    @Override
    public Rect getHitbox() {
        Rect r = super.getHitbox();
        final double bx = r.getX() + r.getWidth() / 2;
        final double by = r.getY() + r.getHeight() / 2;
        final double bw = 1;
        final double bh = 1;
        return new Rect(bx, by, bw, bh);
    }

    /**
     * When the ball is created, it would be awkward if it suddenly
     * appeared, so allow it to fade in. This method gets the fade in time.
     *
     * @return
     */
    public abstract int getFadeTime();

    /**
     * @see AttackMelee#getHitType()
     * @return
     */
    public abstract int getHitType();

    /**
     * Whether or not the projectile will hit only one enemy or pierce
     * through all of them.
     *
     * @return
     */
    public abstract boolean pierceMonster();

    /**
     * Whether or not the projectile should be fired from the attacker when
     * it is launched or from the attack's initial position.
     *
     * @return
     */
    public abstract boolean fireFromEntity();

    /**
     * Get the horizontal speed of the projectile.
     *
     * @return
     */
    public abstract double getBallDx();

    /**
     * By default, the projectile will travel in a straight line, though
     * that can be changed by overriding this.
     *
     * @return
     */
    public double getBallDy() {
        return 0.0;
    }

    /**
     * Get the horizontal position from which to launch the ball.
     *
     * @return
     */
    public double getBallX() {
        SpriteSheet ss = getSprites().get(EFFECT);
        SpriteSheet box = getAttacker().getHitboxSheet();
        double bx = getX() + ss.getOffsetX() + ss.getWidth() / 2.0;
        if (getDirX() != DEFAULT_DIR) {
            bx += 2 * box.getOffsetX() + box.getWidth() - 2 * ss.getOffsetX() - ss.getWidth();
        }
        return bx;
    }

    /**
     * Get the vertical position from which to launch the ball.
     *
     * @return
     */
    public double getBallY() {
        SpriteSheet ss = getSprites().get(EFFECT);
        return getY() + ss.getOffsetY() + ss.getHeight() / 2.0;
    }

    /**
     * Check to see if we should fire a projectile.
     *
     * @param dt
     */
    @Override
    public void update(int dt) {
        super.update(dt);
        int frame = getState() == EFFECT ? launchBall() : NO_BALL;
        if (frame != NO_BALL && !fired[frame]) {
            Class<?> c = getClass();
            Constructor<?> ctor = null;
            try {
                ctor = c.getConstructor(Game.class, double.class, double.class, Direction.class, Attacker.class);
            } catch (NoSuchMethodException | SecurityException ex) {
                fail(ex);
            }
            if (ctor != null) {
                try {
                    AttackRange ball = (AttackRange) ctor.newInstance(getGame(), getBallX(), getBallY(), getDirX(), getAttacker());
                    ball.setState(ball.BALL);
                    getGame().add(ball);
                    Effects.fade(Effects.Fade.IN, getGame(), ball.getSprites().get(ball.BALL), getFadeTime());
                } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | InvocationTargetException ex) {
                    fail(ex);
                }
            }
            fired[frame] = true;
        }
    }

    /**
     * Add the hit animation.
     *
     * @param hx
     * @param hy
     */
    private void addHit(double hx, double hy) {
        Class<?> c = getClass();
        Constructor<?> ctor = null;
        try {
            ctor = c.getConstructor(Game.class, double.class, double.class, Direction.class, Attacker.class);
        } catch (NoSuchMethodException | SecurityException ex) {
            fail(ex);
        }
        if (ctor != null) {
            try {
                AttackRange hit = (AttackRange) ctor.newInstance(getGame(), hx, hy, getDirX(), getAttacker());
                hit.setState(hit.HIT[getHitType()]);
                getGame().add(hit);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                fail(ex);
            }
        }
    }

    /**
     * If it hits a wall, destroy itself.
     *
     * @param other
     */
    @Override
    public void collidedWith(Collideable other) {
        super.collidedWith(other);
        if (getState() == BALL && other instanceof Platform) {
            Rect hitbox = getHitbox();
            double hx = hitbox.getX() + hitbox.getWidth() / 2;
            double hy = hitbox.getY() + hitbox.getHeight() / 2;
            addHit(hx, hy);
            died();
        }
    }

    /**
     * Indicate that it has hit a target. Deal damage and create the hit
     * animation.
     *
     * @param m
     */
    @Override
    public void hitTarget(AttackTarget m) {
        m.takeDamage(getAttacker(), this);
        Rect hitbox = getHitbox();
        double hx = hitbox.getX() + hitbox.getWidth() / 2;
        double hy = hitbox.getY() + hitbox.getHeight() / 2;
        addHit(hx, hy);
        if (!pierceMonster()) {
            died();
        }
    }

    /**
     * Update the special effects and trace the player if needed.
     */
    @Override
    public void doLogic() {
        if (fireFromEntity() && getState() == EFFECT) {
            setX(getAttacker().getX());
            setY(getAttacker().getY());
        }
        for (SpecialEffect spc : specials) {
            spc.doLogic();
        }
    }

    /**
     * When changing the state to the ball, this overridden method will
     * intercept that and set the speeds of the projectile.
     *
     * @param state
     */
    @Override
    public void setState(int state) {
        super.setState(state);
        if (state == BALL) {
            getSprites().get(state).cycleFrames(true);
            setDx(getBallDx() * getDirX().unit());
            setDy(getBallDy());
        }
    }

    /**
     * Do not remove the attack from the game when its animation is over.
     *
     * @param dt
     */
    @Override
    public void updateState(int dt) {
        if (getState() != BALL) {
            super.updateState(dt);
        }
    }

    /**
     * If the projectile leaves the screen, remove it.
     *
     * @param dir
     */
    @Override
    public void isOutOfBounds(Direction dir) {
        if (getState() == BALL) {
            died();
        }
    }

    @Override
    @SuppressWarnings("NoopMethodInAbstractClass")
    public void isInBounds() {
    }

    /**
     * It is always deadly during the ball state.
     *
     * @return
     */
    @Override
    public int isDeadly() {
        return getState() == BALL ? 0 : NOT_DEADLY;
    }

    /**
     * Draw the hitbox only when in the ball state (debugging).
     *
     * @param g
     */
    @Override
    public void draw(Graphics g) {
        if (getState() == BALL) {
            super.draw(g);
        } else {
            drawEntity(g);
        }
    }

    /**
     * Draw the ball and hit states without hitbox-centered reflection.
     *
     * @param g
     */
    @Override
    public void drawEntity(Graphics g) {
        if (stateSubsetOf(getState(), HIT) || getState() == BALL) {
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

    private class SpecialEffect implements Logicable {

        private AttackRange special;
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
                Class<?> c = AttackRange.this.getClass();
                Constructor<?> ctor = null;
                try {
                    ctor = c.getConstructor(Attacker.class);
                } catch (NoSuchMethodException | SecurityException ex) {
                    fail(ex);
                }
                if (ctor != null) {
                    try {
                        special = (AttackRange) ctor.newInstance(getAttacker());
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

}
