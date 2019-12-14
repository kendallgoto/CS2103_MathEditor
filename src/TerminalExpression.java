import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;

public class TerminalExpression implements Expression {
    private boolean isChar = false;
    private int numericValue = 0;
    private char characterValue = 0;
    private String stringValue = "";
    private CompoundExpression parent;
    boolean isValid = true;
    final private boolean withJavaControls;
    final private Label storedNode;

    /**
     * Given a string value, determine if this is a numeric or symbolic literal
     * Differentiating these two doesn't seem useful in the requirements of this program, but it could be useful for accomplishing symbolic solves, etc.
     * @param value String representation of this terminal
     */
    TerminalExpression(String value, boolean withJavaControls) {
        stringValue = value;
        this.withJavaControls = withJavaControls;

        if (value.matches("\\d+")) {
            numericValue = Integer.parseInt(value);
        } else if (value.length() == 1 && value.matches("[a-z]")) {
            isChar = true;
            characterValue = value.charAt(0);

        } else {
            isValid = false;
        }
        if(withJavaControls)
            storedNode = new ReferenceLabel(value, this);
        else
            storedNode = null;
    }
    /**
     * Returns the expression's parent.
     *
     * @return the expression's parent
     */
    public CompoundExpression getParent() {
        return parent;
    }

    /**
     * Sets the parent be the specified expression.
     *
     * @param parent the CompoundExpression that should be the parent of the target object
     */
    public void setParent(CompoundExpression parent) {
        this.parent = parent;
    }

    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     *
     * @return the deep copy
     */
    @Override
    public Expression deepCopy() {
        final TerminalExpression cloned = new TerminalExpression(stringValue, withJavaControls);
        if(withJavaControls)
            cloned.storedNode.setBorder(storedNode.getBorder());
        return cloned;
    }

    /**
     * Returns the JavaFX node associated with this expression.
     *
     * @return the JavaFX node associated with this expression.
     */
    @Override
    public Node getNode() {
        return storedNode;
    }

    /**
     * Recursively flattens the expression as much as possible
     * throughout the entire tree. Specifically, in every multiplicative
     * or additive expression x whose first or last
     * child c is of the same type as x, the children of c will be added to x, and
     * c itself will be removed. This method modifies the expression itself.
     */
    public void flatten() {
        //you can't flatten a terminal
    }

    public Bounds computeBounds() {
        return this.getNode().localToScene(this.getNode().getBoundsInLocal());
    }

    /**
     * Creates a String representation by recursively printing out (using indentation) the
     * tree represented by this expression, starting at the specified indentation level.
     *
     * @param stringBuilder the StringBuilder to use for building the String representation
     * @param indentLevel   the indentation level (number of tabs from the left margin) at which to start
     */
    @Override
    public void convertToString(StringBuilder stringBuilder, int indentLevel) {
       Expression.indent(stringBuilder, indentLevel);
       stringBuilder.append(stringValue).append("\n");
    }
    public Expression findGhost() {
        if(!withJavaControls)
            return null;
        if(storedNode.getOpacity() == 0.5)
            return this;
        return null;
    }

    @Override
    public Expression deepCopyWithPlacement(int placement, Expression search) {
        return deepCopy();
    }
}
