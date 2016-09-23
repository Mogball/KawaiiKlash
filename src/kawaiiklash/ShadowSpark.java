package kawaiiklash;

/**
 *
 * @author Jeff Niu
 */
public class ShadowSpark extends AttackRange implements Logicable {

    private static final int[] EFFECT_BALL_FRAME = {8};

    public ShadowSpark(Attacker attacker) {
        this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
    }

    public ShadowSpark(Game game, double x, double y, Direction dir, Attacker attacker) {
        super(game, x, y, dir, attacker, EFFECT_BALL_FRAME, Monster.class);
        setDamage(new Bound(45, 65));
        setKnockback(new Bound(250, 280));
        addSound("use", EFFECT, 0);
        addSound("hit", HIT[0], 0);
    }

    @Override
    public double getBallDx() {
        return 1400.0;
    }

    @Override
    public int getFadeTime() {
        return 50;
    }

    @Override
    public int getHitType() {
        return 0;
    }

    @Override
    public boolean pierceMonster() {
        return true;
    }

    @Override
    public boolean fireFromEntity() {
        return true;
    }
}
