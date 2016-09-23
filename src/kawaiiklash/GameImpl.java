package kawaiiklash;

import java.awt.Toolkit;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static kawaiiklash.Utility.arraycopy;
import static kawaiiklash.Utility.fail;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

/**
 * The core of the game. This class handles everything that relates to
 * playing the game, including keyboard input and updating and drawing all
 * the entities.
 *
 * @author Jeff Niu
 */
public class GameImpl extends BasicGameState implements Game, InputProviderListener {

    /**
     * References for all the levels.
     */
    public static final String[][] MAPS = new String[][]{
        {
            "levels/level1.xml",
            "levels/level2.xml",
            "levels/level3.xml",
            "levels/level4.xml",
            "levels/level5.xml"
        },
        {
            "levels/reference.xml"
        },
        {
            "levels/train1.xml",
            "levels/train2.xml",
            "levels/train3.xml"
        }
    };

    /**
     * The minimum value of the change in time for the game to update. This
     * forces a minimum update speed, but also prevents large lag spikes
     * that may mess with game logic.
     */
    public static final int MINIMUM_TIME_RESOLUTION = 35;
    /**
     * A ratio value for the border described by the game screen where the
     * player causes the screen to scroll.
     */
    private static final double BORDER = 7.0 / 16.0;

    /**
     * A reference to the master.
     */
    private Master master;
    /**
     * The array of worlds that contain the level references.
     */
    private String[][] worlds;
    /**
     * The current player type. When the player is created, it should be
     * created to this player type.
     */
    private PlayerType playerType;

    /**
     * The current world that is being played.
     */
    private int world;
    /**
     * The current level of the current that is being played.
     */
    private int level;

    /**
     * The {@code Rect} that represents the screen.
     */
    private Rect screen;
    /**
     * This {@code Rect} represents the area in which the player may be
     * before it causes the screen to scroll.
     */
    private Rect border;

    /**
     * The input provider for the game. This object handles all keyboard
     * input and mouse input.
     */
    private InputProvider provider;

    /**
     * The array of booleans for each key on the keyboard representing
     * whether or not it is currently pressed.
     */
    private boolean[] keyDown;
    /**
     * A boolean for each direction that dictates whether or not it should
     * scroll in that direction.
     */
    private boolean[] scroll;

    /**
     * The list of all the game objects.
     */
    private List<Object> objects;
    /**
     * The list of game objects that should be added at the start of the
     * next update cycle.
     */
    private List<Object> remove;
    /**
     * The list of game objects that should be removed at the start of the
     * next update cycle.
     */
    private List<Object> add;

    /**
     * A reference for the current player that is in the game.
     */
    private Player player;
    /**
     * The heads up display for the game.
     */
    private HUD headsUpDisplay;
    /**
     * An internal list of references for all the bosses that are on the
     * current level, if any.s
     */
    private List<Boss> bosses;

    /**
     * The volume pitch.
     */
    private float pitch;
    /**
     * The volume gain.
     */
    private float gain;

    /**
     * Whether or not the game is in debugging mode.
     */
    private boolean debugging;

    /**
     * Create a game using the default maps, starting at level 1 with no
     * specified player type.
     *
     * @param master
     */
    public GameImpl(Master master) {
        this(master, MAPS, 0, null);
    }

    public GameImpl(Master master, String map, PlayerType playerType) {
        this(master, new String[][]{{map}}, 0, playerType);
    }

    public GameImpl(Master master, String[][] worlds, int level, PlayerType playerType) {
        this.worlds = worlds;
        this.playerType = playerType;
        this.level = level;
        this.master = master;
        pitch = 1.0f;
        gain = 1.0f;
    }

    /**
     * Set the level set that is being played.
     *
     * @param worlds
     */
    protected void setWorlds(String[][] worlds) {
        this.worlds = worlds;
    }

    /**
     * Set the current player type.
     *
     * @param playerType
     */
    @Override
    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    /**
     * Set the current level.
     *
     * @param level
     */
    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    /**
     * Set the current world.
     *
     * @param world
     */
    @Override
    public void setWorld(int world) {
        this.world = world;
    }

    /**
     * Initialize the screen and border. Initialize the provider and bind
     * all the commands.
     *
     * @param gc
     * @param master
     * @throws SlickException
     */
    @Override
    public void init(final GameContainer gc, final StateBasedGame master) throws SlickException {
        final Dimensions dScreen = new Dimensions(Toolkit.getDefaultToolkit().getScreenSize());
        screen = new Rect(0.0, 0.0, dScreen.getWidth(), dScreen.getHeight());
        border = new Rect(screen.getWidth() * BORDER, screen.getHeight() * BORDER, 1.0, 1.0);

        provider = new InputProvider(gc.getInput());
        provider.addListener(this);
        provider.setActive(false);

        final Command[] commands = new Command[Keyboard.KEYBOARD_SIZE];
        for (int i = 0; i < commands.length; i++) {
            commands[i] = new KeyCommand(i);
            provider.bindCommand(new KeyControl(i), commands[i]);
        }

        provider.bindCommand(new KeyControl(Keyboard.KEY_ESCAPE), new Action(this::returnToMenu));
        provider.bindCommand(new KeyControl(Keyboard.KEY_0), new Action(this::playerDead));
        provider.bindCommand(new KeyControl(Keyboard.KEY_9), new Action(this::levelCompleted));
        provider.bindCommand(new KeyControl(Keyboard.KEY_8), new Action(() -> {
            debugging = !debugging;
        }));

        keyDown = new boolean[Keyboard.KEYBOARD_SIZE];

        scroll = new boolean[Direction.DIRECTIONS];
        objects = new ArrayList<>(0);
        remove = new ArrayList<>(0);
        add = new ArrayList<>(0);
        bosses = new ArrayList<>(0);

        debugging = false;
    }

    /**
     * Add and remove all objects if necessary and update the game if the
     * minimum time resolution is met.Update all the game objects. Updating
     * must occur in the following order: first, the standard update,
     * during which time-dependent updates are made. This includes state
     * changing, etc. Next, all collisions are calculated. This occurs
     * after the standard update because that is when things will be moved.
     * Then, game logic will be resolved, the most important of which is
     * collisions.
     *
     * @param gc
     * @param master
     * @param dt
     * @throws SlickException
     */
    @Override
    public void update(GameContainer gc, StateBasedGame master, int dt) throws SlickException {
        exchangeObjects();

        if (dt >= MINIMUM_TIME_RESOLUTION) {
            dt = MINIMUM_TIME_RESOLUTION;
        }

        for (final Object obj : objects) {
            if (obj instanceof Updateable) {
                ((Updateable) obj).update(dt);
            }
        }
        final List<Collideable> cols = new ArrayList<>(objects.size());
        for (final Object o : objects) {
            if (o instanceof Collideable) {
                final Collideable c = (Collideable) o;
                if (c.canCollide()) {
                    cols.add(c);
                }
            }
        }
        final Collideable[] c = cols.toArray(new Collideable[cols.size()]);
        for (int n = 0; n < c.length; n++) {
            for (int k = n + 1; k < c.length; k++) {
                if (c[n].collidesWith(c[k])) {
                    c[n].collidedWith(c[k]);
                    c[k].collidedWith(c[n]);
                }
            }
        }

        for (final Object obj : objects) {
            if (obj instanceof Logicable) {
                ((Logicable) obj).doLogic();
            }
        }

    }

    /**
     * Draw all game objects that can be drawn.
     *
     * @param gc
     * @param master
     * @param g
     * @throws SlickException
     */
    @Override
    public void render(GameContainer gc, StateBasedGame master, Graphics g) throws SlickException {
        g.setWorldClip(0.0f, 0.0f, (float) screen.getWidth(), (float) screen.getHeight());
        for (final Object obj : objects) {
            if (obj instanceof Drawable) {
                ((Drawable) obj).draw(g);
            }
        }
    }

    /**
     * Active the provider only when the player is in the game.
     *
     * @param gc
     * @param master
     */
    @Override
    public void enter(GameContainer gc, StateBasedGame master) {
        provider.setActive(true);
    }

    /**
     * Disable the provider when the player leaves the game.
     *
     * @param gc
     * @param master
     */
    @Override
    public void leave(GameContainer gc, StateBasedGame master) {
        provider.setActive(false);
    }

    @Override
    public int getID() {
        return Master.GAME;
    }

    /**
     * When a certain key is pressed, the boolean corresponding to that is
     * set to true. If a key bound to a command is pressed, execute that
     * command.
     *
     * @param cmd
     */
    @Override
    public void controlPressed(Command cmd) {
        if (cmd instanceof KeyCommand) {
            keyDown[((KeyCommand) cmd).getKey()] = true;
        }
        if (cmd instanceof Action) {
            ((Action) cmd).execute();
        }
    }

    /**
     * If a key is released, set the corresponding boolean flag to false.
     *
     * @param cmd
     */
    @Override
    public void controlReleased(Command cmd) {
        if (cmd instanceof KeyCommand) {
            keyDown[((KeyCommand) cmd).getKey()] = false;
        }
    }

    /**
     * Start playing the game.
     *
     * @throws SlickException
     */
    @Override
    public void begin() throws SlickException {
        nextLevel();
    }

    /**
     * Add and remove all objects.
     */
    protected void exchangeObjects() {
        objects.addAll(add);
        objects.removeAll(remove);
        add.clear();
        remove.clear();
    }

    /**
     * Load the next level.
     *
     * @throws SlickException
     */
    private void nextLevel() throws SlickException {
        // Create the heads up display
        headsUpDisplay = new HUD(this);

        // Load all the game objects
        final Parser parser = Parser.get();
        final String levelRef = worlds[world][level];
        try {
            objects = parser.loadLevel(this, levelRef);
        } catch (final SlickException ex) {
            throw ex;
        }

        // Retrieve the level configuration
        LevelConfiguration levelConfig = null;
        for (final Object obj : objects) {
            if (obj instanceof LevelConfiguration) {
                levelConfig = (LevelConfiguration) obj;
                break;
            }
        }

        // Set up the screen and ensure that the level configuration exists
        if (levelConfig != null) {
            final Vector screenLoc = levelConfig.getScreen().getPosition();
            final double sx = screenLoc.getX();
            final double sy = screenLoc.getY();
            screen.setRect(sx, sy, screen.getWidth(), screen.getHeight());
            final double bw = screen.getWidth() - 2 * border.getX();
            final double bh = screen.getHeight() - 2 * border.getY();
            border.setRect(border.getX(), border.getY(), bw, bh);
            scroll = levelConfig.canScroll();
        } else {
            fail(new SlickException("LevelConfiguration object not specified in level: " + levelRef));
        }

        // Ensure that there is only one player entity
        int playerCount = 0;
        for (Object obj : objects) {
            if (obj instanceof Player) {
                playerCount++;
            }
        }
        if (playerCount != 1) {
            fail(new SlickException("Level must have only one Player Entity: " + levelRef));
        }

        // Sort all the game objects according to the draw order
        final int size = objects.size();
        final List<Background> backgrounds = new ArrayList<>(size);
        final List<Background> foregrounds = new ArrayList<>(size);
        final List<Platform> tiles = new ArrayList<>(size);
        final List<Monster> monsters = new ArrayList<>(size);
        final List<Item> items = new ArrayList<>(size);
        final List<Object> otherObjects = new ArrayList<>(size);
        player = null;
        for (final Object obj : objects) {
            if (obj instanceof Background) {
                Background bck = (Background) obj;
                if (bck.isForeground()) {
                    foregrounds.add(bck);
                } else {
                    backgrounds.add(bck);
                }
            } else if (obj instanceof Platform) {
                tiles.add((Platform) obj);
            } else if (obj instanceof Monster) {
                monsters.add((Monster) obj);
            } else if (obj instanceof Player) {
                player = (Player) obj;
            } else if (obj instanceof Item) {
                items.add((Item) obj);
            } else {
                otherObjects.add(obj);
            }
            if (obj instanceof Boss) {
                bosses.add((Boss) obj);
            }
        }
        objects.clear();
        objects.addAll(backgrounds);
        objects.addAll(otherObjects);
        objects.addAll(tiles);
        objects.addAll(monsters);
        objects.addAll(items);

        // Instantiate the player
        final Package[] packages = Package.getPackages();
        Class<?> playerClass = null;
        Constructor<?> playerConstructor = null;
        for (final Package p : packages) {
            final String tentative = p.getName() + "." + playerType.toString();
            try {
                playerClass = Class.forName(tentative);
                playerConstructor = playerClass.getConstructor(Player.class);
            } catch (final ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
                continue;
            }
            break;
        }
        if (playerClass != null && playerConstructor != null) {
            try {
                player = (Player) playerConstructor.newInstance(player);
            } catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                fail("Could not instantiate the Player", ex);
            }
        } else {
            fail("Player class type does not exist: " + playerType.toString());
        }

        // Finally, add the player, the foregrounds, and the HUD
        objects.add(player);
        objects.addAll(foregrounds);
        objects.add(headsUpDisplay);
    }

    @Override
    public List<Object> getObjects() {
        return Collections.unmodifiableList(objects);
    }

    @Override
    public void add(Object o) {
        add.add(o);
    }

    @Override
    public void remove(Object o) {
        remove.add(o);
    }

    @Override
    public boolean objectAddQueued(Object o) {
        return add.contains(o);
    }

    @Override
    public boolean[] keyDown() {
        return keyDown;
    }

    @Override
    public Rect getScreen() {
        return screen;
    }

    @Override
    public Rect getBorder() {
        return border;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    @Override
    public void xScroll(double xScroll) {
        if (xScroll != 0) {
            if (scroll[Direction.xDirOf(xScroll).index()]) {
                screen.setRect(screen.getX() + xScroll, screen.getY(), screen.getWidth(), screen.getHeight());
                for (Object obj : objects) {
                    if (obj instanceof Cartesian) {
                        Cartesian ent = (Cartesian) obj;
                        ent.moveX(-xScroll);
                    }
                }
                if (screen.getX() < 0) {
                    xScroll(-screen.getX());
                }
            }
        }
    }

    @Override
    public void yScroll(double yScroll) {
        if (yScroll != 0) {
            if (scroll[Direction.yDirOf(yScroll).index()]) {
                screen.setRect(screen.getX(), screen.getY() + yScroll, screen.getWidth(), screen.getHeight());
                for (Object obj : objects) {
                    if (obj instanceof Cartesian) {
                        Cartesian ent = (Cartesian) obj;
                        ent.moveY(-yScroll);
                    }
                }
                if (screen.getY() < 0) {
                    yScroll(-screen.getY());
                }
            }
        }
    }

    @Override
    public void playerDead() {
        try {
            nextLevel();
        } catch (SlickException ex) {
            fail(ex);
        }
    }

    /**
     * If all the levels in the world are completed, reset the level
     * counter and move to the next world. If all worlds are completed,
     * return to the title screen.
     */
    @Override
    public void levelCompleted() {
        boolean next = true;
        level++;
        try {
            String s = worlds[world][level];
        } catch (ArrayIndexOutOfBoundsException e) {
            world++;
            level = 0;
            try {
                String s = worlds[world][level];
            } catch (ArrayIndexOutOfBoundsException ex) {
                next = false;
                returnToMenu();
            }
        }
        if (next) {
            try {
                nextLevel();
            } catch (SlickException ex) {
                fail("Cannot find level:" + worlds[world][level], ex);
            }
        }
    }

    @Override
    public void returnToMenu() {
        master.enterState(Master.MENU);
    }

    public InputProvider getProvider() {
        return provider;
    }

    public void setScroll(boolean[] scroll) {
        this.scroll = arraycopy(scroll);
    }

    @SuppressWarnings("AssignmentToCollectionOrArrayFieldFromParameter")
    public void setObjects(List<Object> objects) {
        this.objects = objects;
    }

    @Override
    public float getPitch() {
        return pitch;
    }

    @Override
    public float getGain() {
        return gain;
    }

    @Override
    public HUD getHUD() {
        return headsUpDisplay;
    }

    @Override
    public boolean debugging() {
        return debugging;
    }

    /**
     * Set whether or not we are currently in debugger mode. For use in the
     * subclass.
     *
     * @param bool
     */
    public void debugging(boolean bool) {
        debugging = bool;
    }

}
