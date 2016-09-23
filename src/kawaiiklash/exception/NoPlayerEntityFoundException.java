package kawaiiklash.exception;

import java.util.logging.Logger;

/**
 *
 * @author Jeff Niu
 */
public class NoPlayerEntityFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(NoPlayerEntityFoundException.class.getName());

    public NoPlayerEntityFoundException(String message) {
        super(message);
    }
}
