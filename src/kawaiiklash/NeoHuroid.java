package kawaiiklash;

import java.util.List;
import static kawaiiklash.Direction.DOWN;
import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Direction.RIGHT;
import static kawaiiklash.Direction.UP;
import org.newdawn.slick.Graphics;

/**
 * This monster will locate the locate and move towards it without walking
 * off platforms. When the player is in its attack range, it will fire a
 * rocket at the player's position.
 *
 * @author Jeff Niu
 */
public class NeoHuroid extends Monster implements Boss, PlatformWalker, Attacker {

    private final int ATTACK_ROCKET = getStateNumber("attack");
    private final int HITBOX = getStateNumber("naked");

    private final Scanner playerFinder = new PlayerFinder();
    private final Scanner rocketScanner = new RocketScanner();

    private Player plr;
    private int t;
    private boolean boss;
    private boolean fired;
    private Direction edgeDir;

    public NeoHuroid(Game game) {
        super(game);

        setHealth(200);
        setSpeed(150);
        setDefense(30);
        setStance(0.60);
        setAttack(60.0);
        setBreach(0.40);

        setState(STAND);

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
        fired = false;
        edgeDir = Direction.NONE;
    }

    @Override
    public void updateBehavior(int dt) {
        if (plr == null) {
            plr = getGame().getPlayer();
        }
        t += dt;
        if (!isDead() && getState() != HIT) {
            if (getState() != ATTACK_ROCKET) {
                boolean playerFound = playerFinder.scan();
                boolean inRocketRange = rocketScanner.scan();
                final Vector p = plr.getMidpoint();
                final Vector r = getMidpoint();
                Direction dirX = Direction.xDirOf(p.getX() - r.getX());
                setDirX(dirX);
                if (dirX != edgeDir) {
                    edgeDir = Direction.NONE;
                    if (playerFound) {
                        if (inRocketRange) {
                            setDx(0);
                            changeToState(ATTACK_ROCKET);
                            addSound("attack", ATTACK_ROCKET, 0);
                            t = 0;
                        } else {
                            setDx(getSpeed() * dirX.unit());
                            changeToState(MOVE);
                        }
                    } else {
                        setDx(0);
                        changeToState(STAND);
                    }
                } else if (inRocketRange) {
                    setDx(0);
                    changeToState(ATTACK_ROCKET);
                    addSound("attack", ATTACK_ROCKET, 1);
                    t = 0;
                } else {
                    changeToState(STAND);
                    setDx(0);
                }
            } else {
                if (t >= getStateDelay(ATTACK_ROCKET)) {
                    t = 0;
                    changeToState(STAND);
                }
                if (!fired && getStateFrame(ATTACK_ROCKET) == 7) {
                    getGame().add(new NeoHuroidRocket(plr, this).create());
                    fired = true;
                }
                if (getStateFrame(ATTACK_ROCKET) == 8) {
                    fired = false;
                }
            }
        } else if (getState() == HIT && getCount() >= getHitTime()) {
            setDx(0);
            setDdx(0);
            setDirX(getNextDir());
            changeToState(STAND);
        }
    }

    @Override
    public void doLogic() {
        for (final Platform platform : platforms) {
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

    @Override
    public Rect getHitbox() {
        final SpriteSheet ss = getSprites().get(HITBOX);
        final double bx = getX() + ss.getOffsetX();
        final double by = getY() + ss.getOffsetY();
        final double bw = ss.getWidth();
        final double bh = ss.getHeight();
        return new Rect(bx, by, bw, bh);
    }

    @Override
    public Bound getDamage() {
        return new Bound(200, 300);
    }

    @Override
    public Bound getKnockback() {
        return new Bound(700, 1000);
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
            rocketScanner.drawArea(g);
        }
        super.draw(g);
    }

    @Override
    protected void drawEntity(Graphics g) {
        SpriteSheet box = getSprites().get(HITBOX);
        SpriteSheet ss = getSprites().get(getState());
        Sprite s = ss.getSprite();
        double x;
        double y = getY() + ss.getOffsetY();
        if (getDirX() == Direction.LEFT) {
            x = getX() + ss.getOffsetX();
        } else {
            x = getX() + 2 * box.getOffsetX() + box.getWidth() - ss.getOffsetX() - ss.getWidth();
            s = s.flipHorizontal();
        }
        s.draw((float) x, (float) y);
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

    @SuppressWarnings("PublicConstructorInNonPublicClass")
    private static class NeoHuroidRocket extends AttackRange {

        private static final double SPEED = 600;
        private final double dx;
        private final double dy;

        public NeoHuroidRocket(Attacker attacker) {
            this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
        }

        public NeoHuroidRocket(Game game, double x, double y, Direction dir, Attacker attacker) {
            super(game, x, y, dir, attacker, new int[]{0}, Player.class);
            setDamage(new Bound(400, 600));
            setKnockback(new Bound(800, 1000));
            addSound("hit", HIT[0], 0);
            dx = 0;
            dy = 0;
        }

        private NeoHuroidRocket(Player plr, final NeoHuroid h) {
            super(h.getGame(), h.getX(), h.getY() - 40, h.getDirX(), h, new int[]{0}, Player.class);
            setDamage(new Bound(400, 600));
            setKnockback(new Bound(1200, 1300));
            addSound("hit", HIT[0], 0);
            Vector p = plr.getMidpoint();
            Vector t = getMidpoint();
            double x = p.getX() - t.getX();
            double y = p.getY() - t.getY();
            double theta = Math.atan(Math.abs(y / x));
            dx = SPEED * Math.cos(theta);
            double dyTemp = SPEED * Math.sin(theta) * Direction.sgn(y);
            dy = Math.round(dyTemp / 10) * 10;
        }

        @Override
        public AttackRange create() {
            setState(BALL);
            return this;
        }

        @Override
        public int getFadeTime() {
            return 200;
        }

        @Override
        public int getHitType() {
            return 0;
        }

        @Override
        public boolean pierceMonster() {
            return false;
        }

        @Override
        public boolean fireFromEntity() {
            return true;
        }

        @Override
        public double getBallDx() {
            return dx;
        }

        @Override
        public double getBallDy() {
            return dy;
        }

    }

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
            final double sw = 1000.0;
            final double sh = 900.0;
            double sx = r.getX() + r.getWidth() / 2;
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

    private class RocketScanner extends PlayerFinder {

        @Override
        public Rect getScanArea() {
            final Rect r = getHitbox();
            final double sy = r.getY() - 400.0;
            final double sw = 600.0;
            final double sh = 650.0;
            double sx = r.getX() + r.getWidth() / 2;
            if (getScanDir() == Direction.LEFT) {
                sx -= sw;
            }
            return new Rect(sx, sy, sw, sh);
        }

    }

}
