package kawaiiklash;

/**
 * An {@code Updateable} is an Object that can be updated.
 * 
 * @author Jeff Niu
 */
@FunctionalInterface
public interface Updateable {
    
    /**
     * Tell the {@code Updateable} to update itself.
     * 
     * @param dt the amount of passed time (milliseconds)
     */
    void update(int dt);
    
}
