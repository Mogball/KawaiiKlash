package kawaiiklash;

import org.newdawn.slick.command.Command;

/**
 * A key command is a command that is tailored for use with the key codes.
 *
 * @author Jeff Niu
 */
public class KeyCommand implements Command {

    private final int key;

    public KeyCommand(int key) {
        this.key = key;
    }

    public int getKey() {
        return key;
    }

}
