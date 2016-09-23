package kawaiiklash;

import static java.lang.Math.abs;
import org.newdawn.slick.Graphics;

/**
 * Axis Aligned Bounding Box.
 *
 * @author Jeff Niu
 */
@SuppressWarnings("PublicField")
public class AABB {

    public static boolean collide(final AABB box1, final AABB box2) {
        final boolean xOverlap = abs(box1.center.x - box2.center.x) <= box1.r[0] + box2.r[0];
        final boolean yOverlap = abs(box1.center.y - box2.center.y) <= box1.r[1] + box2.r[1];
        return xOverlap && yOverlap;
    }

    public static double distSQPointAABB(final Vector p, final AABB aabb) {
        final double minX, minY, maxX, maxY;
        double sqDist = 0.0f;
        double v;
        minX = aabb.center.x - aabb.r[0];
        maxX = aabb.center.x + aabb.r[0];
        minY = aabb.center.y - aabb.r[1];
        maxY = aabb.center.y + aabb.r[1];
        v = p.x;
        if (v < minX) {
            sqDist += (minX - v) * (minX - v);
        }
        if (v > maxX) {
            sqDist += (v - maxX) * (v - maxX);
        }
        v = p.y;
        if (v < minY) {
            sqDist += (minY - v) * (minY - v);
        }
        if (v > maxY) {
            sqDist += (v - maxY) * (v - maxY);
        }
        return sqDist;
    }

    public Vector center;
    public double[] r;

    public AABB(final double width, final double height) {
        this(new Vector(), width, height);
    }

    public AABB(final Rect b) {
        r = new double[2];
        r[0] = b.getWidth() * 0.5f;
        r[1] = b.getHeight() * 0.5f;
        center = new Vector(b.getX() + r[0], b.getY() + r[1]);
    }

    public AABB(final Vector center, final double width, final double height) {
        this.center = center;
        r = new double[2];
        r[0] = width * 0.5f;
        r[1] = height * 0.5f;
    }

    public void update(final Vector position) {
        center.x = position.x;
        center.y = position.y;
    }

    public boolean collidesWith(final AABB aabb) {
        return collide(this, aabb);
    }
    
    public boolean collidesWith(final Collideable col) {
        return collidesWith(new AABB(col.getHitbox()));
    }

    public boolean collidesWith(final Circle c) {
        return c.collidesWith(this);
    }

    public boolean containsPoint(final Vector p) {
        final double distX = abs(center.x - p.x);
        final double distY = abs(center.y - p.y);
        return distX <= r[0] && distY <= r[1];
    }

    public double distSQPoint(final Vector p) {
        return distSQPointAABB(p, this);
    }

    public void draw(final Graphics g) {
        double x = center.x - r[0];
        double y = center.y - r[1];
        double width = r[0] * 2.0f;
        double height = r[1] * 2.0f;
        g.drawRect((float) x, (float) y, (float) width, (float) height);
    }

    public Rect toRect() {
        final double width = r[0] * 2.0f;
        final double height = r[1] * 2.0f;
        final double x = center.x - r[0];
        final double y = center.y - r[1];
        return new Rect(x, y, width, height);
    }

    public void setX(final double x) {
        center.x = (float) x;
    }

    public void setY(final double y) {
        center.y = (float) y;
    }

    public void setWidth(final double width) {
        r[0] = width * 0.5f;
    }

    public void setHeight(final double height) {
        r[1] = height * 0.5f;
    }

    public double getWidth() {
        return 2.0f * r[0];
    }

    public double getHeight() {
        return 2.0f * r[0];
    }

    public double getHalfWidth() {
        return r[0];
    }

    public double getHalfHeight() {
        return r[1];
    }

}
