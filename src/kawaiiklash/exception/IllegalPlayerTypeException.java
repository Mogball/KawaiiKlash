package exception;

import java.util.logging.Logger;

/**
 *
 * @author Jeff Niu
 */
public class IllegalPlayerTypeException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(IllegalPlayerTypeException.class.getName());

    public IllegalPlayerTypeException(String message) {
        super(message);
    }
}
