package monkey.ai;

/**
 * A <code>State</code> represents a single scenario between those which can be
 * encountered when playing a deterministic, turn-taking, two-player, zero-sum
 * games of perfect information. Backtracking and alpha-beta pruning support is
 * included. See S. Russell, P. Norvig, <i>Artificial Intelligence: A Modern
 * Approach</i>, 3rd ed., Prentice Hall, p. 166f. A circular type definition is
 * needed in order to declare methods returning the correct kind of instances of
 * <code>State</code>. See K. Arnold, J. Gosling, D. Holmes, <i>The Java
 * Programming Language</i>, 4th ed., Addison-Wesley Professional, p. 148.
 *
 * @param <Self>   The class implementing the interface.
 * @param <Action> The type of the moves of the game.
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public interface State<Self extends State<Self, Action>, Action> extends Cloneable {

	/**
	 * Creates a clone of this object.
	 *
	 * @return The desired clone.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public Self clone();

	/**
	 * Defines which {@link Player} should play next.
	 *
	 * @return The {@link Player} who should play next.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public Player player();

	/**
	 * Checks if a certain <code>Action</code> is legal.
	 *
	 * @param a The <code>Action</code> to test.
	 * @throws NullPointerException a is <code>null</code>.
	 * @return <code>true</code> just in case a is legal.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public boolean isLegal(Action a);

	/**
	 * Defines the set of legal <code>Action</code>s.
	 *
	 * @return An {@link java.lang.Iterable Iterator} containing the legal
	 *         <code>Action</code>s for the state.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public java.util.Iterator<Action> actions();

	/**
	 * Defines the result of a certain move updating the {@link State} accordingly.
	 *
	 * @param a The <code>Action</code> to be applied.
	 * @throws IllegalArgumentException Illegal move.
	 * @return A reference to this {@link State}.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public Self result(Action a);

	/**
	 * Reverts the state to its parent.
	 *
	 * @return A reference to this {@link State}.
	 * @throws IllegalCallerException This is the initial {@link State}.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public Self revert();

	/**
	 * Checks for terminal {@link State}s.
	 *
	 * @see #utility
	 * @return <code>true</code> just in case the {@link State} is terminal.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public boolean terminalTest();

	/**
	 * When called on a terminal {@link State}, defines the payoff of such an ending
	 * for a certain {@link Player}.
	 *
	 * @see #terminalTest
	 * @param p The {@link Player} whose payoff is to be returned.
	 * @return The payoff for {@link Player} <code>p</code>.
	 * @throws IllegalCallerException Non-terminal {@link State}.
	 * @throws NullPointerException   The {@link Player} is null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int utility(Player p);

	/**
	 * Defines the alpha value of the relative initial {@link State} for a certain
	 * {@link Player}.
	 *
	 * @see #initialBeta
	 * @param p the {@link Player} whose initial alpha is to be returned.
	 * @return The non-null initial alpha of the relative initial {@link State} for
	 *         <code>p</code>.
	 * @throws NullPointerException The {@link Player} is null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int initialAlpha(Player p);

	/**
	 * Defines the beta value of the relative initial {@link State} for a certain
	 * {@link Player}.
	 *
	 * @see #initialAlpha
	 * @param p the {@link Player} whose initial beta is to be returned.
	 * @return The non-null initial beta of the relative initial {@link State} for
	 *         <code>p</code>.
	 * @throws NullPointerException The {@link Player} is null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int initialBeta(Player p);

	/**
	 * Returns an estimate of the expected utility of the game from the current
	 * {@link Action} for a certain {@link Player}.
	 *
	 * @see #utility
	 * @param p The {@link Player} whose estimated payoff is to be returned.
	 * @return The estimated payoff for {@link Player} <code>p</code>.
	 * @throws NullPointerException The {@link Player} is null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int eval(Player p);

	/**
	 * Computes an overestimate of the height of the game tree whose root is this
	 * {@link State}.
	 *
	 * @return The overestimated height.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int overestimatedHeight();

	/**
	 * Suggests a reasonable transposition table capacity assuming this instance is
	 * used as initial {@link State}.
	 *
	 * @return The suggested capacity.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int ttSuggestedCapacity();

	/**
	 * Computes the number of legal actions for the current {@link State}.
	 *
	 * @return The number of legal actions.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public int countLegalActions();

}
