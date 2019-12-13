import javafx.scene.control.Label;

public class ReferenceLabel extends Label {
    private Expression expr;
    public ReferenceLabel(String label, Expression expr) {
        super(label);
        this.expr = expr;
        this.setStyle("-fx-font: 30 \"serif\"; -fx-font-weight: bold;");
    }
    public Expression getExpression() {
        return expr;
    }
}
