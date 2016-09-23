package kawaiiklash;

/**
 * A functional interface describing a set of instructions that can be
 * completed at any moment. Used with lambdas for an inner method sort of
 * thing.
 *
 * @author Jeff Niu
 */
@FunctionalInterface
public interface Execution {

    void execute();

}
