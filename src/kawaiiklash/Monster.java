package kawaiiklash;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import static kawaiiklash.Direction.DOWN;
import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Direction.RIGHT;
import kawaiiklash.Effects.Fade;
import org.newdawn.slick.Graphics;

/**
 * A Monster is any Entity which is an enemy of the Player. This class
 * handles the standard behavior of the Monsters in the Game.
 *
 * @author Jeff Niu
 */
public abstract class Monster extends Entity implements Logicable, AttackTarget, Attacker {

    /**
     * This {@code BiFunction} is used to calculate the effective damage.
     *
     * @param g the gradient between the defense and attack
     * @param a the value of the attack
     * @return the effective damage
     */
    private static final BiFunction<Double, Double, Double> DAMAGE_FUNCTION = (Double g, Double a) -> {
        return 3 * pow(a, -2) * pow(g, 2) - 2 * pow(a, -3) * pow(g, 3);
    };

    /**
     * Get the {@link #DAMAGE_FUNCTION damage function}.
     *
     * @return
     */
    public static BiFunction<Double, Double, Double> damageFunction() {
        return DAMAGE_FUNCTION;
    }

    /**
     * The state of the {@code Monster} when it is standing still.
     */
    protected final int STAND = getStateNumber("stand");

    /**
     * The state of the {@code Monster} when it is moving.
     */
    protected final int MOVE = getStateNumber("move");

    /**
     * The state of the {@code Monster} that represents its death
     * animation.
     */
    protected final int DIE = getStateNumber("die");

    /**
     * The state of the {@code Monster} when it has been hit by an Attack.
     */
    protected final int HIT = getStateNumber("hit");

    /**
     * The amount of health points that the {@code Monster} has. When it
     * takes damage, the {@code health} will be reduced by an amount,
     * called the effective damage. When the health is zero or less than
     * zero, the monster is dead.
     */
    private double health;

    /**
     * The standard movement speed of the {@code Monster}. Whenever the
     * {@code Monster} moves, it will be moving at this speed (pixels/sec).
     */
    private double speed;

    /**
     * This stat interacts with the {@code attack} stat to decide the
     * effective damage. If defense is greater or equal to twice the
     * attack, then no damage is taken. Otherwise, damage is taken.
     */
    private double defense;

    /**
     * A percentage resist of the knockback inflicted on this Monster. That
     * is, if {@code stance = 0.25}, then the effective knockback is
     * {@code 0.75 * knockback}.
     */
    private double stance;

    /**
     * The attack stat interacts with the {@code defense} stat to decide
     * the effective damage taken. If the attack is overwhelmingly higher
     * than the defense, then damage is taken.
     */
    private double attack;

    /**
     * A percentage ignore of {@code defense}. That is, if
     * {@code breach = 0.25}, then the effective defense is
     * {@code 0.75 * defense}.
     */
    private double breach;

    /**
     * A standard update counter for the Monster.
     */
    private double count;

    /**
     * The {@code Direction} the Monster will assume after it has finished
     * its hit and knockback animation. It is normally going to be towards
     * the {@code Player} who attacked it.
     */
    private Direction nextDir;

    /**
     * The amount of time for which a hit and knockback animation lasts.
     * After this time, the Monster will revert to its normal state.
     */
    private double hitTime;

    /**
     * Whether or not the {@code Monster} should play its hit sound upon
     * being hit.
     */
    private boolean playHitSound;

    /**
     * A {@code List} that is composed of {@code Rect} objects that
     * represent the {@code Tile} objects with which the {@code Monster}
     * has collided. The {@code ArrayList} is initialized to {@code 3}
     * because it is anticipated that a {@code Monster} would generally
     * encounter two or three {@code Tile} objects; it would stand on two
     * and hit a third as a wall.
     */
    protected final List<Platform> platforms = new ArrayList<>(3);

    /**
     * A constructor for a {@code Monster} called from a subclass.
     *
     * @param game
     */
    public Monster(Game game) {
        super(game);
        playHitSound = false;
    }

    /**
     * Get the damage range that will be dealt.
     *
     * @return
     */
    public abstract Bound getDamage();

    /**
     * Get the knockback range.
     *
     * @return
     */
    public abstract Bound getKnockback();

    /**
     * When a {@code Monster} collides with another {@code Collideable}, it
     * will check its health to see if its dead; if so, it will invoke the
     * method {@link #died() died}. If it collides with a {@code Tile},
     * then it will add it to the {@link #platforms List of Tiles}.
     *
     * @param other the {@code Collideable} with which collision occurred
     */
    @Override
    public void collidedWith(Collideable other) {
        if (other instanceof Platform) {
            platforms.add((Platform) other);
        }
        if (health <= 0 && getState() != HIT) {
            died();
            dropItems(getGame().getPlayer());
        }
    }

    /**
     * Method called to change to a new state, resetting that state's
     * animation counters.
     *
     * @param state the new state
     */
    @Override
    public void changeToState(int state) {
        if (getState() != state) {
            setState(state);
            getSprites().get(state).setCount(0);
            getSprites().get(state).setFrame(0);
        }
    }

    /**
     * Method called to set the {@code Direction} of this {@code Monster}.
     *
     * @param dir the {@code Direction} to which to change
     */
    @Override
    public void setDirX(Direction dir) {
        super.setDirX(dir);
        if (getState() == MOVE) {
            double dx = speed * getDirX().unit();
            setDx(dx);
        }
    }

    /**
     * Tell the {@code Monster} that it is out of bounds. For this, the
     * {@code Monster} will be inactive if it is horizontally out of
     * bounds, and it will be dead if it has fallen off the screen (i.e.
     * {@code Direction = Direction.DOWN}).
     *
     * @param dir the out of bounds {@code Direction}
     */
    @Override
    public void isOutOfBounds(Direction dir) {
        if (dir == LEFT || dir == RIGHT) {
            setActive(false);
        } else if (dir == DOWN && !isDead()) {
            died();
        }
    }

    /**
     * Tell the {@code Monster} that it is in bounds, which will make it
     * active.
     */
    @Override
    public void isInBounds() {
        setActive(true);
    }

    /**
     * Tell the {@code Monster} that it is dead. The {@code Monster} will
     * initiate its death animation, switching to {@code state = state.DIE}
     */
    @Override
    public void died() {
        setDead(true);
        setCount(0);
        cycleFrames(DIE, false);
        changeToState(DIE);
        Effects.fade(Fade.OUT, getGame(), getSprites().get(DIE), getStateDelay(DIE));
    }

    /**
     * Upon death, not by falling off the screen, drop any {@code Item}s.
     *
     * @param plr
     */
    public void dropItems(Player plr) {
        Game game = getGame();
        HealthPotion.drop(game, this, plr);
    }

    /**
     * The {@code @Override} {@link #draw(java.awt.Graphics) draw} method
     * of this class {@code Monster} will invert the image to the opposite
     * of the current {@code Direction} if the {@code Monster} is being
     * hit; {@code state = HIT}.
     *
     * @param g the {@code Graphics} to which to draw.
     */
    @Override
    public void draw(Graphics g) {
        if (getState() == HIT) {
            setDirX(getDirX().inverse());
            super.draw(g);
            setDirX(getDirX().inverse());
        } else {
            super.draw(g);
        }
    }

    /**
     * Update the internal counter, reset the platforms list, and remove
     * the monster if it is dead.
     *
     * @param dt
     */
    @Override
    public void updateState(int dt) {
        count += dt;
        if (isDead() && count >= getStateDelay(DIE)) {
            getGame().remove(this);
        }
        updateBehavior(dt);
        platforms.clear();
    }

    public abstract void updateBehavior(int dt);

    @Override
    public boolean isDamageable() {
        return true;
    }

    /**
     * Tell the {@code Monster} that it has taken damage from a certain
     * {@code Player} who is doing a certain {@code Attack}. The method
     * will not run if the health of the {@code Monster} is less than zero.
     * If it does, it will first get a random damage value then calculate
     * the effective defense of this {@code Monster}. Using the different
     * between the attack value of the attacking {@code Player}, it will
     * use a cubic function to determine the effective damage. Then it will
     * compute the effective knockback using the stance stat.
     *
     * @param attacker the {@code Player} doing damage
     *
     * @param attack the {@code Attack} being done
     */
    @Override
    public void takeDamage(Attacker attacker, Attack attack) {
        // Do not damage the Monster if its health is zero
        if (health <= 0) {
            return;
        }
        
        // Calculate the effective damage
        double dmg = attack.getDamage().rand();
        double def = defense * (1 - attack.getBreach());
        double gradient = attacker.getAttack() - def;
        if (gradient > 0) {
            dmg *= DAMAGE_FUNCTION.apply(gradient, attacker.getAttack());
        }
        health -= dmg;

        // Find the effective knockback
        double kb = attack.getKnockback().rand() * (1 - stance);
        if (kb > 0) {
            if (playHitSound) {
                Game game = getGame();
                getHitSound().playEffect(game.getPitch(), game.getGain(), false);
            }
            Direction attackDir = attack.getDirX();
            count = 0;
            double spd = sqrt(2 * FRICTION * kb);
            double dx = spd * attackDir.unit();
            setDx(dx);
            hitTime = MILLISECONDS * sqrt(2 * kb / FRICTION);
            setDdx(FRICTION * -attackDir.unit());
            nextDir = attackDir.inverse();
            changeToState(HIT);
            knockback();
        }
    }

    @SuppressWarnings("NoopMethodInAbstractClass")
    public void knockback() {
    }

    /**
     * @return the nextDir
     */
    public Direction getNextDir() {
        return nextDir;
    }

    /**
     * @param nextDir the nextDir to set
     */
    public void setNextDir(Direction nextDir) {
        this.nextDir = nextDir;
    }

    /**
     * @return the hitTime
     */
    public double getHitTime() {
        return hitTime;
    }

    /**
     * @param hitTime the hitTime to set
     */
    public void setHitTime(double hitTime) {
        this.hitTime = hitTime;
    }

    /**
     * @return the count
     */
    public double getCount() {
        return count;
    }

    /**
     * @param count the count to set
     */
    public void setCount(double count) {
        this.count = count;
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

    /**
     * @return the health
     */
    public double getHealth() {
        return health;
    }

    /**
     * @param health the health to set
     */
    public void setHealth(double health) {
        this.health = health;
    }

    /**
     * @return the speed
     */
    public double getSpeed() {
        return speed;
    }

    /**
     * @param speed the speed to set
     */
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    /**
     * @return the defense
     */
    public double getDefense() {
        return defense;
    }

    /**
     * @param defense the defense to set
     */
    public void setDefense(double defense) {
        this.defense = defense;
    }

    /**
     * @return the stance
     */
    public double getStance() {
        return stance;
    }

    /**
     * @param stance the stance to set
     */
    public void setStance(double stance) {
        this.stance = stance;
    }

    /**
     * @return the attack
     */
    @Override
    public double getAttack() {
        return attack;
    }

    /**
     * @param attack the attack to set
     */
    public void setAttack(double attack) {
        this.attack = attack;
    }

    public abstract Sound getHitSound();

    public void setPlayHitSound(boolean play) {
        playHitSound = play;
    }

    @Override
    public SpriteSheet getHitboxSheet() {
        return getSprites().get(getState());
    }

    @Override
    public int getZ() {
        return Drawable.MONSTER;
    }

}
