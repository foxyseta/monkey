package monkey.mnk;

import mnkgame.MNKCellState;
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
		final Threat[] threats = Threat.values();
		for (Threat t : threats)
			counters.insert(new Pair<Threat, Integer>(t, 0));
	}

	/**
	 * (Un)records a mark for the whole {@link Board}.
	 * 
	 * @param position   {@link monkey.ai.Position Position} of the mark to
	 *                   (un)record.
	 * @param cellStates The updated state of the {@link Board}.
	 * @throws IllegalArgumentException Either position or cellStates are meant for
	 *                                  another M-N-K tuple.
	 * @throws NullPointerException     Either position or cellStates are null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public void updateAlignments(Position position, MNKCellState[][] cellStates) {
		if (position == null || cellStates == null)
			throw new NullPointerException("Either position or cellStates are null.");
		if (position.ROWSNUMBER != board.M || position.COLUMNSNUMBER != board.N || cellStates.length != board.M
				|| cellStates[0].length != board.N)
			throw new IllegalArgumentException("M-N-K incompatibility.");
		// TODO
	}

	/**
	 * Sets a new {@link Board} whose dimensions must be the same for the previous
	 * one. The actual <code>MNKCellState</code>s of the {@link Board} do not
	 * matter.
	 *
	 * @param b The new {@link Board}.
	 * @throws IllegalArgumentException The dimensions of the grid changed.
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
	 * Returns the counter of {@link Threat}s of the specified nature.
	 *
	 * @param t Queried {@link Threat} type.
	 * @return Current value of the counter.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int count(Threat t) {
		return counters.search(t.ordinal()).getValue();
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

	/**
	 * (Un)records a mark for a certain {@link Alignment}.
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
			throw new NullPointerException("Either query or player are null");
		if (query.FIRSTCELL.ROWSNUMBER != board.M || query.FIRSTCELL.COLUMNSNUMBER != board.N)
			throw new IllegalArgumentException("M-N-K incompatibility.");
		Alignment result = alignments.search(toKey(query));
		if (result == null) {
			query.clear();
			alignments.insert(result = query);
		}
		final Threat oldThreat = result.getThreat();
		try {
			if (add)
				result.addMark(player);
			else
				result.removeMark(player);
		} catch (IllegalCallerException e) {
			throw new IllegalArgumentException("Cannot " + (add ? "add" : "remove") + " any more marks.");
		}
		if (result.getThreat() != oldThreat) {
			updateCounter(oldThreat, -1);
			updateCounter(result.getThreat(), 1);
		}
	}

	/**
	 * (Un)records an extremity for a certain {@link Alignment}.
	 *
	 * @param query      Its coordinates are used to identify the element to update.
	 *                   May be dirtied after its use.
	 * @param first      <code>true</code> just in case the extremity to update is
	 *                   the first one.
	 * @param state      The new state of the extremity. May be null.
	 * @param cellStates The current state of the board.
	 * @throws IllegalArgumentException query or cellState is meant for another
	 *                                  M-N-K tuple.
	 * @throws IllegalCallerException   The extremity cannot be changed anymore.
	 * @throws IllegalArgumentException The extremity cannot be set to this
	 *                                  particular state.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private void updateAlignmentExtremity(Alignment query, boolean first, MNKCellState state,
			MNKCellState[][] cellStates) {
		if (query == null)
			throw new NullPointerException("query is null.");
		if (query.FIRSTCELL.ROWSNUMBER != board.M || query.FIRSTCELL.COLUMNSNUMBER != board.N
				|| cellStates.length != board.M || cellStates[0].length != board.N)
			throw new IllegalArgumentException("M-N-K incompatibility.");
		Alignment result = alignments.search(toKey(query));
		if (result == null) {
			query.clear();
			alignments.insert(result = query);
		}
		final Threat oldThreat = result.getThreat();
		if (first)
			result.setFirstExtremity(state, cellStates);
		else
			result.setSecondExtremity(state, cellStates);
		if (result.getThreat() != oldThreat) {
			updateCounter(oldThreat, -1);
			updateCounter(result.getThreat(), 1);
		}
	}

	/**
	 * Updates the counter assigned to a certain {@link Threat} using the specified
	 * offset.
	 *
	 * @param t      The {@link Threat} whose counter is to be updated.
	 * @param offset The increment (or decrement) to use.
	 * @throws IllegalArgumentException Negative counter value.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private void updateCounter(Threat t, int offset) {
		if (t != null) {
			final int value = counters.search(t.ordinal()).getValue() + offset;
			if (value < 0)
				throw new IllegalArgumentException("Negative counter value.");
			counters.insert(new Pair<Threat, Integer>(t, value));
		}
	}

	/**
	 * The non-<code>null</code> {@link Board} whose {@link Threat}s are counted.
	 */
	private Board board;
	/** Stores all of the {@link Board}'s possible {@link Alignment}s. */
	final private DirectAddressTable<Alignment> alignments;
	/** Stores a counter for each type of {@link Threat}. */
	final private DirectAddressTable<Pair<Threat, Integer>> counters = new DirectAddressTable<Pair<Threat, Integer>>(
			Pair<Threat, Integer>.class, p -> p.getKey().ordinal(), Threat.SIZE);
}
