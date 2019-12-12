import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractCompoundExpression implements CompoundExpression {
    private List<Expression> children = new ArrayList<>();
    private CompoundExpression parent;
    private String operation = "";
    final private HBox storedNode;
    /**
     * Given a string representing a mathematical operation (*, +, ()), creates a new compound expression
     * @param operation
     */
    AbstractCompoundExpression(String operation) {
        this.operation = operation;
        storedNode = new HBox();
    }
    /**
     * Adds the specified expression as a child.
     *
     * @param subexpression the child expression to add
     */
    public void addSubexpression(Expression subexpression) {
        children.add(subexpression);
        subexpression.setParent(this);
        storedNode.getChildren().add(subexpression.getNode());
    }

    /**
     * Get children sub expressions
     * @return children
     */
    List<Expression> getSubexpressions() {
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

    /**
     * Runs whenever our HBox changes in order to insert signs between symbols or parentheses around the entire thing
     */
    public void addSigns() {
        final List<Node> children = storedNode.getChildren();
        if(operation.equals("()")) {
            children.add(0, new ReferenceLabel("(", null));
            children.add(new ReferenceLabel(")", null));
        }
        System.out.println(Arrays.toString(children.toArray()));
        for(Expression subExp : getSubexpressions()) {
            if(subExp instanceof AbstractCompoundExpression) {
                ((AbstractCompoundExpression) subExp).addSigns();
            }
            if(!operation.equals("()")) {
                Node nodeForExp = subExp.getNode();
                int index = storedNode.getChildren().indexOf(nodeForExp);
                if (index > 0)
                    storedNode.getChildren().add(index, new ReferenceLabel(operation, null));
            }
        }
    }

    public Bounds computeBounds() {
        return this.getNode().localToScene(this.getNode().getBoundsInLocal());
    }
    /**
     * Returns the JavaFX node associated with this expression.
     * @return the JavaFX node associated with this expression.
     */
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
        final List<Expression> newChildren = new ArrayList<>();
        storedNode.getChildren().clear();
        for(Expression child : children) {
            //Flatten ahead first
            child.flatten();
            //Is this child also the same operation as we are?
            if(child.getClass() == this.getClass()) {
                //Merge + adjust new parent
                AbstractCompoundExpression compoundChild = (AbstractCompoundExpression) child;
                for(Expression subChild : compoundChild.getSubexpressions()) {
                    newChildren.add(subChild);
                    subChild.setParent(this);
                    storedNode.getChildren().add(subChild.getNode());
                }
            } else {
                newChildren.add(child);
                storedNode.getChildren().add(child.getNode());
            }
        }
        children = newChildren;
    }
    abstract AbstractCompoundExpression createSelf();

    public Expression deepCopy() {
        final AbstractCompoundExpression clone = createSelf();
        for(Expression child : children) {
            clone.addSubexpression(child.deepCopy());
        }

        clone.addSigns();
        return clone;
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

    public Expression findFocus() {
        Region self = (Region)getNode();
        if(self.getBorder() != Expression.NO_BORDER) {
            return this;
        }
        for(Expression child : children) {
            Node c_node = child.getNode();
            Region c_region = (Region) c_node;
            if(c_region.getBorder() != Expression.NO_BORDER)
                return child;
            if(child instanceof AbstractCompoundExpression) {
                AbstractCompoundExpression cast_child = (AbstractCompoundExpression)child;
                Expression searchRecursively = cast_child.findFocus();
                if(searchRecursively != null)
                    return searchRecursively;
            }
        }
        return null;
    }
    public Expression focusDeeper(double mouseX, double mouseY) {
        //find child that contains x, y.
        for(Expression c : children) {
            Bounds box = c.computeBounds();
            System.out.println("Checking for children at ("+mouseX+", "+mouseY+")");
            System.out.println("Against bounds "+box);
            if(box.contains(mouseX, mouseY)) {
                return c;
            }
        }
        return null;
    }
    public AbstractCompoundExpression[] buildPermutations(Expression search) {
        ArrayList<AbstractCompoundExpression> permutations = new ArrayList<>();
        int placementPerm = -1;
        while(true) {
            placementPerm++;
            AbstractCompoundExpression thisPermutation;
            try {
                thisPermutation = (AbstractCompoundExpression)deepCopyWithPlacement(placementPerm, search);
                permutations.add(thisPermutation);
            } catch (NoMoreCombinationsException e) { break; }
        }
        AbstractCompoundExpression[] permutationResult = new AbstractCompoundExpression[permutations.size()];
        permutations.toArray(permutationResult);
        return permutationResult;
    }
    public Expression deepCopyWithPlacement(int placement, Expression search) throws NoMoreCombinationsException {
        final AbstractCompoundExpression clone = createSelf();
        final List<Expression> reorderedChildren = new ArrayList<>(children);
        if(children.contains(search)) {
            if(placement >= children.size())
                throw new NoMoreCombinationsException("No more combinations remain!"); //Will be caught by permutations array assembler
            //Move our clone of "search" into the correct slot
            int originalIndex = reorderedChildren.indexOf(search);
            reorderedChildren.remove(search);
            reorderedChildren.add(placement, search);
        }
        for(Expression child : reorderedChildren) {
            Expression deepcopy = child.deepCopyWithPlacement(placement, search);
            clone.addSubexpression(deepcopy);
            if(child == search) deepcopy.setGhost(true);
        }
        clone.addSigns();
        return clone;
    }
    public Expression findGhost() {
        //Find our ghosting expression in here ...
        if(this.getNode().getOpacity() == 0.5)
            return this;
        for(Expression c : children) {
            Expression ghost = c.findGhost();
            if(ghost != null)
                return ghost;
        }
        return null;
    }
}
