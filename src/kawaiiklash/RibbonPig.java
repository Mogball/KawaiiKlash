package kawaiiklash;

/**
 * A {@code RibbonPig} is a {@code Monster} that is a {@code Walker}. It
 * has very simple behavior mechanics; it will run until it hits a wall
 * then switch {@code Direction}.
 *
 * @author Jeff Niu
 */
public class RibbonPig extends Walker {

    /**
     * Construct a {@code RibbonPig}. It will have low health, above
     * average speed, no defense, no stance, low attack, and minimal
     * breach.
     *
     * @param game
     */
    public RibbonPig(Game game) {
        super(game);

        // Initialize the stats
        setHealth(100.0);
        setSpeed(170.0);
        setDefense(0.0);
        setStance(0.0);
        setAttack(70.0);
        setBreach(0.05);

        // Initialize the state
        setState(MOVE);

        // Initialize the movement
        setDx(getSpeed() * getDirX().unit());
        setDy(0);
        setDdx(0);
        setDdy(GRAVITY);
        setMaxDx(UNLIMITED);
        setMaxDy(GRAVITY);

        addSound("die", DIE, 0);
    }

    /**
     * The {@code RibbonPig} will do average damage.
     *
     * @return
     */
    @Override
    public Bound getDamage() {
        return new Bound(300, 350);
    }

    /**
     * Deal moderate knockback.
     *
     * @return
     */
    @Override
    public Bound getKnockback() {
        return new Bound(875, 925);
    }

    /**
     * Update the {@code RibbonPig} according to its behavior. It will
     * remove itself from the {@code Game} upon finishing its death
     * animation. After its hit animation, it will return to running
     * towards the {@code Player}.
     *
     * @param dt
     */
    @Override
    public void updateBehavior(int dt) {
        if (getState() == HIT && getCount() >= getHitTime()) {
            setDx(getSpeed() * getNextDir().unit());
            setDdx(0);
            setDirX(getNextDir());
            changeToState(MOVE);
        }
    }

    @Override
    public Sound getHitSound() {
        return getSounds().get(getSoundNumber("damage"));
    }

}
