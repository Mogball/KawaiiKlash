package kawaiiklash;

import java.util.List;
import org.newdawn.slick.Graphics;

/**
 * The {@code BlueRibbonPig} is a {@code Monster} that is a {@code Walker}.
 * When spawned, the {@code BlueRibbonPig} will stand still facing a
 * certain {@code Direction}. When a {@code Player} enters its line of
 * sight, the {@code BlueRibbonPig} will start moving towards him.
 *
 *
 * @author Jeff Niu
 */
public class BlueRibbonPig extends Walker {

    /**
     * The {@code Scanner} used by the {@code BlueRibbonPig} as a
     * {@code Player} detector.
     */
    private final Scanner scanner = new PlayerSearcher();

    /**
     * Construct a {@code BlueRibbonPig}. It will have low health, very
     * high movement speed, low defense, some stance, medium attack, and
     * some defense ignore. It will be initially set to the {@link #STAND}
     * state.
     *
     * @param game
     */
    public BlueRibbonPig(Game game) {
        super(game);

        // Initialize the stats
        setHealth(120.0);
        setSpeed(450.0);
        setDefense(20.0);
        setStance(0.20);
        setAttack(150.0);
        setBreach(0.20);

        // Initialize the state
        setState(STAND);

        // Intialize the movement
        setDx(0);
        setDy(0);
        setDdx(0);
        setDdy(GRAVITY);
        setMaxDx(UNLIMITED);
        setMaxDy(GRAVITY);

        addSound("die", DIE, 0);
    }

    /**
     * The {@code BlueRibbonPig} will deal average damage.
     *
     * @return
     */
    @Override
    public Bound getDamage() {
        return new Bound(340, 380);
    }

    /**
     * Deal high knockback.
     *
     * @return
     */
    @Override
    public Bound getKnockback() {
        return new Bound(950, 1050);
    }

    /**
     * Update the {@code BlueRibbonPig} to reflect its intended behavior.
     * Once its death animation is over, it will be removed from the
     * {@code Game}. After the hit animation is over, it will start running
     * immediately at the {@code Player}, regardless of whether it was
     * standing before. If it is still standing, if a {@code Player} enters
     * its line of sight, it will start running.
     *
     * @param dt
     */
    @Override
    public void updateBehavior(int dt) {
        int state = getState();
        if (state == HIT && getCount() >= getHitTime()) {
            double dx = getSpeed() * getNextDir().unit();
            setDx(dx);
            setDdx(0);
            setDirX(getNextDir());
            changeToState(MOVE);
        }
        if (state == STAND && scanner.scan()) {
            double dx = getSpeed() * getDirX().unit();
            setDx(dx);
            setDdx(0);
            changeToState(MOVE);
        }
    }

    @Override
    public Sound getHitSound() {
        return getSounds().get(getSoundNumber("damage"));
    }

    @Override
    public void draw(Graphics g) {
        if (getGame().debugging()) {
            scanner.drawArea(g);
        }
        super.draw(g);
    }

    /**
     * The {@code BlueRibbonPigScanner} is the {@code Scanner} used
     * by the {@code BlueRibbonPig} specifically as a {@code Player}
     * detector.
     */
    private class PlayerSearcher implements Scanner {

        /**
         * Create and return the {@code EntityScanner} associated with this
         * {@code Scanner}. Its {@code Target} will be a
         * {@code Player} and it will have the obstacle of any
         * {@code Tile}.
         *
         * @return the {@code EntityScanner} of this {@code BlueRibbonPig}.
         */
        @Override
        public ScannerImpl<Player, Platform> getScanner() {
            return new ScannerImpl<>(Player.class, Platform.class);
        }

        /**
         * Get the {@code Direction} in which the scan occurs. The
         * {@code BlueRibbonPigScanner} will scan in the {@code Direction}
         * facing which is the {@code BlueRibbonPig}.
         *
         * @return the {@code Direction} of the {@code BlueRibbonPig}.
         */
        @Override
        public Direction getScanDir() {
            return getDirX();
        }

        /**
         * Get the {@code Dimension} representing the scan area. The
         * {@code BlueRibbonPigScanner} will scan an area of
         * {@code 870 x 50} pixels, which is a long horizontal section of
         * the screen.
         *
         * @return the scan area
         */
        @Override
        public Rect getScanArea() {
            final Rect r = getHitbox();
            double sx = r.getX() + r.getWidth() / 2;
            final double sy = r.getY();
            final double sw = 800;
            final double sh = r.getHeight();
            if (getScanDir() == Direction.LEFT) {
                sx -= sw;
            }
            return new Rect(sx, sy, sw, sh);
        }

        /**
         * Get the percent blockage of the line of sight. For the
         * {@code BlueRibbonPigScanner}, three quarters {@code 75%} of the
         * sight must be blocked in the scanning {@code Direction}, which
         * is a safe majority of the area.
         *
         * @return
         */
        @Override
        public float getScanMargin() {
            return 0.75f;
        }

        /**
         * Get the {@code List} of {@code Object}s through which to scan.
         * The {@code BlueRibbonPigScanner} will scan through the
         * {@code Game Object}s.
         *
         * @return
         */
        @Override
        public List<Object> getScanObjects() {
            return getGame().getObjects();
        }
    }

}
