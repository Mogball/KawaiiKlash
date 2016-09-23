package kawaiiklash;

import java.util.Collections;
import java.util.List;

/**
 * A {@code Background} is a {@code Drawable} object that is drawn behind
 * all other objects. It is a class that handles the backdrop of the
 * {@code Game} graphics.
 *
 * @author Jeff Niu
 */
public abstract class Background implements Drawable {

    /**
     * The reference of the {@code data.xml}.
     */
    private final String REFERENCE;

    /**
     * The {@code Game} to which this {@code Background} belongs.
     */
    private final Game game;

    /**
     * The {@code List} of all the {@code SpriteSheet}s.
     */
    private final List<SpriteSheet> sprites;
    
    private boolean foreground;

    /**
     * Instance initializer to set retrieve the reference of the
     * {@code Background}.
     */
    {
        REFERENCE = Bank.getSpriteRef(this);
        sprites = SpriteLoader.get().loadSprites(REFERENCE);
    }

    /**
     * Create the {@code Background}.
     *
     * @param game
     */
    public Background(Game game) {
        this.game = game;
        foreground = false;
    }

    /**
     * Return the {@code Game} to which this {@code Background} belongs.
     *
     * @return the {@code Game}
     */
    public Game getGame() {
        return game;
    }

    /**
     * Get the {@code List} of {@code SpriteSheet}s that represent this
     * {@code Background}.
     *
     * @return the sprites
     */
    public List<SpriteSheet> getSprites() {
        return Collections.unmodifiableList(sprites);
    }

    public boolean isForeground() {
        return foreground;
    }

    public void foreground(String s) {
        foreground = Boolean.parseBoolean(s);
    }

    @Override
    public int getZ() {
        if (foreground) {
            return Drawable.FOREGROUND;
        } else {
            return Drawable.BACKGROUND;
        }
    }
    
}
