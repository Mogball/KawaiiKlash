package kawaiiklash;

/**
 *
 * @author Jeff Niu
 */
public class Shroom extends Walker {

    public Shroom(Game game) {
        super(game);
        setHealth(50.0);
        setSpeed(250.0);
        setDefense(0.0);
        setStance(0.0);
        setAttack(0.0);
        setBreach(0.0);

        setState(MOVE);

        setDx(getSpeed() * getDirX().unit());
        setDy(0);
        setDdx(0);
        setDdy(GRAVITY);
        setMaxDx(UNLIMITED);
        setMaxDy(GRAVITY);

        addSound("die", DIE, 0);
    }

    @Override
    public Bound getDamage() {
        return new Bound(200, 250);
    }

    @Override
    public Bound getKnockback() {
        return new Bound(600, 700);
    }

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
    }

    @Override
    public Sound getHitSound() {
        return getSounds().get(getSoundNumber("damage"));
    }

}
