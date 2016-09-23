package kawaiiklash;

/**
 * @see Mage
 * @author Jeff Niu
 */
public class FlameOrb extends AttackRange {

    private static final int[] EFFECT_BALL_FRAME = {4};

    public FlameOrb(Attacker attacker) {
        this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
    }

    public FlameOrb(Game game, double x, double y, Direction dir, Attacker attacker) {
        super(game, x, y, dir, attacker, EFFECT_BALL_FRAME, Monster.class);
        setDamage(new Bound(45, 60));
        setKnockback(new Bound(60, 65));
        addSound("use", EFFECT, 0);
        addSound("hit", HIT[0], 0);
    }

    @Override
    public double getBallDx() {
        return 600.0;
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
        return true;
    }

    @Override
    public boolean fireFromEntity() {
        return false;
    }

}
