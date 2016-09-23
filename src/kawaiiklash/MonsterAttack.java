package kawaiiklash;

/**
 * An awkward wrapper class to fit into the new system for handling
 * attacks. Instead of the {@code Player} taking damage directly from the
 * {@code Monster} itself, upon contact, that is, the player will create an
 * instance of this class based on that {@code Monster}, will can then fit
 * into its {@link AttackCible#takeDamage(kawaiiklash.Attacker, kawaiiklash.Attack)
 * takeDamage(Attacker, Attack)} method.
 *
 * @author Jeff Niu
 * @see kawaiiklash.Player#collidedWith(kawaiiklash.Collidable)
 * @see kawaiiklash.Player#takeDamage(kawaiiklash.Attacker,
 * kawaiiklash.Attack)
 */
public class MonsterAttack extends Attack {

    public MonsterAttack(Monster monster) {
        super(monster.getGame(), monster.getX(), monster.getY(), Direction.NONE, monster, "", new int[]{0}, Player.class);
        setDamage(monster.getDamage());
        setKnockback(monster.getKnockback());
        setBreach(monster.getBreach());
    }

    @Override
    public void hitTarget(AttackTarget m) {
        m.takeDamage(getAttacker(), this);
    }

    @Override
    public void isOutOfBounds(Direction dir) {
    }

    @Override
    public void isInBounds() {
    }

}
