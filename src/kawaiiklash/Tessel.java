package kawaiiklash;

import java.awt.Toolkit;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static kawaiiklash.Direction.DOWN;
import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Direction.NONE;
import static kawaiiklash.Direction.RIGHT;
import static kawaiiklash.Direction.UP;
import static kawaiiklash.Utility.fail;
import org.newdawn.slick.Graphics;

/**
 * A {@code Tessel} is a wrapper class for {@code Tile} to create a
 * tessellation of that {@code Tile}. The {@code Tessel} will create a
 * rectangular tessellation of a {@code Tile} based on a {@link #width} and
 * {@link #height}.
 *
 * @author Jeff Niu
 */
public class Tessel implements Updateable, Drawable, Platform {

    private static final Rect screen = new Rect(new Vector(), Toolkit.getDefaultToolkit().getScreenSize());
    private static final Dimensions border = new Dimensions(Toolkit.getDefaultToolkit().getScreenSize());

    /**
     * The {@code Game} to which the {@code Tessel} belongs.
     */
    private final Game game;

    /**
     * Whether or not the {@code Tessel} is in bounds.
     */
    private boolean inBounds;

    /**
     * The horizontal position of the {@code Tessel}.
     */
    private double x;

    /**
     * The vertical position of the {@code Tessel}.
     */
    private double y;

    /**
     * The {@code Tile} that this {@code Tessel} is tessellating.
     */
    private Tile tile;

    /**
     * The width of this {@code Tessel} represents the number of
     * {@code Tile} objects that are in the horizontal.
     */
    private int width;

    /**
     * The height of this {@code Tessel} represents the number of
     * {@code Tile} objects that are in the vertical.
     */
    private int height;

    /**
     * The state of the {@code Tile} represented by this {@code Tessel}.
     */
    private int state;

    /**
     * The type of this {@code Tile} represented by this {@code Tessel}.
     */
    private int type;

    /**
     * Create an empty {@code Tessel}.
     *
     * @param game
     */
    public Tessel(Game game) {
        this.game = game;
        inBounds = true;
        x = 0;
        y = 0;
        width = 1;
        height = 1;
        state = 0;
        type = 0;
    }

    /**
     * Set the width of the tessellation.
     *
     * @param width
     */
    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * Set the height of the tessellation.
     *
     * @param height
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * Set the x-position of the tessellation.
     *
     * @param x
     */
    @Override
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set the y-position of the tessellation.
     *
     * @param y
     */
    @Override
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the x-position of the tessellation.
     *
     * @return
     */
    @Override
    public double getX() {
        return x;
    }

    /**
     * Get the y-position of the tessellation.
     *
     * @return
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * Change the state of the wrapped {@code Tile}.
     *
     * @param state
     */
    public void setState(int state) {
        try {
            tile.getSprites().get(state);
        } catch (ArrayIndexOutOfBoundsException ex) {
            fail(String.format("Could not set state %d for Tile %s", state, tile), ex);
        }
        this.state = state;
    }

    /**
     * Change the type of the wrapped {@code Tile}.
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Set the {@code Tile} based on its name.
     *
     * @param name
     */
    public void setTile(String name) {
        this.tile = getTileInstance(name);
    }

    /**
     * Get an instance of a {@code Tile} based on name name.
     *
     * @param name
     * @return
     */
    public Tile getTileInstance(String name) {
        Package[] packages = Package.getPackages();
        Class<?> cls = null;
        Constructor<?> ctor = null;
        Tile instance = null;
        for (Package p : packages) {
            String attempt = p.getName() + "." + name;
            try {
                cls = Class.forName(attempt);
                ctor = cls.getConstructor(Game.class);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
                continue;
            }
            break;
        }
        if (cls != null && ctor != null) {
            try {
                instance = (Tile) ctor.newInstance(game);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                instance = null;
            }
        }
        return instance;
    }

    /**
     * Get the x-offset of this {@code Tessel}.
     *
     * @return
     */
    @Override
    public double getOffsetX() {
        return tile.getSprites().get(state).getOffsetX();
    }

    /**
     * Get the y-offset of this {@code Tessel}.
     *
     * @return
     */
    @Override
    public double getOffsetY() {
        return tile.getSprites().get(state).getOffsetY();
    }

    /**
     * Get the width of this {@code Tessel}.
     *
     * @return
     */
    @Override
    public double getWidth() {
        return tile.getSprites().get(state).getWidth() * width;
    }

    /**
     * Get the height of this {@code Tessel}.
     *
     * @return
     */
    @Override
    public double getHeight() {
        return tile.getSprites().get(state).getHeight() * height;
    }

    @Override
    @SuppressWarnings("LocalVariableHidesMemberVariable")
    public void draw(Graphics g) {
        if (inBounds) {
            tile.setState(state);
            tile.setType(type);
            SpriteSheet ss = tile.getSprites().get(state);
            double tileWidth = ss.getWidth(type);
            double tileHeight = ss.getHeight(type);
            double x = this.x + ss.getOffsetX(type);
            double y = this.y + ss.getOffsetY(type);
            for (int w = 0; w < width; w++) {
                for (int h = 0; h < height; h++) {
                    double tx = x + tileWidth * w;
                    double ty = y + tileHeight * h;
                    if (pointInBounds(new Vector(tx, ty))) {
                        Sprite s = ss.get(type);
                        s.draw((float) tx, (float) ty);
                    }
                }
            }
        }
    }

    /**
     * Check if a {@code Point} is acceptably within the bounds. Used for
     * drawing very large {@code Tessel} objects.
     *
     * @param p
     * @return
     */
    public boolean pointInBounds(Vector p) {
        final double tileWidth = tile.getSprites().get(state).getWidth();
        final double tileHeight = tile.getSprites().get(state).getHeight();
        final boolean xOverlap = p.x > -tileWidth && p.x < border.width + tileWidth;
        final boolean yOverlap = p.y > -tileHeight && p.y < border.height + tileHeight;
        return xOverlap && yOverlap;
    }

    /**
     *
     */
    public void checkBounds() {
        if (getHitbox().intersects(screen)) {
            isInBounds();
        } else if (x + getOffsetX() > screen.width) {
            isOutOfBounds(RIGHT);
        } else if (x + getOffsetX() + getWidth() < 0) {
            isOutOfBounds(LEFT);
        } else if (y + getOffsetY() > screen.height) {
            isOutOfBounds(DOWN);
        } else if (y + getOffsetY() + getHeight() < 0) {
            isOutOfBounds(UP);
        }
    }

    /**
     * To update this {@code Tessel}, simply check its bounds.
     *
     * @param dt
     */
    @Override
    public void update(int dt) {
        checkBounds();
    }

    /**
     *
     * @param dir
     */
    public void isOutOfBounds(Direction dir) {
        inBounds = false;
    }

    /**
     *
     */
    public void isInBounds() {
        inBounds = true;
    }

    /**
     * Get the hitbox representing this {@code Tessel}.
     *
     * @return
     */
    @Override
    @SuppressWarnings("LocalVariableHidesMemberVariable")
    public Rect getHitbox() {
        Rect hitbox = new Rect();
        double x = this.x + getOffsetX();
        double y = this.y + getOffsetY();
        hitbox.setRect(x, y, getWidth(), getHeight());
        return hitbox;
    }

    @Override
    public boolean canCollide() {
        return inBounds;
    }

    @Override
    public void collidedWith(Collideable other) {
    }

    @Override
    public boolean isSticky() {
        return false;
    }

    @Override
    public boolean isMoving() {
        return false;
    }

    @Override
    public double getDx() {
        return 0.0;
    }

    @Override
    public double getDy() {
        return 0.0;
    }

    /**
     * Wrapper method for dynamically setting the x-position.
     *
     * @param x
     */
    public void x(String x) {
        setX(Double.parseDouble(x));
    }

    /**
     * Wrapper method for dynamically setting the y-position.
     *
     * @param y
     */
    public void y(String y) {
        setY(Double.parseDouble(y));
    }

    /**
     * Wrapper method for dynamically setting the width.
     *
     * @param width
     */
    public void width(String width) {
        setWidth(Integer.parseInt(width));
    }

    /**
     * Wrapper method for dynamically setting the height.
     *
     * @param height
     */
    public void height(String height) {
        setHeight(Integer.parseInt(height));
    }

    /**
     * Wrapper method for dynamically setting the state.
     *
     * @param state
     */
    public void state(String state) {
        setState(Integer.parseInt(state));
    }

    /**
     * Wrapper method for dynamically setting the type.
     *
     * @param type
     */
    public void type(String type) {
        setType(Integer.parseInt(type));
    }

    /**
     * Wrapper method for dynamically setting the Tile.
     *
     * @param name
     */
    public void tile(String name) {
        setTile(name);
    }

    @Override
    public Direction getDirX() {
        return NONE;
    }

    @Override
    public Direction getDirY() {
        return NONE;
    }

    @Override
    public int getZ() {
        return Drawable.TILE;
    }

}
