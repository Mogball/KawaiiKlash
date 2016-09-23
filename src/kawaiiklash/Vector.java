package kawaiiklash;

import java.awt.geom.Point2D;
import org.newdawn.slick.util.FastTrig;

/**
 *
 * @author Jeff Niu
 */
@SuppressWarnings("PublicField")
public class Vector extends Point2D {

    public double x;
    public double y;

    public Vector() {
        this(0, 0);
    }

    public Vector(Vector other) {
        this(other.getX(), other.getY());
    }

    public Vector(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @SuppressWarnings("FinalMethod")
    public final void setTheta(double theta) {
        double angle = theta;
        if ((angle < -360) || (angle > 360)) {
            angle %= 360;
        }
        if (angle < 0) {
            angle = 360 + angle;
        }
        double len = length();
        x = len * FastTrig.cos(StrictMath.toRadians(angle));
        y = len * FastTrig.sin(StrictMath.toRadians(angle));
    }

    public Vector add(double theta) {
        setTheta(getTheta() + theta);
        return this;
    }

    public Vector sub(double theta) {
        setTheta(getTheta() - theta);
        return this;
    }

    public double getTheta() {
        double theta = StrictMath.toDegrees(StrictMath.atan2(y, x));
        if ((theta < -360) || (theta > 360)) {
            theta %= 360;
        }
        if (theta < 0) {
            theta = 360 + theta;
        }
        return theta;
    }

    @Override
    public double getX() {
        return x;
    }

    @Override
    public double getY() {
        return y;
    }

    public void set(Vector other) {
        set(other.getX(), other.getY());
    }

    public double dot(Vector other) {
        return (x * other.getX()) + (y * other.getY());
    }

    public Vector set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vector getPerpendicular() {
        return new Vector(-y, x);
    }

    public Vector set(double[] pt) {
        return set(pt[0], pt[1]);
    }

    public Vector negate() {
        return new Vector(-x, -y);
    }

    public Vector negateLocal() {
        x = -x;
        y = -y;
        return this;
    }

    public Vector add(Vector v) {
        x += v.getX();
        y += v.getY();
        return this;
    }

    public Vector sub(Vector v) {
        x -= v.getX();
        y -= v.getY();
        return this;
    }

    public Vector scale(double a) {
        x *= a;
        y *= a;
        return this;
    }

    public Vector normalise() {
        double l = length();
        if (l == 0) {
            return this;
        }
        x /= l;
        y /= l;
        return this;
    }

    public Vector getNormal() {
        Vector cp = copy();
        cp.normalise();
        return cp;
    }

    public double lengthSquared() {
        return (x * x) + (y * y);
    }

    public double length() {
        return Math.sqrt(lengthSquared());
    }

    public void projectOntoUnit(Vector b, Vector result) {
        double dp = b.dot(this);
        result.x = dp * b.getX();
        result.y = dp * b.getY();
    }

    public Vector copy() {
        return new Vector(x, y);
    }

    public double distance(Vector other) {
        return Math.sqrt(distSQ(other));
    }

    public double distSQ(Vector other) {
        double dx = other.getX() - getX();
        double dy = other.getY() - getY();
        return (dx * dx) + (dy * dy);
    }

    @Override
    public String toString() {
        return "[Vector " + x + "," + y + " (" + length() + ")]";
    }

    @Override
    public int hashCode() {
        return 997 * ((int) x) ^ 991 * ((int) y);
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Vector) {
            Vector o = ((Vector) other);
            return (o.x == x) && (o.y == y);
        }

        return false;
    }

    @Override
    public void setLocation(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public Vector clone() {
        super.clone();
        return new Vector(x, y);
    }

}
