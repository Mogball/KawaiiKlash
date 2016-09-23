package kawaiiklash;

/**
 * An element that belongs to the heads up display. It has a position and
 * is drawable. In this case, since it is a game object, it also extends
 * reactor.
 *
 * @author Jeff Niu
 */
public abstract class HUDAbstractElement extends Reactor implements Drawable {

    private double x;
    private double y;

    public HUDAbstractElement(Game game) {
        super(game);
    }

    @Override
    public void setX(double x) {
        this.x = x;
    }

    @Override
    public void setY(double y) {
        this.y = y;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    /**
     * The heads up display will not move around.
     *
     * @param dx
     */
    @Override
    @SuppressWarnings("NoopMethodInAbstractClass")
    public void moveX(double dx) {
    }

    /**
     * @see HUDAbstractElement#moveX(double) 
     * @param dy 
     */
    @Override
    @SuppressWarnings("NoopMethodInAbstractClass")
    public void moveY(double dy) {
    }

    @Override
    public int getZ() {
        return Drawable.HUD;
    }
    
}
