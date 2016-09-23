package kawaiiklash;

import java.awt.Point;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Input;

/**
 * A button can be clicked to do something.
 *
 * @author Jeff Niu
 */
public abstract class UIAbstractButton extends UIAbstractElement {

    private boolean clicked;

    public UIAbstractButton(double x, double y) {
        super(x, y);
        clicked = false;
    }

    public Rect getBox() {
        return new Rect(getX(), getY(), getWidth(), getHeight());
    }

    @Override
    public void update(GameContainer gc, int dt) {
        Input input = gc.getInput();
        int mx = input.getMouseX();
        int my = input.getMouseY();
        Point mouse = new Point(mx, my);
        if (getBox().contains(mouse)) {
            if (input.isMouseButtonDown(Input.MOUSE_LEFT_BUTTON)) {
                mousePressed();
                clicked = true;
            } else {
                mouseOver();
                if (clicked) {
                    mouseClicked();
                }
                clicked = false;
            }
        } else {
            mouseNotOver();
            clicked = false;
        }
        updateState(dt);
    }

    public abstract void updateState(int dt);

    public abstract void mouseNotOver();

    public abstract void mouseOver();

    public abstract void mousePressed();

    public abstract void mouseClicked();

}
