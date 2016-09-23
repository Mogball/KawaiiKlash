package kawaiiklash;

import java.util.ArrayList;
import java.util.List;
import static kawaiiklash.Direction.DOWN;
import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Direction.RIGHT;
import static kawaiiklash.Direction.UP;
import org.newdawn.slick.Graphics;

/**
 * A stronger monster. The monster's behavior is determined by three
 * scanners and a platform walking module. Before anything, the DRoy will
 * always face the player.The first scanner {@link #playerFinder} has a
 * large range and, when the player is within this range, the DRoy will
 * move towards the player horizontally. If the player is within the range
 * of {@link #smashScanner}, then the DRoy will do a powerful smashing
 * attacking with a small range. Otherwise, if the player is in the range
 * of {@link #laserScanner}, the DRoy will fire a medium range laser beam.
 * When reaching the edge of a platform, the DRoy will cease to move but
 * can still do attacks.
 *
 * As a more advanced monster, the DRoy will have its own dedicated hitbox.
 * This DRoy can be made into a boss monster. Strategies to deal with the
 * DRoy include dodging its laser beam by ducking or jumping and staying
 * clear from the smash attack. One could, if necessary, touch the DRoy,
 * take some contact damage, and avoid the smash attack via invincibility.
 *
 * @author Jeff Niu
 */
public class DRoy extends Monster implements Boss, PlatformWalker, Attacker {

    // State references for the DRoy
    private final int ATTACK_SMASH = getStateNumber("attack1");
    private final int ATTACK_LASER = getStateNumber("attack2");
    private final int HITBOX = getStateNumber("naked");

    // The various scanners used by the DRoy
    private final Scanner playerFinder = new PlayerFinder();
    private final Scanner smashScanner = new SmashScanner();
    private final Scanner laserScanner = new LaserScanner();

    private final List<AttackMelee> melees;

    /**
     * A reference to the player so that the DRoy can face itself towards
     * the player.
     */
    private Player plr;

    /**
     * An internal counter.
     */
    private int t;

    /**
     * Whether or not this instance of the DRoy is a boss.
     */
    private boolean boss;

    /**
     * The direction in which it is walking off a platform. Used to keep
     * the DRoy from walking off platforms. When this value is equal to
     * {@code Direction.NONE}, it is an indicator that it is no longer
     * walking off an edge.
     */
    private Direction edgeDir;

    /**
     * Create the DRoy.
     *
     * @param game
     */
    public DRoy(Game game) {
        super(game);

        setHealth(300);
        setSpeed(225);
        setDefense(40.0);
        setStance(0.40);
        setAttack(60);
        setBreach(0.70);

        setDx(0);
        setDy(0);
        setDdx(0);
        setDdy(GRAVITY);
        setMaxDx(UNLIMITED);
        setMaxDy(GRAVITY);

        addSound("die", DIE, 0);
        setPlayHitSound(true);
        boss = false;
        t = 0;
        edgeDir = Direction.NONE;
        melees = new ArrayList<>(10);
    }

    /**
     * Do the smash attack.
     *
     * @param game
     */
    private void attackSmash(Game game) {
        setDx(0);
        changeToState(ATTACK_SMASH);
        addSound("attack1", ATTACK_SMASH, 0);
        t = 0;
        final AttackMelee melee = new DRoySmash(this).create();
        game.add(melee);
        melees.add(melee);
    }

    /**
     * Do the laser attack.
     *
     * @param game
     */
    private void attackLaser(Game game) {
        setDx(0);
        changeToState(ATTACK_LASER);
        addSound("attack2", ATTACK_LASER, 0);
        t = 0;
        final AttackMelee melee = new DRoyLaser(this).create();
        game.add(melee);
        melees.add(melee);
    }

    /**
     * Update its behavior.
     *
     * @param dt
     */
    @Override
    public void updateBehavior(int dt) {
        Game game = getGame();
        if (plr == null) {
            plr = game.getPlayer();
        }
        t += dt;
        int state = getState();
        if (!isDead() && state != HIT) {
            if (state != ATTACK_SMASH && state != ATTACK_LASER) {
                boolean playerFound = playerFinder.scan();
                boolean inSmashRange = smashScanner.scan();
                boolean inLaserRange = laserScanner.scan();
                final Vector p = plr.getMidpoint();
                final Vector r = getMidpoint();
                Direction dirX = Direction.xDirOf(p.getX() - r.getX());
                setDirX(dirX);
                if (dirX != edgeDir) {
                    edgeDir = Direction.NONE;
                    if (playerFound) {
                        if (inSmashRange) {
                            attackSmash(game);
                        } else if (inLaserRange) {
                            attackLaser(game);
                        } else {
                            setDx(getSpeed() * dirX.unit());
                            changeToState(MOVE);
                        }
                    } else {
                        setDx(0);
                        changeToState(STAND);
                    }
                } else if (inSmashRange) {
                    attackSmash(game);
                } else if (inLaserRange) {
                    attackLaser(game);
                } else {
                    changeToState(STAND);
                    setDx(0);
                }
            } else {
                if (t >= getStateDelay(ATTACK_SMASH)) {
                    t = 0;
                    changeToState(STAND);
                }
                if (t >= getStateDelay(ATTACK_LASER)) {
                    t = 0;
                    changeToState(STAND);
                }
            }
        } else if (state == HIT && getCount() >= getHitTime()) {
            setDx(0);
            setDdx(0);
            setDirX(getNextDir());
            changeToState(STAND);
        }
    }

    @Override
    public void knockback() {
        final Game game = getGame();
        for (final AttackMelee melee : melees) {
            game.remove(melee);
        }
        melees.clear();
        removeSound("attack1");
        removeSound("attack2");
    }

    /**
     * Resolve collisions with platforms and get the edge direction.
     */
    @Override
    public void doLogic() {
        for (Platform platform : platforms) {
            Rect tile = platform.getHitbox();
            Direction dir = getCollideDir(tile);
            Direction dirX = getDirX();
            Direction dirY = getDirY();
            double x = getX();
            double y = getY();
            double dx = getDx();
            double dy = getDy();
            Rect box = getHitbox();
            SpriteSheet ss = getSprites().get(HITBOX);
            if (dir == LEFT && dirX == RIGHT) {
                x = tile.getX() - box.getWidth() - ss.getOffsetX();
            }
            if (dir == RIGHT && dirX == LEFT) {
                x = tile.getX() + tile.getWidth() - ss.getOffsetX();
            }
            if (dir == UP && dirY == DOWN) {
                y = tile.getY() - box.getHeight() - ss.getOffsetY();
                dy = 0;
            }
            if (dir == DOWN && dirY == UP) {
                y = tile.getY() + box.getHeight() - ss.getOffsetY();
                dy = 0;
            }
            setX(x);
            setY(y);
            setDx(dx);
            setDy(dy);
            setDirX(dirX);
            setDirY(dirY);
        }
        Direction dir = edgeDirection(getPlatforms(platforms));
        edgeDir = dir == Direction.NONE ? edgeDir : dir;
    }

    /**
     * Return the hitbox.
     *
     * @return
     */
    @Override
    public Rect getHitbox() {
        SpriteSheet ss = getSprites().get(HITBOX);
        final double bx = getX() + ss.getOffsetX();
        final double by = getY() + ss.getOffsetY();
        final double bw = ss.getWidth();
        final double bh = ss.getHeight();
        return new Rect(bx, by, bw, bh);
    }

    @Override
    public Bound getDamage() {
        return new Bound(100, 150);
    }

    @Override
    public Bound getKnockback() {
        return new Bound(650, 750);
    }

    @Override
    public Sound getHitSound() {
        return getSounds().get(getSoundNumber("damage"));
    }

    @Override
    public boolean isBoss() {
        return boss;
    }

    @Override
    public void draw(Graphics g) {
        if (getGame().debugging()) {
            playerFinder.drawArea(g);
            smashScanner.drawArea(g);
            laserScanner.drawArea(g);
        }
        super.draw(g);
    }

    /**
     * Draw the DRoy with hitbox-centered reflection.
     *
     * @param g
     */
    @Override
    public void drawEntity(Graphics g) {
        SpriteSheet box = getSprites().get(HITBOX);
        SpriteSheet ss = getSprites().get(getState());
        Sprite s = ss.getSprite();
        double sx;
        double sy = getY() + ss.getOffsetY();
        if (getDirX() == DEFAULT_DIR) {
            sx = getX() + ss.getOffsetX();
        } else {
            sx = getX() + 2 * box.getOffsetX() + box.getWidth() - ss.getOffsetX() - ss.getWidth();
            s = s.flipHorizontal();
        }
        s.draw((float) sx, (float) sy);
    }

    @Override
    public SpriteSheet getHitboxSheet() {
        return getSprites().get(HITBOX);
    }

    @Parsable
    public void boss(String b) {
        setBoss(Boolean.parseBoolean(b));
    }

    public void setBoss(boolean boss) {
        this.boss = boss;
    }

    /**
     * The class for the DRoy's smash attack. It is a melee attack. The
     * reason for which the public constructors exist is because, to the
     * best of my knowledge, java reflection can only use publicly-declared
     * constructors when dynamically instantiating objects.
     */
    @SuppressWarnings("PublicConstructorInNonPublicClass")
    private static class DRoySmash extends AttackMelee {

        public DRoySmash(Attacker attacker) {
            this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
        }

        public DRoySmash(Game game, double x, double y, Direction dir, Attacker attacker) {
            super(game, x, y, dir, attacker, "effect", new int[]{1}, Player.class);
            setDamage(new Bound(600, 650));
            setKnockback(new Bound(1500, 1700));
            addSound("hit", HIT[0], 0);
        }

        @Override
        public int getHitType() {
            return HIT[0];
        }

    }

    /**
     * DRoy's laser attack, which is a melee attack in concept.
     *
     * @see DRoySmash
     */
    @SuppressWarnings("PublicConstructorInNonPublicClass")
    private static class DRoyLaser extends AttackMelee {

        public DRoyLaser(Attacker attacker) {
            this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
        }

        public DRoyLaser(Game game, double x, double y, Direction dir, Attacker attacker) {
            super(game, x, y, dir, attacker, "effect", new int[]{2, 3, 4}, Player.class);
            setDamage(new Bound(450, 550));
            setKnockback(new Bound(700, 800));
            addSound("hit", HIT[0], 0);
        }

        @Override
        public int getHitType() {
            return HIT[0];
        }

    }

    /**
     * ScannerDprc that locates the player.
     */
    private class PlayerFinder implements Scanner {

        @Override
        public ScannerImpl<Player, Platform> getScanner() {
            return new ScannerImpl<>(Player.class, Platform.class);
        }

        @Override
        public List<Object> getScanObjects() {
            return getGame().getObjects();
        }

        @Override
        public Rect getScanArea() {
            final Rect r = getHitbox();
            final double sy = r.getY() - 600.0;
            final double sw = 1400.0;
            final double sh = 900.0;
            double sx = r.getX() + r.getWidth() / 2.0;
            if (getScanDir() == Direction.LEFT) {
                sx -= sw;
            }
            return new Rect(sx, sy, sw, sh);
        }

        @Override
        public float getScanMargin() {
            return 0.75f;
        }

        @Override
        public Direction getScanDir() {
            return getDirX();
        }

    }

    /**
     * ScannerDprc for the smash attack range. Extends player finder to
     * save space.
     *
     * @see PlayerFinder
     */
    private class SmashScanner extends PlayerFinder {

        @Override
        public Rect getScanArea() {
            final Rect r = getHitbox();
            final double sy = r.getY() - 15.0;
            final double sw = 123.0;
            final double sh = 140.0;
            double sx = r.getX() + r.getWidth() / 2.0;
            if (getScanDir() == Direction.LEFT) {
                sx -= sw;
            }
            return new Rect(sx, sy, sw, sh);
        }

    }

    /**
     * ScannerDprc for the laser attack range.
     *
     * @see PlayerFinder
     */
    private class LaserScanner extends PlayerFinder {

        @Override
        public Rect getScanArea() {
            final Rect r = getHitbox();
            final double sy = r.getY();
            final double sw = 380.0;
            final double sh = 120.0;
            double sx = r.getX() + r.getWidth() / 2.0;
            if (getScanDir() == Direction.LEFT) {
                sx -= sw;
            }
            return new Rect(sx, sy, sw, sh);
        }

    }

}
