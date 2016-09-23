package kawaiiklash;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Jeff Niu
 * @param <Target>
 * @param <Obstacle>
 */
public class ScannerImpl<Target extends Interactable, Obstacle extends Interactable> {

    @SuppressWarnings("unchecked")
    private static <T extends Interactable> List<T> getTypes(final List<Object> objects, final Class<T> cls) {
        if (objects != null) {
            final List<T> types = new ArrayList<>(objects.size());
            for (final Object obj : objects) {
                if (cls.isAssignableFrom(obj.getClass())) {
                    types.add((T) obj);
                }
            }
            return types;
        } else {
            return new ArrayList<>(0);
        }
    }

    private static <T extends Interactable> List<Rect> getRects(final List<T> types, final Rect r) {
        final List<Rect> boxes = new ArrayList<>(types.size());
        for (final T t : types) {
            if (t.canCollide()) {
                final Rect box = t.getHitbox();
                if (r.intersects(box)) {
                    boxes.add(box.intersection(r));
                }
            }
        }
        return boxes;
    }

    private final Class<Target> target;
    private final Class<Obstacle> obstacle;

    public ScannerImpl(final Class<Target> target, final Class<Obstacle> obstacle) {
        this.target = target;
        this.obstacle = obstacle;
    }

    public boolean scan(final Direction dir, final List<Object> objects, final Rect sight, final float margin) {
        if (target == null) {
            return false;
        }
        final Rect scan = search(dir, objects, sight, margin);
        final List<Target> targets = getTypes(objects, target);
        for (final Target t : targets) {
            if (t.getHitbox().intersects(scan)) {
                return true;
            }
        }
        return false;
    }

    public List<Target> locate(final Direction dir, final List<Object> objects, final Rect sight, final float margin) {
        if (target == null) {
            return new ArrayList<>(0);
        }
        final Rect scan = search(dir, objects, sight, margin);
        final List<Target> targets = getTypes(objects, target);
        final List<Target> located = new ArrayList<>(targets.size());
        for (final Target t : targets) {
            if (t.getHitbox().intersects(scan)) {
                located.add(t);
            }
        }
        return located;
    }

    public Rect search(final Direction dir, final List<Object> objects, final Rect sight, final float margin) {
        if (obstacle == null || margin == 0.0) {
            return sight;
        }
        final List<Obstacle> obstacles = getTypes(objects, obstacle);
        if (dir == Direction.RIGHT) {
            return searchRight(obstacles, sight, margin);
        }
        if (dir == Direction.LEFT) {
            return searchLeft(obstacles, sight, margin);
        }
        if (dir == Direction.DOWN) {
            return searchDown(obstacles, sight, margin);
        }
        if (dir == Direction.UP) {
            return searchUp(obstacles, sight, margin);
        }
        return sight;
    }

    private Rect searchRight(final List<Obstacle> obstacles, final Rect sight, final float margin) {
        final List<Rect> boxes = getRects(obstacles, sight);
        final Rect[] rects = boxes.toArray(new Rect[boxes.size()]);
        if (rects == null || rects.length == 0) {
            return sight;
        }
        final RectangleSorter sorter = new RectangleSorter();
        sorter.sortHorAsc(rects);
        double v = sight.getX() + sight.getWidth();
        for (Rect r : rects) {
            if (r.getHeight() / (float) sight.getHeight() > margin) {
                v = r.getX();
                break;
            }
        }
        return new Rect(sight.getX(), sight.getY(), v - sight.getX(), sight.getHeight());
    }

    private Rect searchLeft(final List<Obstacle> obstacles, final Rect sight, final float margin) {
        final List<Rect> boxes = getRects(obstacles, sight);
        final Rect[] rects = boxes.toArray(new Rect[boxes.size()]);
        if (rects == null || rects.length == 0) {
            return sight;
        }
        final RectangleSorter sorter = new RectangleSorter();
        sorter.sortHorDes(rects);
        double v = sight.getX();
        for (Rect r : rects) {
            if (r.getHeight() / (float) sight.getHeight() > margin) {
                v = r.getX() + r.getWidth();
                break;
            }
        }
        return new Rect(v, sight.getY(), sight.getX() + sight.getWidth() - v, sight.getHeight());
    }

    private Rect searchDown(final List<Obstacle> obstacles, final Rect sight, final float margin) {
        final List<Rect> boxes = getRects(obstacles, sight);
        final Rect[] rects = boxes.toArray(new Rect[boxes.size()]);
        if (rects == null || rects.length == 0) {
            return sight;
        }
        final RectangleSorter sorter = new RectangleSorter();
        sorter.sortVerAsc(rects);
        double v = sight.getY() + sight.getHeight();
        for (Rect r : rects) {
            if (r.getWidth() / (float) sight.getWidth() > margin) {
                v = r.getY();
                break;
            }
        }
        return new Rect(sight.getX(), sight.getY(), sight.getWidth(), v - sight.getY());
    }

    private Rect searchUp(final List<Obstacle> obstacles, final Rect sight, final float margin) {
        final List<Rect> boxes = getRects(obstacles, sight);
        final Rect[] rects = boxes.toArray(new Rect[boxes.size()]);
        if (rects == null || rects.length == 0) {
            return sight;
        }
        final RectangleSorter sorter = new RectangleSorter();
        sorter.sortVerDes(rects);
        double v = sight.getY();
        for (Rect r : rects) {
            if (r.getWidth() / (float) sight.getWidth() > margin) {
                v = r.getY() + r.getHeight();
                break;
            }
        }
        return new Rect(sight.getX(), v, sight.getWidth(), sight.getY() + sight.getHeight() - v);
    }

}
