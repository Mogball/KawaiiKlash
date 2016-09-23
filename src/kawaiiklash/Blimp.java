package kawaiiklash;

/**
 * A {@code Blinp} is a background object that it animated and moves across
 * the screen.
 *
 * @author Jeff Niu
 */
public class Blimp extends BackgroundLayer implements Updateable {

    /**
     * The movement speed (horizontal).
     */
    private double dx;

    /**
     * An internal value of the upshift.
     */
    private int upshift;

    /**
     * The {@code SpriteSheet} handling the animation.
     */
    private final SpriteSheet sprite;

    /**
     * Create a {@code Blimp}.
     *
     * @param game
     */
    public Blimp(Game game) {
        super(game, 0.65);
        dx = -500;
        sprite = getSprites().get(0);
        upshift = -800;
    }

    /**
     * Update the position and animation.
     *
     * @param dt
     */
    @Override
    public void update(int dt) {
        double t = dt / Entity.MILLISECONDS;
        double delta = t * dx;
        moveX(delta);
        sprite.update(dt);
    }

    @Override
    public Sprite getSprite() {
        return sprite.getSprite();
    }

    @Override
    public int getUpshift() {
        return upshift;
    }

    @Parsable
    public void upshift(String u) {
        upshift = Integer.parseInt(u);
    }

    @Parsable
    public void dx(String dx) {
        this.dx = Double.parseDouble(dx);
    }

}
