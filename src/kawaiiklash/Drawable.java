package kawaiiklash;

import org.newdawn.slick.Graphics;

/**
 * A {@code Drawable} is an Object that can be drawn to a {@code Graphics}.
 * Consider extending {@code Comparable<Drawable>} to control draw order?
 *
 * @author Jeff Niu
 */
public interface Drawable extends Comparable<Drawable> {

    // Z values
    public static final int BACKGROUND = -1;
    public static final int OTHER = 0;
    public static final int TILE = 1;
    public static final int MONSTER = 2;
    public static final int PLAYER = 3;
    public static final int ITEM = 4;
    public static final int ATTACK = 5;
    public static final int FOREGROUND = 6;
    public static final int HUD = 7;
    
    /**
     * This method is called to draw the {@code Object} to the specified
     * {@code Graphics}.
     *
     * @param g the {@code Graphics} to which to draw
     */
    void draw(Graphics g);
    
    int getZ();
    
    @Override
    default int compareTo(Drawable o) {
        return getZ() - o.getZ();
    }
}
