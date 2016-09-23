package kawaiiklash;

/**
 * An {@code AttackTarget} is exactly as the name suggests: something
 * (specifically, an {@code Interactable}) that can take damage from an
 * {@code Attack}.
 *
 * @author Jeff Niu
 */
public interface AttackTarget extends Interactable {

    /**
     * Whether or not the target can currently be damaged.
     *
     * @return true if vulnerable, false if invulnerable
     */
    boolean isDamageable();

    /**
     * Tell the target that it has taken damage from an {@code Attack}
     * created by an {@code Attacker}.
     *
     * @param attacker whatever created the attack
     * @param attack the attack from which it took damage
     */
    void takeDamage(Attacker attacker, Attack attack);

}
