package monkey.ai;

import monkey.util.Pair;

/**
 * An <code>AI</code> is a generic alpha-beta pruner for a deterministic,
 * turn-taking, two-player, zero-sum game of perfect information. See S.
 * Russell, P. Norvig, <i>Artificial Intelligence: A Modern Approach</i>, 3rd
 * ed., Prentice Hall, p. 167f.
 *
 * @param <S> The type to be used for game {@link State}s.
 * @param <A> The type of the moves of the game.
 * @param <U> The type used to quantify the payoffs.
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public class AI<S extends State<A, U>, A, U extends Comparable<U>> {

	/**
	 * Construct a new {@link AI} for a certain {@link Player} given an initial
	 * {@link State} and a timeout in milliseconds.
	 *
	 * @param p  The player the {@link AI} will play as.
	 * @param s0 The initial {@link State} of the game.
	 * @param t  The maximum number of milliseconds usable to select a move.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	// AI(Player p, S s0, long t) { }

	/**
	 * Given a state in which the player has the move, selects one of the legal
	 * actions to be played using alpa-beta pruning.
	 *
	 * @param s Current state of the game. It might be dirtied by the method.
	 * @return An legal action to be played.
	 * @throws IllegalArgumentException The player does not have the move.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	// public A alphaBetaSearch(S s) { }

	/**
	 * Executes a "max" alpha-beta pruning step. Unlike in Russel and Norvig's
	 * implementation, here the whole state-utility {@link monkey.util.Pair [Pair]}
	 * is returned instead. This is due to performance reasons.
	 *
	 * @s The state to be considered.
	 * @alpha The current alpha value.
	 * @beta The current beta value.
	 * @returns A state-utility {@link monkey.util.Pair [Pair]} describing the most
	 *          ideal state among the results of applying one of the legal moves to
	 *          the state <code>s</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	// protected Pair<S, U> maxPair(S s, U alpha, U beta) { }

	/**
	 * Executes a "min" alpha-beta pruning step. Unlike in Russel and Norvig's
	 * implementation, here the whole state-utility {@link monkey.util.Pair [Pair]}
	 * is returned instead. This is due to performance reasons.
	 *
	 * @s The state to be considered.
	 * @alpha The current alpha value.
	 * @beta The current beta value.
	 * @returns A state-utility {@link monkey.util.Pair [Pair]} describing the most
	 *          ideal state among the results of applying one of the legal moves to
	 *          the state <code>s</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	// protected Pair<S, U> minPair(S s, U alpha, U beta) { }

}
