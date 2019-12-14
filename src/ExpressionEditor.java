import javafx.application.Application;
import javafx.geometry.*;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

public class ExpressionEditor extends Application {
	public static void main (String[] args) {
		launch(args);
	}

	/**
	 * Mouse event handler for the entire pane that constitutes the ExpressionEditor
	 */
	private static class MouseEventHandler implements EventHandler<MouseEvent> {
		final private Pane pane;
		private AbstractCompoundExpression root;

		private Expression focused;

		private double ini_x, ini_y;
		private Expression floatingClone;
		private boolean dragging = false;
		private boolean didDrag = false;
		private AbstractCompoundExpression[] dragPermutations;
		private AbstractCompoundExpression lastSeenPermutation;

		MouseEventHandler (Pane pane_, AbstractCompoundExpression rootExpression_) {
			pane = pane_;
			root = rootExpression_;
			focused = root;
		}

		/**
		 * Handles mouse events by dividing labour based on press, drag, release
		 * @param event MouseEvent with a click, drag, or release
		 */
		public void handle (MouseEvent event) {
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED)
				handlePress(event);
			else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED)
				handleDrag(event);
			else if (event.getEventType() == MouseEvent.MOUSE_RELEASED)
				handleRelease(event);
		}

		/**
		 * Handles the "on-click" action by seeing if we need to take measurements to start dragging
		 * @param event MouseEvent describing the user action.
		 */
		private void handlePress(MouseEvent event) {
			if(focused == root || focused == null) {
				return;
			}
			ini_x = event.getSceneX();
			ini_y = event.getSceneY();
			if(!focused.computeBounds().contains(ini_x, ini_y))
				return;

			dragging = true;
			didDrag = false;
		}

		/**
		 * Handles the "drag" action by moving the floating node if it exists, and updating the ghost
		 * @param event MouseEvent describing the user action.
		 */
		private void handleDrag(MouseEvent event) {
			if(dragging && focused != root) {
				if (!didDrag)
					initialDragSetup();
				floatingClone.getNode().setTranslateX(event.getSceneX() - ini_x);
				floatingClone.getNode().setTranslateY(event.getSceneY() - ini_y);
				double closestDistance = Integer.MAX_VALUE;
				for (AbstractCompoundExpression possibility : dragPermutations) {
					final Node possibility_node = possibility.getNode();
					if(!didDrag) {
						possibility_node.setLayoutX(WINDOW_WIDTH / 4);
						possibility_node.setLayoutY(WINDOW_HEIGHT / 2);
						pane.getChildren().add(possibility_node);

						pane.applyCss();
						pane.layout(); // Force getChildren() to update early in order to calculate ghost's bounds
						// on the same tick as adding to getChildren()
					}
					possibility_node.setOpacity(0);
					final Node ghostingNode = possibility.findGhost().getNode();
					double distance = calculateDistance(floatingClone.getNode(), ghostingNode);
					if (distance < closestDistance) {
						closestDistance = distance;
						lastSeenPermutation = possibility;
					}
				}
				didDrag = true;
				lastSeenPermutation.getNode().setOpacity(1);
			}
		}

		/**
		 * Handle mouse "release" action by adjusting focus if necessary, or dropping the expression if we were dragging.
		 * @param event MouseEvent describing the user action.
		 */
		private void handleRelease(MouseEvent event) {
			if(dragging && didDrag) {
				dropFocused();
				return;
			}
			if(focused != null)
				((Region)focused.getNode()).setBorder(Expression.NO_BORDER);
			//Find our focus
			if(focused instanceof AbstractCompoundExpression) {
				final AbstractCompoundExpression casted = (AbstractCompoundExpression) focused;
				final Expression newFocus = casted.focusDeeper(event.getSceneX(), event.getSceneY());
				if(focused == newFocus) focused = root;
				else focused = newFocus;
			}
			else if(focused instanceof TerminalExpression) {
				//we're clicking onto a terminal. just unfocus
				focused = root;
			}
			if(focused == null) focused = root;
			if(focused != root) {
				((Region) focused.getNode()).setBorder(Expression.RED_BORDER);
			}
		}

		/**
		 * Sets up the initial drag process by cloning our node and making it "float"
		 * Additionally calculates permutations possible by moving our node.
		 */
		private void initialDragSetup() {
			floatingClone = focused.deepCopy();
			final Bounds sceneBoundsOfFocused = focused.computeBounds();
			final Bounds paneBoundsOfFocused = pane.sceneToLocal(sceneBoundsOfFocused);
			floatingClone.getNode().setLayoutX(paneBoundsOfFocused.getMinX());
			floatingClone.getNode().setLayoutY(paneBoundsOfFocused.getMinY());
			((Region) floatingClone.getNode()).setBorder(Expression.NO_BORDER);
			pane.getChildren().add(floatingClone.getNode());

			//Generate potential "alternative configurations"
			dragPermutations = root.buildPermutations(focused);
			pane.getChildren().remove(root.getNode()); //Remove our actual original root since we generate it as a possibility.
		}

		/**
		 * Calculates the distance between two nodes' center.
		 * @param node The first node to calculate distance from
		 * @param ghostingNode The second node to calculate distance to
		 * @return a Double describing the distance.
		 */
		private double calculateDistance(Node node, Node ghostingNode) {
			final Bounds firstNode = node.localToScene(node.getBoundsInLocal());
			final Bounds secondNode = ghostingNode.localToScene(ghostingNode.getBoundsInLocal());
			final Point2D firstCenter = new Point2D((firstNode.getMinX() + firstNode.getMaxX()) / 2, (firstNode.getMinY() + firstNode.getMaxY()) / 2);
			final Point2D secondCenter = new Point2D((secondNode.getMinX() + secondNode.getMaxX()) / 2, (secondNode.getMinY() + secondNode.getMaxY()) / 2);
			final double xDelta = (firstCenter.getX() - secondCenter.getX());
			final double yDelta = (firstCenter.getY() - secondCenter.getY());
			return Math.sqrt(xDelta * xDelta + yDelta * yDelta);
		}

		/**
		 * Handles the action of "dropping" the floating element by locking in the best permutation.
		 */
		private void dropFocused() {
			dragging = false;
			didDrag = false;
			//lock us into a new configuration
			pane.getChildren().clear();
			pane.getChildren().add(lastSeenPermutation.getNode());
			lastSeenPermutation.findGhost().setGhost(false);
			focused = lastSeenPermutation.findFocus();
			root = lastSeenPermutation;
			System.out.println(root.convertToString(0));
		}
	}

	/**
	 * Size of the GUI
	 */
	private static final int WINDOW_WIDTH = 500, WINDOW_HEIGHT = 250;

	/**
	 * Initial expression shown in the textbox
	 */
	private static final String EXAMPLE_EXPRESSION = "2*x+3*y+4*z+(7+6*z)";

	/**
	 * Parser used for parsing expressions.
	 */
	private final ExpressionParser expressionParser = new SimpleExpressionParser();

	/**
	 * Renders the main window and hooks listeners.
	 * @param primaryStage State to draw onto
	 */
	@Override
	public void start (Stage primaryStage) {
		primaryStage.setTitle("Expression Editor");

		// Add the textbox and Parser button
		final Pane queryPane = new HBox();
		final TextField textField = new TextField(EXAMPLE_EXPRESSION);
		final Button button = new Button("Parse");
		queryPane.getChildren().add(textField);

		final Pane expressionPane = new Pane();

		// Add the callback to handle when the Parse button is pressed	
		button.setOnMouseClicked(e -> {
			// Try to parse the expression
			try {
				// Success! Add the expression's Node to the expressionPane
				final Expression expression = expressionParser.parse(textField.getText(), true);
				expressionPane.getChildren().clear();
				expressionPane.getChildren().add(expression.getNode());
				expression.getNode().setLayoutX(WINDOW_WIDTH/4);
				expression.getNode().setLayoutY(WINDOW_HEIGHT/2);

				// If the parsed expression is a CompoundExpression, then register some callbacks
				if (expression instanceof AbstractCompoundExpression) {
					((Pane) expression.getNode()).setBorder(Expression.NO_BORDER);
					final MouseEventHandler eventHandler = new MouseEventHandler(expressionPane, (AbstractCompoundExpression) expression);
					expressionPane.setOnMousePressed(eventHandler);
					expressionPane.setOnMouseDragged(eventHandler);
					expressionPane.setOnMouseReleased(eventHandler);
				}
			} catch (ExpressionParseException epe) {
				// If we can't parse the expression, then mark it in red
				textField.setStyle("-fx-text-fill: red");
			}
		});
		queryPane.getChildren().add(button);

		// Reset the color to black whenever the user presses a key
		textField.setOnKeyPressed(e -> textField.setStyle("-fx-text-fill: black"));
		
		final BorderPane root = new BorderPane();
		root.setTop(queryPane);
		root.setCenter(expressionPane);

		primaryStage.setScene(new Scene(root, WINDOW_WIDTH, WINDOW_HEIGHT));
		primaryStage.show();
	}
}
