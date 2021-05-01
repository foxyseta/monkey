package monkey.mnk;

import java.lang.Iterable;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Stack;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;
import monkey.ai.Player;
import monkey.util.DirectAddressTable;
import monkey.util.ObjectUtils;

/**
 * A <code>Board</code> describes the {@link monkey.ai.State [State]} of a
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
		B = objectUtils.max(0, N - K + 1);
		H = objectUtils.max(0, M - K + 1);
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
		if (a.ROWSNUMBER != M || a.COLUMNSNUMBER != N)
			throw new IllegalArgumentException("Referring to a board of different extents.");
		final int row = a.getRow(), column = a.getColumn();
		if (cellStates[row][column] != MNKCellState.FREE)
			throw new IllegalArgumentException("(" + row + ", " + column + ") is not free.");
		cellStates[row][column] = player() == Player.P1 ? MNKCellState.P1 : MNKCellState.P2;
		history.push(a);
		// TODO Update alignments
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

	private int toKey(Alignment a) {
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

	private int countAlignments() {
		return B * (M + H) + H * (N + B);
	}

	final private ObjectUtils objectUtils = new ObjectUtils();
	final private int B, H;
	/** Stores the {@link Board}'s {@link mnkgame.MNKCell [cells]}. */
	final private MNKCellState[][] cellStates;
	final private Stack<Position> history = new Stack<Position>();
	/** Stores all of the {@link Board}'s possible {@link Alignment}s. */
	final private DirectAddressTable<Alignment> alignments;
	private MNKGameState state;

}
