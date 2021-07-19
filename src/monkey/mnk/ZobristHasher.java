package monkey.mnk;

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

	}

}
