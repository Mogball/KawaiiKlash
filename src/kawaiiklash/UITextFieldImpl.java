package kawaiiklash;

import java.awt.Font;
import java.util.List;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.gui.TextField;

/**
 * Wrapper class for the Slick2D text field. Uses loaded sprites.
 *
 * @author Jeff Niu
 */
public class UITextFieldImpl extends UIAbstractTextField {

    public static final TrueTypeFont FONT = new TrueTypeFont(new Font("Tahoma", Font.PLAIN, 15), false, null);

    private static final int START = 0;
    private static final int FILL = 1;
    private static final int END = 2;

    private final List<SpriteSheet> sprites;
    private final TextField field;

    public UITextFieldImpl(GUIContext context, int x, int y, int width, ComponentListener listener) {
        super(x, y, width, FONT.getHeight());
        sprites = SpriteLoader.get().loadSprites(Bank.getSpriteRef("ChatField"));
        field = new TextField(context, FONT, x - 3, y, width, height);
        field.setBackgroundColor(null);
        field.setBorderColor(null);
        field.setTextColor(Color.black);
        if (listener != null) {
            field.addListener(listener);
        }
    }

    public UITextFieldImpl(GUIContext context, int x, int y, int width) {
        this(context, x, y, width, null);
    }

    @Override
    public String getText() {
        return field.getText();
    }

    @Override
    public void setText(String text) {
        field.setText(text);
    }

    @Override
    public void update(GameContainer gc, int dt) {
    }

    @Override
    public Sprite getSprite() {
        return null;
    }

    @Override
    public void draw(GameContainer gc, Graphics g) {
        for (double i = 0; i < width; i++) {
            SpriteSheet ss;
            if (i == 0) {
                ss = sprites.get(START);
            } else if (i == width - 1) {
                ss = sprites.get(END);
            } else {
                ss = sprites.get(FILL);
            }
            double x = getX() + ss.getOffsetX();
            double y = getY() + ss.getOffsetY();
            ss.get(0).draw((float) (x + i), (float) y);
        }
        field.render(gc, g);
    }

}
