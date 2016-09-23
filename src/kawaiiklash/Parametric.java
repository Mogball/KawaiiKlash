package kawaiiklash;

import java.util.function.Function;

/**
 * A parametric allows for an entity's position to be determined by
 * parametric equations, where the variable is t, time. This is done in
 * such a way that the function can be normalized based on how the screen
 * scrolls.
 *
 * @author Jeff Niu
 */
public class Parametric implements Updateable {

    private final Function<Double, Double> f;
    private double d;
    private double t;

    /**
     * {@code P(x) = A * f(c * (x - h)) + k}
     *
     * @param A
     * @param c
     * @param h
     * @param k
     * @param f
     */
    public Parametric(double A, double c, double h, double k, final Function<Double, Double> f) {
        d = k;
        t = 0;
        this.f = (x) -> {
            return -A * f.apply(c * (x - h));
        };
    }

    public void move(double delta) {
        d += delta;
    }

    @Override
    public void update(int dt) {
        t += dt;
    }

    public double get() {
        return f.apply(t) + d;
    }

}
