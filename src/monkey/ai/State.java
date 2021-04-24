package monkey.ai;

/**
 * A <code>State</code> represents a single scenario between those which can be
 * encountered when playing a deterministic, turn-taking, two-player, zero-sum
 * games of perfect information. Backtracking and alpha-beta pruning support is
 * included. See S. Russell, P. Norvig, <i>Artificial Intelligence: A Modern
 * Approach</i>, 3rd ed., Prentice Hall, p. 166f.
 *
 * @param <Self>    The class implementing the interface.
 * @param <Action>  The type of the moves of the game.
 * @param <Utility> The type used to quantify the payoffs.
 * @author Stefano Volpe
 * @version 1.0
 * @since 1.0
 */
public interface State<Self extends State<Self, Action, Utility>, Action, Utility extends Comparable<Utility>> {

	/**
	 * Defines which {@link Player} has the move.
	 *
	 * @return The {@link Player} which has the move.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public Player player();

	/**
	 * Defines the set of legal <code>Action</code>s.
	 *
	 * @return An array containing the legal <code>Action</code>s for the state.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public Action[] actions();

	/**
	 * Defines the result of a certain move updating the {@link State} accordingly.
	 *
	 * @param a The <code>Action</code> to be applied.
	 * @throws IllegalArgumentExeption Illegal move.
	 * @throws NullPointerException    The action is null.
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
	public Utility utility(Player p);

	/**
	 * Defines the alpha value of the relative initial {@link State} for a certain
	 * {@link Player}.
	 *
	 * @see #initial_beta
	 * @param p the {@link Player} whose initial alpha is to be returned.
	 * @return The initial alpha of the relative initial {@link State} for
	 *         <code>p</code>.
	 * @throws NullPointerException The {@link Player} is null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public Utility initial_alpha(Player p);

	/**
	 * Defines the beta value of the relative initial {@link State} for a certain
	 * {@link Player}.
	 *
	 * @see #initial_alpha
	 * @param p the {@link Player} whose initial beta is to be returned.
	 * @return The initial beta of the relative initial {@link State} for
	 *         <code>p</code>.
	 * @throws NullPointerException The {@link Player} is null.
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public Utility initial_beta(Player p);

}
