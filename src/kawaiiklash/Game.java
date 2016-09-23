package kawaiiklash;

import java.util.List;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.GameState;

/**
 * This interface was developed to aid with refactoring when I moved the
 * game over from the initial ad-hoc game engine to Slick2D. Herein is
 * described things that the game implementation must do.
 *
 * @author Jeff Niu
 */
public interface Game extends GameState {

    /**
     * Get the game objects.
     *
     * @return
     */
    List<Object> getObjects();

    /**
     * Add an object to the game.
     *
     * @param o
     */
    void add(Object o);

    /**
     * Remove an object from the game.
     *
     * @param o
     */
    void remove(Object o);

    /**
     * Whether or not an object is currently being added on this update
     * cycle.
     *
     * @param o
     * @return
     */
    boolean objectAddQueued(Object o);

    /**
     * Get the set of booleans that represent whether each key on the
     * keyboard is pressed.
     *
     * @return
     */
    boolean[] keyDown();

    /**
     * Get the {@code Rect} that represents the game screen.
     *
     * @return
     */
    Rect getScreen();

    /**
     * Get the border area that represents when the screen should scroll
     * according to the player's position.
     *
     * @return
     */
    Rect getBorder();

    /**
     * Get a reference for the player.
     *
     * @return
     */
    Player getPlayer();

    /**
     * Scroll the screen horizontally.
     *
     * @param xScroll
     */
    void xScroll(double xScroll);

    /**
     * Scroll the screen vertically.
     *
     * @param yScroll
     */
    void yScroll(double yScroll);

    /**
     * Indication that the player has died.
     */
    void playerDead();

    /**
     * Indication that the level has been completed.
     */
    void levelCompleted();

    /**
     * Get the volume pitch.
     *
     * @return
     */
    float getPitch();

    /**
     * Get the volume gain.
     *
     * @return
     */
    float getGain();

    /**
     * Get the heads up display for the current game.
     *
     * @return
     */
    HUD getHUD();

    /**
     * Set the player type of the player in the game.
     *
     * @param playerType
     */
    void setPlayerType(PlayerType playerType);

    /**
     * Set the level of the world that will be played.
     *
     * @param level
     */
    void setLevel(int level);

    /**
     * Set the world that will be played.
     *
     * @param world
     */
    void setWorld(int world);

    /**
     * Start a level. It either reloads the current level or loads the next
     * level.
     *
     * @throws SlickException
     */
    void begin() throws SlickException;

    /**
     * Return the main menu.
     */
    void returnToMenu();

    /**
     * Whether or not we are currently in debugging monde.
     *
     * @return
     */
    boolean debugging();

}
