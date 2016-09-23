package kawaiiklash;

import artificalintelligence.GameAI;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Not exactly the heart of the game, but all the game's various states are
 * controlled from here.
 *
 * @author Jeff Niu
 */
public class GameMaster extends StateBasedGame implements Master {

    /**
     * The main menu.
     */
    private GameMainMenu menu;
    /**
     * The character selection screen.
     */
    private GameCharacterMenu character;
    /**
     * The game level editor.
     */
    private GameEditor editor;
    /**
     * The actual heart of the game, the game.
     */
    private Game game;

    /**
     * A region for testing things.
     */
    private GameTest test;
    
    /**
     * AI arena.
     */
    private GameAI ai;

    /**
     * Create the game master.
     */
    public GameMaster() {
        super("Kawaii Klash");
    }

    /**
     * Create all of the states and add them.
     *
     * @param gc
     * @throws SlickException
     */
    @Override
    public void initStatesList(GameContainer gc) throws SlickException {
        menu = new GameMainMenu(this);
        character = new GameCharacterMenu(this);
        editor = new GameEditor(this);
        game = new GameImpl(this);
        test = new GameTest();
        ai = new GameAI(this);
        addState(menu);
        addState(character);
        addState(game);
        addState(editor);
        addState(test);
        addState(ai);
        enterState(menu.getID());
    }

    /**
     * Get the game.
     *
     * @return
     */
    @Override
    public Game getGame() {
        return game;
    }

    /**
     * Get the editor.
     *
     * @return
     */
    @Override
    public GameEditor getEditor() {
        return editor;
    }

    /**
     * Get the character selection menu.
     *
     * @return
     */
    @Override
    public GameCharacterMenu getSelection() {
        return character;
    }

    @Override
    public GameAI getAI() {
        return ai;
    }
    
}
