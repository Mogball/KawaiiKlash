package kawaiiklash.exception;

import java.util.logging.Logger;

/**
 *
 * @author Jeff Niu
 */
public class TooManyPlayerEntitiesException extends Exception
{
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(TooManyPlayerEntitiesException.class.getName());

    public TooManyPlayerEntitiesException(String message) {
        super(message);
    }
}
