package kawaiiklash;

import java.util.ArrayList;
import java.util.List;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * The user interface is something through which the user can interact with
 * the game. Used in game menus.
 *
 * @author Jeff Niu
 */
public class UI {

    private final List<UIAbstractElement> elements;
    private final List<UIAbstractElement> add;
    private final List<UIAbstractElement> remove;

    public UI() {
        elements = new ArrayList<>(0);
        add = new ArrayList<>(0);
        remove = new ArrayList<>(0);
    }

    public void addElement(UIAbstractElement element) {
        add.add(element);
    }

    public void removeElement(UIAbstractElement element) {
        remove.add(element);
    }

    public void update(GameContainer gc, int dt) {
        elements.addAll(add);
        elements.removeAll(remove);
        add.clear();
        remove.clear();
        for (UIAbstractElement element : elements) {
            element.update(gc, dt);
        }
    }

    public void draw(GameContainer gc, Graphics g) {
        for (UIAbstractElement element : elements) {
            element.draw(gc, g);
        }
    }

}
