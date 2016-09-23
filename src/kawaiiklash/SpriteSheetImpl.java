package kawaiiklash;

import static kawaiiklash.Utility.arraycopy;
import org.newdawn.slick.Color;
import org.newdawn.slick.Image;

/**
 * The sprite sheet is an object that contains various sprites that can be
 * used in an animation. The sprite sheet can be used simply as a list for
 * various images or it can be used to handle animations.
 *
 * @author Jeff Niu
 */
public class SpriteSheetImpl implements SpriteSheet {

    /**
     * The name of the sprite sheet.
     */
    private final String name;

    /**
     * All the images of the sprite sheet.
     */
    private final Image[] imgs;
    /**
     * All the sprites of the sprite sheet.
     */
    private final Sprite[] sprites;

    /**
     * The frame widths.
     */
    private final int[] widths;
    /**
     * The frame heights.
     */
    private final int[] heights;
    /**
     * The frame x-offsets.
     */
    private final int[] xOffsets;
    /**
     * The frame y-offsets.
     */
    private final int[] yOffsets;
    /**
     * The delay of each frame.
     */
    private final int[] delays;

    /**
     * The current frame.
     */
    private int frame;
    /**
     * An internal counter.
     */
    private int count;

    /**
     * Whether or not the animation should cycle itself.
     */
    private boolean cycle;

    /**
     * Create the sprite sheet.
     *
     * @param imgs
     * @param delays
     * @param xOffsets
     * @param yOffsets
     * @param name
     */
    public SpriteSheetImpl(Image[] imgs, int[] delays, int[] xOffsets, int[] yOffsets, String name) {
        this.name = name;
        this.imgs = imgs;
        this.xOffsets = xOffsets;
        this.yOffsets = yOffsets;
        this.delays = delays;
        sprites = new Sprite[imgs.length];
        widths = new int[imgs.length];
        heights = new int[imgs.length];
        for (int n = 0; n < sprites.length; n++) {
            sprites[n] = new SpriteImpl(imgs[n]);
            widths[n] = imgs[n].getWidth();
            heights[n] = imgs[n].getHeight();
        }
        count = 0;
        frame = 0;
        cycle = true;
    }

    @Override
    public int getFrame() {
        return frame;
    }

    @Override
    public void setFrame(int frame) {
        this.frame = frame;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int getTotalDelay() {
        int total = 0;
        for (int delay : delays) {
            total += delay;
        }
        return total;
    }

    @Override
    public int getWidth(int frame) {
        return widths[frame];
    }

    @Override
    public int getHeight(int frame) {
        return heights[frame];
    }

    @Override
    public int getOffsetX(int frame) {
        return xOffsets[frame];
    }

    @Override
    public int getOffsetY(int frame) {
        return yOffsets[frame];
    }

    @Override
    public int getDelay(int frame) {
        return delays[frame];
    }

    @Override
    public Sprite get(int frame) {
        return sprites[frame];
    }

    @Override
    public void cycleFrames(boolean cycle) {
        this.cycle = cycle;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public SpriteSheet copy() {
        return new SpriteSheetImpl(imgs, arraycopy(delays), arraycopy(xOffsets), arraycopy(yOffsets), name);
    }

    @Override
    public void setFilter(Color filter) {
        for (Sprite sprite : sprites) {
            sprite.setFilter(filter);
        }
    }

    @Override
    public void update(int dt) {
        count += dt;
        while (count >= delays[frame]) {
            count -= delays[frame];
            frame++;
            if (frame >= sprites.length) {
                if (cycle) {
                    frame %= sprites.length;
                } else {
                    frame--;
                }
            }
        }
    }

}
