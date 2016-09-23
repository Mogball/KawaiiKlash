package kawaiiklash;

/**
 * An {@code Object} that {@code Logicable} is one that has a method called
 * to do its logic, whatever that may be.
 *
 * @author Jeff Niu
 */
@FunctionalInterface
public interface Logicable {

    /**
     * Tell the {@code Logicable} to do its logic.
     */
    void doLogic();
    
}
