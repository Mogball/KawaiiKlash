package kawaiiklash;

/**
 * An {@code Interactable} is any {@code Object} that is both
 * {@code Cartesian} and {@code Collideable}.
 *
 * @author Jeff Niu
 */
public interface Interactable extends Cartesian, Collideable {

    @Override
    default Vector getMidpoint() {
        Rect r = getHitbox();
        double x = r.x + r.width * 0.5;
        double y = r.y + r.height * 0.5;
        return new Vector(x, y);
    }

}
