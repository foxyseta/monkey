package monkey.mnk;

import monkey.ai.Player;

/**
 * In an MNK-game, an <code>Alignment</code> is one of the possible arrangements
 * in a straight line and of length K. If a {@link monkey.ai.Player [Player]}
 * fills an <code>Alignment</code> with K of their own marks, they win.
 *
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public class Alignment {

	/**
	 * Starting from the top left cell of the {@link Alignment}, you can move
	 * following a cerain <code>Direction</code> to get to the others.
	 *
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public enum Direction {
		HORIZONTAL, VERTICAL, PRIMARY_DIAGONAL, SECONDARY_DIAGONAL
	}

	/**
	 * An {@link Alignment} can be in exactly one of the following
	 * <code>State</code>s, depending on the marks added to it.
	 *
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public enum State {
		EMPTY, P1, P2, MIXED
	}

	/** The {@link Position} of the top left cell of this {@link Alignment}. */
	public final Position FIRSTCELL;
	/** The {@link Alignment.Direction [Direction]} of this {@link Alignment}. */
	public final Direction DIRECTION;
	/** The length of this {@link Alignment}. */
	public final int LENGTH;

	/**
	 * Constructs a new empty {@link Alignment} given its coordinates.
	 * 
	 * @param firstCell An initializer for {@link #FIRSTCELL}
	 * @param direction An initializer for {@link #DIRECTION}
	 * @param length    An initializer for {@link #LENGTH}
	 * @throws IllegalArgumentException  length is negative
	 * @throws IndexOutOfBoundsException last cell out of firstCell's bounds
	 * @throws NullPointerException      firstCell, or direction, or both are null
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Alignment(Position firstCell, Direction direction, int legnth) {
		FIRSTCELL = null; // TODO Constructor stub
		DIRECTION = Direction.HORIZONTAL;
		LENGTH = 0;
	}

	/**
	 * The number of marks for a certain {@link monkey.ai.Player [Player]} in this
	 * {@link Alignment}.
	 *
	 * @param p The {@link monkey.ai.Player [Player]} whose score will be returned.
	 * @return The specified {@link monkey.ai.Player [Player]}'s score.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @return 1.0
	 */
	public int score(Player p) {
		return 0; // TODO Method stub
	}

	/**
	 * The number of free cells in this {@link Alignment}.
	 *
	 * @return Number of free cells in this {@link Alignment}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public int getFreeCells() {
		return 0; // TODO Method stub
	}

	/**
	 * A getter for the current {@link Alignment.State [State]}.
	 *
	 * @return The current {@link Alignment.State [State]}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public State getState() {
		return State.EMPTY; // TODO Method stub
	}

	/**
	 * Adds a new mark for the specified {@link monkey.ai.Player [Player]}.
	 *
	 * @param p The {@link monkey.ai.Player [Player]} whose mark is to be added.
	 * @throws IllegalCallerException No free cells to be marked.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void addMark(Player p) {
		// TODO Method stub
	}

	/**
	 * Removes an old mark for the specified {@link monkey.ai.Player [Player]}.
	 *
	 * @param p The {@link monkey.ai.Player [Player]} whose mark is to be removed.
	 * @throws IllegalCallerException No marked cells to be removed.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void removeMark(Player p) {
		// TODO Method stub
	}

	/** Number of cells marked by the first {@link monkey.ai.Player [Player]}. */
	private int p1Cells = 0;
	/** Number of cells marked by the second {@link monkey.ai.Player [Player]}. */
	private int p2Cells = 0;
	/** Current state. */
	private State state = State.EMPTY;

}
