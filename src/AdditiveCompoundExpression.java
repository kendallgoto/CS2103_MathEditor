public class AdditiveCompoundExpression extends AbstractCompoundExpression {

    /**
     * Create a new AdditiveCompoundExpression with the "+" operator
     */
    AdditiveCompoundExpression() {
        super("+");
    }
    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     *
     * @return the deep copy
     */
    public AbstractCompoundExpression createSelf() {
        return new AdditiveCompoundExpression();
    }

}
