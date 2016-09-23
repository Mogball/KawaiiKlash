package kawaiiklash;

import java.util.List;
import org.newdawn.slick.Graphics;

/**
 *
 * @author Jeff Niu
 */
public class StoneGolem extends Walker implements PlatformWalker {

    private final int SKILL = getStateNumber("skill1");
    private final int HITBOX = getStateNumber("naked");
    private final Scanner scanner = new StoneGolemScanner();

    private Direction edgeDir;
    private boolean active;
    private Player plr;

    public StoneGolem(Game game) {
        super(game);

        // Initialize the stats
        setHealth(700.0);
        setSpeed(70.0);
        setDefense(70.0);
        setStance(1.00);
        setAttack(70.0);
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

        addSound("die", DIE, 0);

        edgeDir = Direction.NONE;
        active = false;
    }

    @Override
    public Bound getDamage() {
        return new Bound(250, 350);
    }

    @Override
    public Bound getKnockback() {
        return new Bound(1000, 1100);
    }

    @Override
    public void updateBehavior(int dt) {
        if (plr == null) {
            plr = getGame().getPlayer();
        }
        final int state = getState();
        if (state == HIT && getCount() >= getHitTime()) {
            final double dx = getSpeed() * getNextDir().unit();
            setDx(dx);
            setDdx(0);
            setDirX(getNextDir());
            changeToState(MOVE);
            active = true;
        }
        if (state != HIT && state != DIE) {
            if (!active) {
                final boolean located = scanner.scan();
                if (located) {
                    changeToState(SKILL);
                    cycleFrames(SKILL, false);
                    active = located;
                    setCount(0);
                }
            } else {
                if (state == SKILL && getCount() >= getStateDelay(SKILL)) {
                    changeToState(MOVE);
                    setDx(getSpeed() * getDirX().unit());
                }
                if (state == MOVE || state == STAND) {
                    final Vector p = plr.getMidpoint();
                    final Vector r = getMidpoint();
                    final Direction dirX = Direction.xDirOf(p.getX() - r.getX());
                    setDirX(dirX);
                    if (getDirX() != edgeDir) {
                        edgeDir = Direction.NONE;
                        final boolean located = scanner.scan();
                        if (located) {
                            changeToState(MOVE);
                            setDx(getSpeed() * getDirX().unit());
                        } else {
                            changeToState(STAND);
                            setDx(0);
                        }
                    } else {
                        changeToState(STAND);
                        setDx(0);
                    }
                }
            }
        }
    }

    @Override
    public void takeDamage(Attacker attacker, Attack attack) {
        super.takeDamage(attacker, attack);
        if (!active) {
            changeToState(SKILL);
            cycleFrames(SKILL, false);
            active = true;
            setCount(0);
        }
    }

    @Override
    public void doLogic() {
        super.doLogic();
        Direction dir = edgeDirection(getPlatforms(platforms));
        edgeDir = dir == Direction.NONE ? edgeDir : dir;
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
    public double getOffsetX() {
        return getSprites().get(HITBOX).getOffsetX(0);
    }
    
    @Override
    public double getOffsetY() {
        return getSprites().get(HITBOX).getOffsetY();
    }
    
    private class StoneGolemScanner implements Scanner {

        @Override
        public ScannerImpl<?, ?> getScanner() {
            return new ScannerImpl<>(Player.class, Platform.class);
        }

        @Override
        public List<Object> getScanObjects() {
            return getGame().getObjects();
        }

        @Override
        public Rect getScanArea() {
            final Rect r = getHitbox();
            double sx = r.x;
            double sy = r.y;
            double sw = 500;
            double sh = r.height;
            if (active) {
                sy -= 200;
                sh += 400;
                sw += 200;
            }
            if (getScanDir() == Direction.LEFT) {
                sx += r.width - sw;
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

}
