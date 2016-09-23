package kawaiiklash;

import org.newdawn.slick.Color;

/**
 * Used for refactoring from old ad-hoc engine to new Slick2D engine.
 * 
 * @see SpriteSheetImpl
 * @author Jeff Niu
 */
public interface SpriteSheet extends Updateable {

    int getFrame();

    int getCount();

    int getWidth(int frame);

    int getHeight(int frame);

    int getDelay(int frame);

    int getOffsetX(int frame);

    int getOffsetY(int frame);

    void setFrame(int frame);

    void setCount(int count);

    int getTotalDelay();

    void cycleFrames(boolean cycle);

    String getName();

    Sprite get(int frame);

    SpriteSheet copy();

    void setFilter(Color filter);
    
    default int getWidth() {
        return getWidth(getFrame());
    }

    default int getHeight() {
        return getHeight(getFrame());
    }

    default int getDelay() {
        return getDelay(getFrame());
    }

    default int getOffsetX() {
        return getOffsetX(getFrame());
    }

    default int getOffsetY() {
        return getOffsetY(getFrame());
    }

    default Sprite getSprite() {
        return get(getFrame());
    }

}
