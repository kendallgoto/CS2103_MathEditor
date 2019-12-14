import javafx.scene.control.Label;

public class ReferenceLabel extends Label {
    final private Expression expr;

    /**
     * Creates a new "ReferenceLabel", a simple add-in to Label that configures the typeface and holds references to the parenting expresssion if ever needed
     * @param label The text string to display
     * @param expr The associated expression
     */
    public ReferenceLabel(String label, Expression expr) {
        super(label);
        this.expr = expr;
        this.setStyle("-fx-font: 30 \"serif\"; -fx-font-weight: bold;");
    }

    /**
     * Returns a reference to the Expression shown by this label
     * @return associated Expression
     */
    public Expression getExpression() {
        return expr;
    }
}
