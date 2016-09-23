package kawaiiklash;

import artificalintelligence.GameAI;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * The main menu screen for Kawaii Klash. This is what players will see
 * when they first launch the game. From here, they can enter the character
 * selection screen or exit the game. I have also included a text field so
 * that I can use it for testing purposes.
 *
 * The text field does the following:
 *
 * Entering "Cory" will start the game as the player character Cory, who is
 * invincible, has very high movement speeds and jump height, and can kill
 * all monsters on screen. This is a testing character.
 *
 * Entering the name of the level file, say "level1" will start
 * {@link kawaiiklash.GameEditor editor mode} on that level.
 *
 * Entering the command "play" followed by the world number and the level
 * number will start that level as Cory. For example, "play 2 1" will
 * start, as Cory, level 1 on world 2.
 *
 * @author Jeff Niu
 */
public class GameMainMenu extends BasicGameState {

    /**
     * A reference for the game master.
     */
    private final Master master;

    /**
     * The user interface object for this menu.
     */
    private UI userInterface;

    /**
     * Create the main menu.
     *
     * @param master
     */
    public GameMainMenu(Master master) {
        this.master = master;
    }

    /**
     * Create the user interface and all the buttons and text fields.
     *
     * @param gc
     * @param master
     * @throws SlickException
     */
    @Override
    public void init(GameContainer gc, StateBasedGame master) throws SlickException {
        double center = this.master.getGame().getScreen().getWidth() / 2;

        userInterface = new UI();

        UIAbstractTextField editField = new UITextFieldImpl(gc, 750, 760, 100);

        UIAbstractButton playButton = new UIButtonImpl(new Action(() -> {
            master.enterState(Master.SELECTION);
        }), "PlayButton", center, 400);
        UIAbstractButton editButton = new UIButtonImpl(new Action(() -> {
            String input = editField.getText();
            editField.setText("");
            textInput(input);
        }), "EditButton", 800, 800);
        UIAbstractButton exitButton = new UIButtonImpl(new Action(() -> {
            gc.exit();
        }), "ExitButton", center, 500);

        userInterface.addElement(playButton);
        userInterface.addElement(editButton);
        userInterface.addElement(exitButton);
        userInterface.addElement(editField);
    }

    /**
     * Draw the user interface.
     *
     * @param gc
     * @param master
     * @param g
     * @throws SlickException
     */
    @Override
    public void render(GameContainer gc, StateBasedGame master, Graphics g) throws SlickException {
        userInterface.draw(gc, g);
    }

    /**
     * Update the user interface.
     *
     * @param gc
     * @param master
     * @param dt
     * @throws SlickException
     */
    @Override
    public void update(GameContainer gc, StateBasedGame master, int dt) throws SlickException {
        userInterface.update(gc, dt);
    }

    @Override
    public int getID() {
        return Master.MENU;
    }

    /**
     * Handle the text input into the text field. Outcomes are described in
     * the class documentation.
     *
     * @see kawaiiklash.GameMainMenu
     * @param input
     */
    private void textInput(String input) {
        if (input == null || input.isEmpty()) {
            return;
        }
        java.util.Scanner scanner = new java.util.Scanner(input);
        String next = scanner.next();
        if (next.equals("ai")) {
            int world = scanner.nextInt() - 1;
            int level = scanner.nextInt() - 1;
            PlayerType type = PlayerType.Cory;
            if (scanner.hasNext()) {
                try {
                    type = PlayerType.valueOf(scanner.next());
                } catch (Exception ex) {
                    return;
                }
            }
            GameAI ai = master.getAI();
            ai.setLevel(level);
            ai.setWorld(world);
            ai.setPlayerType(type);
            this.master.enterState(Master.AI);
            try {
                ai.begin();
            } catch (SlickException ex) {
            }
            return;
        }
        if (input.equals("test")) {
            this.master.enterState(Master.TEST);
            return;
        }
        if (input.equals("Cory")) {
            this.master.getSelection().startGame(PlayerType.Cory);
        } else if (next.equals("play")) {
            int world = scanner.nextInt() - 1;
            int level = scanner.nextInt() - 1;
            PlayerType type = PlayerType.Cory;
            if (scanner.hasNext()) {
                try {
                    type = PlayerType.valueOf(scanner.next());
                } catch (Exception ex) {
                    return;
                }
            }
            master.getSelection().startGame(type, world, level);
        } else {
            GameEditor editor = this.master.getEditor();
            editor.setLevel(input);
            editor.setWorlds(new String[][]{{editor.getData()}});
            boolean succeed = true;
            try {
                editor.begin();
            } catch (SlickException ex) {
                succeed = false;
            }
            if (succeed) {
                master.enterState(Master.EDITOR);
            }
        }
    }

}
