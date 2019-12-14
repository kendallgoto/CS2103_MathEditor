public class AdditiveCompoundExpression extends AbstractCompoundExpression {

    final private boolean withJavaControls;

    /**
     * Create a new AdditiveCompoundExpression with the "+" operator
     * @param withJavaControls enable onscreen Node creation
     */
    AdditiveCompoundExpression(boolean withJavaControls) {
        super("+", withJavaControls);
        this.withJavaControls = withJavaControls;
    }
    /**
     * Constructs an instance of this object
     * @return an instance of AdditiveCompoundExpression
     */
    public AbstractCompoundExpression createSelf() {
        return new AdditiveCompoundExpression(withJavaControls);
    }

}
