public class MultiplicativeCompoundExpression extends AbstractCompoundExpression {

    final private boolean withJavaControls;

    /**
     * Create a new MultiplicativeCompoundExpression with the "*" operator
     * @param withJavaControls enables on-screen Node creation
     */
    MultiplicativeCompoundExpression(boolean withJavaControls) {
        super("*", withJavaControls);
        this.withJavaControls = withJavaControls;

    }

    /**
     * Constructs an instance of this object
     * @return an instance of MultiplicativeCompoundExpression
     */
    public AbstractCompoundExpression createSelf() {
        return new MultiplicativeCompoundExpression(withJavaControls);
    }
}