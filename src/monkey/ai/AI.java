package monkey.ai;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Callable;
import monkey.ai.table.Entry;
import monkey.ai.table.SearchResult;
import monkey.ai.table.SearchResult.ScoreType;
import monkey.util.ObjectUtils;

/**
 * An <code>AI</code> is a generic, backtracking alpha-beta pruner for a
 * deterministic, turn-taking, two-player, zero-sum game of perfect information.
 * See S. Russell, P. Norvig, <i>Artificial Intelligence: A Modern Approach</i>,
 * 3rd ed., Prentice Hall, p. 167f.
 *
 * @param <S> The type to be used for game {@link State}s.
 * @param <A> The type of the moves of the game.
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public class AI<S extends State<S, A>, A> implements Callable<A> {

	/**
	 * Constructs a new {@link AI} for a certain {@link Player} given an initial
	 * {@link State} and a timeout in milliseconds.
	 *
	 * @param p  The player the {@link AI} will play as.
	 * @param s0 The initial {@link State} of the game.
	 * @param t  The maximum number of milliseconds usable to select a move.
	 * @throws NullPointerException Any of the arguments are <code>null</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public AI(Player p, S s0) {
		// if (p == null || s0 == null)
		// throw new NullPointerException("Some of the arguments are null.");
		player = p;
		state = s0;
		transpositionTable = new HashMap<Integer, Entry<S, A>>(state.ttSuggestedCapacity());
	}

	/**
	 * Updates the current {@link State} with the given action.
	 *
	 * @param a A legal action to inform this {@link AI} about.
	 * @throws IllegalArgumentException <code>a</code> is an illegal action for the
	 *                                  current state.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void update(A a) {
		state.result(a);
	}

	/**
	 * When called in a state in which the player has the move, selects one of the
	 * legal actions to be played using iterative deepening search. See S. Russell,
	 * P. Norvig, <i>Artificial Intelligence: A Modern Approach</i>, 3rd ed.,
	 * Prentice Hall, p. 88f.
	 *
	 * @throws IllegalArgumentException The player does not have the move or if the
	 *                                  state is terminal.
	 * @return A legal action to be played.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public A call() {
		// if (state.terminalTest())
		// throw new IllegalArgumentException("s is a terminal state.");
		// if (player != state.player())
		// throw new IllegalArgumentException("It's not your turn.");

		final S backupState = state.clone();
		final int maxLimit = state.overestimatedHeight();
		res = null;
		for (int d = 0; d <= maxLimit; ++d)
			try {
				// System.err.println("\t🙈 = " + depthLimit);
				res = bestNodeLimitedSearch(d);
			} catch (Exception e) {
				state = backupState;
				throw e;
			}
		return res;
	}

	/**
	 * Retrieves the partial result computed by the last search.
	 *
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public A partial_result() {
		if (res == null)
			res = state.actions().next();
		return res;
	}

	/**
	 * When called in a state in which the player has the move, selects one of the
	 * legal actions to be played using immediate search. See the project report.
	 *
	 * @throws IllegalArgumentException The player does not have the move or if the
	 *                                  state is terminal.
	 * @return A legal action to be played.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public A immediateSearch() {
		// if (state.terminalTest())
		// throw new IllegalArgumentException("s is a terminal state.");
		// if (player != state.player())
		// throw new IllegalArgumentException("It's not your turn.");
		final java.util.ArrayList<A> bestMoves = new java.util.ArrayList<A>(state.countRelevantActions());
		final Iterator<A> actions = state.actions();
		A action = actions.next();
		int maxEval = state.result(action).eval(player);
		state.revert();
		bestMoves.add(action);
		while (actions.hasNext()) {
			action = actions.next();
			final int currentEval = state.result(action).eval(player);
			state.revert();
			if (currentEval >= maxEval) {
				if (currentEval > maxEval) {
					maxEval = currentEval;
					bestMoves.clear();
				}
				bestMoves.add(action);
			}
		}
		return bestMoves.get(random.nextInt(bestMoves.size()));
	}

	/**
	 * When called in a state in which the player has the move, selects one of the
	 * legal actions to be played using best node search with limited depth. See
	 * Dmitrijs Rutko, <i>Fuzzified Algorithm for Game Tree Search with Statistical
	 * and Analytical Evaluation</i>, in <i>Scientific Papers</i>, vol. 770, 2011,
	 * Univerity of Latvia, p. 94f.
	 *
	 * @param depthLimit Maximum depth to be inspected
	 * @throws TimeoutException The time limit is almost over.
	 * @return A legal action to be played.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	protected A bestNodeLimitedSearch(int depthLimit) {
		int alpha = state.initialAlpha(player), beta = state.initialBeta(player),
				subtreeCount = state.countRelevantActions(), betterCount;
		A bestNode;
		do {
			bestNode = null;
			int test = nextGuess(alpha, beta, subtreeCount);
			// System.err.println("\t\t🌳 × " + subtreeCount + ", 🧱 = " + test + " ∈ [" +
			// alpha + ", " + beta + "]");
			betterCount = 0;
			Iterator<A> actions = state.actions();
			while (actions.hasNext()) {
				final A child = actions.next();
				inspectedNodes = 0;
				if (minValue(state.result(child), test - 1, test, depthLimit) >= test) {
					++betterCount;
					if (bestNode == null)
						bestNode = child;
				}
				state.revert();
			}
			if (betterCount == 0)
				beta = test;
			else if (betterCount > 1) {
				subtreeCount = betterCount;
				alpha = test;
			}
		} while (beta - alpha >= 2 && betterCount != 1 || betterCount == 0);
		return bestNode;
	}

	/**
	 * Suggests a separation value for a best node search. See Dmitrijs Rutko,
	 * <i>Fuzzified Algorithm for Game Tree Search with Statistical and Analytical
	 * Evaluation</i>, in <i>Scientific Papers</i>, vol. 770, 2011, Univerity of
	 * Latvia, p. 95f.
	 *
	 * @see #bestNodeLimitedSearch
	 * @param alpha        First extreem of the alpha-beta range.
	 * @param beta         Second extreem of the alpha-beta range.
	 * @param subtreeCount Number of subtrees that will be tested.
	 * @return The suggested separation value.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	protected int nextGuess(int alpha, int beta, int subtreeCount) {
		return alpha + (beta - alpha) * (subtreeCount - 1) / subtreeCount;
	}

	/**
	 * Executes a "max" alpha-beta pruning step using depth limited search. See S.
	 * Russell, P. Norvig, <i>Artificial Intelligence: A Modern Approach</i>, 3rd
	 * ed., Prentice Hall, p. 87f. A transposition table is also used. See D.M.
	 * Breuker, J.W.H.M. Uiterwijk, H.J. van den Herik, <i>Information in
	 * Transposition Tables</i>, in H.J. van den Herik, J.W.H.M. Uiterwijk (eds),
	 * <i>Advances in Computer Chess 8</i>, Computer Science Department,
	 * Universiteit Maastricht. 1997, pp. 2-3, 4.
	 *
	 * @param s          The state to be considered.
	 * @param alpha      The current alpha value. It may be null.
	 * @param beta       The current beta value. It may be null.
	 * @param depthLimit Maximum depth to be inspected.
	 * @return The utility brought by the most useful action for the <code>AI</code>
	 *         within <code>s</code>.
	 * @throws NullPointerException The state is null.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	protected int maxValue(S s, int alpha, int beta, int depthLimit) {
		// exceptions/base case
		// if (s == null)
		// throw new NullPointerException("s is null.");
		final long previouslyInspectedNodes = inspectedNodes++;
		if (cutoffTest(s, depthLimit))
			return s.eval(player);

		// transposition table lookup
		final Entry<S, A> cachedEntry = transpositionTable.get(s.hashCode());
		A bestOrRefutationMove = null, cachedMove = null;
		if (cachedEntry != null) {
			final SearchResult<A> cachedSearchResult = cachedEntry.pickSearchResult(s, depthLimit);
			if (cachedSearchResult != null) {
				if (depthLimit <= cachedSearchResult.SEARCHDEPTH) {
					switch (cachedSearchResult.FLAG) {
					case TRUEVALUE: // purpose 1
						return cachedSearchResult.SCORE;
					case UPPERBOUND: // purpose 2
						beta = objectUtils.min(beta, cachedSearchResult.SCORE);
						break;
					case LOWERBOUND: // purpose 2 (sic.)
						alpha = objectUtils.max(alpha, cachedSearchResult.SCORE);
						break;
					default:
						throw new InternalError("Unknown score type.");
					}
					if (alpha >= beta)
						return alpha;
				}
				// purposes 2 and 3
				bestOrRefutationMove = s.revertFromHashedAction(cachedSearchResult.MOVE);
				cachedMove = bestOrRefutationMove;
			}
		}

		// check best/refutation move first
		Integer v = null;

		if (bestOrRefutationMove != null) {
			v = minValue(s.result(bestOrRefutationMove), alpha, beta, depthLimit - 1);
			s.revert();
			if (v.compareTo(beta) >= 0) {
				addSearchResult(cachedEntry, new SearchResult<A>(s.convertToHashedAction(bestOrRefutationMove), v,
						ScoreType.LOWERBOUND, depthLimit, inspectedNodes - previouslyInspectedNodes));
				return v;
			}
			alpha = objectUtils.max(alpha, v);
		}

		// check other moves next
		final Iterator<A> actions = s.actions();
		while (actions.hasNext()) {
			final A toChild = actions.next();
			if (!toChild.equals(cachedMove)) {
				final int newV = minValue(s.result(toChild), alpha, beta, depthLimit - 1);
				if (v == null || newV > v) {
					v = newV;
					bestOrRefutationMove = toChild;
				}
				s.revert();
				if (v.compareTo(beta) >= 0) {
					addSearchResult(cachedEntry, new SearchResult<A>(s.convertToHashedAction(bestOrRefutationMove), v,
							ScoreType.LOWERBOUND, depthLimit, inspectedNodes - previouslyInspectedNodes));
					return v;
				}
				alpha = objectUtils.max(alpha, v);
			}
		}
		addSearchResult(cachedEntry, new SearchResult<A>(s.convertToHashedAction(bestOrRefutationMove), v,
				ScoreType.TRUEVALUE, depthLimit, inspectedNodes - previouslyInspectedNodes));
		return v;
	}

	/**
	 * Executes a "min" alpha-beta pruning step using depth limited search. See S.
	 * Russell, P. Norvig, <i>Artificial Intelligence: A Modern Approach</i>, 3rd
	 * ed., Prentice Hall, p. 87f. A transposition table is also used. See D.M.
	 * Breuker, J.W.H.M. Uiterwijk, H.J. van den Herik, <i>Information in
	 * Transposition Tables</i>, in H.J. van den Herik, J.W.H.M. Uiterwijk (eds),
	 * <i>Advances in Computer Chess 8</i>, Computer Science Department,
	 * Universiteit Maastricht. 1997, pp. 2-3, 4.
	 *
	 * @param s          The state to be considered.
	 * @param alpha      The current alpha value. It may be null.
	 * @param beta       The current beta value. It may be null.
	 * @param depthLimit Maximum depth to be inspected.
	 * @return The utility brought by the most useful action for the opponent within
	 *         s.
	 * @throws NullPointerException The state is null.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	protected int minValue(S s, int alpha, int beta, int depthLimit) {
		// exceptions/base case
		// if (s == null)
		// throw new NullPointerException("s is null.");
		final long previouslyInspectedNodes = inspectedNodes++;
		if (cutoffTest(s, depthLimit))
			return s.eval(player);

		// transposition table lookup
		final Entry<S, A> cachedEntry = transpositionTable.get(s.hashCode());
		A bestOrRefutationMove = null, cachedMove = null;
		if (cachedEntry != null) {
			final SearchResult<A> cachedSearchResult = cachedEntry.pickSearchResult(s, depthLimit);
			if (cachedSearchResult != null) {
				if (depthLimit <= cachedSearchResult.SEARCHDEPTH) {
					switch (cachedSearchResult.FLAG) {
					case TRUEVALUE: // purpose 1
						return cachedSearchResult.SCORE;
					case UPPERBOUND: // purpose 2
						beta = objectUtils.min(beta, cachedSearchResult.SCORE);
						break;
					case LOWERBOUND: // purpose 2 (sic.)
						alpha = objectUtils.max(alpha, cachedSearchResult.SCORE);
						break;
					default:
						throw new InternalError("Unknown score type.");
					}
					if (beta <= alpha)
						return beta;
				}
				// purposes 2 and 3
				bestOrRefutationMove = s.revertFromHashedAction(cachedSearchResult.MOVE);
				cachedMove = bestOrRefutationMove;
			}
		}

		// check best/refutation move first
		Integer v = null;

		if (bestOrRefutationMove != null) {
			v = maxValue(s.result(bestOrRefutationMove), alpha, beta, depthLimit - 1);
			s.revert();
			if (v.compareTo(alpha) <= 0) {
				addSearchResult(cachedEntry, new SearchResult<A>(s.convertToHashedAction(bestOrRefutationMove), v,
						ScoreType.UPPERBOUND, depthLimit, inspectedNodes - previouslyInspectedNodes));
				return v;
			}
			beta = objectUtils.min(beta, v);
		}

		// check other moves next
		final Iterator<A> actions = s.actions();
		while (actions.hasNext()) {
			final A toChild = actions.next();
			if (!toChild.equals(cachedMove)) {
				final int newV = maxValue(s.result(toChild), alpha, beta, depthLimit - 1);
				if (v == null || newV < v) {
					v = newV;
					bestOrRefutationMove = toChild;
				}
				s.revert();
				if (v.compareTo(alpha) <= 0) {
					addSearchResult(cachedEntry, new SearchResult<A>(s.convertToHashedAction(bestOrRefutationMove), v,
							ScoreType.UPPERBOUND, depthLimit, inspectedNodes - previouslyInspectedNodes));
					return v;
				}
				beta = objectUtils.min(beta, v);
			}
		}
		addSearchResult(cachedEntry, new SearchResult<A>(s.convertToHashedAction(bestOrRefutationMove), v,
				ScoreType.TRUEVALUE, depthLimit, inspectedNodes - previouslyInspectedNodes));
		return v;
	}

	/**
	 * During an alpha-beta search, decides when to apply {@link State#eval}. See S.
	 * Russell, P. Norvig, <i>Artificial Intelligence: A Modern Approach</i>, 3rd
	 * ed., Prentice Hall, p. 171.
	 *
	 * @param s The state to be considered.
	 * @param d Maximum depth to be inspected
	 * @return <code>true</code> just in case {@link State#eval} should be applied.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	protected boolean cutoffTest(S s, int d) {
		return d <= 0 || s.terminalTest();
	}

	/**
	 * Adds a new {@link SearchResult} to the {@link #transpositionTable}.
	 *
	 * @param cachedEntry     The correct retrieved {@link monkey.ai.table.Entry},
	 *                        or <code>null</code> if no
	 *                        {@link monkey.ai.table.Entry Entries} were present.
	 * @param newSearchResult The {@link monkey.ai.table.SearchResult} to be added.
	 *                        Cannot be <code>null</code>.
	 * @throws NullPointerException newSearchResult is <code>null</code>.
	 * @throws TimeoutException     The time limit is almost over.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	private void addSearchResult(Entry<S, A> cachedEntry, SearchResult<A> newSearchResult) {
		if (cachedEntry == null)
			transpositionTable.put(state.hashCode(), new Entry<S, A>(newSearchResult));
		else
			cachedEntry.add(newSearchResult);
	}

	/**
	 * The player the {@link AI} will play as.
	 */
	final private Player player;
	/** The current {@link State} of the game. */
	private S state;
	/** Utilities instance for generic objects. */
	final private ObjectUtils objectUtils = new ObjectUtils();
	/** A transposition table for this instance of the {@link AI}. */
	final private HashMap<Integer, Entry<S, A>> transpositionTable;
	/**
	 * Number of nodes actually inspected since the beginning of the last alpha-beta
	 * search.
	 */
	private long inspectedNodes;
	/** Random number generator. */
	final private java.util.Random random = new java.util.Random(System.currentTimeMillis());
	/** The action which is currently considered as stronger. */
	private A res;

}
