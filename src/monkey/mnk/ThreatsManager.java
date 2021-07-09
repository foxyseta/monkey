package monkey.mnk;

import mnkgame.MNKCellState;
import monkey.ai.Player;
import monkey.util.DirectAddressTable;

/**
 * A <code>ThreatsManager</code> for a certain {@link #L L} counts the {@link #L
 * L}-long {@link Threat}s with no hole and the <code>{@link #L L}-1</code>-long
 * {@link Threat}s with a hole in them. A single istance of this class takes
 * Θ({@link #SIZE}) memory.
 *
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public class ThreatsManager {

	/** See {@link ThreatsManager}. */
	final public int L;
	/** See the project report. */
	final public int B;
	/** See the project report. */
	final public int H;
	/** Number of possible {@link Alignment}s. */
	final public int ALIGNMENTS;

	/**
	 * Constructs a new {@link ThreatsManager} given the {@link #L L} parameter and
	 * a {@link Board}. The actual <code>MNKCellState</code>s of the {@link Board}
	 * do not matter. Takes Θ({@link #SIZE}) time.
	 *
	 * @param l The {@link #L L} parameter. Must be greater than 1.
	 * @param b The {@link Board} to consider.
	 * @throws IllegalArgumentException l is not greater than 1.
	 * @throws NullPointerException     b is null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public ThreatsManager(int l, Board b) {
		if (l <= 1)
			throw new IllegalArgumentException("l is not greater than 1.");
		if (b == null)
			throw new NullPointerException("b is null.");
		L = l;
		board = b;
		// alignments
		B = Math.max(0, board.N - L + 1);
		H = Math.max(0, board.M - L + 1);
		ALIGNMENTS = countAlignments();
		alignments = new DirectAddressTable<Alignment>(Alignment.class, a -> toKey(a), ALIGNMENTS);
		final Threat[] threats = Threat.values();
		for (Threat t : threats)
			counters.insert(new ThreatsCounter(t));
	}

	/**
	 * (Un)records a mark for the whole {@link Board}. Takes Θ({@link #K}) time.
	 * 
	 * @param pos        {@link Position} of the mark to (un)record.
	 * @param pl         The {@link monkey.ai.Player Player} responsible for the
	 *                   change.
	 * @param cellStates The updated state of the {@link Board}.
	 * @throws IllegalArgumentException Either position or cellStates are meant for
	 *                                  another M-N-K tuple.
	 * @throws NullPointerException     Either pos, pl, or cellStates are null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public void updateAlignments(Position pos, Player pl, MNKCellState[][] cellStates) {
		if (pos == null || pl == null || cellStates == null)
			throw new NullPointerException("Either pos, pl, or cellStates are null.");
		if (pos.ROWSNUMBER != board.M || pos.COLUMNSNUMBER != board.N || cellStates.length != board.M
				|| cellStates[0].length != board.N)
			throw new IllegalArgumentException("M-N-K incompatibility.");
		final int row = pos.getRow(), column = pos.getColumn();
		final boolean add = cellStates[pos.getRow()][pos.getColumn()] != MNKCellState.FREE;
		// horizontal alignments
		int max = Math.min(board.N - L, column);
		for (int j = Math.max(0, column - L + 1); j <= max; ++j) {
			final Position position = new Position(board, row, j);
			final MNKCellState firstExt = j == 0 ? null : cellStates[row][j - 1],
					secondExt = j + L == board.N ? null : cellStates[row][j + L];
			final Alignment a = new Alignment(position, Alignment.Direction.HORIZONTAL, L, firstExt, secondExt);
			updateAlignmentContent(a, add, pl);
		}
		// vertical alignments
		max = Math.min(board.M - L, row);
		for (int i = Math.max(0, row - L + 1); i <= max; ++i) {
			final Position position = new Position(board, i, column);
			final MNKCellState firstExt = i == 0 ? null : cellStates[i - 1][column],
					secondExt = i + L == board.M ? null : cellStates[i + L][column];
			final Alignment a = new Alignment(position, Alignment.Direction.VERTICAL, L, firstExt, secondExt);
			updateAlignmentContent(a, add, pl);
		}
		// primary diagonal alignments
		max = Math.min(board.N - L + row - column, Math.min(board.M - board.K, row));
		for (int i = Math.max(0, Math.max(row - L + 1, row - column)), j = i + column - row; i <= max; ++i, ++j) {
			final Position position = new Position(board, i, j);
			final MNKCellState firstExt = i == 0 || j == 0 ? null : cellStates[i - 1][j - 1],
					secondExt = i + L == board.M || j + L == board.N ? null : cellStates[i + L][j + L];
			final Alignment a = new Alignment(position, Alignment.Direction.PRIMARY_DIAGONAL, L, firstExt, secondExt);
			updateAlignmentContent(a, add, pl);
		}
		// secondary diagonal alignments
		max = Math.min(column + row, Math.min(board.M - 1, row + L - 1));
		for (int i = Math.max(row + column + L - board.N, Math.max(L - 1, row)), j = row + column - i; i <= max; ++i, --j) {
			final Position position = new Position(board, i, j);
			final MNKCellState firstExt = i == board.M - 1 || j == 0 ? null : cellStates[i + 1][j - 1],
					secondExt = i - L == -1 || j + L == board.N ? null : cellStates[i - L][j + L];
			final Alignment a = new Alignment(position, Alignment.Direction.SECONDARY_DIAGONAL, L, firstExt, secondExt);
			updateAlignmentContent(a, add, pl);
		}
		// horizontal extremities
		if (column + L < board.N) {
			final Position position = new Position(board, row, column + 1);
			final MNKCellState firstExt = cellStates[row][column],
					secondExt = column + L + 1 == board.N ? null : cellStates[row][column + L + 1];
			final Alignment a = new Alignment(position, Alignment.Direction.HORIZONTAL, L, firstExt, secondExt);
			updateAlignmentExtremity(a, true, firstExt, cellStates);
		}
		if (column - L >= 0) {
			final Position position = new Position(board, row, column - L);
			final MNKCellState firstExt = column - L == 0 ? null : cellStates[row][column - L - 1],
					secondExt = cellStates[row][column];
			final Alignment a = new Alignment(position, Alignment.Direction.HORIZONTAL, L, firstExt, secondExt);
			updateAlignmentExtremity(a, false, secondExt, cellStates);
		}
		// vertical extremities
		if (row + L < board.M) {
			final Position position = new Position(board, row + 1, column);
			final MNKCellState firstExt = cellStates[row][column],
					secondExt = row + L + 1 == board.M ? null : cellStates[row + L + 1][column];
			final Alignment a = new Alignment(position, Alignment.Direction.VERTICAL, L, firstExt, secondExt);
			updateAlignmentExtremity(a, true, firstExt, cellStates);
		}
		if (row - L >= 0) {
			final Position position = new Position(board, row - L, column);
			final MNKCellState firstExt = row - L == 0 ? null : cellStates[row - L - 1][column],
					secondExt = cellStates[row][column];
			final Alignment a = new Alignment(position, Alignment.Direction.VERTICAL, L, firstExt, secondExt);
			updateAlignmentExtremity(a, false, secondExt, cellStates);
		}
		// primary diagonal extremities
		if (row + L < board.M && column + L < board.N) {
			final Position position = new Position(board, row + 1, column + 1);
			final MNKCellState firstExt = cellStates[row][column],
					secondExt = row + L + 1 == board.M || column + L + 1 == board.N ? null
							: cellStates[row + L + 1][column + L + 1];
			final Alignment a = new Alignment(position, Alignment.Direction.PRIMARY_DIAGONAL, L, firstExt, secondExt);
			updateAlignmentExtremity(a, true, firstExt, cellStates);
		}
		if (row - L >= 0 && column - L >= 0) {
			final Position position = new Position(board, row - L, column - L);
			final MNKCellState firstExt = row - L == 0 || column - L == 0 ? null : cellStates[row - L - 1][column - L - 1],
					secondExt = cellStates[row][column];
			final Alignment a = new Alignment(position, Alignment.Direction.PRIMARY_DIAGONAL, L, firstExt, secondExt);
			updateAlignmentExtremity(a, false, secondExt, cellStates);
		}
		// secondary diagonal extremities
		if (row - L >= 0 && column + L < board.N) {
			final Position position = new Position(board, row - 1, column + 1);
			final MNKCellState firstExt = cellStates[row][column],
					secondExt = row - L == 0 || column + L + 1 == board.N ? null : cellStates[row - L - 1][column + L + 1];
			final Alignment a = new Alignment(position, Alignment.Direction.SECONDARY_DIAGONAL, L, firstExt, secondExt);
			updateAlignmentExtremity(a, true, firstExt, cellStates);
		}
		if (row + L < board.M && column - L >= 0) {
			final Position position = new Position(board, row + L, column - L);
			final MNKCellState firstExt = row + L + 1 == board.M || column - L == 0 ? null
					: cellStates[row + L + 1][column - L - 1], secondExt = cellStates[row][column];
			final Alignment a = new Alignment(position, Alignment.Direction.SECONDARY_DIAGONAL, L, firstExt, secondExt);
			updateAlignmentExtremity(a, false, secondExt, cellStates);
		}
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
	 * Returns the number of {@link Threat}s of the specified nature and threatener.
	 *
	 * @param t Queried {@link Threat} type.
	 * @param p The {@link monkey.ai.Player Player} to be used as threatener.
	 * @throws NullPointerException Either t or p are <code>null</code>.
	 * @return Current value of the counter.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int count(Threat t, Player p) {
		if (t == null || p == null)
			throw new NullPointerException("Either t or p are null.");
		return counters.search(t.ordinal()).get(p);
	}

	/**
	 * Computes the number of possible {@link monkey.mnk.Alignment Alignment}s for
	 * this {@link Board}.
	 *
	 * @return The number of possible {@link monkey.mnk.Alignment Alignment}s.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private int countAlignments() {
		return B * (board.M + H) + H * (board.N + B);
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
			return row * B + column;
		case VERTICAL: // B * M + [0 .. N * H - 1]
			return B * board.M + row * board.N + column;
		case PRIMARY_DIAGONAL: // B * M + N * H + [0 .. B * H - 1]
			return B * (board.M + row) + board.N * H + column;
		case SECONDARY_DIAGONAL: // B * (M + H) + N * H + [0 .. B * H - 1]
			return B * (2 * H + row) + board.N * H + column;
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
		final Player oldThreatener = result.getThreatener();
		try {
			if (add)
				result.addMark(player);
			else
				result.removeMark(player);
		} catch (IllegalCallerException e) {
			throw new IllegalArgumentException("Cannot " + (add ? "add" : "remove") + " any more marks.");
		}
		final Threat newThreat = result.getThreat();
		final Player newThreatener = result.getThreatener();
		if (newThreat != oldThreat || newThreatener != oldThreatener) {
			if (oldThreat != null)
				counters.search(oldThreat.ordinal()).decrement(oldThreatener);
			if (result.getThreat() != null)
				counters.search(result.getThreat().ordinal()).increment(result.getThreatener());
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
		final Player oldThreatener = result.getThreatener();
		if (first)
			result.setFirstExtremity(state, cellStates);
		else
			result.setSecondExtremity(state, cellStates);
		final Threat newThreat = result.getThreat();
		final Player newThreatener = result.getThreatener();
		if (newThreat != oldThreat || newThreatener != oldThreatener) {
			if (oldThreat != null)
				counters.search(oldThreat.ordinal()).decrement(oldThreatener);
			if (result.getThreat() != null)
				counters.search(result.getThreat().ordinal()).increment(result.getThreatener());
		}
	}

	@Override // inherit doc comment
	public String toString() {
		return String.format("<" + L + ">%n" + alignments + "%n" + counters);
	}

	/**
	 * The non-<code>null</code> {@link Board} whose {@link Threat}s are counted.
	 */
	private Board board;
	/** Stores all of the {@link Board}'s possible {@link Alignment}s. */
	final private DirectAddressTable<Alignment> alignments;
	/** Stores a counter for each type of {@link Threat}. */
	final private DirectAddressTable<ThreatsCounter> counters = new DirectAddressTable<ThreatsCounter>(
			ThreatsCounter.class, c -> c.THREAT.ordinal(), Threat.SIZE);
}
