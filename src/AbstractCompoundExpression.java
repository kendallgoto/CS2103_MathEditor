import com.sun.tools.javac.code.Attribute;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCompoundExpression implements CompoundExpression {
    List<Expression> children = new ArrayList<>();
    CompoundExpression parent;
    String operation = "";

    AbstractCompoundExpression(String operation) {
        this.operation = operation;
    }
    /**
     * Adds the specified expression as a child.
     *
     * @param subexpression the child expression to add
     */
    public void addSubexpression(Expression subexpression) {
        children.add(subexpression);
        subexpression.setParent(this);
    }

    public List<Expression> getSubexpressions() {
        return children;
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
//    Implement in sub classes.
//    public Expression deepCopy() {
//    }

    /**
     * Recursively flattens the expression as much as possible
     * throughout the entire tree. Specifically, in every multiplicative
     * or additive expression x whose first or last
     * child c is of the same type as x, the children of c will be added to x, and
     * c itself will be removed. This method modifies the expression itself.
     */
    public void flatten() {
        List<Expression> newChildren = new ArrayList<>();
        for(Expression child : children) {
            //Flatten ahead first
            child.flatten();
            //Is this child also the same operation as we are?
            if(child.getClass() == this.getClass()) {
                //Merge
                AbstractCompoundExpression compoundChild = (AbstractCompoundExpression) child;
                newChildren.addAll(compoundChild.getSubexpressions());
            } else {
                newChildren.add(child);
            }
        }
        children = newChildren;
    }

    /**
     * Creates a String representation by recursively printing out (using indentation) the
     * tree represented by this expression, starting at the specified indentation level.
     *
     * @param stringBuilder the StringBuilder to use for building the String representation
     * @param indentLevel   the indentation level (number of tabs from the left margin) at which to start
     */
    public void convertToString(StringBuilder stringBuilder, int indentLevel) {
        Expression.indent(stringBuilder, indentLevel);
        stringBuilder.append(operation).append("\n");
        for(Expression child : children) {
            child.convertToString(stringBuilder, indentLevel+1);
        }
    }
}
