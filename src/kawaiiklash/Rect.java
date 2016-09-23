package kawaiiklash;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import org.newdawn.slick.Graphics;

/**
 *
 * @author Jeff Niu
 */
@SuppressWarnings("serial")
public class Rect extends Rectangle2D.Double {

    public Rect() {
        this(0, 0, 0, 0);
    }

    public Rect(double width, double height) {
        this(0, 0, width, height);
    }

    public Rect(Vector v) {
        this(v, new Dimensions());
    }

    public Rect(Dimension2D d) {
        this(new Vector(), d);
    }

    public Rect(Vector v, Dimension2D d) {
        this(v.x, v.y, d.getWidth(), d.getHeight());
    }

    public Rect(Rectangle2D r) {
        this(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    public Rect(double x, double y, double width, double height) {
        super(x, y, width, height);
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public Vector getPosition() {
        return new Vector(getX(), getY());
    }

    public Dimensions getSize() {
        return new Dimensions(getWidth(), getHeight());
    }

    public void setPosition(Vector v) {
        this.x = v.x;
        this.y = v.y;
    }

    public void setSize(Dimensions d) {
        this.width = d.width;
        this.height = d.height;
    }

    public Rect intersection(Rect r) {
        final Rectangle2D r2d = super.createIntersection(r);
        return new Rect(r2d);
    }

    public Rect union(Rect r) {
        final Rectangle2D r2d = super.createUnion(r);
        return new Rect(r2d);
    }

    public boolean intersects(Rect r) {
        return super.intersects(r);
    }

    public boolean conatins(Vector v) {
        return super.contains(new Point2D.Double(v.x, v.y));
    }

    public void draw(Graphics g) {
        g.drawRect((float) x, (float) y, (float) width, (float) height);
    }

    public Rect translate(double dx, double dy) {
        x += dx;
        y += dy;
        return this;
    }

    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public Rect clone() {
        super.clone();
        return new Rect(x, y, width, height);
    }

}
