public class AdditiveCompoundExpression extends AbstractCompoundExpression {

    /**
     * Create a new AdditiveCompoundExpression with the "+" operator
     * @param withJavaControls enable onscreen Node creation
     */
    final private boolean withJavaControls;
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
