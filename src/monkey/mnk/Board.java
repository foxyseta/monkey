package monkey.mnk;

import mnkgame.MNKCellState;
import monkey.ai.Player;

/**
 * A <code>Board</code> describes the {@link monkey.ai.State [State]} of a
 * MNK-game. It supports backtracking and alpha-beta pruning.
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
	/** Number of cells. */
	final public int SIZE;
	/** Quantifies the satisfaction earned by winning the game. */
	final public static int VICTORYUTILITY = 1;
	/** Quantifies the satisfaction earned when the game ends in a draw. */
	final public static int DRAWUTILITY = 0;
	/** Quantifies the satisfaction earned by losing the game. */
	final public static int LOSSUTILITY = -1;

	/**
	 * Constructs a new {@link Board} given its m, n and k parameters.
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
		// cell states
		cellStates = new MNKCellState[M][N];
		java.util.Arrays.fill(cellStates, MNKCellState.FREE);
	}

	@Override
	public Player player() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Position[] actions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Board result(Position a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Board revert() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean terminalTest() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer utility(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer initial_alpha(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer initial_beta(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * The turn number (starting from 0). It is equal to the number of non-free
	 * cells of the board.
	 */
	private int turn = 0;
	/** Stores the board's {@link mnkgame.MNKCell [cells]}. */
	private MNKCellState[][] cellStates;

}
