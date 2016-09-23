package kawaiiklash;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.StringTokenizer;
import java.util.function.Function;
import static kawaiiklash.Direction.DOWN;
import static kawaiiklash.Direction.LEFT;
import static kawaiiklash.Direction.NONE;
import static kawaiiklash.Direction.RIGHT;
import static kawaiiklash.Direction.UP;
import static kawaiiklash.Utility.fail;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * An {@code Entity} is a {@code Game Object} implementation that is
 * {@code Updateable}, {@code Drawable}, and {@code Interactable}. As such,
 * the {@code Entity} is a fully-functional {@code Game Object}. Most such
 * {@code Object}s are either extensions of {@code Entity} or posses very
 * similar traits. Specifically, the {@code Entity} describes something
 * that is physical and exists within the {@code Game} world. That is, an
 * {@code Entity} has position, speed, and acceleration in both
 * {@code Cartesian Diretion}s. With such, it is capable of very
 * rudimentary physics behavior.
 *
 * @author Jeff Niu
 * @version 24 February 2015
 */
public abstract class Entity implements Drawable, Updateable, Interactable {

    /**
     * The conversion unit used to convert milliseconds to seconds. This is
     * important because all speeds and accelerations are defined in terms
     * of seconds.
     */
    public static final double MILLISECONDS = 1_000.0;

    /**
     * The acceleration due to gravity in pixels per second per second
     * (p/s/s). Most {@code Entities} will use this as their standard value
     * for {@link #ddy vertical acceleration}. Those that implement this
     * value as their vertical acceleration will also use the same value in
     * pixels per second (p/s) as their
     * {@link #dyMax maximum downward speed}.
     */
    public static final double GRAVITY = 2_000.0;

    /**
     * The deceleration due to Friction experienced by the {@code Entity}.
     * When an {@code Entity} is attempting to resist forced movement, it
     * will do so at this rate.
     */
    public static final double FRICTION = 1_000.0;

    /**
     * Due to the way the {@code Sprite}s of the {@code Entity} are
     * organized, a default {@code Direction} must be set. In other words,
     * based on the {@code Sprite}s, it would appear as if the
     * {@code Entity} visual representation were "facing"
     * {@code Direction.LEFT} as their primary {@code Direction}. It is
     * important to define a default {@code Direction} because drawing a
     * reflected {@code Sprite} is more complicated than simply inverting
     * the {@code Image}.
     */
    public static final Direction DEFAULT_DIR = LEFT;

    /**
     * A constant to indicate an unlimited maximum speed.
     */
    public static final int UNLIMITED = -1;

    /**
     * The data reference path for the {@code Sprite}s of this
     * {@code Entity}.
     */
    private final String spriteRef;
    private final String soundRef;

    /**
     * the {@code Game} to which this {@code Entity} belongs.
     */
    private final Game game;

    /**
     * The horizontal position (p). Used in screen coordinates.
     */
    private double x;

    /**
     * The vertical position (p). Used in screen coordinates.
     */
    private double y;

    /**
     * The horizontal speed (p/s).
     */
    private double dx;

    /**
     * The vertical speed (p/s).
     */
    private double dy;

    /**
     * The horizontal acceleration (p/s/s).
     */
    private double ddx;

    /**
     * The vertical acceleration (p/s/s).
     */
    private double ddy;

    /**
     * The maximum horizontal speed. We wish not to have the {@code Entity}
     * accelerated to ridiculous speeds, so a maximum is set. The
     * horizontal maximum is absolute; the speed in either horizontal
     * {@code Direction}s is limited to this value. A value of
     */
    private double dxMax;

    /**
     * The maximum vertical speed. Unlike the maximum horizontal speed,
     * this maximum applies only on downward speeds. That is, upward is
     * essentially unlimited. However, it is unlikely that there will ever
     * be an {@code Entity} with an upward acceleration because of gravity.
     */
    private double dyMax;

    /**
     * The horizontal {@code Direction}.
     */
    private Direction dirX;

    /**
     * The vertical {@code Direction}.
     */
    private Direction dirY;

    /**
     * The {@code List} containing all of the {@code SpriteSheet}s that
     * represent this {@code Entity}.s
     */
    private final List<SpriteSheet> sprites;
    private final List<Sound> sounds;
    private final List<SoundEffect> effects;

    /**
     * Whether or not the {@code Entity} is active. When an {@code Entity}
     * is active, it will assume characteristics of its implementations;
     * that is, it will be {@code Interactable}, {@code Collideable}, and
     * {@code Drawable}. When it is inactive, the {@code Entity} is
     * effective removed from the {@code Game}, as if it were not even
     * there.
     */
    private boolean active;

    /**
     * Whether or not the {@code Entity} is dead. When an {@code Entity} is
     * dead, it will still be drawn; however, it will no longer be
     * {@code Collideable} nor {@code Interactable}.
     */
    private boolean dead;

    /**
     * Whether or not this {@code Entity} should update its animation. A
     * late addition to the class, so it should not affect many things. If
     * false, prevents {@link SpriteSheet#update(int)}.
     */
    private boolean animate;

    /**
     * Whether or not this {@code Entity} should be drawn. A late addition
     * to the class. If false, prevents {@link Sprite#draw(float, float)}.
     */
    private boolean draw;

    /**
     * The state is a positive integer that describes various states of the
     * {@code Entity}. For example, one may define a particular state as
     * being {@code STATE_WALK} which is an integer. This integer would be
     * used as an index, especially in the
     * {@link #sprites spritesheet list}, to tell the {@code Entity} how to
     * do various things. Rather than having an enumeration for each
     * possible state, a general integer value is much more dynamic and
     * usable.
     */
    private int state;

    /**
     * This instance initializer retrieves the reference path of this
     * {@code Entity} based on said {@code Entity}'s name. This is done
     * using {@code Reflection}. If no such reference path exists, then it
     * will set the path to null. This instance initializer will
     * immediately thereafter preload the {@code Sprite}s using the
     * {@code SpriteStore}. This is done as each {@code Entity} is
     * instantiated, but not necessarily constructed, upon level loading by
     * the {@code Parser} (reading {@code level.xml}.
     */
    {
        spriteRef = Bank.getSpriteRef(this);
        soundRef = Bank.getSoundRef(this);
        sprites = SpriteLoader.get().loadSprites(spriteRef);
        sounds = SoundLoader.get().loadSounds(soundRef);
    }

    /**
     * Constructor for an {@code Entity}.
     *
     * @param game
     */
    public Entity(Game game) {
        // Set the Game
        this.game = game;

        // Set the default Directions
        dirX = DEFAULT_DIR;
        dirY = NONE;

        // Load the sprites and sounds
        effects = new ArrayList<>(3);

        // Set the states
        active = true;
        dead = false;
        animate = true;
        draw = true;
    }

    /**
     * Set the horizontal position.
     *
     * @param x the new x-position
     */
    @Override
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Set the vertical position.
     *
     * @param y the new y-position
     */
    @Override
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Get the horizontal position.
     *
     * @return
     */
    @Override
    public double getX() {
        return x;
    }

    /**
     * Get the vertical position.
     *
     * @return
     */
    @Override
    public double getY() {
        return y;
    }

    /**
     * Get the horizontal speed.
     *
     * @return
     */
    public double getDx() {
        return dx;
    }

    /**
     * Get the vertical speed.
     *
     * @return
     */
    public double getDy() {
        return dy;
    }

    /**
     * Set the horizontal speed.
     *
     * @param dx
     */
    public void setDx(double dx) {
        this.dx = dx;
    }

    /**
     * Set the vertical speed.
     *
     * @param dy
     */
    public void setDy(double dy) {
        this.dy = dy;
    }

    /**
     * Get the horizontal acceleration.
     *
     * @return
     */
    public double getDdx() {
        return ddx;
    }

    /**
     * Get the vertical acceleration.
     *
     * @return
     */
    public double getDdy() {
        return ddy;
    }

    /**
     * Set the horizontal acceleration.
     *
     * @param ddx
     */
    public void setDdx(double ddx) {
        this.ddx = ddx;
    }

    /**
     * Set the vertical acceleration.
     *
     * @param ddy
     */
    public void setDdy(double ddy) {
        this.ddy = ddy;
    }

    /**
     * Set the maximum horizontal speed.
     *
     * @param dx
     */
    public void setMaxDx(double dx) {
        dxMax = dx;
    }

    /**
     * Set the maximum vertical speed.
     *
     * @param dy
     */
    public void setMaxDy(double dy) {
        dyMax = dy;
    }

    /**
     * Get the width of this {@code Entity}.
     *
     * @return the width
     */
    @Override
    public double getWidth() {
        return sprites.get(state).getWidth();
    }

    /**
     * Get the height of this {@code Entity}.
     *
     * @return the height
     */
    @Override
    public double getHeight() {
        return sprites.get(state).getHeight();
    }

    /**
     * Get the current horizontal offset.
     *
     * @return
     */
    @Override
    public double getOffsetX() {
        return sprites.get(state).getOffsetX();
    }

    /**
     * Get the current vertical offset.
     *
     * @return
     */
    @Override
    public double getOffsetY() {
        return sprites.get(state).getOffsetY();
    }

    /**
     * Return the {@code offsetX} of this {@code Entity}, based on a
     * specific {@code frame} of a specific {@code state}.
     *
     * @param state the state for which to get the offset
     * @param frame the frame of the state for which to get the offset
     * @return
     */
    public int getOffsetX(int state, int frame) {
        return sprites.get(state).getOffsetX(frame);
    }

    /**
     * Return the {@code offsetY} of this {@code Entity}, based on a
     * specific {@code frame} of a specific {@code state}.
     *
     * @param state the state for which to get the offset
     * @param frame the frame of the state for which to get the offset
     * @return
     */
    public int getOffsetY(int state, int frame) {
        return sprites.get(state).getOffsetY(frame);
    }

    /**
     * Get the {@code Rect} that represents this {@code Entity}.
     *
     * @return
     */
    @Override
    public Rect getHitbox() {
        Rect hitbox = new Rect();
        hitbox.x = x + getOffsetX();
        hitbox.y = y + getOffsetY();
        hitbox.width = getWidth();
        hitbox.height = getHeight();
        return hitbox;
    }

    /**
     * Get the current animation frame number.
     *
     * @return the frame number
     */
    public int getFrame() {
        return sprites.get(state).getFrame();
    }

    /**
     * Returns the integer value assigned uniquely to each state given the
     * name of that state. The name will be compared to the name of the
     * base {@code SpriteSheet} of that state.
     *
     * @param stateName the name of the state for which we are looking
     * @return the integer number representing that state in the List
     */
    public int getStateNumber(String stateName) {
        int stateNumber = -1;
        if (sprites == null) {
            return stateNumber;
        }
        Function<String, Integer> f = (s) -> {
            for (int i = 0; i < sprites.size(); i++) {
                if (sprites.get(i).getName().equals(s)) {
                    return i;
                }
            }
            return -1;
        };
        stateNumber = f.apply(stateName);
        if (stateNumber == -1) {
            stateNumber = f.apply(stateName + "1");
        }
        return stateNumber;
    }

    /**
     * Get an array that is composed of all the state numbers of a set of
     * related states.
     *
     * @param stateName the encompassing state name
     * @return an array containing the state numbers
     */
    public int[] getStateNumberSet(String stateName) {
        String stateCarrier = "";
        for (int i = 0; i < sprites.size(); i++) {
            String name = sprites.get(i).getName();
            StringBuilder sb = new StringBuilder(name.length());
            for (char c : name.toCharArray()) {
                if (Character.isLetter(c)) {
                    sb.append(c);
                }
            }
            String attempt = sb.toString();
            if (attempt.equals(stateName)) {
                stateCarrier += String.valueOf(i) + " ";
            } else if ((attempt + ".").equals(stateName)) {
                stateCarrier += String.valueOf(i) + " ";
            }
        }
        StringTokenizer tokenizer = new StringTokenizer(stateCarrier, " ");
        int[] numbers = new int[tokenizer.countTokens()];
        for (int i = 0; i < numbers.length; i++) {
            numbers[i] = Integer.parseInt(tokenizer.nextToken());
        }
        return numbers;
    }

    public int getSoundNumber(String soundName) {
        int soundNumber = -1;
        for (int i = 0; i < sounds.size(); i++) {
            if (sounds.get(i).getName().equals(soundName)) {
                soundNumber = i;
                break;
            }
        }
        return soundNumber;
    }

    /**
     * Change the Player state to another animation state. When changing,
     * reset the animation counter of the state to which we are changing.
     * Do not reset if we are already in that state.
     *
     * @param state the number representing the state to which we change
     */
    public void changeToState(int state) {
        if (getState() != state) {
            setState(state);
            getSprites().get(state).setCount(0);
            getSprites().get(state).setFrame(0);
        }
    }

    /**
     * Returns the total delay for a state; that is, gives the sum of the
     * delays for each frame of a state.
     *
     * @param state the state whose total delay is to be found
     * @return the total delay
     */
    public int getStateDelay(int state) {
        return sprites.get(state).getTotalDelay();
    }

    /**
     * Get an array that is composed of the total delays of all the state
     * numbers specified in another array.
     *
     * @param stateNums the array of state numbers
     * @return an array of delays
     */
    public int[] getStateDelay(int[] stateNums) {
        int[] delaySet = new int[stateNums.length];
        for (int i = 0; i < stateNums.length; i++) {
            delaySet[i] = getStateDelay(stateNums[i]);
        }
        return delaySet;
    }

    /**
     * Return the frame number of the current state.
     *
     * @param state
     * @return
     */
    public int getStateFrame(int state) {
        return sprites.get(state).getFrame();
    }

    /**
     * Whether this {@code SpriteSheet} will cycle its animation frames.
     *
     * @param state
     * @param handle
     */
    public void cycleFrames(int state, boolean handle) {
        sprites.get(state).cycleFrames(handle);
    }

    /**
     * Whether this set of {@code SpriteSheet}s will cycle its animation
     * frames.
     *
     * @param states
     * @param handle
     */
    public void cycleFrames(int[] states, boolean handle) {
        for (int st : states) {
            Entity.this.cycleFrames(st, handle);
        }
    }

    /**
     * Checks whether or not this {@code Entity} is currently within the
     * screen bounds.
     */
    public void checkBounds() {
        final Rect screen = getGame().getScreen();
        final Rect sBox = new Rect(0.0, 0.0, screen.getWidth(), screen.getHeight());
        if (getHitbox().intersects(sBox)) {
            isInBounds();
        } else if (x + getOffsetX() > screen.getWidth()) {
            isOutOfBounds(RIGHT);
        } else if (x + getOffsetX() + getWidth() < 0) {
            isOutOfBounds(LEFT);
        } else if (y + getOffsetY() > screen.getHeight()) {
            isOutOfBounds(DOWN);
        } else if (y + getOffsetY() + getHeight() < 0) {
            isOutOfBounds(UP);
        }
    }

    /**
     * Notify the {@code Entity} that it has left the screen bounds in a
     * particular {@code Direction}.
     *
     * @param dir
     */
    public abstract void isOutOfBounds(Direction dir);

    /**
     * Notify the {@code Entity} that is has re-entered the screen bounds.
     */
    public abstract void isInBounds();

    /**
     * Request that the {@code Entity} update its horizontal and vertical
     * {@code Direction}s.
     */
    public void updateDirection() {
        if (dx > 0) {
            dirX = RIGHT;
        }
        if (dx < 0) {
            dirX = LEFT;
        }
        if (dy > 0) {
            dirY = DOWN;
        }
        if (dy < 0) {
            dirY = UP;
        }
    }

    /**
     * Request that the {@code Entity} update its position and speeds.
     *
     * @param dt the amount of passed time
     */
    public void move(int dt) {
        double t = dt / MILLISECONDS;
        if (dxMax == UNLIMITED) {
            dx += ddx * t;
        } else {
            if (ddx > 0) {
                dx += dx + ddx * t < dxMax ? ddx * t : 0;
            }
            if (ddx < 0) {
                dx += dx + ddx * t > -dxMax ? ddx * t : 0;
            }
        }
        if (dyMax == UNLIMITED || dy < dyMax) {
            dy += ddy * t;
        }
        x += dx * t;
        y += dy * t;
    }

    /**
     * Request that the {@code Entity} update its gameplay and animation
     * states.
     *
     * @param dt
     */
    public abstract void updateState(int dt);

    /**
     * Request that the {@code Entity} update itself based on the amount of
     * passed time. All other updating methods are called within in a
     * particular order.
     *
     * @param dt the amount of passed time (milliseconds)
     */
    @Override
    public void update(int dt) {
        final List<SoundEffect> removeEffects = new ArrayList<>(10);
        for (final SoundEffect effect : effects) {
            effect.update(dt);
            if (effect.played()) {
                removeEffects.add(effect);
            }
        }
        effects.removeAll(removeEffects);
        checkBounds();
        if (active) {
            updateDirection();
            if (!dead) {
                move(dt);
            }
            updateState(dt);
            if (animate) {
                sprites.get(state).update(dt);
            }
        }
    }

    /**
     * Notify the {@code Entity} that is has collided with another
     * {@code Collideable} game object.
     *
     * @param other the other {@code Collideable} with which a collision has
     * occurred
     */
    @Override
    public abstract void collidedWith(Collideable other);

    /**
     * Whether or not the {@code Entity} can collide. If it is both alive
     * and active, then it is {@code Collideable}.
     *
     * @return
     */
    @Override
    public boolean canCollide() {
        return !dead && active;
    }

    /**
     * Notification that the {@code Entity} is dead. Depending on the
     * {@code Entity}, being "dead" might have different meanings.
     * Generally, if the {@code Entity} has health of any sort, when that
     * health runs out, it will be declared "dead".
     */
    public abstract void died();

    /**
     * Draw the {@code Entity}.
     *
     * @param g the Graphics object to which to draw the Entity
     */
    @Override
    public void draw(Graphics g) {
        if (draw) {
            drawEntity(g);
        }
        if (game.debugging()) {
            final Rect r = getHitbox();
            final float tx = (float) r.getX();
            final float ty = (float) r.getY();
            final float tw = (float) r.getWidth();
            final float th = (float) r.getHeight();
            g.setColor(Color.red);
            g.drawRect(tx, ty, tw, th);
        }
    }

    protected void drawEntity(Graphics g) {
        final SpriteSheet ss = sprites.get(state);
        Sprite s = ss.getSprite();
        final double ex;
        final double ey = y + ss.getOffsetY();
        if (dirX == DEFAULT_DIR) {
            ex = x + ss.getOffsetX();
        } else {
            ex = x + sprites.get(0).getOffsetX();
            s = s.flipHorizontal();
        }
        s.draw((float) ex, (float) ey);
    }

    /**
     * Get the {@code Game}.
     *
     * @return
     */
    public Game getGame() {
        return game;
    }

    /**
     * Get the {@code List} of {@code SpriteSheet}s.
     *
     * @return
     */
    public List<SpriteSheet> getSprites() {
        return Collections.unmodifiableList(sprites);
    }

    public List<Sound> getSounds() {
        return Collections.unmodifiableList(sounds);
    }

    /**
     * Get the current {@code Entity} state.
     *
     * @return
     */
    public int getState() {
        return state;
    }

    /**
     * Set the state of this {@code Entity}.
     *
     * @param state
     */
    public void setState(int state) {
        try {
            getSprites().get(state);
        } catch (ArrayIndexOutOfBoundsException ex) {
            fail(String.format("Could not set state %d for Entity %s", state, this), ex);
        }
        this.state = state;
    }

    /**
     * Get the horizontal {@code Direction}.
     *
     * @return
     */
    @Override
    public Direction getDirX() {
        return dirX;
    }

    /**
     * Get the vertical {@code Direction}.
     *
     * @return
     */
    @Override
    public Direction getDirY() {
        return dirY;
    }

    /**
     * Set the horizontal {@code Direction}.
     *
     * @param direction
     */
    public void setDirX(Direction direction) {
        this.dirX = direction;
    }

    /**
     * Set the vertical {@code Direction}.
     *
     * @param direction
     */
    public void setDirY(Direction direction) {
        this.dirY = direction;
    }

    /**
     * Check whether or not this {@code Entity} is active.
     *
     * @return
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Set whether or not this {@code Entity} is currently active.
     *
     * @param active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Check whether or not this {@code Entity} is dead.
     *
     * @return
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Set whether or not this {@code Entity} is dead.
     *
     * @param dead
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }

    /**
     * Dynamically-called set x.
     *
     * @param x
     */
    @Parsable
    public void x(String x) {
        setX(Integer.parseInt(x));
    }

    /**
     * Dynamically-called set y.
     *
     * @param y
     */
    @Parsable
    public void y(String y) {
        setY(Integer.parseInt(y));
    }

    /**
     * Dynamically-called set {@code Direction}.
     *
     * @param dir
     */
    @Parsable
    public void dir(String dir) {
        setDirX(Direction.valueOf(dir));
    }

    /**
     * Dynamically-called set state.
     *
     * @param state
     */
    @Parsable
    public void state(String state) {
        setState(Integer.parseInt(state));
    }

    public void addSound(String name, int state, int frame) {
        effects.add(new SoundEffect(name, state, frame));
    }

    public void removeSound(final String name) {
        final List<SoundEffect> remove = new ArrayList<>(effects.size());
        for (final SoundEffect effect : effects) {
            if (effect.getName().equals(name)) {
                effect.getSound().getAudio().stop();
                remove.add(effect);
            }
        }
        effects.removeAll(remove);
    }

    public void addSound(String name, int[] stateSet, int frame) {
        for (int subState : stateSet) {
            addSound(name, subState, frame);
        }
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public void setDraw(boolean draw) {
        this.draw = draw;
    }

    private class SoundEffect implements Updateable {

        private final Sound sound;
        private final int state;
        private final int frame;
        private final String name;
        private boolean played;

        private SoundEffect(String name, int state, int frame) {
            this.state = state;
            this.frame = frame;
            this.name = name;
            played = false;
            sound = sounds.get(getSoundNumber(name));
        }

        private void play(float pitch, float gain) {
            sound.playEffect(pitch, gain, false);
        }

        private boolean shouldPlay() {
            int entState = Entity.this.state;
            int entFrame = getStateFrame(entState);
            return entState == state && entFrame == frame;
        }

        @Override
        public void update(int dt) {
            if (!played && shouldPlay()) {
                played = true;
                play(game.getPitch(), game.getGain());
            }
        }

        private boolean played() {
            return played && !sound.getAudio().isPlaying();
        }

        private String getName() {
            return name;
        }

        private Sound getSound() {
            return sound;
        }

    }

}
