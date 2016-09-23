package kawaiiklash;

/**
 *
 * @author Jeff Niu
 */
public class QuadStar extends AttackRange implements Logicable {

    private static final int[] EFFECT_BALL_FRAME = {1, 2, 3, 4};

    public QuadStar(Attacker attacker) {
        this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
    }

    public QuadStar(Game game, double x, double y, Direction dir, Attacker attacker) {
        super(game, x, y, dir, attacker, EFFECT_BALL_FRAME, Monster.class);
        setDamage(new Bound(20, 25));
        setKnockback(new Bound(10, 12));
        addSound("use", EFFECT, 0);
        addSound("hit", HIT[0], 0);
    }
    
    @Override
    public double getBallDx() {
        return 800.0;
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
        return false;
    }

    @Override
    public boolean fireFromEntity() {
        return true;
    }
}
