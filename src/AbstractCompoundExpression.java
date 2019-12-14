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
    final private String operation;
    final private HBox storedNode;
    private boolean signsUpToDate = false;
    final private boolean withJavaControls;
    /**
     * Given a string representing a mathematical operation (*, +, ()), creates a new compound expression
     * @param operation
     */
    AbstractCompoundExpression(String operation, boolean withJavaControls) {
        this.operation = operation;
        this.withJavaControls = withJavaControls;
        if(withJavaControls)
            storedNode = new HBox();
        else
            storedNode = null;
    }
    /**
     * Adds the specified expression as a child.
     *
     * @param subexpression the child expression to add
     */
    public void addSubexpression(Expression subexpression) {
        children.add(subexpression);
        subexpression.setParent(this);
        if(withJavaControls)
            storedNode.getChildren().add(subexpression.getNode());
        signsUpToDate = false;
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
        if(!withJavaControls)
            return;
        if(signsUpToDate)
            return;
        final List<Node> children = storedNode.getChildren();
        if(operation.equals("()")) {
            children.add(0, new ReferenceLabel("(", null));
            children.add(new ReferenceLabel(")", null));
        }
        for(Expression subExp : getSubexpressions()) {
            if(subExp instanceof AbstractCompoundExpression) {
                ((AbstractCompoundExpression) subExp).addSigns();
            }
            if(!operation.equals("()")) {
                final Node nodeForExp = subExp.getNode();
                int index = children.indexOf(nodeForExp);
                if (index > 0) {
                    children.add(index, new ReferenceLabel(operation, null));
                }
            }
        }
        signsUpToDate = true;
    }

    /**
     * Calculates the scene-relative bounds of this expression.
     * @return scene-relative bounds
     */
    public Bounds computeBounds() {
        if(!withJavaControls)
            return null;
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
        if(withJavaControls)
            storedNode.getChildren().clear();
        for(Expression child : children) {
            //Flatten ahead first
            child.flatten();
            //Is this child also the same operation as we are?
            if(child.getClass() == this.getClass()) {
                //Merge + adjust new parent
                final AbstractCompoundExpression compoundChild = (AbstractCompoundExpression) child;
                for(Expression subChild : compoundChild.getSubexpressions()) {
                    newChildren.add(subChild);
                    subChild.setParent(this);
                    if(withJavaControls)
                        storedNode.getChildren().add(subChild.getNode());
                }
            } else {
                newChildren.add(child);
                if(withJavaControls)
                    storedNode.getChildren().add(child.getNode());
            }
        }
        children = newChildren;
        signsUpToDate = false;
    }

    /**
     * Allows for sub-types of expressions to be created by this AbstractClass for deep clones.
     * @return created instance of AbstractCompoundExpression subclass.
     */
    abstract AbstractCompoundExpression createSelf();

    /**
     * Creates and returns a deep copy of the expression.
     * The entire tree rooted at the target node is copied, i.e.,
     * the copied Expression is as deep as possible.
     * @return the deep copy
     */
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

    /**
     * Determines the currently focused Expression by searching inside of this CompoundExpression.
     * @return focused Expression
     */
    public Expression findFocus() {
        final Region self = (Region)getNode();
        if(self.getBorder() != Expression.NO_BORDER) {
            return this;
        }
        for(Expression child : children) {
            final Node c_node = child.getNode();
            final Region c_region = (Region) c_node;
            if(c_region.getBorder() != Expression.NO_BORDER)
                return child;
            if(child instanceof AbstractCompoundExpression) {
                final AbstractCompoundExpression cast_child = (AbstractCompoundExpression)child;
                final Expression searchRecursively = cast_child.findFocus();
                if(searchRecursively != null)
                    return searchRecursively;
            }
        }
        return null;
    }

    /**
     * See if there's a deeper focus that contains the mouse's position.
     * @param mouseX Mouse scene-relative X
     * @param mouseY Mouse scene-relative y
     * @return A deeper focus if it exists.
     */
    public Expression focusDeeper(double mouseX, double mouseY) {
        //find child that contains x, y.
        for(Expression c : children) {
            Bounds box = c.computeBounds();
            if(box.contains(mouseX, mouseY)) {
                return c;
            }
        }
        return null;
    }

    /**
     * Builds each permutation of moving the ghost expression ("search") in its parent.
     * @param search The ghost expression to move
     * @return An array of permutations for each possible valid move of Search.
     */
    public AbstractCompoundExpression[] buildPermutations(Expression search) {
        final ArrayList<AbstractCompoundExpression> permutations = new ArrayList<>();
        int placementPerm = -1;
        while(true) {
            placementPerm++;
            AbstractCompoundExpression thisPermutation;
            try {
                thisPermutation = (AbstractCompoundExpression)deepCopyWithPlacement(placementPerm, search);
                permutations.add(thisPermutation);
            } catch (NoMoreCombinationsException e) { break; }
        }
        final AbstractCompoundExpression[] permutationResult = new AbstractCompoundExpression[permutations.size()];
        permutations.toArray(permutationResult);
        return permutationResult;
    }

    /**
     * Deep copies this expression, reordering a searched element in it's parent's subexpression list.
     * @param placement An index to move the search element to in its parent
     * @param search A search element to copy and change throughout the copy
     * @return A deep copy with the "search" element to be the placement-th's child of its parent.
     * @throws NoMoreCombinationsException if there is no possible arrangement given a placement value
     */
    public Expression deepCopyWithPlacement(int placement, Expression search) throws NoMoreCombinationsException {
        final AbstractCompoundExpression clone = createSelf();
        if(withJavaControls) {
            final HBox clonedNode = (HBox) clone.getNode();
            clonedNode.setBorder(((HBox) getNode()).getBorder());
        }
        final List<Expression> reorderedChildren = new ArrayList<>(children);
        if(children.contains(search)) {
            if(placement >= children.size())
                throw new NoMoreCombinationsException("No more combinations remain!"); //Will be caught by permutations array assembler
            //Move our clone of "search" into the correct slot
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

    /**
     * Recursively scans for a ghosting expression.
     * @return the ghosting Expression
     */
    public Expression findGhost() {
        //Find our ghosting expression in here ...
        if(!withJavaControls)
            return null;
        if(storedNode.getOpacity() == 0.5)
            return this;
        for(Expression c : children) {
            Expression ghost = c.findGhost();
            if(ghost != null)
                return ghost;
        }
        return null;
    }
}
