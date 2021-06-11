package monkey.ai;

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
	/** Utilities instance for generic objects. */
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
		A a = null;
		U alpha = state.initialAlpha(player), beta = state.initialBeta(player), v = alpha;
		final Iterable<A> actions = state.actions();
		for (A toChild : actions) {
			final U minValue = minValue(state.result(toChild), alpha, beta);
			if (a == null || v.compareTo(minValue) < 0) {
				a = toChild;
				v = minValue;
			}
			state.revert();
			if (v.compareTo(beta) >= 0)
				return a;
			alpha = objectUtils.max(alpha, v);
		}
		return a;
	}

	/**
	 * Executes a "max" alpha-beta pruning step.
	 *
	 * @param s     The state to be considered.
	 * @param alpha The current alpha value. It may be null.
	 * @param beta  The current beta value. It may be null.
	 * @return The utility brought by the most useful action for the <code>AI</code>
	 *         within <code>s</code>.
	 * @throws NullPointerException The state is null.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	protected U maxValue(S s, U alpha, U beta) {
		if (s == null)
			throw new NullPointerException("s is null.");
		if (s.terminalTest())
			return s.utility(player);
		U v = s.initialAlpha(player);
		final Iterable<A> actions = s.actions();
		for (A toChild : actions) {
			v = objectUtils.max(v, minValue(s.result(toChild), alpha, beta));
			s.revert();
			if (v.compareTo(beta) >= 0)
				return v;
			alpha = objectUtils.max(alpha, v);
		}
		return v;
	}

	/**
	 * Executes a "min" alpha-beta pruning step.
	 *
	 * @param s     The state to be considered.
	 * @param alpha The current alpha value. It may be null.
	 * @param beta  The current beta value. It may be null.
	 * @return The utility brought by the most useful action for the opponent within
	 *         <code>s</code>.
	 * @throws NullPointerException The state is null.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	protected U minValue(S s, U alpha, U beta) {
		if (s == null)
			throw new NullPointerException("s is null.");
		if (s.terminalTest())
			return s.utility(player);
		U v = s.initialBeta(player);
		final Iterable<A> actions = s.actions();
		for (A toChild : actions) {
			v = objectUtils.min(v, maxValue(s.result(toChild), alpha, beta));
			s.revert();
			if (v.compareTo(alpha) <= 0)
				return v;
			beta = objectUtils.min(beta, v);
		}
		return v;
	}

}
