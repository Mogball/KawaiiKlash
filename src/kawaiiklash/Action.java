package kawaiiklash;

import org.newdawn.slick.command.Command;

/**
 * The {@code Action} object holds an {@code Execution} functional
 * interface and can execute the procedure defined therein upon calling the
 * {@link #execute()} method.
 *
 * @author Jeff Niu
 */
public class Action implements Command {

    /**
     * The {@code Execution} of the {@code Action}.
     */
    private final Execution action;

    /**
     * Create an {@code Action}.
     * 
     * @param action the action to execute
     */
    public Action(Execution action) {
        this.action = action;
    }

    /**
     * Execute the action.
     */
    public void execute() {
        action.execute();
    }

}
