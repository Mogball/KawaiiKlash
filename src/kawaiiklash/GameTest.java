package kawaiiklash;

import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author Jeff Niu
 */
public class GameTest extends BasicGameState implements InputProviderListener {

    private InputProvider provider;

    @Override
    public int getID() {
        return Master.TEST;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame game) throws SlickException {
        provider = new InputProvider(gc.getInput());
        provider.addListener(this);
        provider.setActive(false);
        provider.bindCommand(new KeyControl(Keyboard.KEY_ESCAPE), new Action(() -> {
            game.enterState(Master.MENU);
        }));
    }

    @Override
    public void render(GameContainer gc, StateBasedGame game, Graphics g) throws SlickException {
    }

    @Override
    public void update(GameContainer gc, StateBasedGame game, int dt) throws SlickException {
    }

    @Override
    public void controlPressed(Command cmd) {
        if (cmd instanceof Action) {
            ((Action) cmd).execute();
        }
    }

    @Override
    public void controlReleased(Command cmd) {
    }

    @Override
    public void enter(GameContainer gc, StateBasedGame master) {
        provider.setActive(true);
    }

    @Override
    public void leave(GameContainer gc, StateBasedGame master) {
        provider.setActive(false);
    }
}
