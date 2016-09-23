package kawaiiklash;

import org.newdawn.slick.Graphics;

/**
 * A {@code BlueSky} is a {@code StaticBackground}. It does not move and
 * simply remains as the last backdrop layer. It starts off as a solid blue
 * color but, as one moves down, it eventually grades out to become white.
 *
 * @author Jeff Niu
 */
public class BlueSky extends Background {

    /**
     * The {@code Sprite} that represents this {@code BlueSky}.
     */
    private final Sprite sky;

    /**
     * Create a {@code BlueSky Background} and add it to the {@code Game}.
     *
     * @param game
     */
    public BlueSky(Game game) {
        super(game);
        sky = getSprites().get(0).get(2);
    }

    /**
     *
     * @param g
     */
    @Override
    public void draw(Graphics g) {
        sky.draw(0, 0);
    }

}
