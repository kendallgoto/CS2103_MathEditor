public class MultiplicativeCompoundExpression extends AbstractCompoundExpression {

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
    @Override
    public Expression deepCopy() {
        return null;
    }
}
