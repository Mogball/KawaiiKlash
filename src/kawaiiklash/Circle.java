package kawaiiklash;

/**
 *
 * @author Jeff Niu
 */
@SuppressWarnings("PublicField")
public class Circle {

    public static boolean collide(final Circle c1, final Circle c2) {
        final double distSQ = c1.center.distSQ(c2.center);
        final double radiusSum = c1.r + c2.r;
        return distSQ <= radiusSum * radiusSum;
    }

    public static boolean collide(final AABB box, final Circle c) {
        return collide(c, box);
    }

    public static boolean collide(final Circle c, final AABB box) {
        final double sqDist = box.distSQPoint(c.center);
        final double r = c.r;
        return sqDist <= r * r;
    }

    public Vector center;
    public double r;

    public Circle(final double radius) {
        this(new Vector(), radius);
    }

    public Circle(final Vector center, final double radius) {
        this.center = center;
        r = radius;
    }

    public void update(final Vector position) {
        center.x = position.x;
        center.y = position.y;
    }

    public boolean collidesWith(final Circle c) {
        return collide(this, c);
    }

    public boolean collidesWith(final AABB aabb) {
        return collide(this, aabb);
    }

    public void setX(final double x) {
        center.x = (float) x;
    }

    public void setY(final double y) {
        center.y = (float) y;
    }

}
