import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Starter code to implement an ExpressionParser. Your parser methods should use the following grammar:
 * E := A | X
 * A := A+M | M
 * M := M*M | X
 * X := (E) | L
 * L := [0-9]+ | [a-z]
 */
public class SimpleExpressionParser implements ExpressionParser {
	private boolean withJavaControls;
	/**
	 * Attempts to create an expression tree -- flattened as much as possible -- from the specified String.
         * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * @param str the string to parse into an expression tree
	 * @param withJavaFXControls you can just ignore this variable for R1
	 * @return the Expression object representing the parsed expression tree
	 */
	public Expression parse (String str, boolean withJavaFXControls) throws ExpressionParseException {
		// Remove spaces -- this simplifies the parsing logic
		withJavaControls = withJavaFXControls;
		str = str.replaceAll(" ", "");
		final Expression expression = parseExpression(str);
		if (expression == null) {
			// If we couldn't parse the string, then raise an error
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Flatten the expression before returning
		expression.flatten();
		//Add signs
		if(expression instanceof AbstractCompoundExpression) {
			AbstractCompoundExpression absExpression = (AbstractCompoundExpression)expression;
			absExpression.addSigns();
		}
		return expression;
	}

	/**
	 * Begin the processing process, starting with parseAddition (for name consistency)
	 * @param str equation to process
	 * @return An expression, or null if invalid.
	 */
	protected Expression parseExpression (String str) {
		return parseAddition(str);
	}

	/**
	 * Parse a given symbol (+ or *) using the recursive process on that text before that symbol and afterwards
	 * @param input String to split
	 * @param target A char representing our desired split symbol (+ or *)
	 * @param newExp A function that, when called, will generate a new classs of CompoundExpressison (differentiated for Additive or Multiplicative)
	 * @param firstHalf The function to run on the first half of the string before the symbol (String -> Expression)
	 * @param secondHalf The function to run on the second half of the string after the symbol (String -> Expression)
	 * @return the expression, or null if invalid.
	 */
	private Expression parseSymbol(String input, char target, Supplier<CompoundExpression> newExp, Function<String, Expression> firstHalf, Function<String, Expression> secondHalf) {
		for(int i = input.indexOf(target); i < input.length() - 1 && i > 0; i = input.indexOf(target, i+1)) {
			final Expression firstExp = firstHalf.apply(input.substring(0, i));
			final Expression secondExp = secondHalf.apply(input.substring(i+1));
			if(	firstExp != null && secondExp != null) {
				final CompoundExpression result = newExp.get();
				result.addSubexpression(firstExp);
				result.addSubexpression(secondExp);
				return result;
			}
		}
		return secondHalf.apply(input);
	}

	/**
	 * Parse the addition operator, or just jump to multiplication
	 * @param input String to parse
	 * @return Expression representing input or null.
	 */
	private Expression parseAddition(String input) {
		return parseSymbol(input, '+',
				() -> new AdditiveCompoundExpression(withJavaControls),
				this::parseAddition,
				this::parseMultiplication
		);
	}
	/**
	 * Parse the multiplication operator, or just jump to parenthetical
	 * @param input String to parse
	 * @return Expression representing input or null.
	 */
	private Expression parseMultiplication(String input) {
		return parseSymbol(input, '*',
				() -> new MultiplicativeCompoundExpression(withJavaControls),
				this::parseMultiplication,
				this::parseParenthetical
				);
	}
	/**
	 * Parse the parenthetical operator, or just jump to literal
	 * @param input String to parse
	 * @return Expression representing input or null.
	 */
	private Expression parseParenthetical(String input) {
		if(input.length() >= 3) {
			final char firstChar = input.charAt(0);
			final char lastChar = input.charAt(input.length() - 1);
			final String midSection = input.substring(1, input.length() - 1);
			if (firstChar == '(' && lastChar == ')' && parseAddition(midSection) != null) {
				CompoundExpression result = new ParentheticalCompoundExpression(withJavaControls);
				result.addSubexpression(parseAddition(midSection));
				return result;
			}
		}
		return parseTerminal(input);
	}
	/**
	 * Parse literals (0-9, a-z), and confirm if the literal is valid.
	 * @param input String to parse
	 * @return Expression representing input or null.
	 */
	private Expression parseTerminal(String input) {
		final TerminalExpression result = new TerminalExpression(input, withJavaControls);
		if(result.isValid)
			return result;
		return null;
	}

}
