package monkey.mnk;

import java.util.Random;
import monkey.ai.Player;

/**
 * A <code>ZobristHasher</code> stores the data needed for Zobrist hashing. It
 * is specifically designed for m,n,k-games. See A. L. Zobrist, <i>A New Hashing
 * Method with Application for Game Playing</i>. 1 Jan. 1990, pp. 5-7. A single
 * istance of this class for a certain {@link Board} takes Θ({@link Board#SIZE})
 * memory.
 *
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public class ZobristHasher {

	/** Number of {@link monkey.ai.Player Player}s to be considered. */
	final public static int PLAYERS = 2;
	/** Seed to be used for the generated pseudo-random sequence. */
	final public static long SEED = 0l;

	/**
	 * Constructs a new {@link ZobristHasher} from the board's extents. Takes
	 * Θ({@link Board#SIZE}) time.
	 *
	 * @param m Number of rows.
	 * @param n Number of columns.
	 * @throws IllegalArgumentException m or n or k is not positive.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public ZobristHasher(int m, int n) {
		if (m <= 0)
			throw new IllegalArgumentException("m <= 0");
		if (n <= 0)
			throw new IllegalArgumentException("n <= 0");
		disjuncts = new int[m][n][PLAYERS];
		Random r = new Random(SEED);
		for (int[][] grid : disjuncts)
			for (int[] row : grid)
				for (int i = 0; i < PLAYERS; ++i)
					row[i] = r.nextInt();
	}

	/**
	 * Retrieves the disjunct for a given {@link Position}-{@link mnk.ai.Player
	 * Player} couple.
	 *
	 * @param position The {@link Position} of the mark.
	 * @param player   The {@link} Player} who could add/remove the mark.
	 * @throws NullPointerException     At least one argument is <code>null</code>.
	 * @throws IllegalArgumentException position does not have the right extents.
	 * @return The desired disjunct.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int getDisjunct(Position position, Player player) {
		if (position == null || player == null)
			throw new NullPointerException("Both arguments must be non-null.");
		if (position.ROWSNUMBER != disjuncts.length || position.COLUMNSNUMBER != disjuncts[0].length)
			throw new IllegalArgumentException("Incompatible extents.");
		return disjuncts[position.getRow()][position.getColumn()][player.ordinal()];
	}

	/**
	 * Checks whether the pseudo-random disjuncts generated for a {@link Board} are
	 * distinct or not. Takes Θ(1) in the best case and Θ({@link Board#SIZE^2}) time
	 * in the average and worst cases.
	 *
	 * @param m Strictly positive number of rows.
	 * @param n Strictly positive number of columns.
	 * @throws IllegalArgumentException At least one of the arguments is not
	 *                                  strictly positive.
	 * @return <code>true</code> just in case the disjuncts are distinct.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public static boolean distincDisjuncts(int m, int n) {
		int[] test = new int[m * n * 2];
		Random r = new Random(SEED);
		for (int i = 0; i < test.length; ++i) {
			test[i] = r.nextInt();
			for (int j = 0; j < i; ++j)
				if (test[j] == test[i]) {
					System.out.println("Failed: #" + j + " and #" + i + " have the same value.");
					return false;
				}
		}
		return true;
	}

	/**
	 * Random generated values for every {@link Position}-{@link mnk.ai.Player
	 * Player} couple.
	 */
	private int disjuncts[][][];

}
