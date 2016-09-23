package kawaiiklash;

import java.util.Iterator;

/**
 * A utility class, or really a class where random methods go.
 *
 * @author Jeff Niu
 */
@SuppressWarnings({"UseOfSystemOutOrSystemErr", "CallToPrintStackTrace"})
public class Utility {

    /**
     * The standard exit status.
     */
    private static final int EXIT_STATUS = 0;

    public static boolean[] arraycopy(boolean[] src) {
        boolean[] dest = new boolean[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }

    public static int[] arraycopy(int[] src) {
        int[] dest = new int[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }

    public static double[] arraycopy(double[] src) {
        double[] dest = new double[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }

    public static Object[] arraycopy(Object[] src) {
        Object[] dest = new Object[src.length];
        System.arraycopy(src, 0, dest, 0, src.length);
        return dest;
    }

    /**
     * Generate a random integer between and including two numbers.
     *
     * @param startInt the lower bound
     * @param endInt the upper bound
     * @return a random integer between and including the bounds
     */
    public static int randInt(int startInt, int endInt) {
        return (int) (Math.round(Math.random() * (endInt - startInt) + startInt));
    }

    /**
     * Generate a random double between two numbers.
     *
     * @param lower
     * @param upper
     * @return
     */
    public static double randDouble(double lower, double upper) {
        return Math.random() * (upper - lower) + lower;
    }

    /**
     * Checks if two double variables have the same sign (+/-).
     *
     * @param a
     * @param b
     * @return
     */
    public static boolean sameSign(double a, double b) {
        return (a >= 0) ^ (b < 0);
    }

    /**
     * Counts the number of elements in the {@code Iterator<E>}. Exhausts
     * the {@code Iterator<E>}; the method {@code hasNext} will return
     * false.
     *
     * @param <E>
     * @param iter
     * @return
     */
    public static <E> int iteratorSize(Iterator<E> iter) {
        int num = 0;
        while (iter.hasNext()) {
            num++;
            iter.next();
        }
        return num;
    }

    /**
     * Something has gone wrong. Dump the message then exit.
     *
     * @param message
     */
    public static void fail(String message) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stackTrace) {
            System.err.println(e);
        }
        System.out.println(message);
        Runtime.getRuntime().exit(EXIT_STATUS);
    }

    /**
     * Something has gone wrong. Dump the message then exit.
     *
     * @param ex
     */
    public static void fail(Exception ex) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stackTrace) {
            System.err.println(e);
        }
        System.out.println(ex);
        ex.printStackTrace();
        Runtime.getRuntime().exit(EXIT_STATUS);
    }

    /**
     * Something has gone wrong. Dump the message then exit.
     *
     * @param message
     * @param ex
     */
    public static void fail(String message, Exception ex) {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : stackTrace) {
            System.err.println(e);
        }
        System.out.println(message);
        System.out.println(ex);
        ex.printStackTrace();
        Runtime.getRuntime().exit(EXIT_STATUS);
    }

    /**
     * Prevent instantiation.
     */
    private Utility() {
    }

}
