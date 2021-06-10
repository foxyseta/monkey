package monkey.mnk;

import java.lang.Iterable;
import java.lang.Math;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.ArrayList;
import java.util.Stack;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;
import monkey.ai.Player;
import monkey.util.DirectAddressTable;

/**
 * A <code>Board</code> describes the {@link monkey.ai.State State} of a
 * MNK-game. It supports backtracking and alpha-beta pruning. A single istance
 * of this class takes Θ({@link #SIZE}) memory.
 *
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public class Board implements monkey.ai.State<Board, Position, Integer> {

	/** Number of rows. */
	final public int M;
	/** Number of columns. */
	final public int N;
	/** Number of symbols to be aligned. */
	final public int K;
	/** Number of cells of the {@link Board}. */
	final public int SIZE;
	/** Number of possible {@link Alignment}s. */
	final public int ALIGNMENTS;
	/** Quantifies the satisfaction earned by winning the game. */
	final public static int VICTORYUTILITY = 1;
	/** Quantifies the satisfaction earned when the game ends in a draw. */
	final public static int DRAWUTILITY = 0;
	/** Quantifies the satisfaction earned by losing the game. */
	final public static int LOSSUTILITY = -1;

	/**
	 * Constructs a new {@link Board} given its m, n and k parameters. Takes
	 * Θ({@link #SIZE}) time.
	 *
	 * @param m Number of rows.
	 * @param n Number of columns.
	 * @param k Number of symbols to be aligned.
	 * @throws IllegalArgumentException m or n or k is negative.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public Board(int m, int n, int k) {
		// constants
		if (m < 0)
			throw new IllegalArgumentException("m < 0");
		M = m;
		if (n < 0)
			throw new IllegalArgumentException("n < 0");
		N = n;
		if (k < 0)
			throw new IllegalArgumentException("k < 0");
		K = k;
		SIZE = m * n;
		// states
		state = SIZE > 0 ? MNKGameState.OPEN : MNKGameState.DRAW;
		cellStates = new MNKCellState[M][N];
		for (MNKCellState[] row : cellStates)
			Arrays.fill(row, MNKCellState.FREE);
		// alignments
		B = Math.max(0, N - K + 1);
		H = Math.max(0, M - K + 1);
		ALIGNMENTS = countAlignments();
		alignments = new DirectAddressTable<Alignment>(Alignment.class, a -> toKey(a), ALIGNMENTS);
		// action candidates
		actionsCandidates = generateActionCandidates();
	}

	@Override // inherit doc comment
	public Player player() {
		return history.size() % 2 == 0 ? Player.P1 : Player.P2;
	}

	/**
	 * {@inheritDoc} Takes Ο({@link #SIZE}) time.
	 */
	@Override
	public Iterable<Position> actions() {
		ArrayList<Position> res = new ArrayList<Position>(SIZE - history.size());
		for (Position p : actionsCandidates)
			if (cellStates[p.getRow()][p.getColumn()] == MNKCellState.FREE)
				res.add(p);
		return res;
	}

	/**
	 * {@inheritDoc} Takes Θ({@link #K}) time.
	 */
	@Override
	public Board result(Position a) {
		if (state != MNKGameState.OPEN)
			throw new IllegalCallerException("The game is already over.");
		if (a.ROWSNUMBER != M || a.COLUMNSNUMBER != N)
			throw new IllegalArgumentException("Referring to a board of different extents.");
		final int row = a.getRow(), column = a.getColumn();
		if (cellStates[row][column] != MNKCellState.FREE)
			throw new IllegalArgumentException("(" + row + ", " + column + ") is not free.");
		cellStates[row][column] = player() == Player.P1 ? MNKCellState.P1 : MNKCellState.P2;
		updateAlignments(a, x -> updateMark(x, true));
		history.push(a);
		if (state == MNKGameState.OPEN && history.size() == SIZE)
			state = MNKGameState.DRAW;
		return this;
	}

	/**
	 * {@inheritDoc} Takes Θ({@link #K}) time.
	 */
	@Override
	public Board revert() {
		try {
			final Position a = history.pop();
			final int row = a.getRow(), column = a.getColumn();
			cellStates[row][column] = MNKCellState.FREE;
			updateAlignments(a, x -> updateMark(x, false));
			state = MNKGameState.OPEN;
		} catch (java.util.EmptyStackException e) {
			throw new IllegalCallerException("No previous action to revert.");
		}
		return this;
	}

	@Override // inherit doc comment
	public boolean terminalTest() {
		return !(state == MNKGameState.OPEN);
	}

	@Override // inherit doc comment
	public Integer utility(Player p) {
		switch (state) {
		case DRAW:
			return DRAWUTILITY;
		case OPEN:
			throw new IllegalCallerException("The game is still open");
		case WINP1:
			return p == Player.P1 ? VICTORYUTILITY : LOSSUTILITY;
		case WINP2:
			return p == Player.P2 ? VICTORYUTILITY : LOSSUTILITY;
		default:
			throw new IllegalArgumentException("Unknown game state");
		}
	}

	@Override // inherit doc comment
	public Integer initialAlpha(Player p) {
		return LOSSUTILITY;
	}

	@Override // inherit doc comment
	public Integer initialBeta(Player p) {
		return VICTORYUTILITY;
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
		return B * (M + H) + H * (N + B);
	}

	/**
	 * Maps a valid {@link monkey.mnk.Alignment Alignment} for this {@link Board} to
	 * an appropriate integer key in [0 .. {@link #ALIGNMENTS} - 1].
	 *
	 * @see #alignments
	 * @param a Value to be mapped.
	 * @throws IllegalArgumentException a's grid extents are different from this
	 *                                  {@link Board}'s.
	 * @throws NullPointerException     a is <code>null</code>.
	 * @return An integer key
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private int toKey(Alignment a) {
		if (a == null)
			throw new NullPointerException("Null alignment.");
		if (a.FIRSTCELL.ROWSNUMBER != M || a.FIRSTCELL.COLUMNSNUMBER != N)
			throw new IllegalArgumentException("Incompatible grid extents.");
		final int row = a.FIRSTCELL.getRow(), column = a.FIRSTCELL.getColumn();
		switch (a.DIRECTION) {
		case HORIZONTAL: // [0 .. B * M - 1]
			return row * B + column;
		case VERTICAL: // B * M + [0 .. N * H - 1]
			return B * M + row * N + column;
		case PRIMARY_DIAGONAL: // B * M + N * H + [0 .. B * H - 1]
			return B * (M + row) + N * H + column;
		case SECONDARY_DIAGONAL: // B * (M + H) + N * H + [0 .. B * H - 1]
			return B * (2 * H + row) + N * H + column;
		default:
			throw new IllegalArgumentException("Unknown direction");
		}
	}

	/**
	 * (Un)records a mark for a certain {@link monkey.mnk.Alignment Alignment} based
	 * on the current {@link monkey.ai.Player Player}.
	 *
	 * @param query Its coordinates are used to identify the element to update. May
	 *              be dirtied after its use.
	 * @param add   <code>true</code> just in case the cell has to be added instead
	 *              of removed.
	 * @throws IllegalArgumentException query is meant for another M-N-K tuple.
	 * @throws IllegalArgumentException Cannot add any more marks.
	 * @throws IllegalCallerException   Unknown direction.history.peek()
	 * @throws NullPointerException     query is null
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private void updateMark(Alignment query, boolean add) {
		if (query == null)
			throw new NullPointerException("query is null");
		if (query.FIRSTCELL.ROWSNUMBER != M || query.FIRSTCELL.COLUMNSNUMBER != N || query.LENGTH != K)
			throw new IllegalArgumentException("M-N-K incompatibility.");
		Alignment result = alignments.search(toKey(query));
		if (result == null) {
			query.clear();
			alignments.insert(result = query);
		}
		try {
			if (add)
				switch (result.addMark(player())) {
				case P1FULL:
					state = MNKGameState.WINP1;
					break;
				case P2FULL:
					state = MNKGameState.WINP2;
					break;
				default:
					break;
				}
			else
				result.removeMark(player());
		} catch (IllegalCallerException e) {
			throw e.getMessage() == "Unknown direction." ? e
					: new IllegalArgumentException("Cannot " + (add ? "add" : "remove") + " any more marks.");
		}
	}

	/**
	 * Applies a generic "update" function to every {@link monkey.mnk.Alignment
	 * Alignment} containing a given cell. Takes Θ({@link #K} f) time, where f is
	 * the cost of the "update" function.
	 *
	 * @param p      Location of the cell which has just been (un)marked.
	 * @param update Functional argument invoked as an "update" function.
	 * @throws IllegalArgumentException p caused an M-N-K incompatibility.
	 * @throws NullPointerException     p, or update, or both are null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private void updateAlignments(Position p, Consumer<Alignment> update) {
		if (p == null)
			throw new NullPointerException("p is null.");
		if (update == null)
			throw new NullPointerException("update is null.");
		if (p.ROWSNUMBER != M || p.COLUMNSNUMBER != N)
			throw new IllegalArgumentException("M-N-K incompatibility");
		final int row = p.getRow(), column = p.getColumn();
		// horizontal alignments
		int max = Math.min(N - K, column);
		for (int j = Math.max(0, column - K + 1); j <= max; ++j)
			update.accept(new Alignment(new Position(this, row, j), Alignment.Direction.HORIZONTAL, K));
		// vertical alignments
		max = Math.min(M - K, row);
		for (int i = Math.max(0, row - K + 1); i <= max; ++i)
			update.accept(new Alignment(new Position(this, i, column), Alignment.Direction.VERTICAL, K));
		// primary diagonal alignments
		max = Math.min(N - K + row - column, Math.min(M - K, row));
		for (int i = Math.max(0, Math.max(row - K + 1, row - column)), j = i + column - row; i <= max; ++i, ++j)
			update.accept(new Alignment(new Position(this, i, j), Alignment.Direction.PRIMARY_DIAGONAL, K));
		// secondary diagonal alignments
		max = Math.min(column + row, Math.min(M - 1, row + K - 1));
		for (int i = Math.max(row + column + K - N, Math.max(K - 1, row)), j = row + column - i; i <= max; ++i, --j)
			update.accept(new Alignment(new Position(this, i, j), Alignment.Direction.SECONDARY_DIAGONAL, K));
	}

	/**
	 * Generates a sequence containing all of the {@link Position}s of this
	 * <code>Board</code>, sorted by decreasing heuristic value. See the project
	 * report. Takes Θ({@link #SIZE}) time.
	 *
	 * @return The generated sequence.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	Position[] generateActionCandidates() {
		Position[] res = new Position[SIZE];
		int firstRow = 0, lastRow = M - 1, firstColumn = 0, lastColumn = N - 1;
		int i = SIZE - 1, row = firstRow, column = firstColumn;
		// Escargot
		while (i >= 0) {
			// Top left to top right
			while (column < lastColumn)
				res[i--] = new Position(this, row, column++);
			res[i--] = new Position(this, row++, column);
			if (i < 0)
				break;
			++firstRow;
			// Top right to bottom right
			while (row < lastRow)
				res[i--] = new Position(this, row++, column);
			res[i--] = new Position(this, row, column--);
			if (i < 0)
				break;
			--lastColumn;
			// Bottom right to bottom left
			while (column > firstColumn)
				res[i--] = new Position(this, row, column--);
			res[i--] = new Position(this, row--, column);
			if (i < 0)
				break;
			--lastRow;
			// Bottom left to top left
			while (row > firstRow)
				res[i--] = new Position(this, row--, column);
			res[i--] = new Position(this, row, column++);
			++firstColumn;
		}
		return res;
	}

	/** See the study group's notes. */
	final private int B;
	/** See the study group's notes. */
	final private int H;
	/** Stores the {@link Board}'s {@link mnkgame.MNKCell cells}. */
	final private MNKCellState[][] cellStates;
	/** The moves played so far. */
	final private Stack<Position> history = new Stack<Position>();
	/** Stores all of the {@link Board}'s possible {@link Alignment}s. */
	final private DirectAddressTable<Alignment> alignments;
	/** The current game state. */
	private MNKGameState state;
	/**
	 * Stores both currently legal and illegal actions ({@link #SIZE} in total),
	 * sorted by decreasing heuristic value.
	 */
	final private Position[] actionsCandidates;

}
