package kawaiiklash;

import java.util.List;
import org.newdawn.slick.Graphics;

/**
 *
 * @author Jeff Niu
 */
public class BlueMushroom extends Jumper implements PlatformWalker, PlatformJumper {

    /**
     * The momentary delay before the blue mushroom jumps.
     */
    private final int PREPARE = getStateNumber("prepare");

    public BlueMushroom(Game game) {
        super(game);

        setHealth(200.0);
        setSpeed(200.0);
        setDefense(20.0);
        setStance(0.20);
        setAttack(60.0);
        setBreach(0.10);

        setState(MOVE);

        setDx(getSpeed() * getDirX().unit());
        setDy(0);
        setDdx(0);
        setDdy(GRAVITY);
        setMaxDx(UNLIMITED);
        setMaxDy(GRAVITY);

        setJumpHeight(360.0);
        setJumpLength(360.0);

        addSound("die", DIE, 0);
    }

    /**
     * After being hit, return to regular movement. If it has detected a
     * viable jump target, then prepare to jump. If preparations are over,
     * jump.
     *
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
            setCount(0);
        }
        if (getState() == MOVE && shouldJump()) {
            changeToState(PREPARE);
            setDx(0);
            setDdx(0);
            setDy(0);
            cycleFrames(JUMP, false);
            setCount(0);
        }
        if (getState() == PREPARE && getCount() >= getStateDelay(PREPARE)) {
            jump();
            setCount(0);
        }
    }

    @Override
    public void draw(Graphics g) {
        if (getGame().debugging()) {
            drawArcs(g);
        }
        super.draw(g);
    }

    /**
     * At the edge of a platform, turn around, to prevent walking off
     * platforms.
     */
    @Override
    public void doLogic() {
        super.doLogic();
        if (getState() == MOVE) {
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
    public void jump() {
        changeToState(JUMP);
        setY(getY() - 5);
        setDx(getJumpDx() * getDirX().unit());
        setDy(getJumpDy());
        setDdx(0);
        setDdy(GRAVITY);
    }

    @Override
    public void hitGround(Rect ground) {
        setDy(0);
        if (getState() == JUMP) {
            setDx(getSpeed() * getDirX().unit());
            setCount(0);
            changeToState(MOVE);
        }
    }

    @Override
    public void hitCeiling(Rect ceil) {
        setDy(0);
    }

    @Override
    public void hitWall(Rect wall) {
        setDirX(getDirX().inverse());
        setDx(-getDx());
    }

    @Override
    public Bound getDamage() {
        return new Bound(350, 450);
    }

    @Override
    public Bound getKnockback() {
        return new Bound(900, 1000);
    }

    @Override
    public Sound getHitSound() {
        return getSounds().get(getSoundNumber("damage"));
    }

    @Override
    public List<Platform> getPlatforms() {
        return platforms;
    }

    @Override
    public List<Object> getObjects() {
        return getGame().getObjects();
    }

}
