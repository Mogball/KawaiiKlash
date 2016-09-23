package kawaiiklash;

import java.awt.geom.QuadCurve2D;
import org.newdawn.slick.Graphics;

/**
 * Gives a more concrete form to the Java {@code QuadCurve2D}. Has a few
 * methods that specialize in symmetrical (perfectly quadratic,
 * non-parametric, defined by a function) quadratic curves.
 *
 * @author Jeff Niu
 */
@SuppressWarnings({"PublicField", "serial"})
public class QuadCurve extends QuadCurve2D.Double {

    private double[] points;
    private int segments;

    public QuadCurve() {
        this(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    public QuadCurve(Vector p, Vector ctrl, Vector q) {
        this(p.x, p.y, ctrl.x, ctrl.y, q.x, q.y);
    }

    public QuadCurve(double x1, double y1, double ctrlx, double ctrly, double x2, double y2) {
        super(x1, y1, ctrlx, ctrly, x2, y2);
        points = null;
        segments = -1;
    }

    private void createPoints() {
        final double step = 1.0 / segments;
        points = new double[(segments + 1) * 2];
        for (int i = 0; i < segments + 1; i++) {
            final double t = i * step;
            final Vector p = pointAt(t);
            points[i * 2] = p.x;
            points[(i * 2) + 1] = p.y;
        }
    }

    public void setSegments(int segments) {
        if (this.segments != segments) {
            this.segments = segments;
            createPoints();
        }
    }

    public Vector pointAt(double t) {
        final double a = 1.0 - t;
        final double b = t;
        final double f1 = a * a;
        final double f2 = 2 * a * b;
        final double f3 = b * b;
        final double nx = (x1 * f1) + (ctrlx * f2) + (x2 * f3);
        final double ny = (y1 * f1) + (ctrly * f2) + (y2 * f3);
        return new Vector(nx, ny);
    }

    public void draw(Graphics g) {
        if (points == null || segments == -1) {
            setSegments(20);
        }
        createPoints();
        for (int i = 0; i < points.length - 2; i += 2) {
            final float px = (float) points[i];
            final float py = (float) points[i + 1];
            final float qx = (float) points[i + 2];
            final float qy = (float) points[i + 3];
            g.drawLine(px, py, qx, qy);
        }
    }

    public void subdivide(QuadCurve qc1, QuadCurve qc2) {
        super.subdivide(qc1, qc2);
    }

    public QuadCurve translate(double dx, double dy) {
        x1 += dx;
        x2 += dx;
        ctrlx += dx;
        y1 += dy;
        y2 += dy;
        ctrly += dy;
        return this;
    }
    
    @Override
    public Rect getBounds2D() {
        return new Rect(super.getBounds2D());
    }

    @Override
    public Vector getP1() {
        return new Vector(x1, y1);
    }

    @Override
    public Vector getP2() {
        return new Vector(x2, y2);
    }

    @Override
    public Vector getCtrlPt() {
        return new Vector(ctrlx, ctrly);
    }

    @Override
    @SuppressWarnings("CloneDeclaresCloneNotSupported")
    public QuadCurve clone() {
        super.clone();
        return new QuadCurve(x1, y1, ctrlx, ctrly, x2, y2);
    }

    @Override
    public String toString() {
        return String.format("[QuadCurve: x1 = %.1f, y1 = %.1f, ctrlx = %.1f, ctrly = %.1f, x2 = %.1f, y2 = %.1f%n", x1, y1, ctrlx, ctrly, x2, y2);
    }
    
}
