public class ParentheticalCompoundExpression extends AbstractCompoundExpression {

    final private boolean withJavaControls;
    /**
     * Create a new ParentheticalCompoundExpression with the "()" operator
     * @param withJavaControls enables on-screen Node creation
     */
    ParentheticalCompoundExpression(boolean withJavaControls) {
        super("()", withJavaControls);
        this.withJavaControls = withJavaControls;
    }

    /**
     * Constructs an instance of this object
     * @return an instance of ParentheticalCompoundExpression
     */
    public AbstractCompoundExpression createSelf() {
        return new ParentheticalCompoundExpression(withJavaControls);
    }
}
