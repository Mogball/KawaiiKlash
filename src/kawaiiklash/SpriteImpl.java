package kawaiiklash;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * The {@code Sprite} implementation.
 *
 * @author Jeff Niu
 */
public class SpriteImpl implements Sprite {

    /**
     * The image that represents this sprite.
     */
    private final Image img;

    /**
     * The color filter of the sprite.
     */
    private Color filter;

    /**
     * Create the sprite based on an image.
     *
     * @param img
     */
    public SpriteImpl(Image img) {
        this.img = img.copy();
        filter = Color.white;
    }

    /**
     * Get the width of the sprite.
     *
     * @return
     */
    @Override
    public int getWidth() {
        return img.getWidth();
    }

    /**
     * Get the height of the sprite.
     *
     * @return
     */
    @Override
    public int getHeight() {
        return img.getHeight();
    }

    /**
     * Draw the sprite to a graphics object with a color filter. Since the
     * Slick2D graphics object is really just a wrapper for the OpenGL API,
     * it turns out that drawing to a graphics object is much slower than
     * simply drawing the image directly with OpenGL. So this method is
     * obsolete.
     *
     * @param g
     * @param x
     * @param y
     * @param c
     * @deprecated it is slower to draw to a graphics object
     */
    @Override
    @Deprecated
    public void draw(Graphics g, int x, int y, Color c) {
        //g.drawImage(img, x, y, c);
        Sprite.super.draw(x, y, c);
    }

    /**
     * Return a version of the sprite that has been flipped horizontally.
     *
     * @return
     */
    @Override
    public Sprite flipHorizontal() {
        Sprite flipped = new SpriteImpl(img.getFlippedCopy(true, false));
        flipped.setFilter(filter);
        return flipped;
    }

    /**
     * Flip the sprite vertically and return a new instance.
     *
     * @return
     */
    @Override
    public Sprite flipVertical() {
        Sprite flipped = new SpriteImpl(img.getFlippedCopy(false, true));
        flipped.setFilter(filter);
        return flipped;
    }

    /**
     * Get the image that represents this sprite.
     *
     * @return
     */
    @Override
    public Image getImage() {
        return img;
    }

    /**
     * Set the color filter that should be used when drawing the sprite.
     *
     * @param filter
     */
    @Override
    public void setFilter(Color filter) {
        this.filter = filter;
    }

    /**
     * Get the current color filter that is being used to draw the sprite.
     *
     * @return
     */
    @Override
    public Color getFilter() {
        return filter;
    }

}
