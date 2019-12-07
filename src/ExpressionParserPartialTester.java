import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.*;
import java.io.*;

/**
 * Code to test Project 5; you should definitely add more tests!
 */
public class ExpressionParserPartialTester {
	private ExpressionParser _parser;

	@Before
	/**
	 * Instantiates the actors and movies graphs
	 */
	public void setUp () throws IOException {
		_parser = new SimpleExpressionParser();
	}

	@Test
	/**
	 * Just verifies that the SimpleExpressionParser could be instantiated without crashing.
	 */
	public void finishedLoading () {
		assertTrue(true);
		// Yay! We didn't crash
	}

	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testExpression1 () throws ExpressionParseException {
		final String expressionStr = "a+b";
		final String parseTreeStr = "+\n\ta\n\tb\n";
		assertEquals(parseTreeStr, _parser.parse(expressionStr, false).convertToString(0));
	}

	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testExpression2 () throws ExpressionParseException {
		final String expressionStr = "13*x";
		final String parseTreeStr = "*\n\t13\n\tx\n";
		assertEquals(parseTreeStr, _parser.parse(expressionStr, false).convertToString(0));
	}

	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testExpression3 () throws ExpressionParseException {
		final String expressionStr = "4*(z+5*x)";
		final String parseTreeStr = "*\n\t4\n\t()\n\t\t+\n\t\t\tz\n\t\t\t*\n\t\t\t\t5\n\t\t\t\tx\n";
		assertEquals(parseTreeStr, _parser.parse(expressionStr, false).convertToString(0));
	}

	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testExpression4 () throws ExpressionParseException {
		final String expressionStr = "10*x*z + 2*(15+y)";
		final String parseTreeStr = "+\n" +
				"\t*\n" +
				"\t\t10\n" +
				"\t\tx\n" +
				"\t\tz\n" +
				"\t*\n" +
				"\t\t2\n" +
				"\t\t()\n" +
				"\t\t\t+\n" +
				"\t\t\t\t15\n" +
				"\t\t\t\ty\n";
		assertEquals(parseTreeStr, _parser.parse(expressionStr, false).convertToString(0));
	}

	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testExpressionAndFlatten1 () throws ExpressionParseException {
		final String expressionStr = "1+2+3";
		final String parseTreeStr = "+\n\t1\n\t2\n\t3\n";
		assertEquals(parseTreeStr, _parser.parse(expressionStr, false).convertToString(0));
	}

	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testExpressionAndFlatten2 () throws ExpressionParseException {
		final String expressionStr = "(x+(x)+(x+x)+x)";
		final String parseTreeStr = "()\n\t+\n\t\tx\n\t\t()\n\t\t\tx\n\t\t()\n\t\t\t+\n\t\t\t\tx\n\t\t\t\tx\n\t\tx\n";
		assertEquals(parseTreeStr, _parser.parse(expressionStr, false).convertToString(0));
	}
	@Test
	public void test4 () throws ExpressionParseException {
		final String expressionStr = "((((x+(((((((((((((x)))))))))))))+(((((x+x)))))+x))))";
		final String parseTreeStr = "()\n\t+\n\t\tx\n\t\t()\n\t\t\tx\n\t\t()\n\t\t\t+\n\t\t\t\tx\n\t\t\t\tx\n\t\tx\n";
		assertEquals(parseTreeStr, _parser.parse(expressionStr, false).convertToString(0));
	}

	@Test(expected = ExpressionParseException.class)
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testException1 () throws ExpressionParseException {
		final String expressionStr = "1+2+";
		_parser.parse(expressionStr, false);
	}

	@Test(expected = ExpressionParseException.class)
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testException2 () throws ExpressionParseException {
		final String expressionStr = "((()))";
		_parser.parse(expressionStr, false);
	}

	@Test(expected = ExpressionParseException.class)
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
	public void testException3 () throws ExpressionParseException {
		final String expressionStr = "()()";
		_parser.parse(expressionStr, false);
	}
	@Test(expected = ExpressionParseException.class)
	public void testException4 () throws ExpressionParseException {
		final String expressionStr = "";
		_parser.parse(expressionStr, false);
	}

//	@Test
	/**
	 * Verifies that a specific expression is parsed into the correct parse tree.
	 */
//	public void testDeepClone () throws ExpressionParseException {
//		final String expressionStr = "(3+x*2*(9+z+x)*3)*2+36";
//		Expression parsed = _parser.parse(expressionStr, false);
//		Expression cloned = parsed.deepCopy();
//		assertEquals(parsed.convertToString(0), cloned.convertToString(0));
//		LinkedList<Expression> check_source = new LinkedList<>();
//		LinkedList<Expression> check_cloned = new LinkedList<>();
//		check_source.add(parsed);
//		check_cloned.add(cloned);
//		while(check_source.size() > 0) {
//			Expression natural = check_source.pop();
//			Expression duplicate = check_cloned.pop();
//			assertNotEquals(natural.hashCode(), duplicate.hashCode());
//			if(natural instanceof AbstractCompoundExpression) {
//				check_source.addAll(((AbstractCompoundExpression) natural).getSubexpressions());
//				check_cloned.addAll(((AbstractCompoundExpression) duplicate).getSubexpressions());
//			}
//		}
//	}

}
