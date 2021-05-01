package monkey.ai;

import java.util.Comparator;
import monkey.util.Pair;
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

	/** The player the {@link AI} will play as. */
	final private Player player;
	/** The current {@link State} of the game. */
	private S state;
	/** The maximum number of milliseconds usable to select a move. */
	final private long time;

	/** Compare utilities */
	final private Comparator<Pair<A, U>> alphaBetaComparator = new Comparator<Pair<A, U>>() {
		@Override
		public int compare(monkey.util.Pair<A, U> o1, monkey.util.Pair<A, U> o2) {
			if (o1 == null || o2 == null)
				throw new NullPointerException("o1 or o2 are null");
			return o1.getValue().compareTo(o2.getValue());
		};
	};
	final private ObjectUtils objectUtils = new ObjectUtils();

	/**
	 * Constructs a new {@link AI} for a certain {@link Player} given an initial
	 * {@link State} and a timeout in milliseconds.
	 *
	 * @param p  The player the {@link AI} will play as.
	 * @param s0 The initial {@link State} of the game.
	 * @param t  The maximum number of milliseconds usable to select a move.
	 * @throws NullPointerException Any of the arguments are null
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public AI(Player p, S s0, long t) {
		if (p == null || s0 == null)
			throw new NullPointerException("Some of the arguments are null.");
		player = p;
		state = s0;
		time = t;
	}

	/**
	 * Updates the current {@link State} with the given action. It does nothing if
	 * the action passed is null.
	 *
	 * @param a A legal action to inform this {@link AI} about.
	 * @throws IllegalArgumentException <code>a</code> is an illegal action for the
	 *                                  current state.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void update(A a) {
		if (a != null) {
			final Iterable<A> actions = state.actions();
			for (A action : actions)
				if (a.equals(action)) {
					state.result(a);
					return;
				}
			throw new IllegalArgumentException("Illegal action for the current state.");
		}
	}

	/**
	 * Given a state in which the player has the move, selects one of the legal
	 * actions to be played using alpa-beta pruning.
	 *
	 * @return A legal action to be played.
	 * @throws IllegalArgumentException The player does not have the move or if the
	 *                                  state is terminal.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public A alphaBetaSearch() {
		if (state.terminalTest())
			throw new IllegalArgumentException("s is a terminal state");
		if (player != state.player())
			throw new IllegalArgumentException("It's not your turn");
		return maxPair(state, state.initial_alpha(player), state.initial_beta(player)).getKey();
	}

	/**
	 * Executes a "max" alpha-beta pruning step. Unlike in Russel and Norvig's
	 * implementation, here the whole action-utility {@link monkey.util.Pair [Pair]}
	 * is returned instead. This is due to performance reasons.
	 *
	 * @param s     The state to be considered.
	 * @param alpha The current alpha value and it could be null.
	 * @param beta  The current beta value and it could be null.
	 * @return An action-utility {@link monkey.util.Pair Pair} describing the most
	 *         ideal state among the results of applying one of the legal moves to
	 *         the state <code>s</code>.
	 * @throws NullPointerException The state is null.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	protected Pair<A, U> maxPair(S s, U alpha, U beta) {
		if (s == null)
			throw new NullPointerException("s is null.");
		if (s.terminalTest())
			return new Pair<A, U>(null, s.utility(player));
		// There isn't the chance to return a null key because this isn't the base case
		Pair<A, U> v = new Pair<A, U>(null, s.initial_alpha(player));
		final Iterable<A> actions = s.actions();
		for (A toChild : actions) {
			v = objectUtils.max(v, minPair(s.result(toChild), alpha, beta), alphaBetaComparator);
			s.revert();
			if (v.getValue().compareTo(beta) >= 0)
				return v;
			alpha = objectUtils.max(alpha, v.getValue());
		}
		return v;
	}

	/**
	 * Executes a "min" alpha-beta pruning step. Unlike in Russel and Norvig's
	 * implementation, here the whole action-utility {@link monkey.util.Pair Pair}
	 * is returned instead. This is due to performance reasons.
	 *
	 * @param s     The state to be considered.
	 * @param alpha The current alpha value and it could be null.
	 * @param beta  The current beta value and it could be null.
	 * @return An action-utility {@link monkey.util.Pair Pair} describing the most
	 *         ideal state among the results of applying one of the legal moves to
	 *         the state <code>s</code>.
	 * @throws NullPointerException The state is null.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	protected Pair<A, U> minPair(S s, U alpha, U beta) {
		if (s == null)
			throw new NullPointerException("s is null.");
		if (s.terminalTest())
			return new Pair<A, U>(null, s.utility(player));
		// There isn't the chance to return a null key because this isn't the base case
		Pair<A, U> v = new Pair<A, U>(null, s.initial_beta(player));
		final Iterable<A> actions = s.actions();
		for (A toChild : actions) {
			v = objectUtils.min(v, maxPair(s.result(toChild), alpha, beta), alphaBetaComparator);
			s.revert();
			if (v.getValue().compareTo(beta) <= 0)
				return v;
			beta = objectUtils.min(beta, v.getValue());
		}
		return v;
	}

}
