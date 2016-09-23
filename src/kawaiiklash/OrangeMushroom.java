package kawaiiklash;

/**
 * An {@code OrangeMonster} is a {@code Monster} that is a {@code Jumper}.
 * It will stand still on the ground for a certain amount of time (2000ms)
 * then jump a certain height.
 *
 * @author Jeff Niu
 */
public class OrangeMushroom extends Jumper {

    /**
     * The time during which the {@code OrangeMushroom} is standing still
     * before jumping.
     */
    private static final int DELAY_STAND_TO_JUMP = 2_000;

    /**
     * Construct an {@code OrangeMushroom}. It will have low health, above
     * average speed, low defense, minimal stance, high attack, and no
     * breach.
     *
     * @param game
     */
    public OrangeMushroom(Game game) {
        super(game);

        // Initialize the stats
        setHealth(150.0);
        setSpeed(0.0);
        setDefense(20.0);
        setStance(0.05);
        setAttack(65.0);
        setBreach(0.30);

        // Intialize the state
        setState(STAND);

        // Initialize the movement
        setDx(0);
        setDy(0);
        setDdx(0);
        setDdy(GRAVITY);
        setMaxDx(UNLIMITED);
        setMaxDy(GRAVITY);

        // Set the jump height
        setJumpHeight(360.0);
        setJumpLength(300.0);

        addSound("die", DIE, 0);
    }

    /**
     * The {@code OrangeMushroom} will high damage.
     *
     * @return
     */
    @Override
    public Bound getDamage() {
        return new Bound(400, 450);
    }

    /**
     * Deal high knockback.
     *
     * @return
     */
    @Override
    public Bound getKnockback() {
        return new Bound(975, 1025);
    }

    /**
     * The {@code OrangeMushroom} will jump. This method will first kick
     * the {@code OrangeMushroom} up a little bit (5 pixels) to prevent
     * collision with the ground while jumping.
     */
    @Override
    public void jump() {
        setY(getY() - 5);
        setDx(getJumpDx() * getDirX().unit());
        setDy(getJumpDy());
    }

    /**
     * The {@code OrangeMushroom} will do nothing special upon hitting a
     * wall. It will simply get stuck on the wall.
     *
     * @param wall
     */
    @Override
    public void hitWall(Rect wall) {
    }

    /**
     * The {@code OrangeMushroom} will halt all movement upon hitting the
     * ground and change to the stand state.
     *
     * @param ground
     */
    @Override
    public void hitGround(Rect ground) {
        setDy(0);
        if (getState() == JUMP) {
            setDx(0);
            setCount(0);
            changeToState(STAND);
        }
    }

    /**
     * The {@code OrangeMushroom} will stop vertically when it hits a
     * ceiling.
     *
     * @param ceil
     */
    @Override
    public void hitCeiling(Rect ceil) {
        setDy(0);
    }

    /**
     * Update the {@code OrangeMushroom} to reflect its intended behavior.
     * After its death animation, it will remove itself from the
     * {@code Game}. After its hit animation, it will jump at the
     * {@code Player}.
     */
    @Override
    public void updateBehavior(int dt) {
        int state = getState();
        if (getCount() >= DELAY_STAND_TO_JUMP && state == STAND) {
            setCount(0);
            changeToState(MOVE);
        }
        if (getCount() >= getSprites().get(MOVE).getDelay() && state == MOVE) {
            setCount(0);
            changeToState(JUMP);
            jump();
        }
        if (state == HIT && getCount() >= getHitTime()) {
            setDx(0);
            setDdx(0);
            setDirX(getNextDir());
            changeToState(STAND);
        }
    }

    @Override
    public Sound getHitSound() {
        return getSounds().get(getSoundNumber("damage"));
    }

}
