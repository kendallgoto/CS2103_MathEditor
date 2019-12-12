import javafx.application.Application;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
		private Pane pane;
		private AbstractCompoundExpression root;
		private double ini_x, ini_y;
		private Expression focused;
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

		public void handle (MouseEvent event) {
			if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
				if(focused == root) {
					return;
				}
				ini_x = event.getSceneX();
				ini_y = event.getSceneY();
				if(!focused.computeBounds().contains(ini_x, ini_y))
					return;

				dragging = true;
				didDrag = false;
				//clone!

			} else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
				if(dragging) {
					if(!didDrag) {
						floatingClone = focused.deepCopy();
						Bounds sceneBoundsOfFocused = focused.computeBounds();
						Bounds paneBoundsOfFocused = pane.sceneToLocal(sceneBoundsOfFocused);
						floatingClone.getNode().setLayoutX(paneBoundsOfFocused.getMinX());
						floatingClone.getNode().setLayoutY(paneBoundsOfFocused.getMinY());
						((Region)floatingClone.getNode()).setBorder(Expression.NO_BORDER);
						pane.getChildren().add(floatingClone.getNode());

						//Generate potential "alternative configurations"

						dragPermutations = root.buildPermutations(focused);
						System.out.println(dragPermutations);
						double closestDistance = Integer.MAX_VALUE;
						for(AbstractCompoundExpression possibility : dragPermutations) {
							Node possibility_node = possibility.getNode();
							possibility_node.setLayoutX(WINDOW_WIDTH/4);
							possibility_node.setLayoutY(WINDOW_HEIGHT/2);
							possibility_node.setOpacity(0);
							pane.getChildren().add(possibility_node);
							Node ghostingNode = possibility.findGhost().getNode();
							double distance = calculateDistance(floatingClone.getNode(), ghostingNode);
							if(distance < closestDistance) {
								closestDistance = distance;
								lastSeenPermutation = possibility;
							}
						}
						pane.getChildren().remove(root.getNode()); //Remove our actual original root since we generate it as a possibility.
						if(lastSeenPermutation != null) //should **never** be null
							lastSeenPermutation.getNode().setOpacity(1);
					}
					didDrag = true;
					floatingClone.getNode().setTranslateX(event.getSceneX() - ini_x);
					floatingClone.getNode().setTranslateY(event.getSceneY() - ini_y);

					double closestDistance = Integer.MAX_VALUE;
					for(AbstractCompoundExpression possibility : dragPermutations) {
						Node possibility_node = possibility.getNode();
						possibility_node.setOpacity(0);
						Node ghostingNode = possibility.findGhost().getNode();
						double distance = calculateDistance(floatingClone.getNode(), ghostingNode);
						if(distance < closestDistance) {
							closestDistance = distance;
							lastSeenPermutation = possibility;
						}
					}
					lastSeenPermutation.getNode().setOpacity(1);
				}

			} else if (event.getEventType() == MouseEvent.MOUSE_RELEASED) {
				if(dragging && didDrag) {
					dropFocused();
					return;
				}
				System.out.println("Pressed at ("+event.getX()+", "+event.getY()+")");
				if(focused != null)
					((Region)focused.getNode()).setBorder(Expression.NO_BORDER);
				//Find our focus
				if(focused instanceof AbstractCompoundExpression) {
					AbstractCompoundExpression casted = (AbstractCompoundExpression) focused;
					Expression newFocus = casted.focusDeeper(event.getSceneX(), event.getSceneY());
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
		}

		private double calculateDistance(Node node, Node ghostingNode) {
			Bounds firstNode = node.localToScene(node.getBoundsInLocal());
			Bounds secondNode = ghostingNode.localToScene(ghostingNode.getBoundsInLocal());
			Point2D firstCenter = new Point2D((firstNode.getMinX() + firstNode.getMaxX()) / 2, (firstNode.getMinY() + firstNode.getMaxY()) / 2);
			Point2D secondCenter = new Point2D((secondNode.getMinX() + secondNode.getMaxX()) / 2, (secondNode.getMinY() + secondNode.getMaxY()) / 2);
			double xDelta = (firstCenter.getX() - secondCenter.getX());
			double yDelta = (firstCenter.getY() - secondCenter.getY());
			return Math.sqrt(xDelta * xDelta + yDelta * yDelta);
		}

		private void dropFocused() {
			dragging = false;
			pane.getChildren().remove(floatingClone.getNode());
			//lock us into a new configuration
			pane.getChildren().clear();
			pane.getChildren().add(lastSeenPermutation.getNode());
			lastSeenPermutation.findGhost().setGhost(false);
			focused = lastSeenPermutation.findFocus();
			root = lastSeenPermutation;
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
				System.out.println(expression.convertToString(0));
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
