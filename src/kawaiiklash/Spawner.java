package kawaiiklash;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Direction.RIGHT;
import org.newdawn.slick.Graphics;

/**
 * A {@code Spawner} is a {@code Game} object {@code Reactor} that spawns a
 * certain amount of {@code Monster}s at set intervals. It has a position
 * at which spawning occurs.
 *
 * @author Jeff Niu
 */
public class Spawner extends Reactor implements Drawable {

    /**
     * A value of the {@link #delay delay} to indicate that no spawning
     * will occur.
     */
    private static final int NO_SPAWN = -1;

    /**
     * A value of the {@link #spawn spawn} to indicate unlimited spawning.
     */
    private static final int UNLIMITED = -1;

    /**
     * Create an instance of the {@code Monster} that this {@code Spawner}
     * spawns.
     *
     * @param name the name of the {@code Monster}
     * @param game the {@code Game} to which to add the {@code Monster}
     * @return an instance of the {@code Monster}
     */
    public static Monster createMonsterInstance(String name, Game game) {
        Package[] packages = Package.getPackages();
        Class<?> cls = null;
        Constructor<?> ctor = null;
        Monster instance = null;
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
                instance = (Monster) ctor.newInstance(game);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                instance = null;
            }
        }
        return instance;
    }

    /**
     * The sprite that represents the spawner.
     */
    private final SpriteSheet sprite;

    /**
     * The {@code String} that contains the {@code Class} name of the
     * {@code Monster} that this {@code Spawner} will spawn.
     */
    private String monsterName;

    /**
     * The horizontal position of the {@code Spawner}.
     */
    private double x;

    /**
     * The vertical position of the {@code Spawner}.
     */
    private double y;

    /**
     * This {@code Direction} determines the {@code Direction} of the
     * {@code Monster} that is spawned by this {@code Spawner}.
     */
    private Direction dir;

    private boolean upsidedown;

    /**
     * A counter for this {@code Spawner} which allows for {@code Monster}s
     * to be spawned at regular intervals.
     */
    private int t;

    /**
     * The delay between spawn cycles (milliseconds).
     */
    private int delay;

    /**
     * The total number of {@code Monster}s that will be spawned.
     */
    private int spawn;

    /**
     * Create a {@code Spawner} in a {@code Game}.
     *
     * @param game
     */
    public Spawner(Game game) {
        super(game);
        x = 0;
        y = 0;
        dir = LEFT;
        t = 0;
        delay = NO_SPAWN;
        spawn = UNLIMITED;
        monsterName = null;
        sprite = SpriteLoader.get().loadSprites(Bank.getSpriteRef("Spawner")).get(0);
        upsidedown = false;
    }

    /**
     * Tell the {@code Spawner} to spawn another {@code Monster}.
     */
    private void spawn() {
        if (spawn != UNLIMITED) {
            spawn--;
        }
        Monster monster = createMonsterInstance(monsterName, getGame());
        monster.setX(x);
        monster.setY(y);
        if (dir == LEFT || dir == RIGHT) {
            monster.setDirX(dir);
        }
        getGame().add(monster);
    }

    /**
     * Set the name of the {@code Monster} spawned by this {@code Spawner}.
     * Upon setting the name, it will be no longer possible to change it.
     * Moreover, setting the name will instantaneously cause a spawn cycle
     * to occur. This will be the first spawn cycle.
     *
     * @param monsterName the {@code Class} name of the {@code Monster}
     */
    public void setMonster(String monsterName) {
        if (this.monsterName == null) {
            this.monsterName = monsterName;
            spawn();
        }
    }

    /**
     * Set the number of spawn cycles that will be done by this
     * {@code Spawner}. The default value will be
     * {@link #UNLIMITED unlimited}.
     *
     * @param spawn the number of spawn cycles
     */
    public void setSpawn(int spawn) {
        this.spawn = spawn;
    }

    /**
     * Set the delay between each spawn cycle. The
     * {@link #NO_SPAWN default value} will be such that no spawning
     * occurs.
     *
     * @param delay the length of the spawn interval
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Set the {@code Direction} that the spawned {@code Monster} will be
     * facing.
     *
     * @param dir the {@code Direction}
     */
    public void setDir(Direction dir) {
        this.dir = dir;
    }

    /**
     * Set the horizontal position of this {@code Spawner}.
     *
     * @param x the x-position
     */
    @Override
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set the vertical position of this {@code Spawner}.
     *
     * @param y the y-position
     */
    @Override
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the horizontal position of this {@code Spawner}.
     *
     * @return the x-position
     */
    @Override
    public double getX() {
        return x;
    }

    /**
     * Get the vertical position of this {@code Spawner}.
     *
     * @return the y-position
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * Determine whether or not the {@code Spawner} is in bounds. That is,
     * check if its {@code Rect} representation intersects with the
     * {@code Game} screen.
     *
     * @return true if in bounds and false if not
     */
    private boolean inBounds() {
        Game game = getGame();
        Rect screen = game.getScreen();
        Rect bounds = new Rect(0.0, 0.0, screen.getWidth(), screen.getHeight());
        double sx = x + sprite.getOffsetX();
        double sy = y + sprite.getOffsetY();
        if (upsidedown) {
            sy += 80;
        }
        if (dir == Direction.RIGHT) {
            sx += 32;
        }
        return sx > 0 && sx < bounds.getWidth() && sy < bounds.getHeight();
    }

    /**
     * If the {@code Spawner} is in bounds, it will update its
     * {@link #t counter} and check if it needs to spawn.
     *
     * @param dt
     */
    @Override
    public void update(int dt) {
        if (inBounds()) {
            t += dt;
            if (delay != NO_SPAWN && t > delay) {
                t = 0;
                if (spawn == UNLIMITED || spawn > 0) {
                    spawn();
                }
            }
        }
        if (spawn == 0) {
            getGame().remove(this);
        }
        sprite.update(dt);
    }

    /**
     * Dynamically-called wrapper method for {@link #setX(double) setX}.
     *
     * @param x the x-position
     */
    public void x(String x) {
        setX(Double.parseDouble(x));
    }

    /**
     * Dynamically-called wrapper method for {@link #setY(double) setY}.
     *
     * @param y the y-position
     */
    public void y(String y) {
        setY(Double.parseDouble(y));
    }

    /**
     * Dynamically-called wrapper method for
     * {@link #setDir(kawaiiklash.Direction) setDir}.
     *
     * @param dir the {@code Direction}
     */
    public void dir(String dir) {
        setDir(Direction.valueOf(dir));
    }

    /**
     * Dynamically-called wrapper method for
     * {@link #setDelay(int) setDelay}.
     *
     * @param delay the spawn interval
     */
    public void delay(String delay) {
        setDelay(Integer.parseInt(delay));
    }

    /**
     * Dynamically-called wrapper method for
     * {@link #setMonster(java.lang.String) setMonster}.
     *
     * @param monsterName the {@code Monster Class} name
     */
    public void monster(String monsterName) {
        setMonster(monsterName);
    }

    /**
     * Dynamically-called wrapper method for
     * {@link #setSpawn(int) setSpawn}.
     *
     * @param spawn the number of spawn cycles
     */
    public void spawn(String spawn) {
        setSpawn(Integer.parseInt(spawn));
    }

    public void upsidedown(String upsidedown) {
        this.upsidedown = Boolean.parseBoolean(upsidedown);
    }

    @Override
    public void draw(Graphics g) {
        Sprite s = sprite.getSprite();
        double tx = getX() + sprite.getOffsetX();
        double ty = getY() + sprite.getOffsetY();
        if (dir != Direction.LEFT) {
            s = s.flipHorizontal();
            tx += 32;
        }
        if (upsidedown) {
            s = s.flipVertical();
            ty += 80;
        }
        s.draw((float) tx, (float) ty);
    }

    @Override
    public int getZ() {
        return Drawable.OTHER;
    }

}
