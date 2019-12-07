public class MultiplicativeCompoundExpression extends AbstractCompoundExpression {

    /**
     * Create a new MultiplicativeCompoundExpression with the "*" operator
     */
    MultiplicativeCompoundExpression() {
        super("*");
    }
    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     *
     * @return the deep copy
     */
    public Expression deepCopy() {
        CompoundExpression clone = new MultiplicativeCompoundExpression();
        copyChildren(clone);
        return clone;
    }
}
