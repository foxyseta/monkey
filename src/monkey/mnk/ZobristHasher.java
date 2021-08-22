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
	 * A symmetry is a transformation that leaves an empty {@link Board} unchanged.
	 *
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public enum Symmetry {
		/** A symmetry that does nothing (e). */
		A {
			public Position apply(Position p) {
				return p.clone();
			}
		},
		/** 90° clockwise rotation. */
		B {
			public Position apply(Position p) {
				validateSquareBoard(p);
				return new Position(p.ROWSNUMBER, p.COLUMNSNUMBER, p.getColumn(), p.COLUMNSNUMBER - p.getRow() - 1);
			}
		},
		/** 180° clockwise rotation. */
		C {
			public Position apply(Position p) {
				return new Position(p.ROWSNUMBER, p.COLUMNSNUMBER, p.ROWSNUMBER - p.getRow() - 1,
						p.COLUMNSNUMBER - p.getColumn() - 1);
			}
		},
		/** 270° clockwise rotation. */
		D {
			public Position apply(Position p) {
				validateSquareBoard(p);
				return new Position(p.ROWSNUMBER, p.COLUMNSNUMBER, p.ROWSNUMBER - p.getColumn() - 1, p.getRow());
			}
		},
		/** A flip around the primary diagonal. */
		E {
			public Position apply(Position p) {
				validateSquareBoard(p);
				return new Position(p.ROWSNUMBER, p.COLUMNSNUMBER, p.getColumn(), p.getRow());
			}
		},
		/** A flip around the vertical axis. */
		F {
			public Position apply(Position p) {
				return new Position(p.ROWSNUMBER, p.COLUMNSNUMBER, p.getRow(), p.COLUMNSNUMBER - p.getColumn() - 1);
			}
		},
		/** A flip around the secondary diagonal. */
		G {
			public Position apply(Position p) {
				validateSquareBoard(p);
				return new Position(p.ROWSNUMBER, p.COLUMNSNUMBER, p.ROWSNUMBER - p.getColumn() - 1,
						p.COLUMNSNUMBER - p.getRow() - 1);
			}
		},
		/** A flip around the horizontal axis. */
		H {
			public Position apply(Position p) {
				return new Position(p.ROWSNUMBER, p.COLUMNSNUMBER, p.ROWSNUMBER - p.getRow() - 1, p.getColumn());
			}
		};

		/**
		 * Validates the fact that a given {@link Position} is part of a square
		 * {@link Board} or not.
		 *
		 * @param p The {@link Position} to examine.
		 * @throws IllegalArgumentException The {@link Board} in question is not, in
		 *                                  fact, square.
		 * @author Stefano Volpe
		 * @version 1.0
		 * @since 1.0
		 */
		protected void validateSquareBoard(Position p) {
			if (p.ROWSNUMBER != p.COLUMNSNUMBER)
				throw new IllegalArgumentException("The board is not square.");
		}

		/**
		 * Applies this simmetry to a given {@link Position}, returning the result of
		 * the transformation and leaving the original one unchanged.
		 *
		 * @param p The {@link Position} to transform.
		 * @throws IllegalArgumentException The {@link Board} in question should be
		 *                                  square but is not.
		 * @return A new {@link Position} representing the result of the transformation.
		 * @author Stefano Volpe
		 * @version 1.0
		 * @since 1.0
		 */
		abstract public Position apply(Position p);
	};

	/** Number of {@link monkey.ai.Player}s to be considered. */
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
	 * Retrieves the disjunct for a given {@link Position}-{@link monkey.ai.Player}
	 * couple.
	 *
	 * @param position The {@link Position} of the mark.
	 * @param player   The {@link monkey.ai.Player} who could add/remove the mark.
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
	 * distinct or not. Takes Θ(1) in the best case and
	 * Θ({@link Board#SIZE}<sup>2</sup>) time in the average and worst cases.
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
	 * Random generated values for every {@link Position}-{@link monkey.ai.Player}
	 * couple.
	 */
	private int disjuncts[][][];

}
