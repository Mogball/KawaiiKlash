package kawaiiklash;

/**
 * @see Mage
 * @author Jeff Niu
 */
public class HolyArrow extends AttackMelee implements Logicable {

    public HolyArrow(Attacker attacker) {
        this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
    }

    public HolyArrow(Game game, double x, double y, Direction dir, Attacker attacker) {
        super(game, x, y, dir, attacker, "effect", new int[]{5}, Monster.class);
        setDamage(new Bound(70, 90));
        setKnockback(new Bound(90, 95));
        addSound("use", EFFECT, 0);
        addSound("hit", HIT[0], 0);
    }

    @Override
    public int getHitType() {
        return HIT[0];
    }

}
