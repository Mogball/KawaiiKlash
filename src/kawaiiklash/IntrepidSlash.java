package kawaiiklash;

/**
 * @see Hero
 * @author Jeff Niu
 */
public class IntrepidSlash extends AttackMelee {

    public IntrepidSlash(Attacker attacker) {
        this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
    }

    public IntrepidSlash(Game game, double x, double y, Direction dir, Attacker attacker) {
        super(game, x, y, dir, attacker, "effect", new int[]{4, 7, 9}, Monster.class);
        setDamage(new Bound(40, 50));
        setKnockback(new Bound(25, 32));
        addSound("use", EFFECT, 0);
        addSound("hit", HIT, 0);
    }

    @Override
    public int getHitType() {
        return HIT[isDeadly()];
    }

}
