package kawaiiklash;

import java.util.Collections;
import java.util.List;
import org.newdawn.slick.Graphics;

/**
 * A tile describes a single entity that is part of the physical world.
 * Until recently, this class extended the entity class but was separated
 * in preparation for future features to the platforms overall.
 *
 * @author Jeff Niu
 */
public abstract class Tile implements Drawable, Updateable, Platform {

    private final Game game;
    private final List<SpriteSheet> sprites;

    private double x;
    private double y;

    private int state;
    private int type;

    private boolean inBounds;

    {
        SpriteLoader loader = SpriteLoader.get();
        String s = Bank.getSpriteRef(this);
        sprites = loader.loadSprites(s);
    }

    public Tile(Game game) {
        this.game = game;
        x = 0;
        y = 0;
        state = 0;
        type = 0;
        inBounds = true;
    }

    @Override
    public void update(int dt) {
        Rect gScreen = game.getScreen();
        Rect screen = new Rect(-100.0, -100.0, gScreen.getWidth() + 200.0, gScreen.getHeight() + 200.0);
        inBounds = screen.intersects(getHitbox());
        updateState(dt);
    }

    @Override
    public Rect getHitbox() {
        SpriteSheet ss = sprites.get(state);
        Rect r = new Rect();
        r.x = x + ss.getOffsetX(type);
        r.y = y + ss.getOffsetY(type);
        r.width = ss.getWidth(type);
        r.height = ss.getHeight(type);
        return r;
    }

    public abstract void updateState(int dt);

    @Override
    public void draw(Graphics g) {
        if (inBounds) {
            SpriteSheet sheet = sprites.get(state);
            double tx = getX() + sheet.getOffsetX();
            double ty = getY() + sheet.getOffsetY();
            Sprite sprite = sheet.get(type);
            sprite.draw((float) tx, (float) ty);
        }
    }

    public List<SpriteSheet> getSprites() {
        return Collections.unmodifiableList(sprites);
    }

    public void setState(int state) {
        this.state = state;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean inBounds() {
        return inBounds;
    }

    @Override
    public boolean canCollide() {
        return inBounds;
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

    @Override
    public double getOffsetX() {
        return sprites.get(state).getOffsetX(type);
    }

    @Override
    public double getOffsetY() {
        return sprites.get(state).getOffsetY(type);
    }

    @Override
    public double getWidth() {
        return sprites.get(state).getWidth(type);
    }

    @Override
    public double getHeight() {
        return sprites.get(state).getHeight(type);
    }

    public void state(String state) {
        setState(Integer.parseInt(state));
    }

    public void type(String type) {
        setType(Integer.parseInt(type));
    }

    public void x(String x) {
        this.x = Double.parseDouble(x);
    }

    public void y(String y) {
        this.y = Double.parseDouble(y);
    }

    @Override
    public int getZ() {
        return Drawable.TILE;
    }

}
