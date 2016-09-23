package kawaiiklash;

/**
 * An attacker is something that can do an attack.
 *
 * @author Jeff Niu
 */
public interface Attacker extends Interactable {

    /**
     * Get the sprite sheet that represents the hitbox. Though sometimes it
     * just returns some other sheet if the hitbox sheet corresponds with
     * another.
     *
     * @return
     */
    SpriteSheet getHitboxSheet();

    /**
     * Get the attack stat.
     *
     * @return
     */
    double getAttack();

    /**
     * Get the game to which it belongs.
     *
     * @return
     */
    Game getGame();

    /**
     * A default method for getting the hitbox, but it is oftentimes
     * overridden.
     *
     * @return
     */
    @Override
    default Rect getHitbox() {
        double x = getX() + getOffsetX();
        double y = getY() + getOffsetY();
        double w = getWidth();
        double h = getHeight();
        return new Rect(x, y, w, h);
    }

}
