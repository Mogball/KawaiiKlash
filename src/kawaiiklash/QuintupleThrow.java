package kawaiiklash;

/**
 *
 * @author Jeff Niu
 */
public class QuintupleThrow extends AttackRange {

    private static final int[] EFFECT_BALL_FRAME = {2, 3, 4, 5, 6};

    private int ball;

    public QuintupleThrow(Attacker attacker) {
        this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
    }

    public QuintupleThrow(Game game, double x, double y, Direction dir, Attacker attacker) {
        super(game, x, y, dir, attacker, EFFECT_BALL_FRAME, Monster.class);
        setDamage(new Bound(10, 15));
        setKnockback(new Bound(0, 0));
        addSound("use", EFFECT, 0);
        addSound("hit", HIT[0], 0);
        ball = 0;
    }

    @Override
    public double getBallY() {
        double by = super.getBallY();
        by += (5.0 / 2.0 - ball) * getSprites().get(BALL).getHeight() / 2.0;
        ball++;
        return by;
    }

    @Override
    public double getBallDx() {
        return 1000.0;
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
