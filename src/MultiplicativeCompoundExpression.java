public class MultiplicativeCompoundExpression extends AbstractCompoundExpression {

    /**
     * Create a new MultiplicativeCompoundExpression with the "*" operator
     */
    MultiplicativeCompoundExpression() {
        super("*");
    }

    /**
     * Constructs an instance of this object
     * @return an instance of MultiplicativeCompoundExpression
     */
    public AbstractCompoundExpression createSelf() {
        return new MultiplicativeCompoundExpression();
    }
}
