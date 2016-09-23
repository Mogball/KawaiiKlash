package kawaiiklash;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import org.newdawn.slick.Color;
import org.newdawn.slick.Graphics;

/**
 * Get the jump arc centered on the hitbox. Extend the arc and get the
 * bounding box. To get all the tiles that we could potentially work with,
 * run through the game objects and check to see which intersect with the
 * bounding box. Then, take the original arc and split it into two. Take
 * the back half of the arc, the one that represents the ascending portion
 * of the jump, translate it to the top of the jumper hitbox, and scan
 * through the potential tiles for intersects. Any intersecting rectangle
 * is considered a ceiling. Take the ceiling that is closest to the jumper;
 * that is, whose {@code y + height} value is the largest. Move the arc to
 * be right under the closest ceiling exactly where it would hit.
 *
 * @author Jeff Niu
 */
public interface PlatformJumper extends Interactable {

    double getJumpLength();

    double getJumpHeight();

    double getJumpDx();

    double getJumpDy();

    double getDdy();

    List<Platform> getPlatforms();

    List<Object> getObjects();

    default QuadCurve getArc() {
        final double dx = getJumpLength() * getDirX().unit();
        final double dy = getJumpHeight();
        final Vector m = getMidpoint();
        final Vector p = new Vector(m.x, m.y);
        final Vector q = new Vector(m.x + dx, m.y);
        final Vector c = new Vector(m.x + dx / 2, m.y - 2 * dy);
        return new QuadCurve(p, c, q);
    }

    static QuadCurve getUpArc(final QuadCurve arc) {
        QuadCurve upArc = arc.clone();
        upArc.subdivide(upArc, null);
        return upArc;
    }

    static QuadCurve getFallArc(final QuadCurve extendedArc) {
        QuadCurve fallArc = extendedArc.clone();
        fallArc.subdivide(null, fallArc);
        return fallArc;
    }

    default QuadCurve getExtendedArc(final QuadCurve arc, final double delta) {
        QuadCurve extendedArc = arc.clone();
        final Direction dir = getDirX();
        final double p = arc.x1 - delta * dir.unit();
        extendedArc = extendArc(extendedArc, p);
        return extendedArc;
    }

    static Function<Double, Double> getArcEqn(QuadCurve qc) {
        if (qc.y1 == qc.y2) {
            final double a = (qc.y1 - qc.ctrly) / (qc.x1 - qc.ctrlx) / (qc.x1 - qc.ctrlx) * 0.5;
            return (t) -> {
                double f = a * (t - qc.ctrlx) * (t - qc.ctrlx) + (qc.y1 + qc.ctrly) * 0.5;
                return f;
            };
        } else if (qc.x1 == qc.x2) {
            final double a = (qc.x1 - qc.ctrlx) / (qc.y1 - qc.ctrly) / (qc.y1 - qc.ctrly) * 0.5;
            return (t) -> {
                double f = a * (t - qc.ctrly) * (qc.ctrly) + (qc.x1 + qc.ctrlx) * 0.5;
                return f;
            };
        } else {
            return (t) -> {
                return 0.0;
            };
        }
    }

    static QuadCurve extendArc(QuadCurve qc, double x1) {
        final Function<Double, Double> f = getArcEqn(qc);
        final double x2 = qc.x2 + (qc.x1 - x1);
        final double y = f.apply(x1);
        final double cy = qc.ctrly + qc.y1 - y;
        return new QuadCurve(x1, y, qc.ctrlx, cy, x2, y);
    }

    default List<Platform> getNearbyPlatforms(final List<Object> gameObjects, final Rect boundingBox) {
        final List<Platform> nearbyPlatforms = new ArrayList<>(gameObjects.size());
        for (final Object o : gameObjects) {
            if (o instanceof Platform) {
                final Platform p = (Platform) o;
                if (p.getHitbox().intersects(boundingBox)) {
                    nearbyPlatforms.add(p);
                }
            }
        }
        return nearbyPlatforms;
    }

    static List<Platform> getIntersecting(final List<Platform> nearbyPlatforms, final QuadCurve arc) {
        final List<Platform> intersecting = new ArrayList<>(nearbyPlatforms.size());
        for (final Platform p : nearbyPlatforms) {
            final Rect r = p.getHitbox();
            if (arc.intersects(r)) {
                intersecting.add(p);
            }
        }
        return intersecting;
    }

    static Platform getLowestCeiling(final List<Platform> ceilings) {
        Platform lowest = ceilings.get(0);
        for (final Platform ceiling : ceilings) {
            final Rect cBox = ceiling.getHitbox();
            final Rect lBox = lowest.getHitbox();
            if (cBox.y + cBox.height > lBox.y + lBox.height) {
                lowest = ceiling;
            }
        }
        return lowest;
    }

    default Bound recalculate(final QuadCurve qc, final Rect r) {
        final double yVertex = r.y + r.height;//+ getHitbox().height;
        final Bound solns = solveTimeForHeight(yVertex);
        final double t = solns.lower();
        final double xVertex = qc.x1 + t * getJumpDx() * getDirX().unit();
        final double dx = xVertex - qc.ctrlx;
        double y;
        if (qc.y2 > qc.y1) {
            y = qc.y2;
        } else {
            y = qc.y1;
        }
        final double dy = yVertex - (qc.ctrly + y) * 0.5;
        return new Bound(dx, dy);
    }

    default Bound solveTimeForHeight(double y) {
        final double h = getHitbox().y - y;
        final double v = getJumpDy();
        final double a = getDdy();
        final double tSymmetry = -v / a;
        final double tOffset = Math.sqrt(v * v - 2 * a * h) / a;
        final double soln1 = tSymmetry + tOffset;
        final double soln2 = tSymmetry - tOffset;
        if (soln1 > soln2) {
            return new Bound(soln2, soln1);
        } else {
            return new Bound(soln1, soln2);

        }
    }

    /**
     * Using methods from the {@code PlatformJumper} interface, determine
     * whether or not there is a viable jump target.
     *
     * @return
     */
    default boolean shouldJump() {
        final Rect hitbox = getHitbox();
        final QuadCurve arc = getArc();
        final QuadCurve upArc = getUpArc(arc);
        upArc.translate(0, -0.5 * hitbox.height);
        final QuadCurve extendedArc = getExtendedArc(arc, hitbox.width);
        final Rect boundingBox = extendedArc.getBounds2D();
        boundingBox.translate(0, boundingBox.height * 0.5);
        final List<Object> gameObjects = getObjects();
        if (gameObjects.isEmpty()) {
            return false;
        }
        final List<Platform> nearbyPlatforms = getNearbyPlatforms(gameObjects, boundingBox);
        if (nearbyPlatforms.isEmpty()) {
            return false;
        }
        final List<Platform> ceilings = getIntersecting(nearbyPlatforms, upArc);
        final QuadCurve fallArc;
        final Direction dirX = getDirX();
        if (!ceilings.isEmpty()) {
            final List<Platform> rejects = new ArrayList<>(ceilings.size());
            final QuadCurve fullUpArc = arc.clone().translate(0, -0.5 * hitbox.height);
            final Function<Double, Double> fullUpArcEqn = getArcEqn(fullUpArc);
            for (final Platform ceiling : ceilings) {
                final Rect cBox = ceiling.getHitbox();
                if (cBox.x > hitbox.x + hitbox.width || hitbox.x > cBox.x + cBox.width) {
                    final double x;
                    if (dirX == Direction.LEFT) {
                        x = cBox.x + cBox.width;
                    } else {
                        x = cBox.x;
                    }
                    final double y = fullUpArcEqn.apply(x);
                    if (y < cBox.y + cBox.height) {
                        rejects.add(ceiling);
                    }
                }
            }
            ceilings.removeAll(rejects);
            if (!ceilings.isEmpty()) {
                final Platform ceiling = getLowestCeiling(ceilings);
                final Rect rCeil = ceiling.getHitbox();
                final Bound deltas = recalculate(arc, rCeil);
                fallArc = getFallArc(extendedArc);
                fallArc.translate(deltas.lower(), deltas.upper() + hitbox.height);
                extendedArc.translate(deltas.lower(), deltas.upper() + hitbox.height * 0.5);
            } else {
                fallArc = getFallArc(extendedArc);
                fallArc.translate(0, hitbox.height * 0.5);
                extendedArc.translate(0, hitbox.height * 0.5);
            }
        } else {
            fallArc = getFallArc(extendedArc);
            fallArc.translate(0, hitbox.height * 0.5);
            extendedArc.translate(0, hitbox.height * 0.5);
        }
        final List<Platform> potentials = getIntersecting(nearbyPlatforms, fallArc);
        final List<Platform> rejects = new ArrayList<>(potentials.size());
        if (potentials.isEmpty()) {
            return false;
        }
        final Function<Double, Double> extendedArcEqn = getArcEqn(extendedArc);
        for (final Platform p : potentials) {
            final Rect pBox = p.getHitbox();
            if (pBox.x > hitbox.x + hitbox.width || hitbox.x > pBox.x + pBox.width) {
                final double x;
                if (dirX == Direction.LEFT) {
                    x = pBox.x + pBox.width;
                } else {
                    x = pBox.x;
                }
                final double y = extendedArcEqn.apply(x);
                if (y > pBox.y) {
                    rejects.add(p);
                }
            }
        }
        potentials.removeAll(rejects);
        if (potentials.isEmpty()) {
            return false;
        }
        Platform platform = potentials.get(0);
        for (Platform p : potentials) {
            if (p.getHitbox().y < platform.getHitbox().y) {
                platform = p;
            }
        }
        final List<Platform> floorPlatforms = getPlatforms();
        if (floorPlatforms.isEmpty()) {
            return false;
        }
        Rect unionBox = floorPlatforms.get(0).getHitbox();
        for (final Platform p : floorPlatforms) {
            final Rect pBox = p.getHitbox();
            unionBox = unionBox.union(pBox);
        }
        final Rect pBox = platform.getHitbox();
        if (pBox.y == unionBox.y) {
            final double widthSum = unionBox.width + pBox.width;
            final double widthUnion = unionBox.union(pBox).width;
            return widthSum < widthUnion;
        } else {
            return true;
        }
    }

    default void drawArcs(Graphics g) {
        final Rect hitbox = getHitbox();
        final QuadCurve arc = getArc();
        final QuadCurve upArc = getUpArc(arc);
        upArc.translate(0, -0.5 * hitbox.height);
        final QuadCurve extendedArc = getExtendedArc(arc, hitbox.width);
        final Rect boundingBox = extendedArc.getBounds2D();
        boundingBox.translate(0, boundingBox.height * 0.5);
        final List<Object> gameObjects = getObjects();
        if (gameObjects.isEmpty()) {
            return;
        }
        final List<Platform> nearbyPlatforms = getNearbyPlatforms(gameObjects, boundingBox);
        if (nearbyPlatforms.isEmpty()) {
            return;
        }
        final List<Platform> ceilings = getIntersecting(nearbyPlatforms, upArc);
        final QuadCurve fallArc;
        final Direction dirX = getDirX();
        if (!ceilings.isEmpty()) {
            final List<Platform> rejects = new ArrayList<>(ceilings.size());
            final QuadCurve fullUpArc = arc.clone().translate(0, -0.5 * hitbox.height);
            final Function<Double, Double> fullUpArcEqn = getArcEqn(fullUpArc);
            for (final Platform ceiling : ceilings) {
                final Rect cBox = ceiling.getHitbox();
                if (cBox.x > hitbox.x + hitbox.width || hitbox.x > cBox.x + cBox.width) {
                    final double x;
                    if (dirX == Direction.LEFT) {
                        x = cBox.x + cBox.width;
                    } else {
                        x = cBox.x;
                    }
                    final double y = fullUpArcEqn.apply(x);
                    if (y < cBox.y + cBox.height) {
                        rejects.add(ceiling);
                    }
                }
            }
            ceilings.removeAll(rejects);
            if (!ceilings.isEmpty()) {
                final Platform ceiling = getLowestCeiling(ceilings);
                final Rect rCeil = ceiling.getHitbox();
                final Bound deltas = recalculate(arc, rCeil);
                fallArc = getFallArc(extendedArc);
                fallArc.translate(deltas.lower(), deltas.upper() + hitbox.height);
                extendedArc.translate(deltas.lower(), deltas.upper() + hitbox.height * 0.5);
            } else {
                fallArc = getFallArc(extendedArc);
                fallArc.translate(0, hitbox.height * 0.5);
                extendedArc.translate(0, hitbox.height * 0.5);
            }
        } else {
            fallArc = getFallArc(extendedArc);
            fallArc.translate(0, hitbox.height * 0.5);
            extendedArc.translate(0, hitbox.height * 0.5);
        }
        g.setColor(Color.magenta);
        fallArc.draw(g);
        upArc.draw(g);
    }

}
