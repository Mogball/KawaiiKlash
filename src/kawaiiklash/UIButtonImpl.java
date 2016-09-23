package kawaiiklash;

import java.util.List;

/**
 * @see kawaiiklash.UIAbstractButton
 * @author Jeff Niu
 */
public class UIButtonImpl extends UIAbstractButton {

    private static final int MOUSE_OVER = 0;
    private static final int NORMAL = 1;
    private static final int PRESSED = 2;

    private final List<SpriteSheet> sprite;
    private final Action action;
    private int state;

    public UIButtonImpl(Action action, String refName, double x, double y) {
        super(x, y);
        this.action = action;
        sprite = SpriteLoader.get().loadSprites(Bank.getSpriteRef(refName));
        state = NORMAL;
        initPosition();
    }

    private void initPosition() {
        double x = getX() - getWidth() / 2;
        double y = getY() - getHeight() / 2;
        setX(x);
        setY(y);
    }

    @Override
    public Sprite getSprite() {
        return sprite.get(state).get(0);
    }

    @Override
    public double getWidth() {
        return sprite.get(0).getWidth(0);
    }

    @Override
    public double getHeight() {
        return sprite.get(0).getHeight(0);
    }

    @Override
    public void mouseNotOver() {
        state = NORMAL;
    }

    @Override
    public void mouseOver() {
        state = MOUSE_OVER;
    }

    @Override
    public void mousePressed() {
        state = PRESSED;
    }

    @Override
    public void mouseClicked() {
        action.execute();
    }

    @Override
    public void updateState(int dt) {
    }

}
