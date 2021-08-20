package monkey.ai;

import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.TimeoutException;
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
 * @param <U> The type used to quantify the payoffs.
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public class AI<S extends State<S, A, U>, A, U extends Comparable<U>> {

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
	public AI(Player p, S s0, long t) {
		if (p == null || s0 == null)
			throw new NullPointerException("Some of the arguments are null.");
		player = p;
		state = s0;
		timeLimit = t;
		final int capacity = state.ttSuggestedCapacity();
		transpositionTable = new HashMap<Integer, Entry<S, A, U>>(capacity);
		System.out.println("📋 " + capacity);
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
	 * Given a state in which the player has the move, selects one of the legal
	 * actions to be played using alpa-beta pruning and iterative deepening search.
	 * See S. Russell, P. Norvig, <i>Artificial Intelligence: A Modern Approach</i>,
	 * 3rd ed., Prentice Hall, p. 88f.
	 *
	 * @return A legal action to be played.
	 * @throws IllegalArgumentException The player does not have the move or if the
	 *                                  state is terminal.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public A alphaBetaSearch() {
		startTime = System.currentTimeMillis();
		if (state.terminalTest())
			throw new IllegalArgumentException("s is a terminal state.");
		if (player != state.player())
			throw new IllegalArgumentException("It's not your turn.");
		final S backupState = state.clone();
		final int maxLimit = state.overestimatedHeight();
		A res = null;
		final U beta = state.initialBeta(player);
		for (int depthLimit = 0; depthLimit <= maxLimit; ++depthLimit) {
			A a = null;
			U alpha = state.initialAlpha(player), v = alpha;
			Iterator<A> actions = state.actions();
			while (actions.hasNext()) {
				final A toChild = actions.next();
				inspectedNodes = 0;
				try {
					final U minValue = minValue(state.result(toChild), alpha, beta, depthLimit);
					if (a == null || minValue != null && (v == null || minValue.compareTo(v) > 0)) {
						a = toChild;
						v = minValue;
					}
				} catch (TimeoutException e) {
					state = backupState;
					System.out.println("🙈 limit ≤ " + depthLimit);
					return res;
				}
				state.revert();
				alpha = objectUtils.max(alpha, v);
			}
			res = a;
		}
		return res;
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
	 * @throws TimeoutException     The time limit is almost over.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	protected U maxValue(S s, U alpha, U beta, int depthLimit) throws TimeoutException {
		// exceptions/base case
		if (s == null)
			throw new NullPointerException("s is null.");
		if (System.currentTimeMillis() - startTime > timeLimit * RELAXATION)
			throw new TimeoutException();
		final long previouslyInspectedNodes = inspectedNodes++;
		if (cutoffTest(s, depthLimit))
			return s.eval(player);

		// transposition table lookup
		final Entry<S, A, U> cachedEntry = transpositionTable.get(s.hashCode());
		A bestOrRefutationMove = null;
		if (cachedEntry != null) {
			final SearchResult<A, U> cachedSearchResult = cachedEntry.pickSearchResult(s, depthLimit);
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
					if (alpha.compareTo(beta) >= 0)
						return alpha;
				}
				// purposes 2 and 3
				bestOrRefutationMove = cachedSearchResult.MOVE;
			}
		}
		// check best/refutation move first
		U v = null;

		if (bestOrRefutationMove != null) {
			v = minValue(s.result(bestOrRefutationMove), alpha, beta, depthLimit - 1);
			s.revert();
			if (v.compareTo(beta) >= 0) {
				addSearchResult(cachedEntry, new SearchResult<A, U>(bestOrRefutationMove, v, ScoreType.LOWERBOUND, depthLimit,
						inspectedNodes - previouslyInspectedNodes));
				return v;
			}
			alpha = objectUtils.max(alpha, v);
		}

		// check other moves next
		final Iterator<A> actions = s.actions();
		while (actions.hasNext()) {
			final A toChild = actions.next();
			if (!toChild.equals(bestOrRefutationMove)) {
				final U newV = minValue(s.result(toChild), alpha, beta, depthLimit - 1);
				if (v == null || newV.compareTo(v) > 0) {
					v = newV;
					bestOrRefutationMove = toChild;
				}
				s.revert();
				if (v.compareTo(beta) >= 0) {
					addSearchResult(cachedEntry, new SearchResult<A, U>(bestOrRefutationMove, v, ScoreType.LOWERBOUND, depthLimit,
							inspectedNodes - previouslyInspectedNodes));
					return v;
				}
				alpha = objectUtils.max(alpha, v);
			}
		}
		addSearchResult(cachedEntry, new SearchResult<A, U>(bestOrRefutationMove, v, ScoreType.TRUEVALUE, depthLimit,
				inspectedNodes - previouslyInspectedNodes));
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
	 * @throws TimeoutException     The time limit is almost over.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	protected U minValue(S s, U alpha, U beta, int depthLimit) throws TimeoutException {
		// exceptions/base case
		if (s == null)
			throw new NullPointerException("s is null.");
		if (System.currentTimeMillis() - startTime > timeLimit * RELAXATION)
			throw new TimeoutException();
		final long previouslyInspectedNodes = inspectedNodes++;
		if (cutoffTest(s, depthLimit))
			return s.eval(player);

		// transposition table lookup
		final Entry<S, A, U> cachedEntry = transpositionTable.get(s.hashCode());
		A bestOrRefutationMove = null;
		if (cachedEntry != null) {
			final SearchResult<A, U> cachedSearchResult = cachedEntry.pickSearchResult(s, depthLimit);
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
					if (beta.compareTo(alpha) <= 0)
						return beta;
				}
				// purposes 2 and 3
				bestOrRefutationMove = cachedSearchResult.MOVE;
			}
		}

		// check best/refutation move first
		U v = null;

		if (bestOrRefutationMove != null) {
			v = maxValue(s.result(bestOrRefutationMove), alpha, beta, depthLimit - 1);
			s.revert();
			if (v.compareTo(alpha) <= 0) {
				addSearchResult(cachedEntry, new SearchResult<A, U>(bestOrRefutationMove, v, ScoreType.UPPERBOUND, depthLimit,
						inspectedNodes - previouslyInspectedNodes));
				return v;
			}
			beta = objectUtils.min(beta, v);
		}

		// check other moves next
		final Iterator<A> actions = s.actions();
		while (actions.hasNext()) {
			final A toChild = actions.next();
			if (!toChild.equals(bestOrRefutationMove)) {
				final U newV = maxValue(s.result(toChild), alpha, beta, depthLimit - 1);
				if (v == null || newV.compareTo(v) < 0) {
					v = newV;
					bestOrRefutationMove = toChild;
				}
				s.revert();
				if (v.compareTo(alpha) <= 0) {
					addSearchResult(cachedEntry, new SearchResult<A, U>(bestOrRefutationMove, v, ScoreType.UPPERBOUND, depthLimit,
							inspectedNodes - previouslyInspectedNodes));
					return v;
				}
				beta = objectUtils.min(beta, v);
			}
		}
		addSearchResult(cachedEntry, new SearchResult<A, U>(bestOrRefutationMove, v, ScoreType.TRUEVALUE, depthLimit,
				inspectedNodes - previouslyInspectedNodes));
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
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	protected void addSearchResult(Entry<S, A, U> cachedEntry, SearchResult<A, U> newSearchResult) {
		if (cachedEntry == null)
			transpositionTable.put(state.hashCode(), new Entry<S, A, U>(newSearchResult));
		else
			cachedEntry.add(newSearchResult);
	}

	/**
	 * The player the {@link AI} will play as.
	 */
	final private Player player;
	/** The current {@link State} of the game. */
	private S state;
	/** The maximum number of milliseconds usable to select a move. */
	final private long timeLimit;
	/** Utilities instance for generic objects. */
	final private ObjectUtils objectUtils = new ObjectUtils();
	/** The higher, the more time is used at most for each search. */
	final private float RELAXATION = 0.99f;
	/** A transposition table for this instance of the {@link AI}. */
	final private HashMap<Integer, Entry<S, A, U>> transpositionTable;
	/** Start time of the current turn. */
	private long startTime;
	/**
	 * Number of nodes actually inspected since the beginning of the last alpha-beta
	 * search.
	 */
	private long inspectedNodes;

}
