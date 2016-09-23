package kawaiiklash;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProvider;
import org.newdawn.slick.command.KeyControl;
import org.newdawn.slick.state.StateBasedGame;

/**
 * The game editor is an extension of the game implementation. Unlike the
 * real game, the user cannot control the player entity. Instead, they can
 * scroll around the screen and see what the level looks like. The game can
 * be permanently paused, in that the update time is zero, and it can be
 * un-paused to see how the level plays out. The advantage to game editor
 * mode is that pressing enter will reload the level, which will include
 * any changes to the level file. This way, changes made to the level file
 * can be seen directly on the level as they are made, to much better
 * facilitate the level editing process.
 *
 * @see GameImpl
 * @author Jeff Niu
 */
public class GameEditor extends GameImpl {

    private static final PlayerType DEFAULT_PLAYER = PlayerType.Cory;
    private static final double SCROLL_SPEED = 1_000.0;
    private static final double MILLISECONDS = 1_000.0;

    private String data;

    private boolean[] keyPressed;
    private boolean update;
    private boolean useQuad;

    private QuadTree quad;

    public GameEditor(Master master) {
        this(master, null);
    }

    public GameEditor(Master master, String level) {
        super(master, "levels/" + level + ".xml", DEFAULT_PLAYER);
        data = "levels/" + level + ".xml";
    }

    public String getData() {
        return data;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame master) throws SlickException {
        super.init(gc, master);

        keyPressed = new boolean[Keyboard.KEYBOARD_SIZE];
        update = false;
        useQuad = false;
        debugging(true);

        final Rect screen = getScreen();
        final Vector qCenter = new Vector(screen.getWidth() / 2, screen.getHeight() / 2);
        final AABB qBounds = new AABB(qCenter, screen.getWidth(), screen.getHeight());
        quad = new QuadTree(0, qBounds);

        final InputProvider provider = getProvider();
        Command[] commands = new Command[Keyboard.KEYBOARD_SIZE];
        for (int i = 0; i < commands.length; i++) {
            commands[i] = new KeyCommand(i);
            provider.bindCommand(new KeyControl(i), commands[i]);
        }
        provider.bindCommand(new KeyControl(Keyboard.KEY_ESCAPE), new Action(this::returnToMenu));
        provider.bindCommand(new KeyControl(Keyboard.KEY_RETURN), new Action(this::reload));
        provider.bindCommand(new KeyControl(Keyboard.KEY_0), new Action(() -> {
            update = !update;
        }));
        provider.bindCommand(new KeyControl(Keyboard.KEY_8), new Action(() -> {
            debugging(!debugging());
        }));
        provider.bindCommand(new KeyControl(Keyboard.KEY_9), new Action(() -> {
            useQuad = !useQuad;
        }));

        exchangeObjects();
    }

    @Override
    public void update(GameContainer gc, StateBasedGame master, int dt) throws SlickException {
        super.update(gc, master, update ? dt : 0);
        final double delta = dt / MILLISECONDS * SCROLL_SPEED;
        if (keyPressed[Keyboard.KEY_LEFT]) {
            xScroll(-delta);
        } else if (keyPressed[Keyboard.KEY_RIGHT]) {
            xScroll(delta);
        }
        if (keyPressed[Keyboard.KEY_UP]) {
            yScroll(-delta);
        } else if (keyPressed[Keyboard.KEY_DOWN]) {
            yScroll(delta);
        }
        if (useQuad) {
            quad.clear();
            final List<Object> objs = getObjects();
            for (final Object o : objs) {
                if (o instanceof Collideable) {
                    final AABB box = new AABB(((Collideable) o).getHitbox());
                    quad.insert(box);
                }
            }
        }
    }

    @Override
    public void render(GameContainer gc, StateBasedGame master, Graphics g) throws SlickException {
        super.render(gc, master, g);
        if (useQuad) {
            quad.draw(g);
            Set<AABB> boxes = new HashSet<>(100);
            final Input input = gc.getInput();
            final AABB box = new AABB(new Vector(input.getMouseX(), input.getMouseY()), 100, 100);
            boxes = quad.retrieve(boxes, box);
            g.setColor(Color.blue);
            box.draw(g);
            for (final AABB aabb : boxes) {
                g.setColor(Color.green);
                aabb.draw(g);
            }
        }
    }

    @Override
    public int getID() {
        return Master.EDITOR;
    }

    @Override
    public void controlPressed(Command cmd) {
        if (cmd instanceof KeyCommand) {
            keyPressed[((KeyCommand) cmd).getKey()] = true;
        }
        if (cmd instanceof Action) {
            ((Action) cmd).execute();
        }
    }

    @Override
    public void controlReleased(Command cmd) {
        if (cmd instanceof KeyCommand) {
            keyPressed[((KeyCommand) cmd).getKey()] = false;
        }
    }

    public void setLevel(String level) {
        data = "levels/" + level + ".xml";
    }

    @Override
    public void begin() throws SlickException {
        super.begin();
        reload();
    }

    private void reload() {
        List<Object> objects;
        final Parser parser = Parser.get();
        try {
            objects = parser.loadLevel(this, data);
        } catch (SlickException ex) {
            return;
        }
        LevelConfiguration levelConfig = null;
        List<Object> levelObjects = new ArrayList<>(objects.size());
        for (Object obj : objects) {
            if (obj instanceof LevelConfiguration) {
                levelConfig = (LevelConfiguration) obj;
            } else {
                levelObjects.add(obj);
            }
        }
        if (levelConfig != null) {
            setScroll(levelConfig.canScroll());
        } else {
            return;
        }
        int playerCount = 0;
        for (Object obj : objects) {
            if (obj instanceof Player) {
                playerCount++;
            }
        }
        if (playerCount != 1) {
            return;
        }
        final List<Background> backgrounds = new ArrayList<>(objects.size());
        final List<Background> foregrounds = new ArrayList<>(objects.size());
        final List<Platform> tiles = new ArrayList<>(objects.size());
        final List<Monster> monsters = new ArrayList<>(objects.size());
        final List<Item> items = new ArrayList<>(objects.size());
        final List<Object> otherObjects = new ArrayList<>(objects.size());
        Player player = null;
        for (Object obj : levelObjects) {
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
        }
        objects.clear();
        objects.add(levelConfig);
        objects.addAll(backgrounds);
        objects.addAll(otherObjects);
        objects.addAll(tiles);
        objects.addAll(monsters);
        objects.addAll(items);
        final Package[] packages = Package.getPackages();
        Class<?> playerClass = null;
        Constructor<?> playerConstructor = null;
        for (Package p : packages) {
            String tentative = p.getName() + "." + DEFAULT_PLAYER.toString();
            try {
                playerClass = Class.forName(tentative);
                playerConstructor = playerClass.getConstructor(Player.class);
            } catch (ClassNotFoundException | NoSuchMethodException | SecurityException ex) {
                continue;
            }
            break;
        }
        if (playerClass != null && playerConstructor != null) {
            try {
                player = (Player) playerConstructor.newInstance(player);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                return;
            }
        } else {
            return;
        }
        player.scroll(false);
        objects.add(player);
        objects.addAll(foregrounds);
        setObjects(objects);
        exchangeObjects();
        objects = getObjects();
        for (Object obj : objects) {
            if (obj instanceof Cartesian) {
                Cartesian ent = (Cartesian) obj;
                ent.setX(ent.getX() - getScreen().getX());
                ent.setY(ent.getY() - getScreen().getY());
            }
        }
    }

}
