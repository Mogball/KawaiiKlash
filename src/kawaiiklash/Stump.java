package kawaiiklash;

import static java.lang.Math.abs;

/**
 * A {@code Stump} is a {@code Monster} that is a {@code Walker}. It will
 * start facing a {@code Direction} and standing still. after a certain
 * amount of time (5000ms), it will walk a certain distance (500p) and then
 * stop. After the same waiting time, it will walk the same distance in the
 * opposite {@code Direction}
 *
 * @author Jeff Niu
 */
public class Stump extends Walker implements PlatformWalker {

    /**
     * The time during which the {@code Stump} will wait while standing
     * still. After this time, it will being walking.
     */
    private final int DELAY_STAND = 2_500;

    /**
     * The time during which the {@code Stump} is moving. It will move for
     * this time before returning to its standing state.
     */
    private final int DELAY_MOVE = 2_500;

    /**
     * Whether or not it is the first time the {@code Stump} is walking. If
     * this is true, it will walk in the {@code Direction} in which it is
     * facing when standing. If it is false, it will change
     * {@code Direction} and then walk.
     */
    private boolean firstWalk;

    /**
     * Construct a {@code Stump}. It will have very high health, low speed,
     * low defense, high stance, low attack, and no breach.
     *
     * @param game
     */
    public Stump(Game game) {
        super(game);

        // Initialize the stats
        setHealth(250.0);
        setSpeed(100.0);
        setDefense(50.0);
        setStance(0.40);
        setAttack(50.0);
        setBreach(0.0);

        // Initialize the state
        setState(STAND);

        // Initialize the movement
        setDx(0);
        setDy(0);
        setDdx(0);
        setDdy(GRAVITY);
        setMaxDx(UNLIMITED);
        setMaxDy(GRAVITY);

        // Initialize whether or not it is walking first to true
        firstWalk = true;

        addSound("die", DIE, 0);
        setPlayHitSound(true);
    }

    /**
     * The {@code Stump} will do very low damage.
     *
     * @return
     */
    @Override
    public Bound getDamage() {
        return new Bound(250, 270);
    }

    /**
     * Deal low knockback.
     *
     * @return
     */
    @Override
    public Bound getKnockback() {
        return new Bound(675, 725);
    }

    /**
     * Upon reaching the edge of a platform while walking, the
     * {@code Stump} will change its {@code Direction}. This is per the
     * {@code PlatformWalker} interface.
     */
    @Override
    public void doLogic() {
        super.doLogic();
        if (getState() != HIT) {
            if (edgeDirection(getPlatforms(platforms)) == getDirX()) {
                setDirX(getDirX().inverse());
                setDx(abs(getDx()) * getDirX().unit());
                setCount(DELAY_MOVE - getCount());
            }
        }
    }

    /**
     * Update the {@code Stump} as per its behavior. Upon finishing its
     * death animation, it will remove itself from the {@code Game}. After
     * its hit animation, it will face the {@code Player}, set the
     * {@link #firstWalk first time walking} to true, and walk towards the
     * {@code Player} without delay.
     *
     * @param dt
     */
    @Override
    public void updateBehavior(int dt) {
        int state = getState();
        double dx = getDx();
        double dy = getDy();
        double ddx = getDdx();
        Direction dirX = getDirX();
        if (state == HIT && getCount() >= getHitTime()) {
            setCount(0);
            dirX = getNextDir();
            dx = getSpeed() * dirX.unit();
            ddx = 0.0;
            changeToState(MOVE);
            firstWalk = true;
        }
        if (state == STAND && getCount() >= DELAY_STAND) {
            setCount(0);
            if (!firstWalk) {
                dirX = dirX.inverse();
            } else {
                firstWalk = false;
            }
            dx = getSpeed() * dirX.unit();
            ddx = 0.0;
            changeToState(MOVE);
        }
        if (state == MOVE && getCount() >= DELAY_MOVE) {
            double errorX = (getCount() - DELAY_MOVE) / MILLISECONDS * -dx;
            setX(getX() + errorX - 2 * getDirX().unit());
            setCount(0);
            dx = 0.0;
            ddx = 0.0;
            changeToState(STAND);
        }
        setDx(dx);
        setDy(dy);
        setDdx(ddx);
        setDirX(dirX);
    }

    @Override
    public Sound getHitSound() {
        return getSounds().get(getSoundNumber("damage"));
    }

}
