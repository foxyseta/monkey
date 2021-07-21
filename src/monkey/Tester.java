package monkey;

/**
 * An internal tester class for the whole {@link monkey} package.
 *
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public class Tester {

	/**
	 * Runs some utility tests useful for development and debugging.
	 *
	 * @param args Every argument is ignored.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public static void main(String[] args) {
		System.out.println("MoNKey internal tests");
		final int MAXROWS = 70, MAXCOLUMNS = 70;
		printTest(monkey.mnk.ZobristHasher.distincDisjuncts(MAXROWS, MAXCOLUMNS), "distinct Zobrist disjuncts");
	}

	/**
	 * Displays the results of a test in a formatted fashion.
	 *
	 * @param result The result of the test.
	 * @param name   The name of the test.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	protected static void printTest(boolean result, String name) {
		System.out.println("\t" + (result ? '✓' : '×') + ' ' + name);
	}

}
