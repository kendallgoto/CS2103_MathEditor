import javafx.scene.control.Label;

public class ReferenceLabel extends Label {
    private Expression expr;
    public ReferenceLabel(String label, Expression expr) {
        super(label);
        this.expr = expr;
        this.setStyle("-fx-font: 24 arial;");
    }
    public Expression getExpression() {
        return expr;
    }
}
