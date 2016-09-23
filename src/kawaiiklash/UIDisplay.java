package kawaiiklash;

import java.util.List;

/**
 * A display is a user interface element that is animated. Used mostly to
 * display the characters on the character selection screen.
 *
 * @author Jeff Niu
 */
public class UIDisplay extends UIAbstractButton {

    private static final int NORMAL = 0;
    private static final int MOUSE_OVER = 1;

    private int state;

    private final List<SpriteSheet> sprites;

    public UIDisplay(String refName, int x, int y) {
        super(x, y);
        sprites = SpriteLoader.get().loadSprites(Bank.getSpriteRef(refName));
        state = NORMAL;
    }

    @Override
    public void updateState(int dt) {
        sprites.get(state).update(dt);
    }

    @Override
    public void mouseNotOver() {
        changeToState(NORMAL);
    }

    @Override
    public void mouseOver() {
        changeToState(MOUSE_OVER);
    }

    private void changeToState(int state) {
        if (this.state != state) {
            sprites.get(state).setCount(0);
            sprites.get(state).setFrame(0);
            this.state = state;
        }
    }

    @Override
    public Rect getBox() {
        SpriteSheet ss = sprites.get(0);
        double x = super.getX() + ss.getOffsetX();
        double y = super.getY() + ss.getOffsetY();
        double width = ss.getWidth(0);
        double height = ss.getHeight(0);
        return new Rect(x, y, width, height);
    }

    @Override
    public void mousePressed() {
    }

    @Override
    public void mouseClicked() {
    }

    @Override
    public Sprite getSprite() {
        return sprites.get(state).getSprite();
    }

    @Override
    public double getWidth() {
        return sprites.get(state).getWidth(0);
    }

    @Override
    public double getHeight() {
        return sprites.get(state).getHeight(0);
    }

    @Override
    public double getX() {
        return super.getX() + sprites.get(state).getOffsetX();
    }

    @Override
    public double getY() {
        return super.getY() + sprites.get(state).getOffsetY();
    }

}
