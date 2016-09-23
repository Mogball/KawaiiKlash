package artificalintelligence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import kawaiiklash.AABB;
import kawaiiklash.Collideable;
import kawaiiklash.Drawable;
import kawaiiklash.Game;
import kawaiiklash.GameImpl;
import kawaiiklash.Master;
import kawaiiklash.Monster;
import kawaiiklash.Platform;
import kawaiiklash.Rect;
import kawaiiklash.Updateable;
import kawaiiklash.Vector;
import hackthemarket.Allele;
import hackthemarket.Bound;
import hackthemarket.GeneticAlgorithm;
import hackthemarket.Genome;
import hackthemarket.Link;
import hackthemarket.NeuralNetwork;
import hackthemarket.Node;
import hackthemarket.Population;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 *
 * @author Jeff Niu
 */
public class GameAI extends GameImpl {

    private BoxArray boxes;
    private QuadTree quad;
    private Population pop;
    private Genome genome;
    private NeuralNetwork ai;
    private Iterator<Genome> genomes;

    private int stillTimeout;
    private int lagTimeout;
    private double maxRight;

    private final int[] outputs = {
        Input.KEY_LEFT,
        Input.KEY_RIGHT,
        Input.KEY_SPACE
    };

    public GameAI(Master master) {
        super(master);
    }

    @Override
    public int getID() {
        return Master.AI;
    }

    @Override
    public void init(GameContainer gc, StateBasedGame master)
            throws SlickException {
        super.init(gc, master);

        final Rect screen = getScreen();
        final Vector qCenter = new Vector(screen.getWidth() / 2, screen.getHeight() / 2);
        final AABB qBounds = new AABB(qCenter, screen.getWidth(), screen.getHeight());
        quad = new QuadTree(0, qBounds);
    }

    private void lazyInit() {
        int r = 64; // number of boxes horizontally, width = 2 * r + 1
        int i = 32; // number of boxes vertically, height = 2 * i + 1
        double t = 4;

        boxes = new BoxArray(this, r, i, t);
        int n = boxes.boxes().length;
        List<Node> nodes = new ArrayList<>();
        int k;
        for (k = 0; k < n; k++) {
            nodes.add(new Node(k, Allele.Input));
        }
        Node left = new Node(k++, Allele.Output); // move left
        Node right = new Node(k++, Allele.Output); // move right
        Node jump = new Node(k++, Allele.Output); // jump

        nodes.add(left);
        nodes.add(right);
        nodes.add(jump);
        List<Link> links = new ArrayList<>();
        Genome seed = new Genome(nodes, links);
        GeneticAlgorithm GA = new GeneticAlgorithm(new Bound(-2, 2));

        // DESCRIBE INITIAL GENOME
        int L = 2 * r * i + r + i+ 4; // (0, 0)
        links.add(new Link(nodes.get(L).key(), right.key(), 1, true));
        GA.innovate(links);
        pop = new Population(30, seed, GA);
        genomes = pop.getGenomes();
        genome = null;
        ai = null;

        stillTimeout = 0;
        lagTimeout = 0;
        maxRight = 0;
    }

    @Override
    public void update(GameContainer gc, StateBasedGame master, int dt) throws SlickException {
        super.update(gc, master, dt);

        if (dt >= MINIMUM_TIME_RESOLUTION) {
            dt = MINIMUM_TIME_RESOLUTION;
        }

        // Lazy initialization because of the poor player init system
        if (boxes == null) {
            lazyInit();
        }
        if (genome == null) {
            if (!genomes.hasNext()) {
                pop = pop.evolve();
                genomes = pop.getGenomes();
            }
            genome = genomes.next();
            ai = new NeuralNetwork(genome);
        }

        // Keep the boxes centered on the player
        boxes.update(dt);

        // Check collisions
        List<Object> objects = getObjects();
        int numObjects = objects.size();
        List<Collideable> cols = new ArrayList<>(numObjects);
        for (int i = 0; i < numObjects; i++) {
            Object o = objects.get(i);
            if (o instanceof Monster || o instanceof Platform) {
                cols.add((Collideable) o);
            }
        }

        // Employ a quad tree to improve efficiency
        Box[] boxArray = boxes.boxes();
        quad.clear();
        for (final Collideable col : cols) {
            quad.insert(col);
        }
        for (int i = 0; i < boxArray.length; i++) {
            Set<Collideable> set = new HashSet<>(50);
            set = quad.retrieve(set, boxArray[i]);
            for (Collideable c : set) {
                if (c.collidesWith(boxArray[i])) {
                    boxArray[i].collidedWith(c);
                }
            }
        }

        // Push inputs
        double[] Y = boxes.send(ai);
        int i = 0;
        for (int output : outputs) {
            keyDown()[output] = Y[i] > 0.5;
            i++;
        }

        if (getPlayer().getDx() < 5) {
            stillTimeout += dt;
        } else {
            stillTimeout = 0;
        }
        if (getPlayer().getX() < maxRight) {
            lagTimeout += dt;
        } else {
            lagTimeout = 0;
            maxRight = getPlayer().getX();
        }
        if (stillTimeout >= 2000) {
            stillTimeout = 0;
            playerDead();
        }
        if (lagTimeout >= 10000) {
            lagTimeout = 0;
            maxRight = 0;
            playerDead();
        }
    }

    @Override
    public void playerDead() {
        super.playerDead();
        genome.setFitness(maxRight);
        genome = null;
        ai = null;
    }

    @Override
    public void render(GameContainer gc, StateBasedGame master, Graphics g) throws SlickException {
        super.render(gc, master, g);

        // Draw the boxes for debugging
        if (debugging()) {
            boxes.draw(g);
        }
    }

}

class BoxArray implements Updateable, Drawable {

    private final Box[] boxes;

    BoxArray(Game game, int r, int i, double t) {
        int numBoxes = (2 * r + 1) * (2 * i + 1);
        boxes = new Box[numBoxes];
        int j = 0;
        for (int n = -r; n <= r; n++) {
            for (int k = -i; k <= i; k++) {
                boxes[j] = new Box(game, n, k, t);
                j++;
            }
        }
    }

    Box get(int x, int y) {
        for (Box box : boxes) {
            if (box.x() == x && box.y() == y) {
                return box;
            }
        }
        return null;
    }

    double[] send(NeuralNetwork ai) {
        final double[] X = new double[boxes.length];
        for (int i = 0; i < boxes.length; i++) {
            X[i] = boxes[i].getState();
        }
        final double[] Y = ai.push(X);
        return Y;
    }

    Box[] boxes() {
        return boxes;
    }

    @Override
    public void update(int dt) {
        for (Box box : boxes) {
            box.update(dt);
        }
    }

    @Override
    public void draw(Graphics g) {
        for (Box box : boxes) {
            box.draw(g);
        }
    }

    @Override
    public int getZ() {
        return boxes[0].getZ();
    }

    @Override
    public int compareTo(Drawable t) {
        return boxes[0].getZ() - t.getZ();
    }

}

class Box implements Updateable, Collideable, Drawable {

    private static final int STATE_NEUTRAL = 0;
    private static final int STATE_ENEMY = -1;
    private static final int STATE_TILE = 1;

    private final Game game;
    private final Rect box;
    private final int r, i;

    private int state;

    Box(Game game, int r, int i, double t) {
        this.game = game;
        this.r = r;
        this.i = i;
        Rect hitbox = game.getPlayer().getHitbox();
        box = new Rect(hitbox.width / t, hitbox.height / t);
        state = STATE_NEUTRAL;
    }

    int x() {
        return r;
    }

    int y() {
        return i;
    }

    int getState() {
        return state;
    }

    @Override
    public void update(int dt) {
        Rect hitbox = game.getPlayer().getHitbox();
        box.x = hitbox.x + r * box.width;
        box.y = hitbox.y + i * box.height;
        state = STATE_NEUTRAL;
    }

    @Override
    public void collidedWith(Collideable other) {
        if (other instanceof Monster) {
            state = STATE_ENEMY;
        }
        if (state != STATE_ENEMY && other instanceof Platform) {
            state = STATE_TILE;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (state == STATE_NEUTRAL) {
            return;
        } else if (state == STATE_ENEMY) {
            g.setColor(Color.red);
        } else if (state == STATE_TILE) {
            g.setColor(Color.blue);
        }
        box.draw(g);
    }

    @Override
    public Rect getHitbox() {
        return box;
    }

    @Override
    public int getZ() {
        return Drawable.HUD;
    }

    @Override
    public boolean canCollide() {
        return true;
    }

    @Override
    public int compareTo(Drawable t) {
        return getZ() - t.getZ();
    }

}
