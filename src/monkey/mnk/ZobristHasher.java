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
		final int players = 2;
		disjuncts = new int[m][n][players];
		Random r = new Random(0l);
		for (int[][] grid : disjuncts)
			for (int[] row : grid)
				for (int i = 0; i < n; ++i)
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
	 * Random generated values for every {@link Position}-{@link mnk.ai.Player
	 * Player} couple.
	 */
	private int disjuncts[][][];

}
