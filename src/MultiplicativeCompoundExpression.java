public class MultiplicativeCompoundExpression extends AbstractCompoundExpression {

    /**
     * Create a new MultiplicativeCompoundExpression with the "*" operator
     */
    MultiplicativeCompoundExpression() {
        super("*");
    }
    public AbstractCompoundExpression createSelf() {
        return new MultiplicativeCompoundExpression();
    }
}
