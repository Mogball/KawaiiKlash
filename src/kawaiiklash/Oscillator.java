package kawaiiklash;

import static java.lang.Math.PI;
import static java.lang.Math.sin;
import java.util.function.Function;

/**
 * A type of parametric designed specifically for use with the
 * trigonometric functions (or any periodic function).
 *
 * @author Jeff Niu
 */
public class Oscillator extends Parametric {

    public Oscillator(double disp, double amp, double shift, double period) {
        this(disp, amp, shift, period, (t) -> {
            return sin(t);
        });
    }

    public Oscillator(double disp, double amp, double shift, double period, Function<Double, Double> func) {
        super(amp, 2 * PI / period, shift, disp, func);
    }

}
