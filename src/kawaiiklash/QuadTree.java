package kawaiiklash;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * <li>
 * nodes[0]: North East
 * </li>
 * <li>
 * nodes[1]: North West
 * </li>
 * <li>
 * nodes[2]: South West
 * </li>
 * <li>
 * nodes[3]: South East
 * </li>
 *
 * @author Jeff Niu
 */
public class QuadTree {

    private static final int QT_NODE_CAPACITY = 3;
    private static final int QT_MAX_LEVEL = 5;

    private final int level;
    private final AABB bounds;
    private final List<AABB> objects;
    private final QuadTree[] nodes;

    public QuadTree(final int level, final AABB bounds) {
        this.level = level;
        this.bounds = bounds;
        objects = new ArrayList<>(QT_NODE_CAPACITY);
        nodes = new QuadTree[4];
    }

    public void clear() {
        objects.clear();
        if (nodes[0] != null) {
            for (int i = 0; i < nodes.length; i++) {
                nodes[i].clear();
                nodes[i] = null;
            }
        }
    }

    public AABB getBounds() {
        return bounds;
    }

    public void insert(final AABB box) {
        if (nodes[0] != null) {
            final boolean[] indices = getIndices(box);
            for (int i = 0; i < nodes.length; i++) {
                if (indices[i]) {
                    nodes[i].insert(box);
                }
            }
            return;
        }
        objects.add(box);
        if (objects.size() > QT_NODE_CAPACITY && level < QT_MAX_LEVEL) {
            if (nodes[0] == null) {
                split();
            }
            final int size = objects.size();
            for (int i = 0; i < size; i++) {
                final AABB aabb = objects.get(i);
                final boolean[] indices = getIndices(aabb);
                for (int ii = 0; ii < indices.length; ii++) {
                    if (indices[ii]) {
                        nodes[ii].insert(aabb);
                    }
                }
            }
            objects.clear();
        }
    }

    public Set<AABB> retrieve(final Set<AABB> set, final AABB box) {
        final boolean[] indices = getIndices(box);
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] && nodes[i] != null) {
                nodes[i].retrieve(set, box);
            }
        }
        set.addAll(objects);
        return set;
    }

    public void draw(Graphics g) {
        g.setColor(Color.white);
        bounds.draw(g);
        if (nodes[0] != null) {
            for (QuadTree node : nodes) {
                node.draw(g);
            }
        }
        g.setColor(Color.red);
        for (AABB box : objects) {
            box.draw(g);
        }
    }

    private void split() {
        final float subWidth = (float) bounds.getHalfWidth();
        final float subHeight = (float) bounds.getHalfHeight();
        final float qtWidth = subWidth * 0.5f;
        final float qtHeight = subHeight * 0.5f;
        final double x = bounds.center.x;
        final double y = bounds.center.y;
        nodes[0] = new QuadTree(level + 1, new AABB(new Vector(x + qtWidth, y - qtHeight), subWidth, subHeight));
        nodes[1] = new QuadTree(level + 1, new AABB(new Vector(x - qtWidth, y - qtHeight), subWidth, subHeight));
        nodes[2] = new QuadTree(level + 1, new AABB(new Vector(x - qtWidth, y + qtHeight), subWidth, subHeight));
        nodes[3] = new QuadTree(level + 1, new AABB(new Vector(x + qtWidth, y + qtHeight), subWidth, subHeight));
    }

    private boolean[] getIndices(final AABB box) {
        final boolean[] indices = new boolean[4];
        for (int i = 0; i < nodes.length; i++) {
            indices[i] = nodes[i] != null ? nodes[i].getBounds().collidesWith(box) : false;
        }
        return indices;
    }

}
