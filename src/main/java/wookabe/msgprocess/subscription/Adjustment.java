package wookabe.msgprocess.subscription;

import static wookabe.msgprocess.subscription.Adjustment.Type.ADD;
import static wookabe.msgprocess.subscription.Adjustment.Type.SUBTRACT;

/**
 * Representation of adjustment done to Messages.
 */
public class Adjustment {
    private final Type type;
    private final double value;

    public Adjustment(Type type, double value) {
        this.type = type;
        this.value = value;
    }

    /**
     * Apply adjustment to the given price.
     *
     * @param price Price to adjust
     * @return Adjusted pricing basing on the type
     */
    public double apply(double price) {
        return type == ADD ? price + value
                : type == SUBTRACT ? price - value
                : price * value;

    }

    @Override
    public String toString() {
        return "Adjustment{" +
                "type=" + type +
                ", value=" + value +
                '}';
    }

    /**
     * Fixed type of possible adjustment.
     */
    public enum Type {
        ADD, SUBTRACT, MULTIPLY
    }
}
