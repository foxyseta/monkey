package monkey.mnk;

import java.lang.Iterable;
import java.lang.Math;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Stack;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;
import monkey.ai.Player;

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
	/** See the project report. */
	final public int B;
	/** See the project report. */
	final public int H;
	/** Number of possible {@link Alignment}s. */
	final public int ALIGNMENTS;
	/** Quantifies the satisfaction earned by winning the game. */
	final public static int VICTORYUTILITY = 1000000;
	/** Quantifies the satisfaction earned when the game ends in a draw. */
	final public static int DRAWUTILITY = 0;
	/** Quantifies the satisfaction earned by losing the game. */
	final public static int LOSSUTILITY = -1000000;

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
		if (m < 0)
			throw new IllegalArgumentException("m < 0");
		if (n < 0)
			throw new IllegalArgumentException("n < 0");
		if (k < 0)
			throw new IllegalArgumentException("k < 0");
		// constants
		SIZE = (M = m) * (N = n);
		K = k;
		// states
		state = SIZE > 0 ? MNKGameState.OPEN : MNKGameState.DRAW;
		cellStates = initialCellStates();
		// alignments
		B = Math.max(0, N - K + 1);
		H = Math.max(0, M - K + 1);
		ALIGNMENTS = countAlignments();
		// action candidates
		actionsCandidates = generateActionCandidates();
		// initial alpha and beta values
		INITIALALPHAP1 = LOSSUTILITY;
		Integer tgv = theoreticalGameValue();
		INITIALBETAP1 = tgv == null ? VICTORYUTILITY : tgv;
		// no m,n,k-game has theoreticalGameValue() == LOSSUTILITY anyway
		INITIALALPHAP2 = INITIALBETAP1 == VICTORYUTILITY ? LOSSUTILITY : DRAWUTILITY;
		INITIALBETAP2 = VICTORYUTILITY;
		// counters
		KCOUNTER = K > 1 ? new ThreatsManager(K, this) : null;
		KMINUSONECOUNTER = K > 2 ? new ThreatsManager(K - 1, this) : null;
		KMINUSTWOCOUNTER = K > 3 ? new ThreatsManager(K - 2, this) : null;
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
		if (terminalTest())
			return new ArrayList<Position>();
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
		final Player p = player();
		cellStates[row][column] = p == Player.P1 ? MNKCellState.P1 : MNKCellState.P2;
		updateThreatsManagers(a);
		if (KCOUNTER.count(Threat.ONE, p) + KCOUNTER.count(Threat.TWO, p) + KCOUNTER.count(Threat.THREE, p) > 0)
			state = p == Player.P1 ? MNKGameState.WINP1 : MNKGameState.WINP2;
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
			updateThreatsManagers(a);
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
		return history.empty() ? p == Player.P1 ? INITIALALPHAP1 : INITIALALPHAP2 : LOSSUTILITY;
	}

	@Override // inherit doc comment
	public Integer initialBeta(Player p) {
		return history.empty() ? p == Player.P1 ? INITIALBETAP1 : INITIALBETAP2 : VICTORYUTILITY;
	}

	/**
	 * {@inheritDoc} It is implemented as a simplified (see project report) version
	 * of the heuristic of Abdoulaye-Houndji-Ezin-Aglin. The coefficients are left
	 * as unnamed constants because of their number and their experimental origin.
	 * See A. Abdoulaye, V. R. Houndji, E. C. Ezin, G. Aglin, <i>Generic Heuristic
	 * for the mnk-games</i>, in A. E. Badouel, N. Gmati, B. Watson (eds),
	 * <i>Proceedings of CARI 2018 (African Conference on Research in Computer
	 * Science and Applied Mathematics). Nabil Gmati; Eric Badouel; Bruce Watson.
	 * CARI 2018 - Colloque africain sur la recherche en informatique et
	 * mathématiques appliquées</i>, Oct 2018, Stellenbosch, South Africa. 2018, pp.
	 * 268-269. hal-01881376f
	 */
	public Integer eval(Player p) {
		final int A = 100 * countThreats(K - 2, Threat.ONE, p) + 80 * countHalfOpenThreats(K - 1, p)
				+ 250 * countThreats(K - 1, Threat.ONE, p) + 1000000 * countThreatsWithoutHole(K, p);
		final Player q = p.not();
		final int B = 1300 * countThreats(K - 2, Threat.ONE, q) + 2000 * countHalfOpenThreats(K - 1, q)
				+ 5020 * countThreats(K - 1, Threat.ONE, q) + 1000000 * countThreatsWithoutHole(K, q);
		return A - B;
	}

	@Override // inherit doc comment
	public int overestimatedHeight() {
		return SIZE - history.size();
	}

	/**
	 * Helper function to initialize cell states. Takes Θ({@link #SIZE}) time.
	 *
	 * @return A {@link #M} x {@link #N} matrix with the initial cell states.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	protected MNKCellState[][] initialCellStates() {
		MNKCellState[][] res = new MNKCellState[M][N];
		for (MNKCellState[] row : res)
			Arrays.fill(row, MNKCellState.FREE);
		return res;
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
	 * Computes the theoretical game value of the current configuration looking it
	 * up in a small knowledge base. See J.W.H.M. Uiterwijk, H.J. van den Herik,
	 * <i>The advantage of the initiative</i>, Information Sciences, Volume 122,
	 * Issue 1, 2000, p. 46f.
	 *
	 * @return The theoretical game value, or <code>null</code> if it is unknown.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	protected Integer theoreticalGameValue() {
		if (K == 1)
			return VICTORYUTILITY;
		if (K == 2)
			return SIZE > 2 ? VICTORYUTILITY : DRAWUTILITY;
		if (K == 3)
			return M >= 4 && N >= 3 || M >= 3 && N >= 4 ? VICTORYUTILITY : DRAWUTILITY;
		if (K == 4) {
			if (M <= 8 && N == 4 || M == 4 && N <= 8 || M == 5 && N == 5)
				return DRAWUTILITY;
			if (M >= 6 && N >= 5 || M >= 5 && N >= 6 || M == 4 && N >= 30 || M >= 30 && N == 4)
				return VICTORYUTILITY;
		}
		if (K == 5) {
			if (M <= 6 && N <= 6)
				return DRAWUTILITY;
			if (M == 19 && N == 19)
				return VICTORYUTILITY;
		}
		if (K >= 8)
			return DRAWUTILITY;
		return null;
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
	protected Position[] generateActionCandidates() {
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

	/**
	 * Updates all of the non-<code>null</code> {@link ThreatsManager}s of this
	 * {@link Board}.
	 *
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private void updateThreatsManagers(Position p) {
		if (KCOUNTER != null) {
			KCOUNTER.updateAlignments(p, player(), cellStates);
			if (KMINUSONECOUNTER != null) {
				KMINUSONECOUNTER.updateAlignments(p, player(), cellStates);
				if (KMINUSTWOCOUNTER != null)
					KMINUSTWOCOUNTER.updateAlignments(p, player(), cellStates);
			}
		}
	}

	/**
	 * Retrieves the number of {@link Threat}s stored by this object, filtered by
	 * length, type, and threatener.
	 *
	 * @param length     Length of the {@link Threat}s to count.
	 * @param type       Nature of the {@link Threat}s to count.
	 * @param threatener {@link monkey.ai.Player Player} doing the threatening.
	 * @throws NullPointerException Type or threatener are <code>null</code> and
	 *                              required to compute the result.
	 * @return The current number of the {@link Threat}s queried.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	protected int countThreats(int length, Threat type, Player threatener) {
		final ThreatsManager threatsManager;
		if (length == K)
			threatsManager = type.hasHole() ? null : KCOUNTER;
		else if (length == K - 1)
			threatsManager = type.hasHole() ? KCOUNTER : KMINUSONECOUNTER;
		else if (length == K - 2)
			threatsManager = type.hasHole() ? KMINUSONECOUNTER : KMINUSTWOCOUNTER;
		else if (length == K - 3)
			threatsManager = type.hasHole() ? KMINUSTWOCOUNTER : null;
		else
			threatsManager = null;
		return threatsManager == null ? 0 : threatsManager.count(type, threatener);
	}

	/**
	 * Retrieves the number of half-open {@link Threat}s stored by this object,
	 * filtered by length and threatener.
	 *
	 * @param length     Length of the {@link Threat}s to count.
	 * @param threatener {@link monkey.ai.Player Player} doing the threatening.
	 * @throws NullPointerException threatener is <code>null</code> and required to
	 *                              compute the result.
	 * @return The current number of the {@link Threat}s queried.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	protected int countHalfOpenThreats(int length, Player threatener) {
		return countThreats(length, Threat.TWO, threatener) + countThreats(length, Threat.FOUR, threatener)
				+ countThreats(length, Threat.FIVE, threatener) + countThreats(length, Threat.SIX, threatener);
	}

	/**
	 * Retrieves the number of {@link Threat}s without hole stored by this object,
	 * filtered by length and threatener.
	 *
	 * @param length     Length of the {@link Threat}s to count.
	 * @param threatener {@link monkey.ai.Player Player} doing the threatening.
	 * @throws NullPointerException threatener is <code>null</code> and required to
	 *                              compute the result.
	 * @return The current number of the {@link Threat}s queried.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	protected int countThreatsWithoutHole(int length, Player threatener) {
		return countThreats(length, Threat.ONE, threatener) + countThreats(length, Threat.TWO, threatener)
				+ countThreats(length, Threat.THREE, threatener);
	}

	/** A P1 alpha value valid after a generic first move of theirs. */
	final private int INITIALALPHAP1;
	/** A P1 beta value valid after a generic first move of theirs. */
	final private int INITIALBETAP1;
	/** A P2 alpha value valid after a generic first move of P1. */
	final private int INITIALALPHAP2;
	/** A P2 beta value valid after a generic first move of P1. */
	final private int INITIALBETAP2;
	/** Stores the {@link Board}'s {@link mnkgame.MNKCell cells}. */
	final private MNKCellState[][] cellStates;
	/** The moves played so far. */
	final private Stack<Position> history = new Stack<Position>();
	/** The current game state. */
	private MNKGameState state;
	/**
	 * Stores both currently legal and illegal actions ({@link #SIZE} in total),
	 * sorted by decreasing heuristic value.
	 */
	final private Position[] actionsCandidates;
	/**
	 * Counters for both no-hole {@link #K}-threats and
	 * {@link #K}<code>-1</code>-threats with a hole.
	 */
	final private ThreatsManager KCOUNTER;
	/**
	 * Counters for both no-hole {@link #K}<code>-1</code>-threats and
	 * {@link #K}<code>-2</code>-threats with a hole.
	 */
	final private ThreatsManager KMINUSONECOUNTER;
	/**
	 * Counters for no-hole {@link #K}<code>-2</code>-threats.
	 */
	final private ThreatsManager KMINUSTWOCOUNTER;

}
