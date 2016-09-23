package exception;

import java.util.logging.Logger;

/**
 *
 * @author Jeff Niu
 */
public class LevelNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LevelNotFoundException.class.getName());

    public LevelNotFoundException(String message) {
        super(message);
    }

}
