package kawaiiklash;

/**
 * The {@code GreenMushroom} is a {@code Monster} that is a {@code Walker}.
 * It implements {@code PlatformWalker}, which means that it will simply
 * walk in a single {@code Diriection}, without stopping, and will change
 * {@code Direction} upon hitting a wall or reaching the edge of a
 * platform.
 *
 * @author Jeff Niu
 */
public class GreenMushroom extends Walker implements PlatformWalker {

    /**
     * Construct a {@code GreenMushroom}. It will have high health, slow
     * speed, medium defense, some stance, low attack, and no defense
     * ignore. It will be set initially to the {@link #STAND} state.
     *
     * @param game
     */
    public GreenMushroom(Game game) {
        super(game);

        // Initialize the stats
        setHealth(180.0);
        setSpeed(140.0);
        setDefense(50.0);
        setStance(0.20);
        setAttack(50.0);
        setBreach(0.20);

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
     * The {@code Green Mushroom} will do below average damage.
     *
     * @return
     */
    @Override
    public Bound getDamage() {
        return new Bound(280, 300);
    }

    /**
     * Deal somewhat high knockback.
     * 
     * @return 
     */
    @Override
    public Bound getKnockback() {
        return new Bound(930, 970);
    }
    
    /**
     * Update the {@code GreenMushroom} to reflect its intended behavior.
     * After its death animation, it will remove itself from the
     * {@code Game}. After its hit animation, will will face and move in
     * the {@code Direction} towards the {@code Player}.
     * @param dt
     */
    @Override
    public void updateBehavior(int dt) {
        if (getState() == HIT && getCount() >= getHitTime()) {
            double dx = getSpeed() * getNextDir().unit();
            setDx(dx);
            setDdx(0);
            setDirX(getNextDir());
            changeToState(MOVE);
        }
    }

    /**
     * Do the logic associated with the {@code GreenMushroom}. Upon
     * reaching the edge of a platform, it will change {@code Direction}
     * and maintain its speed.
     */
    @Override
    public void doLogic() {
        super.doLogic();
        if (getState() != HIT) {
            Direction dirX = getDirX();
            if (edgeDirection(getPlatforms(platforms)) == dirX) {
                dirX = dirX.inverse();
                double dx = Math.abs(getDx()) * dirX.unit();
                setDx(dx);
            }
            setDirX(dirX);
        }
    }

    @Override
    public Sound getHitSound() {
        return getSounds().get(getSoundNumber("damage"));
    }
}
