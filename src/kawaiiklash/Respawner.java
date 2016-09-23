package kawaiiklash;

import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Direction.RIGHT;
import org.newdawn.slick.Graphics;

/**
 * A {@code Respawner} is a {@code Game Reactor} that controls a
 * {@code Monster} such that, when it dies, it will be respawned in its
 * original position.
 *
 * @author Jeff Niu
 */
public class Respawner extends Reactor implements Drawable {

    /**
     * A value of {@link #delay delay} to indicate that the {@code Monster}
     * will not respond.
     */
    private static final int NO_RESPAWN = -1;

    /**
     * A value of {@link #respawn respawn} to indicate that the
     * {@code Monster} can respawn indefinitely.
     */
    private static final int UNLIMITED = -1;

    /**
     * The sprite representing this respawner.
     */
    private final SpriteSheet sprite;

    /**
     * The {@code Class} name of the {@code Monster} that will be respawned
     * and is being controlled by this {@code Respawner}.
     */
    private String monsterName;

    /**
     * The current {@code Monster} instance.
     */
    private Monster monster;

    /**
     * The value of the horizontal position.
     */
    private double x;

    /**
     * The value of the vertical position.
     */
    private double y;

    /**
     * The {@code Direction} of the {@code Monster} when it spawns.
     */
    private Direction dir;

    private boolean upsidedown;

    /**
     * The counter for this {@code Respawner}.
     */
    private int t;

    /**
     * The delay in milliseconds after the {@code Monster}
     * {@link #monster instance} has died until the new {@code Monster}
     * instance spawns.
     */
    private int delay;

    /**
     * The amount of times the {@code Monster} can respawn.
     */
    private int respawn;

    /**
     * A flag indicating that the {@code Monster} has died and will need a
     * respawn after the delay.
     */
    private boolean needRespawn;

    /**
     * Create a {@code Respawner} in a {@code Game}.
     *
     * @param game the {@code Game}
     */
    public Respawner(Game game) {
        super(game);
        monsterName = null;
        monster = null;
        x = 0;
        y = 0;
        dir = LEFT;
        t = 0;
        delay = NO_RESPAWN;
        respawn = UNLIMITED;
        needRespawn = false;
        sprite = SpriteLoader.get().loadSprites(Bank.getSpriteRef("Spawner")).get(0);
        upsidedown = false;
    }

    /**
     * Create a new instance of the {@code Monster} as per the
     * {@link #monsterName monster name} and add it to the {@code Game}.
     */
    private void respawn() {
        monster = Spawner.createMonsterInstance(monsterName, getGame());
        monster.setX(x);
        monster.setY(y);
        if (dir == LEFT || dir == RIGHT) {
            monster.setDirX(dir);
        }
        getGame().add(monster);
    }

    /**
     * Set the {@code Class} name of the {@code Monster} that is being
     * controlled and respawned by this {@code Respawned}.
     *
     * @param monsterName the {@code Class} name
     */
    public void setMonster(String monsterName) {
        if (this.monsterName == null) {
            this.monsterName = monsterName;
            respawn();
        }
    }

    /**
     * Set the value of the horizontal position.
     *
     * @param x the x-position
     */
    @Override
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set the value of the vertical position.
     *
     * @param y the y-position
     */
    @Override
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the value of the horizontal position.
     *
     * @return the x-position
     */
    @Override
    public double getX() {
        return x;
    }

    /**
     * Get the value of the vertical position.
     *
     * @return the y-position
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * Set the {@code Direction} of the spawned {@code Monster}.
     *
     * @param dir the {@code Direction}
     */
    public void setDir(Direction dir) {
        this.dir = dir;
    }

    /**
     * Set the delay after {@code Monster} death until the next respawn.
     *
     * @param delay the delay
     */
    public void setDelay(int delay) {
        this.delay = delay;
    }

    /**
     * Set the number of respawns of the {@code Monster}.
     *
     * @param respawn the number of respawns
     */
    public void setRespawn(int respawn) {
        this.respawn = respawn;
    }

    /**
     * Determine whether or not the {@code Respawner} is in bounds. That
     * is, check if its {@code Rect} representation intersects with the
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
     * Update the {@code Respawner}. If the
     * {@link #monster monster instance} has died, reset the counter and
     * indicate that a respawn is needed. If it is in bounds, then it will
     * check to see if it needs a respawn. If there are no more respawns,
     * the {@code Respawner} will remove itself from the {@code Game}.
     *
     * @param dt
     */
    @Override
    public void update(int dt) {
        if (monster.isDead() && !needRespawn) {
            needRespawn = true;
            t = 0;
        }
        if (inBounds()) {
            t += dt;
            if (needRespawn && t != NO_RESPAWN && t > delay) {
                needRespawn = false;
                t = 0;
                if (respawn != UNLIMITED) {
                    respawn--;
                }
                respawn();
            }
        }
        if (respawn == 0) {
            getGame().remove(this);
        }
        sprite.update(dt);
    }

    /**
     * Dynamically-called wrapper method for {@link #setX(double) setX}. s
     *
     * @param x the x-position
     */
    @Parsable
    public void x(String x) {
        setX(Double.parseDouble(x));
    }

    /**
     * Dynamically-called wrapper method for {@link #setY(double) setY}.
     *
     * @param y the y-position
     */
    @Parsable
    public void y(String y) {
        setY(Double.parseDouble(y));
    }

    /**
     * Dynamically-called wrapper method for
     * {@link #setDir(kawaiiklash.Direction) setDir}.
     *
     * @param dir the {@code Direction}
     */
    @Parsable
    public void dir(String dir) {
        setDir(Direction.valueOf(dir));
    }

    /**
     * Dynamically-called wrapper method for
     * {@link #setDelay(int) setDelay}.
     *
     * @param delay the spawn interval
     */
    @Parsable
    public void delay(String delay) {
        setDelay(Integer.parseInt(delay));
    }

    /**
     * Dynamically-called wrapper method for
     * {@link #setMonster(java.lang.String) setMonster}.
     *
     * @param monsterName the {@code Monster Class} name
     */
    @Parsable
    public void monster(String monsterName) {
        setMonster(monsterName);
    }

    /**
     * Dynamically-called wrapper method for
     * {@link #setRespawn(int) setRespawn}.
     *
     * @param respawn the number of spawn cycles
     */
    @Parsable
    public void respawn(String respawn) {
        setRespawn(Integer.parseInt(respawn));
    }

    public void upsidedown(String upsidedown) {
        this.upsidedown = Boolean.parseBoolean(upsidedown);
    }

    @Override
    public void draw(Graphics g) {
        Sprite s = sprite.getSprite();
        double tx = (getX() + sprite.getOffsetX());
        double ty = (getY() + sprite.getOffsetY());
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
