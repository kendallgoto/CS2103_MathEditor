import javafx.geometry.Bounds;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Node;

interface Expression {
	/**
	 * Border for showing a focused expression
	 */
	public static final Border RED_BORDER = new Border(
	  new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)
	);

	/**
	 * Border for showing a non-focused expression
	 */
	public static final Border NO_BORDER = null;

	/**
	 * Color used for a "ghosted" expression
	 */
	public static final Color GHOST_COLOR = Color.LIGHTGREY;

	/**
	 * Returns the expression's parent.
	 * @return the expression's parent
	 */
	CompoundExpression getParent ();
        
	/**
         * Sets the parent be the specified expression.
         * @param parent the CompoundExpression that should be the parent of the target object
         */
	void setParent (CompoundExpression parent);

	/**
	 * Creates and returns a deep copy of the expression.
	 * The entire tree rooted at the target node is copied, i.e.,
	 * the copied Expression is as deep as possible.
	 * @return the deep copy
	 */
	Expression deepCopy ();


	/**
	 * Returns the JavaFX node associated with this expression.
	 * @return the JavaFX node associated with this expression.
	 */
	Node getNode ();

	/**
	 * Recursively flattens the expression as much as possible
	 * throughout the entire tree. Specifically, in every multiplicative
	 * or additive expression x whose first or last
	 * child c is of the same type as x, the children of c will be added to x, and
	 * c itself will be removed. This method modifies the expression itself.
	 */
	void flatten ();

	/**
	 * Calculates the scene-relative bounds of this expression.
	 * @return scene-relative bounds
	 */
	Bounds computeBounds();

	/**
	 * Creates a String representation by recursively printing out (using indentation) the
	 * tree represented by this expression, starting at the specified indentation level.
	 * @param stringBuilder the StringBuilder to use for building the String representation
	 * @param indentLevel the indentation level (number of tabs from the left margin) at which to start
	 */	
	void convertToString (StringBuilder stringBuilder, int indentLevel);

	public default String convertToString (int indentLevel) {
		final StringBuilder stringBuilder = new StringBuilder();
		convertToString(stringBuilder, indentLevel);
		return stringBuilder.toString();
	}

	/**
	 * Static helper method to indent a specified number of times from the left margin, by
	 * appending tab characters to the specified StringBuilder.
	 * @param stringBuilder the StringBuilder to which to append tab characters.
	 * @param indentLevel the number of tabs to append.
	 */
	public static void indent (StringBuilder stringBuilder, int indentLevel) {
		for (int i = 0; i < indentLevel; i++) {
			stringBuilder.append('\t');
		}
	}

	/**
	 * Set this expression to be "ghosting" or at half opacity.
	 * @param ghost true if ghosting should be enabled (half opacity)
	 */
	public default void setGhost(boolean ghost) {
		if(ghost) {
			getNode().setOpacity(0.5);
		} else {
			getNode().setOpacity(1);
		}
	}

	/**
	 * Finds the currently "ghosting" expression block in this expression (if it exists)
	 * @return Ghost expression
	 */
	Expression findGhost();

	/**
	 * Deep copies this expression, reordering a searched element in it's parent's subexpression list.
	 * @param placement An index to move the search element to in its parent
	 * @param search A search element to copy and change throughout the copy
	 * @return A deep copy with the "search" element to be the placement-th's child of its parent.
	 * @throws NoMoreCombinationsException if there is no possible arrangement given a placement value
	 */
	Expression deepCopyWithPlacement(int placement, Expression search) throws NoMoreCombinationsException;
}
