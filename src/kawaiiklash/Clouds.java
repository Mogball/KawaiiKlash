package kawaiiklash;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Entity.MILLISECONDS;
import org.newdawn.slick.Graphics;

/**
 * This class is a controller for {@code Cloud} objects. It gives the
 * impression of clouds moving across the screen and handles when clouds
 * move offscreen or when new clouds are needed.
 *
 * @author Jeff Niu
 */
public class Clouds extends Background {

    /**
     * The direction in which the clouds are moving.
     */
    private Direction dir;

    /**
     * The list of all the clouds.
     */
    private List<Cloud> clouds;

    /**
     * Contains the width and height of each cloud.
     */
    private final Rect cloud;

    /**
     * The range of possible horizontal positions.
     */
    private final Bound x;

    /**
     * The range of possible vertical positions.
     */
    private final Bound y;

    /**
     * The range of possible speeds.
     */
    private final Bound dx;

    /**
     * The background scroll factor.
     */
    private double scroll;

    /**
     * The background draw scale.
     */
    private float scale;

    /**
     * The type of cloud.
     */
    private int state;

    /**
     * Create the cloud controller.
     *
     * @param game
     */
    public Clouds(Game game) {
        super(game);
        dir = LEFT;
        clouds = null;
        Sprite sprite = new Cloud(0, 0, 0).getSprite();
        cloud = new Rect(sprite.getWidth(), sprite.getHeight());
        x = new Bound(-cloud.width, 4.0 / 3.0 * game.getScreen().getWidth());
        y = new Bound(-cloud.height / 3.0, game.getScreen().getHeight() / 2);
        dx = new Bound(10, 25);
        scroll = 0.9;
        scale = 1.0f;
        state = 0;
    }

    /**
     * Randomly generate the first set of clouds.
     *
     * @param density
     */
    private void createClouds(int density) {
        clouds = new ArrayList<>(density);
        for (int i = 0; i < density; i++) {
            Cloud c = new Cloud(x.rand(), y.rand(), dir.unit() * dx.rand());
            clouds.add(c);
            getGame().add(c);
        }
        Collections.sort(clouds);
    }

    /**
     * Set the mvoement direction.
     *
     * @param dir
     */
    private void setDir(Direction dir) {
        this.dir = dir;
        if (clouds != null && !clouds.isEmpty()) {
            for (Cloud c : clouds) {
                c.setSpeed(abs(c.getSpeed()) * dir.unit());
            }
        }
    }

    /**
     * Notification that a cloud has left the screen. Now it will create a
     * new cloud to replace it.
     */
    protected void notifyCloud() {
        Cloud c;
        Game game = getGame();
        if (dir == LEFT) {
            c = new Cloud(game.getScreen().getWidth(), y.rand(), dir.unit() * dx.rand());
        } else {
            c = new Cloud(-cloud.width, y.rand(), dir.unit() * dx.rand());
        }
        clouds.add(c);
        game.add(c);
        while (clouds.size() > 5 * clouds.size()) {
            clouds.remove(0);
        }
        Collections.sort(clouds);
    }

    /**
     * Draw all the clouds.
     *
     * @param g
     */
    @Override
    public void draw(Graphics g) {
        for (Cloud c : clouds) {
            if (c.inBounds()) {
                c.getSprite().draw((float) c.getX(), (float) c.getY(), scale);
            }
        }
    }

    /**
     * Set the number of clouds. This method must be called.
     *
     * @param density
     */
    @Parsable
    public void density(String density) {
        createClouds(Integer.parseInt(density));
    }

    @Parsable
    public void dir(String dir) {
        setDir(Direction.valueOf(dir));
    }

    @Parsable
    public void scale(String s) {
        scale = Float.parseFloat(s);
    }

    @Parsable
    public void scroll(String s) {
        scroll = Double.parseDouble(s);
    }

    @Parsable
    public void state(String s) {
        state = Integer.parseInt(s);
    }

    private class Cloud implements Updateable, Cartesian, Comparable<Cloud> {

        private final SpriteSheet sheet;

        private double x;
        private double y;
        private double dx;

        private boolean notified;

        {
            sheet = SpriteLoader.get().loadSprites(Bank.getSpriteRef(this)).get(0);
        }

        private Cloud(double x, double y, double dx) {
            this.x = x;
            this.y = y;
            this.dx = dx;
            notified = false;
        }

        private Sprite getSprite() {
            return sheet.get(state);
        }

        private double getSpeed() {
            return dx;
        }

        private void setSpeed(double dx) {
            this.dx = dx;
        }

        private boolean halfway() {
            if (Direction.xDirOf(dx) == LEFT) {
                return x + sheet.getWidth(state) / 2.0 <= 0;
            } else {
                return x >= getGame().getScreen().getWidth();
            }
        }

        private boolean inBounds() {
            final Rect hitbox = new Rect(x, y, getWidth(), getHeight());
            final Rect screen = getGame().getScreen();
            return hitbox.intersects(new Rect(0.0, 0.0, screen.getWidth(), screen.getHeight()));
        }

        @Override
        public void update(int dt) {
            x += dx * dt / MILLISECONDS;
            if (!notified && halfway()) {
                notified = true;
                notifyCloud();
            }
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
        public Direction getDirX() {
            return Direction.xDirOf(dx);
        }

        @Override
        public Direction getDirY() {
            return Direction.NONE;
        }

        @Override
        public double getOffsetX() {
            return getSprites().get(0).getOffsetX(0);
        }

        @Override
        public double getOffsetY() {
            return getSprites().get(0).getOffsetY(0);
        }

        @Override
        public double getWidth() {
            return sheet.getWidth(state);
        }

        @Override
        public double getHeight() {
            return sheet.getWidth(state);
        }

        @Override
        public void moveX(double dx) {
            Bound b = Clouds.this.dx;
            double d = (abs(this.dx) - b.lower()) / (b.upper() - b.lower()) * 0.3 + (scroll);
            Cartesian.super.moveX(dx * d);
        }

        @Override
        public void moveY(double dy) {
            Bound b = Clouds.this.dx;
            double d = (abs(this.dx) - b.lower()) / (b.upper() - b.lower()) * 0.3 + (scroll);
            Cartesian.super.moveY(dy * d);
        }

        @Override
        public int compareTo(Cloud o) {
            return (int) (dx - o.getSpeed());
        }
    }

}
