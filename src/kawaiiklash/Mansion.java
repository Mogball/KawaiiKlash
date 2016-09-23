package kawaiiklash;

/**
 *
 * @author Jeff Niu
 */
public class Mansion extends BackgroundObject {

    private int state;

    public Mansion(Game game) {
        super(game);
        state = 0;
    }

    @Override
    public Sprite getSprite() {
        return getSprites().get(state).get(0);
    }

    @Override
    public Direction getDirX() {
        return null;
    }

    @Override
    public Direction getDirY() {
        return null;
    }

    @Override
    public double getOffsetX() {
        return getSprites().get(state).getOffsetX(0);
    }

    @Override
    public double getOffsetY() {
        return getSprites().get(state).getOffsetX(0);
    }

    @Parsable
    public void state(String s) {
        state = Integer.parseInt(s);
    }

}
