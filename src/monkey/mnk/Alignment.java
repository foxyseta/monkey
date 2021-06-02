package monkey.mnk;

import javax.lang.model.util.ElementScanner6;

import monkey.ai.Player;

/**
 * In an MNK-game, an <code>Alignment</code> is one of the possible arrangements
 * in a straight line and of length K. If a {@link monkey.ai.Player Player}
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
		EMPTY, P1PARTIAL, P2PARTIAL, MIXED, P1FULL, P2FULL
	}

	/** The {@link Position} of the top left cell of this {@link Alignment}. */
	public final Position FIRSTCELL;
	/** The {@link Alignment.Direction Direction} of this {@link Alignment}. */
	public final Direction DIRECTION;
	/** The length of this {@link Alignment}. */
	public final int LENGTH;

	/**
	 * Constructs a new empty {@link Alignment} given its coordinates.
	 * 
	 * @param firstCell An initializer for {@link #FIRSTCELL}
	 * @param direction An initializer for {@link #DIRECTION}
	 * @param length    An initializer for {@link #LENGTH}
	 * @throws IllegalArgumentException  length is negative or zero
	 * @throws IndexOutOfBoundsException last cell out of firstCell's bounds
	 * @throws NullPointerException      firstCell, or direction, or both are null
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Alignment(Position firstCell, Direction direction, int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("length can't be negative or zero.");
		}
		if ((firstCell == null) || (direction == null)) {
			throw new NullPointerException("firstCell or direction are null.");
		}
		switch (direction) {
			case HORIZONTAL:
				if (firstCell.COLUMNSNUMBER <= firstCell.getColumn() + (length - 1))
					throw new IndexOutOfBoundsException("Last cell out of firstCell's bounds.");
				break;
			case VERTICAL:
				if (firstCell.ROWSNUMBER <= firstCell.getRow() + (length - 1))
					throw new IndexOutOfBoundsException("Last cell out of firstCell's bounds.");
				break;
			case PRIMARY_DIAGONAL:
				if (firstCell.ROWSNUMBER <= firstCell.getRow() + (length - 1)
						|| firstCell.COLUMNSNUMBER <= firstCell.getColumn() + (length - 1))
					throw new IndexOutOfBoundsException("Last cell out of firstCell's bounds.");
				break;
			case SECONDARY_DIAGONAL:
				if (firstCell.getRow() - (length - 1) < 0
						|| firstCell.COLUMNSNUMBER <= firstCell.getColumn() + (length - 1))
					throw new IndexOutOfBoundsException("Last cell out of firstCell's bounds.");
				break;
		}
		FIRSTCELL = firstCell;
		DIRECTION = direction;
		LENGTH = length;
	}

	/**
	 * The number of marks for a certain {@link monkey.ai.Player Player} in this
	 * {@link Alignment}.
	 *
	 * @param p The {@link monkey.ai.Player Player} whose score will be returned.
	 * @throws NullPointerException p is null.
	 * @return The specified {@link monkey.ai.Player Player}'s score.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public int score(Player p) {
		if (p == null)
			throw new NullPointerException("p is null.");
		return p == Player.P1 ? p1Cells : p2Cells;
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
		return (LENGTH - (p1Cells + p2Cells));
	}

	/**
	 * A getter for the current {@link Alignment.State State}.
	 *
	 * @return The current {@link Alignment.State State}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public State getState() {
		return state;
	}

	/**
	 * Adds a new mark for the specified {@link monkey.ai.Player Player}.
	 *
	 * @param p The {@link monkey.ai.Player Player} whose mark is to be added.
	 * @throws IllegalCallerException No free cells to be marked.
	 * @throws NullPointerException   p is null.
	 * @return The (eventually) updated {@link #state}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public State addMark(Player p) {
		if (p == null)
			throw new NullPointerException("p is null.");
		if (getFreeCells() == 0)
			throw new IllegalCallerException("No free cells to be marked.");
		if (p == Player.P1) {
			++p1Cells;
			if (p1Cells == LENGTH)
				state = State.P1FULL;
			else if (p2Cells == 0)
				state = State.P1PARTIAL;
		} else {
			++p2Cells;
			if (p2Cells == LENGTH)
				state = State.P2FULL;
			else if (p1Cells == 0)
				state = State.P2PARTIAL;
		}
		if (p1Cells != 0 && p2Cells != 0)
			state = State.MIXED;
		return state;
	}

	/**
	 * Removes an old mark for the specified {@link monkey.ai.Player Player}.
	 *
	 * @param p The {@link monkey.ai.Player Player} whose mark is to be removed.
	 * @throws IllegalCallerException No marked cells to be removed.
	 * @throws NullPointerException   p is null.
	 * @return The (eventually) updated {@link #state}.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public State removeMark(Player p) {
		if (p == null)
			throw new NullPointerException("p is null.");
		if (p == Player.P1) {
			if (p1Cells == 0)
				throw new IllegalCallerException("No marked cells to be removed");
			--p1Cells;
		} else {
			if (p2Cells == 0)
				throw new IllegalCallerException("No marked cells to be removed");
			--p2Cells;
		}
		state = p1Cells == 0 ? p2Cells == 0 ? State.EMPTY : State.P2PARTIAL
							 : p2Cells == 0 ? State.P1PARTIAL : State.MIXED;
	}

	/**
	 * Resets every counter of this {@link Alignment} to its initializer and updates
	 * its {@link #state} accordingly.
	 *
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public void clear() {
		p1Cells = 0;
		p2Cells = 0;
		state = State.EMPTY;
	}

	/** Number of cells marked by the first {@link monkey.ai.Player Player}. */
	private int p1Cells = 0;
	/** Number of cells marked by the second {@link monkey.ai.Player Player}. */
	private int p2Cells = 0;
	/** Current state. */
	private State state = State.EMPTY;

}