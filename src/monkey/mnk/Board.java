package monkey.mnk;

import java.util.Arrays;
import java.util.Stack;
import java.util.Iterator;
import mnkgame.MNKCellState;
import mnkgame.MNKGameState;
import monkey.ai.Player;
import monkey.util.ObjectUtils;

/**
 * A <code>Board</code> describes the {@link monkey.ai.State} of a m,n,k-game.
 * It supports backtracking, alpha-beta pruning, pattern search, and more. A
 * single istance of this class takes Θ({@link #SIZE}) memory.
 *
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public class Board implements monkey.ai.State<Board, Position> {

	/** Number of rows. */
	final public int M;
	/** Number of columns. */
	final public int N;
	/** Number of symbols to be aligned. */
	final public int K;
	/** Number of cells of the {@link Board}. */
	final public int SIZE;
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
	 * @throws IllegalArgumentException m or n or k is not positive.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public Board(int m, int n, int k) {
		if (m <= 0)
			throw new IllegalArgumentException("m <= 0");
		if (n <= 0)
			throw new IllegalArgumentException("n <= 0");
		if (k <= 0)
			throw new IllegalArgumentException("k <= 0");
		// constants
		SIZE = (M = m) * (N = n);
		K = k;
		// states
		state = MNKGameState.OPEN;
		cellStates = initialCellStates();
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
		kCounter = K > 1 ? new ThreatsManager(K, this) : null;
		kMinusOneCounter = K > 2 ? new ThreatsManager(K - 1, this) : null;
		kMinusTwoCounter = K > 3 ? new ThreatsManager(K - 2, this) : null;
		adjacencyCounters = new int[M][N];
		// hashing
		zobristHasher = new ZobristHasher(M, N);
	}

	/**
	 * {@inheritDoc} <br>
	 * Takes Θ({@link #SIZE}) time.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Board clone() {
		try {
			Board copy = (Board) super.clone();
			copy.cellStates = new MNKCellState[M][N];
			for (int i = 0; i < cellStates.length; ++i)
				copy.cellStates[i] = cellStates[i].clone();
			copy.history = (Stack<Position>) history.clone();
			if (kCounter != null) {
				copy.kCounter = kCounter.clone();
				copy.kCounter.setBoard(copy);
			}
			if (kMinusOneCounter != null) {
				copy.kMinusOneCounter = kMinusOneCounter.clone();
				copy.kMinusOneCounter.setBoard(copy);
			}
			if (kMinusTwoCounter != null) {
				copy.kMinusTwoCounter = kMinusTwoCounter.clone();
				copy.kMinusTwoCounter.setBoard(copy);
			}
			copy.adjacencyCounters = new int[M][N];
			for (int i = 0; i < adjacencyCounters.length; ++i)
				copy.adjacencyCounters[i] = adjacencyCounters[i].clone();
			copy.zobristHasher = zobristHasher.clone();
			return copy;
		} catch (CloneNotSupportedException e) {
			// Should never happen: we support clone
			throw new InternalError(e.toString());
		}
	}

	@Override // inherit doc comment
	public Player player() {
		return history.size() % 2 == 0 ? Player.P1 : Player.P2;
	}

	@Override // inherit doc comment
	public boolean isLegal(Position p) {
		if (p == null)
			throw new NullPointerException("p is null.");
		return p.ROWSNUMBER == M && p.COLUMNSNUMBER == N && cellStates[p.getRow()][p.getColumn()] == MNKCellState.FREE;
	}

	/**
	 * {@inheritDoc} <br>
	 * Takes Θ(1) time in the best and average cases, but Θ({@link SIZE}} in the
	 * worst case. The act of instantiating an iterator and bringing it to the end
	 * of the sequence always takes Θ({@link SIZE}) time in total.
	 */
	@Override
	public Iterator<Position> actions() {
		return new BoardIterator();
	}

	/**
	 * {@inheritDoc} <br>
	 * Takes Θ({@link #K}) time.
	 */
	@Override
	public Board result(Position a) {
		if (a == null)
			throw new IllegalArgumentException("Null moves are invalid in this game.");
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
		if (countThreatsWithoutHole(K, p) > 0)
			state = p == Player.P1 ? MNKGameState.WINP1 : MNKGameState.WINP2;
		updateAdjacencyCounters(a, 1);
		history.push(a);
		if (state == MNKGameState.OPEN && history.size() == SIZE)
			state = MNKGameState.DRAW;
		zobristHasher.addOrRemove(a, p);
		return this;
	}

	/**
	 * {@inheritDoc} <br>
	 * Takes Θ({@link #K}) time.
	 */
	@Override
	public Board revert() {
		try {
			final Position a = history.pop();
			final int row = a.getRow(), column = a.getColumn();
			cellStates[row][column] = MNKCellState.FREE;
			updateThreatsManagers(a);
			updateAdjacencyCounters(a, -1);
			state = MNKGameState.OPEN;
			zobristHasher.addOrRemove(a, player());
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
	public int utility(Player p) {
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
	public int initialAlpha(Player p) {
		return history.empty() ? p == Player.P1 ? INITIALALPHAP1 : INITIALALPHAP2 : LOSSUTILITY;
	}

	@Override // inherit doc comment
	public int initialBeta(Player p) {
		return history.empty() ? p == Player.P1 ? INITIALBETAP1 : INITIALBETAP2 : VICTORYUTILITY;
	}

	/**
	 * {@inheritDoc} It is implemented as a simplified (see project report) version
	 * of the heuristic of Abdoulaye-Houndji-Ezin-Aglin. The coefficients are left
	 * as unnamed constants because of their large number and their experimental
	 * origin. See A. Abdoulaye, V. R. Houndji, E. C. Ezin, G. Aglin, <i>Generic
	 * Heuristic for the mnk-games</i>, in A. E. Badouel, N. Gmati, B. Watson (eds),
	 * <i>Proceedings of CARI 2018 (African Conference on Research in Computer
	 * Science and Applied Mathematics). Nabil Gmati; Eric Badouel; Bruce Watson.
	 * CARI 2018 - Colloque africain sur la recherche en informatique et
	 * mathématiques appliquées</i>, Oct 2018, Stellenbosch, South Africa. 2018, pp.
	 * 268-269. hal-01881376f.
	 *
	 * @param p The {@link monkey.ai.Player} from whose point of view the current
	 *          {@link Board} is evaluated.
	 * @return The result of the evaluation.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	@Override
	public int eval(Player p) {
		if (terminalTest())
			return utility(p);
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
	 * Returns a string representation of the object. <br>
	 * Takes Θ({@link #SIZE}) time.
	 *
	 * @return A string representation of this object.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	@Override
	public String toString() {
		char[] res = new char[2 * SIZE + 4 * M];
		int i = 0;
		for (MNKCellState[] row : cellStates) {
			for (MNKCellState cell : row)
				res[i++] = cell == MNKCellState.P1 ? '1' : cell == MNKCellState.P2 ? '2' : '.';
			res[i++] = '%';
			res[i++] = 'n';
		}
		for (int[] row : adjacencyCounters) {
			for (int cell : row)
				res[i++] = (char) (cell + '0');
			res[i++] = '%';
			res[i++] = 'n';
		}
		return String.format(new String(res));
	}

	/**
	 * Returns a hash code value for the object. Zobrist hashing is used
	 * (transpositions and symmetric {@link Board}s will return the same hash code).
	 * 
	 * @return A hash code value for this object.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	@Override
	public int hashCode() {
		return zobristHasher.hashCode();
	}

	/**
	 * A getter for the cells of the grid.
	 *
	 * @see cellStates
	 * @param p The {@link Position} to inspect.
	 * @throws IllegalArgumentException p does not have correct extents.
	 * @return The state of the inspected cell, or <code>null</code> if p is
	 *         <code>null</code>.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public MNKCellState getCellState(Position p) {
		if (p == null)
			return null;
		if (p.ROWSNUMBER != M || p.COLUMNSNUMBER != N)
			throw new IllegalArgumentException("This Position is meant for a different grid.");
		return cellStates[p.getRow()][p.getColumn()];
	}

	/**
	 * A getter for the adjacency counters.
	 *
	 * @see adjacencyCounters
	 * @param p The {@link Position} to inspect.
	 * @throws IllegalArgumentException p does not have the correct extents.
	 * @throws NullPointerException     p is <code>null</code>.
	 * @return The number of adjacent, non-free cells.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int getAdjacencyCounter(Position p) {
		if (p == null)
			throw new NullPointerException("This position is null.");
		if (p.ROWSNUMBER != M || p.COLUMNSNUMBER != N)
			throw new IllegalArgumentException("This Position is meant for a different grid.");
		return adjacencyCounters[p.getRow()][p.getColumn()];
	}

	/**
	 * A getter for the cells of the grid.
	 *
	 * @param row    The row of the cell to inspect.
	 * @param column The column of the cell to inspect.
	 * @throws IndexOutOfBoundsException (row, column) is not part of the grid.
	 * @return The state of the inspected cell.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public MNKCellState getCellState(int row, int column) {
		return cellStates[row][column];
	}

	/**
	 * {@inheritDoc} <br>
	 * See the project report. Takes Θ(1) (sic).
	 */
	@Override
	public int ttSuggestedCapacity() {
		final int MAXENTRIES = Integer.MAX_VALUE;
		int sum = 1, lastTerm = 1;
		for (int p = 1; p < SIZE; ++p) {
			lastTerm *= (SIZE - p + 1) * (p % 2 == 0 ? p / 2 : 1);
			sum += lastTerm;
			if (sum < 0 || sum > MAXENTRIES)
				return MAXENTRIES;
		}
		return sum;
	}

	@Override // inherit doc comment
	public Position convertToHashedAction(Position a) {
		return zobristHasher.getSymmetryUsed().apply(a);
	}

	@Override // inherit doc comment
	public Position revertFromHashedAction(Position a) {
		return zobristHasher.getSymmetryUsed().revert(a);
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
	 * <code>Board</code>, sorted by decreasing heuristic value. <i>Escargot</i>
	 * heuristic is used (see the project report). Takes Θ({@link #SIZE}) time.
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
	 * {@link Board}. Takes Θ({@link #K}) time.
	 *
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private void updateThreatsManagers(Position p) {
		if (kCounter != null) {
			kCounter.updateAlignments(p, player());
			if (kMinusOneCounter != null) {
				kMinusOneCounter.updateAlignments(p, player());
				if (kMinusTwoCounter != null)
					kMinusTwoCounter.updateAlignments(p, player());
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
			threatsManager = type.hasHole() ? null : kCounter;
		else if (length == K - 1)
			threatsManager = type.hasHole() ? kCounter : kMinusOneCounter;
		else if (length == K - 2)
			threatsManager = type.hasHole() ? kMinusOneCounter : kMinusTwoCounter;
		else if (length == K - 3)
			threatsManager = type.hasHole() ? kMinusTwoCounter : null;
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

	/**
	 * Updates the value of the adjacency counters neighbours of the desired
	 * {@link Position}.
	 *
	 * @param p      The {@link Position} whose counter is to be updated. If it is
	 *               <code>null</code>, nothing happens.
	 * @param offset The quantity to add to the counter.
	 * @throws IllegalArgumentException p is invalid or offset would make the
	 *                                  counter negative.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private void updateAdjacencyCounters(Position p, int offset) {
		if (p != null) {
			if (p.ROWSNUMBER != M || p.COLUMNSNUMBER != N)
				throw new IllegalArgumentException("p is not meant for this grid.");
			final int row = p.getRow(), column = p.getColumn(), maxRow = objectUtils.min(row + 1, M - 1),
					maxColumn = objectUtils.min(column + 1, N - 1);
			for (int i = objectUtils.max(0, row - 1); i <= maxRow; ++i)
				for (int j = objectUtils.max(0, column - 1); j <= maxColumn; ++j)
					if (i != row || j != column) {
						if (adjacencyCounters[i][j] + offset < 0)
							throw new IllegalArgumentException(
									"offset would make (" + i + ", " + j + ") counter negative.");
						adjacencyCounters[i][j] += offset;
					}
		}
	}

	/**
	 * An <code>Iterator</code> class for {@link Board} which iterates by decreasing
	 * heuristic values. It does not implement <code>remove</code>.
	 *
	 * @author Stefano Volpe
	 * @version 1.0
	 * @version 1.0
	 */
	private class BoardIterator implements Iterator<Position> {

		/**
		 * Constructs a new {@link #BoardIterator}. Takes Θ(1) time in the best and
		 * average cases, but Θ(@link Board#SIZE} in the worst case.
		 *
		 * @author Stefano Volpe
		 * @version 1.0
		 * @since 1.0
		 */
		public BoardIterator() {
			if (terminalTest())
				index = actionsCandidates.length;
			else
				while (index < actionsCandidates.length && (getCellState(actionsCandidates[index]) != MNKCellState.FREE
						|| getAdjacencyCounter(actionsCandidates[index]) == 0 && !history.empty()))
					++index;
		}

		/**
		 * Returns <code>true</code> if the iteration has more elements. (In other
		 * words, returns <code>true</code> if {@link #next} would return an element
		 * rather than throwing an exception.)
		 *
		 * @return <code>true</code> if the iteration has more elements.
		 * @author Stefano Volpe
		 * @version 1.0
		 * @since 1.0
		 */
		@Override
		public boolean hasNext() {
			return index < actionsCandidates.length;
		}

		/**
		 * Returns the next element in the iteration. <br>
		 * Takes Θ(1) time in the best and average cases, but Θ(@link Board#SIZE} in the
		 * worst case.
		 *
		 * @return The next element in the iteration.
		 * @throws NoSuchElementException if the iteration has no more elements.
		 * @author Stefano Volpe
		 * @version 1.0
		 * @since 1.0
		 */
		@Override
		public Position next() {
			if (hasNext()) {
				final int oldIndex = index;
				do
					++index;
				while (index < actionsCandidates.length && getCellState(actionsCandidates[index]) != MNKCellState.FREE);
				return actionsCandidates[oldIndex];
			}
			throw new java.util.NoSuchElementException("No next element.");
		}

		/** The index of the next element, or the length of the table if it is over. */
		private int index = 0;

	}

	@Override // inherit doc comment
	public int countLegalActions() {
		return SIZE - history.size();
	}

	/** Utilities instance for generic objects. */
	final private ObjectUtils objectUtils = new ObjectUtils();
	/** A P1 alpha value valid after a generic first move of theirs. */
	final private int INITIALALPHAP1;
	/** A P1 beta value valid after a generic first move of theirs. */
	final private int INITIALBETAP1;
	/** A P2 alpha value valid after a generic first move of P1. */
	final private int INITIALALPHAP2;
	/** A P2 beta value valid after a generic first move of P1. */
	final private int INITIALBETAP2;
	/**
	 * Stores the {@link Board}'s {@link mnkgame.MNKCell cells}. Not a final field
	 * because of {@link #clone}.
	 */
	private MNKCellState[][] cellStates;
	/** The moves played so far. Not a final field because of {@link #clone}. */
	private Stack<Position> history = new Stack<Position>();
	/** The current game state. */
	private MNKGameState state;
	/**
	 * Stores both currently legal and illegal actions ({@link #SIZE} in total),
	 * sorted by decreasing heuristic value.
	 */
	final private Position[] actionsCandidates;
	/**
	 * Counters for both no-hole {@link #K}-threats and
	 * {@link #K}<code>-1</code>-threats with a hole. Not a final field because of
	 * {@link #clone}.
	 */
	private ThreatsManager kCounter;
	/**
	 * Counters for both no-hole {@link #K}<code>-1</code>-threats and
	 * {@link #K}<code>-2</code>-threats with a hole. Not a final field because of
	 * {@link #clone}.
	 */
	private ThreatsManager kMinusOneCounter;
	/**
	 * Counters for no-hole {@link #K}<code>-2</code>-threats. Not a final field
	 * because of {@link #clone}.
	 */
	private ThreatsManager kMinusTwoCounter;
	/**
	 * Counters for adjacent marked cells used for a simplified pattern search. Not
	 * a final field because of {@link #clone}.
	 */
	private int[][] adjacencyCounters;
	/**
	 * Utility for Zobrist hashing. Not a final field because of {@link #clone}.
	 */
	private ZobristHasher zobristHasher;

}
