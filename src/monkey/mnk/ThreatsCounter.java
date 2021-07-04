package monkey.mnk;

import monkey.ai.Player;
import monkey.util.DirectAddressTable;
import monkey.util.Pair;

/**
 * A <code>ThreatsCounter</code> for a certain {@link #L} counts the
 * {@link #L}-long {@link Threat}s with no hole and the
 * <code>{@link #L}-1</code>-long {@link Threat}s with a hole in them.
 *
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public class ThreatsCounter {

	/** See {@link ThreatsCounter}. */
	final public int L;

	/**
	 * Constructs a new {@link ThreatsCounter} given the {@link #L} parameter and a
	 * {@link Board}. The actual <code>MNKCellState</code>s of the {@link Board} do
	 * not matter.
	 *
	 * @param l The {@link #L} parameter.
	 * @param b The {@Board} to consider.
	 * @throw NullPointerException b is null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public ThreatsCounter(int l, Board b) {
		if (b == null)
			throw new NullPointerException("b is null.");
		L = l;
		board = b;
		alignments = new DirectAddressTable<Alignment>(Alignment.class, a -> toKey(a), b.ALIGNMENTS);

	}

	/**
	 * Sets a new {@link Board} whose dimensions must be the same for the previous
	 * one. The actual <code>MNKCellState</code>s of the {@link Board} do not
	 * matter.
	 *
	 * @param b The new {@link Board}.
	 * @throw IllegalArgumentException The dimensions of the grid changed.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public void setBoard(Board b) {
		if (b.M != board.M || b.N != board.N)
			throw new IllegalArgumentException("The dimensions of the grid changed.");
		board = b;
	}

	/**
	 * Maps a valid {@link monkey.mnk.Alignment Alignment} for the previouslt
	 * specified {@link Board} to an appropriate integer key in [0 ..
	 * {@link Board#ALIGNMENTS} - 1].
	 *
	 * @see #alignments
	 * @param a Value to be mapped.
	 * @throws IllegalArgumentException a's grid extents are different from the
	 *                                  previously specified {@link Board}'s.
	 * @throws NullPointerException     a is <code>null</code>.
	 * @return An integer key
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private int toKey(Alignment a) {
		if (a == null)
			throw new NullPointerException("Null alignment.");
		if (a.FIRSTCELL.ROWSNUMBER != board.M || a.FIRSTCELL.COLUMNSNUMBER != board.N)
			throw new IllegalArgumentException("Incompatible grid extents.");
		final int row = a.FIRSTCELL.getRow(), column = a.FIRSTCELL.getColumn();
		switch (a.DIRECTION) {
		case HORIZONTAL: // [0 .. B * M - 1]
			return row * board.B + column;
		case VERTICAL: // B * M + [0 .. N * H - 1]
			return board.B * board.M + row * board.N + column;
		case PRIMARY_DIAGONAL: // B * M + N * H + [0 .. B * H - 1]
			return board.B * (board.M + row) + board.N * board.H + column;
		case SECONDARY_DIAGONAL: // B * (M + H) + N * H + [0 .. B * H - 1]
			return board.B * (2 * board.H + row) + board.N * board.H + column;
		default:
			throw new IllegalArgumentException("Unknown direction");
		}
	}

	// TODO: updateAlignments

	/**
	 * (Un)records a mark for a certain {@link monkey.mnk.Alignment Alignment}.
	 *
	 * @param query  Its coordinates are used to identify the element to update. May
	 *               be dirtied after its use.
	 * @param add    <code>true</code> just in case the cell has to be added instead
	 *               of removed.
	 * @param player The {@link monkey.ai.Player Player} whose symbol is to be
	 *               added/removed.
	 * @throws IllegalArgumentException query is meant for another M-N-K tuple.
	 * @throws IllegalArgumentException Cannot add any more marks.
	 * @throws NullPointerException     either query or player are null
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private void updateAlignmentContent(Alignment query, boolean add, Player player) {
		if (query == null || player == null)
			throw new NullPointerException("query is null");
		if (query.FIRSTCELL.ROWSNUMBER != board.M || query.FIRSTCELL.COLUMNSNUMBER != board.N)
			throw new IllegalArgumentException("M-N-K incompatibility.");
		Alignment result = alignments.search(toKey(query));
		if (result == null) {
			query.clear();
			alignments.insert(result = query);
		}
		try {
			if (add)
				result.addMark(player);
			else
				result.removeMark(player);
		} catch (IllegalCallerException e) {
			throw new IllegalArgumentException("Cannot " + (add ? "add" : "remove") + " any more marks.");
		}
	}

	// updateAlignmentExtremities

	/**
	 * The non-<code>null</code> {@link Board} whose {@link Threat}s are counted.
	 */
	private Board board;
	/** Stores all of the {@link Board}'s possible {@link Alignment}s. */
	final private DirectAddressTable<Alignment> alignments;
	/** Stores a counter for each type of {@link Threat}. */
	final private DirectAddressTable<Pair<Threat, Integer>> threatCounters = new DirectAddressTable<Pair<Threat, Integer>>(
			Pair<Threat, Integer>.class, p -> p.getKey().ordinal(), Threat.SIZE);
}
