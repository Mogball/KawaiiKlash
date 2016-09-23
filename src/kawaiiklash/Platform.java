package kawaiiklash;

/**
 * A platform describes any object on which an entity may stand. They make
 * up the physical parts of the game world. The methods described below are
 * the facilitate the implementation of moving platforms, but we`re holding
 * off on that until a better collision system is implemented.
 *
 * @author Jeff Niu
 */
public interface Platform extends Interactable {

    boolean isSticky();

    boolean isMoving();

    double getDx();

    double getDy();

}
