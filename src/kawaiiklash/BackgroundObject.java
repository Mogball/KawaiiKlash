package kawaiiklash;

import org.newdawn.slick.Graphics;

/**
 * Like a background layer, except it does not recur periodically; it has a
 * unique position.
 *
 * @author Jeff Niu
 */
public abstract class BackgroundObject extends Background implements Cartesian {

    private double x;
    private double y;
    private float scale;

    public BackgroundObject(Game game) {
        super(game);
        scale = 1.0f;
    }

    public abstract Sprite getSprite();

    @Override
    public void draw(Graphics g) {
        float tx = (float) (getX() + getOffsetX());
        float ty = (float) (getY() + getOffsetY());
        getSprite().draw(tx, ty, scale);
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    @Override
    public double getWidth() {
        return getSprite().getWidth();
    }

    @Override
    public double getHeight() {
        return getSprite().getWidth();
    }

    @Parsable
    public void x(String x) {
        setX(Double.parseDouble(x));
    }

    @Parsable
    public void y(String y) {
        setY(Double.parseDouble(y));
    }

    @Parsable
    public void scale(String s) {
        scale = Float.parseFloat(s);
    }

}
