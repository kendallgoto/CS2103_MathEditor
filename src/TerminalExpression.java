public class TerminalExpression implements Expression {
    private boolean isChar = false;
    private int numericValue = 0;
    private char characterValue = 0;
    private String stringValue = "";
    private CompoundExpression parent;
    boolean isValid = true;

    TerminalExpression(String value) {
        stringValue = value;
        if (value.matches("\\d+")) {
            numericValue = Integer.parseInt(value);
        } else if (value.length() == 1 && value.matches("[a-z]")) {
            isChar = true;
            characterValue = value.charAt(0);

        } else {
            isValid = false;
        }
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
        Expression clone = new TerminalExpression(stringValue);
        return clone;
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
}