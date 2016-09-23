package kawaiiklash;

/**
 * A text field is something into which a user can enter text.
 *
 * @author Jeff Niu
 */
public abstract class UIAbstractTextField extends UIAbstractElement {

    protected final int width;
    protected final int height;

    public UIAbstractTextField(int x, int y, int width, int height) {
        super(x, y);
        this.width = width;
        this.height = height;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getHeight() {
        return height;
    }

    @Override
    public Sprite getSprite() {
        return null;
    }

    public abstract String getText();

    public abstract void setText(String text);
}
