package kawaiiklash;

/**
 * A boss is simply any enemy that must be defeated to complete the level.
 *
 * CONSIDER: adding a method that gets the health of the boss for the
 * potential future implementation of a boss health meter.
 *
 * @author Jeff Niu
 */
public interface Boss {

    /**
     * Sometimes a "boss" enemy can just be a normal enemy.
     *
     * @return
     */
    boolean isBoss();

    /**
     * Whether or not the boss is dead. Used to determine whether the
     * portal to exit the level appears.
     *
     * @return
     */
    boolean isDead();

}
