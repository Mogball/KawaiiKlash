package kawaiiklash;

/**
 * Keeps track of the class is the Player.
 *
 * @author Jeff Niu
 */
public enum PlayerType {

    Hero(0), 
    Mage(1), 
    Cory(2), 
    Hermit(3);
    
    /**
     * The index of the PlayerType.
     */
    private final int index;
    
    /**
     * Create a new PlayerType.
     * 
     * @param index 
     */
    private PlayerType(int index) {
        this.index = index;
    }
}
