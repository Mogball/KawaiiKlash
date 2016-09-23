package kawaiiklash;

import static kawaiiklash.Utility.fail;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * A menu for selecting a character. Contains a button for each character
 * choice and a back button to return to the main menu.
 *
 * @author Jeff Niu
 */
public class GameCharacterMenu extends BasicGameState {

    private final Master master;
    private UI UI;

    public GameCharacterMenu(Master master) {
        this.master = master;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame master) throws SlickException {
        UI = new UI();
        UI.addElement(new UIDisplay("HeroDisplay", 500, 500));
        UI.addElement(new UIButtonImpl(new Action(() -> {
            startGame(PlayerType.Hero);
        }), "DecideButton", 500, 500));
        UI.addElement(new UIDisplay("MageDisplay", 700, 500));
        UI.addElement(new UIButtonImpl(new Action(() -> {
            startGame(PlayerType.Mage);
        }), "DecideButton", 700, 500));
        UI.addElement(new UIDisplay("HermitDisplay", 900, 500));
        UI.addElement(new UIButtonImpl(new Action(() -> {
            startGame(PlayerType.Hermit);
        }), "DecideButton", 900, 500));
        UI.addElement(new UIButtonImpl(new Action(() -> {
            master.enterState(Master.MENU);
        }), "BackButton", 45, 800));
    }

    @Override
    public void render(GameContainer gc, StateBasedGame master, Graphics g) throws SlickException {
        UI.draw(gc, g);
    }

    @Override
    public void update(GameContainer gc, StateBasedGame master, int dt) throws SlickException {
        UI.update(gc, dt);
    }

    public void startGame(PlayerType playerType, int world, int level) {
        Game game = master.getGame();
        game.setLevel(level);
        game.setWorld(world);
        game.setPlayerType(playerType);
        try {
            game.begin();
        } catch (SlickException ex) {
            fail(ex);
        }
        master.enterState(Master.GAME);
    }

    public void startGame(PlayerType playerType) {
        startGame(playerType, 0, 0);
    }

    @Override
    public int getID() {
        return Master.SELECTION;
    }
}
