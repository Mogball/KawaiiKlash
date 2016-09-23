package kawaiiklash;

import org.newdawn.slick.Graphics;

/**
 * A {@code BackgroundLayer} is a type of repeating {@code Background} in
 * the game. The repeating segment, or period, may be such that it appears
 * to give a continuous image, or it may be such that a background object
 * will repeat indefinitely. This class intends to give the user the
 * feeling that the background goes on indefinitely.
 *
 * @author Jeff Niu
 */
public abstract class BackgroundLayer extends Background implements Cartesian {

    /**
     * The horizontal position.
     */
    private double x;

    /**
     * The vertical position.
     */
    private double y;

    /**
     * To give the impression of perspective, the {@code BackgroundLayer}
     * will have a factor that reduces its movement when the screen
     * scrolls. Naturally, {@code BackgroundLayer}s that are further back
     * will have a smaller value, so they scroll less.
     */
    private double scroll;

    /**
     * The repetition period of the {@code BackgroundLayer}. For continuous
     * images, the period will be equal to the image width.
     */
    private int period;

    /**
     * This value controls the image scaling, so that the background size
     * can be altered.
     */
    private float scale;

    private int upshift;

    /**
     * Create the {@code BackgroundLayer}.
     *
     * @param game the {@code Game} to which it belongs
     * @param scroll the default scroll factor
     */
    public BackgroundLayer(Game game, double scroll) {
        super(game);
        this.scroll = scroll;
        SpriteSheet ss = getSprites().get(0);
        x = 0;
        y = game.getScreen().getHeight();
        period = ss.getOffsetX();
        scale = 1.0f;
        upshift = getSprites().get(0).getOffsetY();
    }

    /**
     * An overridable method that retrieves the {@code Sprite} representing
     * the background.
     *
     * @return
     */
    public Sprite getSprite() {
        return getSprites().get(0).get(0);
    }

    /**
     * Draw the background layer.
     *
     * @param g the {@code Graphics} to which to draw
     */
    @Override
    public void draw(Graphics g) {
        // Normalize the horizontal position so that the later loop is kept
        // at minimum length
        x %= period * scale;
        // Ensure that the horizontal position is negative for graphical
        // fidelity when drawing in the loop
        while (x > 0) {
            x -= period * scale;
        }
        // Draw the background according to the period, within the screen
        for (int i = 0; i + x < getGame().getScreen().getWidth(); i += period * scale) {
            getSprite().draw((float) x + i, (float) y + getUpshift() * scale, scale);
        }
    }

    /**
     * An overridable method to get the upshift of the background. The
     * default upshift value will the vertical offset.
     *
     * @return
     */
    public int getUpshift() {
        return upshift;
    }

    /**
     * Scroll horizontally: call to super method with a reduced scroll
     * value, according to the scroll factor.
     *
     * @param dx
     */
    @Override
    public void moveX(double dx) {
        Cartesian.super.moveX(dx * scroll);
    }

    /**
     * Scroll verticaLly.
     *
     * @see #moveX(double)
     * @param dy
     */
    @Override
    public void moveY(double dy) {
        Cartesian.super.moveY(dy * scroll);
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
    public Direction getDirX() {
        return null;
    }

    @Override
    public Direction getDirY() {
        return null;
    }

    @Override
    public double getOffsetX() {
        return 0.0;
    }

    @Override
    public double getOffsetY() {
        return 0.0;
    }

    @Override
    public double getWidth() {
        return getSprite().getWidth();
    }

    @Override
    public double getHeight() {
        return getSprite().getHeight();
    }

    @Parsable
    public void period(String p) {
        period = Integer.parseInt(p);
    }

    @Parsable
    public void scroll(String s) {
        scroll = Double.parseDouble(s);
    }

    @Parsable
    public void scale(String s) {
        scale = Float.parseFloat(s);
    }

    @Parsable
    public void upshift(String u) {
        upshift = Integer.parseInt(u);
    }

}
