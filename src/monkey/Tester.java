package monkey;

/**
 * A tester class for the whole {@link monkey} package.
 *
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public class Tester {

	/**
	 * This class cannot be instantiated.
	 *
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private Tester() {
	}

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
		printTest(monkey.mnk.ZobristHasher.distinctDisjuncts(MAXROWS, MAXCOLUMNS), "distinct Zobrist disjuncts");
		System.out.println("MNKPlayerTester tests");
		configurationTest(3, 3, 3);
		configurationTest(4, 3, 3);
		configurationTest(4, 4, 3);
		// skipping some configurations here...
		configurationTest(6, 6, 6);
		configurationTest(7, 4, 4);
		configurationTest(7, 5, 4);
		// skipping some configurations here...
		// configurationTest(10, 10, 5);
		// configurationTest(50, 50, 10);
		// configurationTest(70, 70, 10);
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

	/**
	 * Tests a given m,n,k configuration. Just like in the official tournament, four
	 * games for each configuration are played. Each player gets to move first two
	 * times out of four.
	 *
	 * @param m The number of rows.
	 * @param n The number of columns.
	 * @param k The length of a winning alignment.
	 * @throws IllegalArgumentException At least one of the arguments is not
	 *                                  strictly positive.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	protected static void configurationTest(int m, int n, int k) {
		if (m <= 0 || n <= 0 || k <= 0)
			throw new IllegalArgumentException("At least one of the arguments is not strictly positive.");
		final String valueOfM = String.valueOf(m), valueOfN = String.valueOf(n), valueOfK = String.valueOf(k);
		mnkgame.MNKPlayerTester.main(
				new String[] { "-r", "2", valueOfM, valueOfN, valueOfK, "monkey.MoNKey", "mnkgame.QuasiRandomPlayer" });
		mnkgame.MNKPlayerTester.main(
				new String[] { "-r", "2", valueOfM, valueOfN, valueOfK, "mnkgame.QuasiRandomPlayer", "monkey.MoNKey" });
	}

}
