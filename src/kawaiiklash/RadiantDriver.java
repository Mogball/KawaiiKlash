package kawaiiklash;

/**
 * Radiant Driver attack for the Hero class.
 *
 * @author Jeff Niu
 */
public class RadiantDriver extends AttackMelee {

    /**
     * Creates a new RadiantDriver attack called for by a Hero.
     *
     * @param attacker 
     */
    public RadiantDriver(Attacker attacker) {
        this(attacker.getGame(), attacker.getX(), attacker.getY(), attacker.getDirX(), attacker);
    }

    /**
     * Creates a new RadiantDriver attack.
     *
     * @param game the Game to which the Player belongs
     * @param x the x-position
     * @param y the y-position
     * @param dir the direction of the Player
     * @param attacker
     */
    public RadiantDriver(Game game, double x, double y, Direction dir, Attacker attacker) {
        super(game, x, y, dir, attacker, "effect", new int[]{9}, Monster.class);
        setDamage(new Bound(30, 35));
        setKnockback(new Bound(480, 520));
        addSpecial(getStateNumber("special"), true, 8);
        addSound("use", EFFECT, 0);
        addSound("hit", HIT[0], 0);
    }

    @Override
    public int getHitType() {
        return HIT[0];
    }
}
