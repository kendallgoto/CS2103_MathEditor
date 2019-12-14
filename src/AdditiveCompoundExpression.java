public class AdditiveCompoundExpression extends AbstractCompoundExpression {

    /**
     * Create a new AdditiveCompoundExpression with the "+" operator
     */
    AdditiveCompoundExpression() {
        super("+");
    }
    /**
     * Constructs an instance of this object
     * @return an instance of AdditiveCompoundExpression
     */
    public AbstractCompoundExpression createSelf() {
        return new AdditiveCompoundExpression();
    }

}
