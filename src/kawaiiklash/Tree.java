package kawaiiklash;

/**
 *
 * @author Jeff Niu
 */
public class Tree extends BackgroundLayer {

    private int type;

    public Tree(Game game) {
        super(game, 0.55);
        type = 0;
    }

    @Override
    public Sprite getSprite() {
        return getSprites().get(0).get(type);
    }

    @Override
    public int getUpshift() {
        return getSprites().get(0).getOffsetY(type);
    }
    
    @Parsable
    public void type(String t) {
        type = Integer.parseInt(t);
    }

}
