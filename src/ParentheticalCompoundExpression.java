public class ParentheticalCompoundExpression extends AbstractCompoundExpression {

    /**
     * Create a new ParentheticalCompoundExpression with the "()" operator
     */
    ParentheticalCompoundExpression() {
        super("()");
    }

    /**
     * Constructs an instance of this object
     * @return an instance of ParentheticalCompoundExpression
     */
    public AbstractCompoundExpression createSelf() {
        return new ParentheticalCompoundExpression();
    }
}
