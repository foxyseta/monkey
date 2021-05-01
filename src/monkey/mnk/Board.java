package monkey.mnk;

import java.lang.Iterable;
import java.lang.Math;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;
import monkey.ai.Player;
import monkey.util.DirectAddressTable;
import monkey.util.ObjectUtils;

/**
 * A <code>Board</code> describes the {@link monkey.ai.State State} of a
 * MNK-game. It supports backtracking and alpha-beta pruning. A single istance
 * of this class takes O({@link #SIZE}) memory.
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
	 * O({@link #SIZE}) time.
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
		Arrays.fill(cellStates, MNKCellState.FREE);
		// alignments
		B = Math.max(0, N - K + 1);
		H = Math.max(0, M - K + 1);
		ALIGNMENTS = countAlignments();
		alignments = new DirectAddressTable<Alignment>(Alignment.class, a -> toKey(a), ALIGNMENTS);
	}

	@Override // inherit doc comment
	public Player player() {
		return history.size() % 2 == 0 ? Player.P1 : Player.P2;
	}

	/**
	 * {@inheritDoc} Takes O({@link #SIZE}) time.
	 */
	@Override
	public Iterable<Position> actions() {
		LinkedList<Position> res = new LinkedList<Position>();
		for (int row = 0; row < M; ++row)
			for (int column = 0; column < N; ++column)
				if (cellStates[row][column] == MNKCellState.FREE)
					res.add(new Position(this, row, column));
		return res;
	}

	/**
	 * {@inheritDoc} Takes O({@link #K}) time.
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
		history.push(a);
		if (history.size() == SIZE)
			state = MNKGameState.DRAW;
		else {
			// horizontal alignments
			int max = Math.min(N - K, column);
			for (int j = Math.max(0, column - K + 1); j <= max; ++j)
				if (addMark(row, j, Alignment.Direction.HORIZONTAL) != MNKGameState.OPEN)
					return this;
			// vertical alignments
			max = Math.min(M - K, row);
			for (int i = Math.max(0, row - K + 1); i <= max; ++i)
				if (addMark(i, column, Alignment.Direction.VERTICAL) != MNKGameState.OPEN)
					return this;
			// primary diagonal alignments
			max = Math.min(N - K + row - column, Math.min(M - K, row));
			for (int i = Math.max(0, Math.max(row - K + 1, row - column)), j = i + column - row; i <= max; ++i, ++j)
				if (addMark(i, j, Alignment.Direction.PRIMARY_DIAGONAL) != MNKGameState.OPEN)
					return this;
			// secondary diagonal alignments
			max = Math.max(column + row, Math.min(M - 1, row + K - 1));
			for (int i = Math.min(N - K, Math.max(K - 1, row)), j = row + column - i; i <= max; ++i, --j)
				if (addMark(i, j, Alignment.Direction.SECONDARY_DIAGONAL) != MNKGameState.OPEN)
					return this;
		}
		return this;
	}

	/**
	 * {@inheritDoc} Takes O({@link #K}) time.
	 */
	@Override
	public Board revert() {
		try {
			final Position a = history.pop();
			final int row = a.getRow(), column = a.getColumn();
			cellStates[row][column] = MNKCellState.FREE;
			// TODO Update alignments
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
	public Integer initial_alpha(Player p) {
		return LOSSUTILITY;
	}

	@Override // inherit doc comment
	public Integer initial_beta(Player p) {
		return VICTORYUTILITY;
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
			return B * (M + 2 * H + row - N) + N * H + column;
		default:
			throw new IllegalArgumentException("Unknown direction");
		}
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
	 * Records a new mark for a certain {@link monkey.mnk.Alignment Alignment} based
	 * on the current {@link monkey.ai.Player Player}.
	 *
	 * @param query Its coordinates are used to identify the element to update. May
	 *              be dirtied after its use.
	 * @throws IllegalArgumentException query is meant for another M-N-K tuple.
	 * @return The updated object's {@link #state}.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private MNKGameState addMark(Alignment query) {
		if (query.FIRSTCELL.ROWSNUMBER != M || query.FIRSTCELL.COLUMNSNUMBER != N || query.LENGTH != K)
			throw new IllegalArgumentException("M-N-K incompatibility.");
		Alignment result = alignments.search(toKey(query));
		if (result == null) {
			query.clear();
			alignments.insert(result = query);
		}
		switch (result.addMark(player())) {
		case EMPTY: // should never occur
		case MIXED:
			return state;
		case P1:
			return state = MNKGameState.WINP1;
		case P2:
			return state = MNKGameState.WINP2;
		default:
			throw new IllegalArgumentException("Unknown alignment state");
		}
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

}
