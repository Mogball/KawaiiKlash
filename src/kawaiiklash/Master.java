package kawaiiklash;

import artificalintelligence.GameAI;
import org.newdawn.slick.state.transition.EmptyTransition;
import org.newdawn.slick.state.transition.Transition;

/**
 * A class that describes what the game master should do.
 *
 * @author Jeff Niu
 */
public interface Master {

    public static final int GAME = 0;
    public static final int EDITOR = 1;
    public static final int MENU = 2;
    public static final int SELECTION = 3;
    public static final int TEST = 765;
    public static final int AI = 766;

    Game getGame();

    GameEditor getEditor();

    GameCharacterMenu getSelection();

    GameAI getAI();

    void enterState(int ID, Transition leave, Transition enter);

    default void enterState(int ID) {
        enterState(ID, new EmptyTransition(), new EmptyTransition());
    }

}
