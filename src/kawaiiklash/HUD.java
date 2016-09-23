package kawaiiklash;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.Graphics;

/**
 * The heads up display is a graphical user interface used in the game.
 * Unlike the user interface, the heads up display cannot take user input.
 * It simply displays information to the user.
 *
 * CONSIDER: making the heads up display a user interface method
 *
 * @author Jeff Niu
 */
public class HUD implements Updateable, Drawable {

    /**
     * The game to which it belongs.
     */
    private final Game game;

    /**
     * The list of all the heads up display elements.
     */
    private final List<HUDAbstractElement> elements;
    private final List<HUDAbstractElement> add;
    private final List<HUDAbstractElement> remove;

    /**
     * Create the heads up display.
     *
     * @param game
     */
    public HUD(Game game) {
        this.game = game;
        elements = new ArrayList<>(0);
        add = new ArrayList<>(0);
        remove = new ArrayList<>(0);
    }

    /**
     * Add an element to the heads up display.
     *
     * @param element
     */
    public void addElement(HUDAbstractElement element) {
        add.add(element);
    }

    /**
     * Remove an element from the heads up display.
     *
     * @param element
     */
    public void removeElement(HUDAbstractElement element) {
        remove.add(element);
    }

    /**
     * Update each of the elements.
     *
     * @param dt
     */
    @Override
    public void update(int dt) {
        elements.addAll(add);
        elements.removeAll(remove);
        add.clear();
        remove.clear();
        for (HUDAbstractElement element : elements) {
            element.update(dt);
        }
    }

    /**
     * Draw all the elements.
     *
     * @param g
     */
    @Override
    public void draw(Graphics g) {
        for (HUDAbstractElement element : elements) {
            element.draw(g);
        }
    }

    @Override
    public int getZ() {
        return Drawable.HUD;
    }
    
}
