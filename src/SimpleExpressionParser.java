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
	/*
	 * Attempts to create an expression tree -- flattened as much as possible -- from the specified String.
         * Throws a ExpressionParseException if the specified string cannot be parsed.
	 * @param str the string to parse into an expression tree
	 * @param withJavaFXControls you can just ignore this variable for R1
	 * @return the Expression object representing the parsed expression tree
	 */
	public Expression parse (String str, boolean withJavaFXControls) throws ExpressionParseException {
		// Remove spaces -- this simplifies the parsing logic
		str = str.replaceAll(" ", "");
		Expression expression = parseExpression(str);
		if (expression == null) {
			// If we couldn't parse the string, then raise an error
			throw new ExpressionParseException("Cannot parse expression: " + str);
		}

		// Flatten the expression before returning
		expression.flatten();
		return expression;
	}
	
	protected Expression parseExpression (String str) {
		return parseAddition(str);
	}

	private Expression parseSymbol(String input, char target, Supplier<CompoundExpression> newExp, Function<String, Expression> firstHalf, Function<String, Expression> secondHalf) {
		for(int i = input.indexOf(target); i < input.length() - 1 && i > 0; i = input.indexOf(target, i+1)) {
			Expression firstExp = firstHalf.apply(input.substring(0, i));
			Expression secondExp = secondHalf.apply(input.substring(i+1));
			if(	firstExp != null && secondExp != null) {
				CompoundExpression result = newExp.get();
				result.addSubexpression(firstExp);
				result.addSubexpression(secondExp);
				return result;
			}
		}
		return secondHalf.apply(input);
	}
	private Expression parseAddition(String input) {
		return parseSymbol(input, '+',
				AdditiveCompoundExpression::new,
				this::parseAddition,
				this::parseMultiplication
		);
	}
		private Expression parseMultiplication(String input) {
		return parseSymbol(input, '*',
				MultiplicativeCompoundExpression::new,
				this::parseMultiplication,
				this::parseParenthetical
				);
	}
	private Expression parseParenthetical(String input) {
		if(input.length() >= 3) {
			char firstChar = input.charAt(0);
			char lastChar = input.charAt(input.length() - 1);
			String midSection = input.substring(1, input.length() - 1);
			if (firstChar == '(' && lastChar == ')' && parseAddition(midSection) != null) {
				CompoundExpression result = new ParentheticalCompoundExpression();
				result.addSubexpression(parseAddition(midSection));
				return result;
			}
		}
		return parseTerminal(input);
	}
	private Expression parseTerminal(String input) {
		TerminalExpression result = new TerminalExpression(input);
		if(result.isValid)
			return result;
		return null;
	}

}
