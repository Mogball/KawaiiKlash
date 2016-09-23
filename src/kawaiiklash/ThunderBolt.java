package kawaiiklash;

import static kawaiiklash.Utility.randInt;

/**
 *
 * @author Jeff Niu
 */
public class ThunderBolt extends AttackMelee {

    public ThunderBolt(Attacker attacker) {
        this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
    }

    public ThunderBolt(Game game, double x, double y, Direction dir, Attacker attacker) {
        super(game, x, y, dir, attacker, "effect", new int[]{5}, Monster.class);
        setDamage(new Bound(35, 50));
        setKnockback(new Bound(20, 45));
        addSound("use", EFFECT, 0);
        addSound("hit", HIT, 0);
    }

    @Override
    public int getHitType() {
        return HIT[randInt(0, HIT.length - 1)];
    }

    @Override
    public double getHitY(AttackTarget m) {
        return m.getY() + m.getOffsetY() + m.getHeight();
    }

}
