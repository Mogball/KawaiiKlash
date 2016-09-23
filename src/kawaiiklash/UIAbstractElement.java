package kawaiiklash;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

/**
 * This class describes any element that can be added to a user interface.
 *
 * @author Jeff Niu
 */
public abstract class UIAbstractElement {

    private double x;
    private double y;

    public UIAbstractElement(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public abstract void update(GameContainer gc, int dt);

    public void draw(GameContainer gc, Graphics g) {
        getSprite().draw((float) getX(), (float) getY());
    }

    public abstract Sprite getSprite();

    public abstract double getWidth();

    public abstract double getHeight();

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }
}
