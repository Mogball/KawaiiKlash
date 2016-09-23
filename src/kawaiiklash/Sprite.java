package kawaiiklash;

import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * A sprite describes anything that can be drawn on the screen with a
 * position. Prior to Slick2D, it was necessary because of the limited
 * functionality of java images, but now it is more of a wrapper class for
 * the Slick2D image. Anyway, this interface was created to help transition
 * from the old engine to an actual engine.
 *
 * @see SpriteImpl
 * @author Jeff Niu
 */
public interface Sprite {

    int getWidth();

    int getHeight();

    @Deprecated
    void draw(Graphics g, int x, int y, Color filter);

    @Deprecated
    default void draw(Graphics g, int x, int y) {
        draw(x, y);
        //draw(g, x, y, getFilter());
    }

    default void draw(float x, float y) {
        draw(x, y, getFilter());
    }

    default void draw(float x, float y, Color filter) {
        draw(x, y, getWidth(), getHeight(), filter);
    }

    default void draw(float x, float y, float scale) {
        draw(x, y, scale, getFilter());
    }

    default void draw(float x, float y, float scale, Color filter) {
        draw(x, y, getWidth() * scale, getHeight() * scale, filter);
    }

    default void draw(float x, float y, float width, float height) {
        draw(x, y, width, height, getFilter());
    }

    default void draw(float x, float y, float width, float height, Color filter) {
        getImage().draw(x, y, width, height, filter);
    }

    Sprite flipHorizontal();

    Sprite flipVertical();

    Image getImage();

    Color getFilter();

    void setFilter(Color filter);

}
