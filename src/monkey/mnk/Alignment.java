package monkey.mnk;

import mnkgame.MNKCellState;
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
public class Alignment implements Cloneable {

	/**
	 * Starting from the top left cell of the {@link Alignment}, you can move
	 * following a cerain <code>Direction</code> to get to the others.
	 *
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public enum Direction {

		/** From left to right. */
		HORIZONTAL,
		/** From top to bottom. */
		VERTICAL,
		/** From top left to bottom right. */
		PRIMARY_DIAGONAL,
		/** From bottom left to top right. */
		SECONDARY_DIAGONAL

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

		/** Every cell is free. */
		EMPTY,
		/** Some cells are free, some were marked by {@link monkey.ai.Player#P1}. */
		P1PARTIAL,
		/** Some cells are free, some were marked by {@link monkey.ai.Player#P2}. */
		P2PARTIAL,
		/** All three kinds of cells can be found here. */
		MIXED,
		/** Every cell has been marked by {@link monkey.ai.Player#P1}. */
		P1FULL,
		/** Every cell has been marked by {@link monkey.ai.Player#P2}. */
		P2FULL

	}

	/** The {@link Position} of the top left cell of this {@link Alignment}. */
	public final Position FIRSTCELL;
	/** The {@link Position} of the bottom right cell of this {@link Alignment}. */
	public final Position LASTCELL;
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
	 * @param firstExt  An initializer for {@link #firstExtremity}
	 * @param secondExt An initializer for {@link #secondExtremity}
	 * @throws IllegalArgumentException  length is negative or zero
	 * @throws IndexOutOfBoundsException last cell out of firstCell's bounds
	 * @throws NullPointerException      firstCell, or direction, or both are null
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Alignment(Position firstCell, Direction direction, int length, MNKCellState firstExt, MNKCellState secondExt) {
		if (length <= 0)
			throw new IllegalArgumentException("length can't be negative or zero.");
		if (firstCell == null || direction == null)
			throw new NullPointerException("firstCell or direction are null.");
		if (direction != Direction.VERTICAL && firstCell.getColumn() + length > firstCell.COLUMNSNUMBER)
			throw new IndexOutOfBoundsException("Last cell out of firstCell's horizontal bounds.");
		if ((direction == Direction.VERTICAL || direction == Direction.PRIMARY_DIAGONAL)
				&& firstCell.getRow() + length > firstCell.ROWSNUMBER
				|| direction == Direction.SECONDARY_DIAGONAL && firstCell.getRow() - length < -1)
			throw new IndexOutOfBoundsException("Last cell out of firstCell's vertical bounds.");
		FIRSTCELL = firstCell;
		DIRECTION = direction;
		LENGTH = length;
		// LASTCELL
		int lastRow = FIRSTCELL.getRow(), lastColumn = FIRSTCELL.getColumn();
		switch (DIRECTION) {
		case HORIZONTAL:
			lastColumn += LENGTH - 1;
			break;
		case VERTICAL:
			lastRow += LENGTH - 1;
			break;
		case PRIMARY_DIAGONAL:
			lastRow += LENGTH - 1;
			lastColumn += LENGTH - 1;
			break;
		case SECONDARY_DIAGONAL:
			lastRow += 1 - LENGTH;
			lastColumn += LENGTH - 1;
		}
		LASTCELL = new Position(FIRSTCELL.ROWSNUMBER, FIRSTCELL.COLUMNSNUMBER, lastRow, lastColumn);
		firstExtremity = firstExt;
		secondExtremity = secondExt;
	}

	/**
	 * Creates a clone of this {@link Alignment}.
	 *
	 * @author Stefano Volpe
	 * @version 1.0
	 * @since 1.0
	 */
	public Alignment clone() {
		try {
			return (Alignment) super.clone();
		} catch (CloneNotSupportedException e) {
			// Should never happen: we support clone
			throw new InternalError(e.toString());
		}
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
		return LENGTH - p1Cells - p2Cells;
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
	 * A getter for the current {@link Threat}.
	 *
	 * @see #getThreatener
	 * @return The current {@link Threat}, or <code>null</code> if there is none.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Threat getThreat() {
		return threat;
	}

	/**
	 * Computes the {@link monkey.ai.Player Player} who caused the current
	 * {@link Threat}, if there is any.
	 *
	 * @see #getThreat
	 * @return The current threatener {@link monkey.ai.Player Player}, or
	 *         <code>null</code> if there is no {@link Threat} at all.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Player getThreatener() {
		if (threat == null)
			return null;
		return state == State.P1PARTIAL || state == State.P1FULL ? Player.P1 : Player.P2;
	}

	/**
	 * Adds a new mark for the specified {@link monkey.ai.Player Player}.
	 *
	 * @param p The {@link monkey.ai.Player Player} whose mark is to be added.
	 * @param b The current state of the board.
	 * @throws IllegalCallerException No free cells to be marked.
	 * @throws NullPointerException   p or b are null.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void addMark(Player p, Board b) {
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
		computeThreat(b);
	}

	/**
	 * Removes an old mark for the specified {@link monkey.ai.Player Player}.
	 *
	 * @param p The {@link monkey.ai.Player Player} whose mark is to be removed.
	 * @param b The current state of the board.
	 * @throws IllegalCallerException No marked cells to be removed.
	 * @throws NullPointerException   p or b are null.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void removeMark(Player p, Board b) {
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
		state = p1Cells == 0 ? p2Cells == 0 ? State.EMPTY : State.P2PARTIAL : p2Cells == 0 ? State.P1PARTIAL : State.MIXED;
		computeThreat(b);
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

	/**
	 * Sets the first extremity. Checks for weird behaviors, such as new cells
	 * appearing from nowhere and cells already marked being overwritten.
	 *
	 * @see #setSecondExtremity
	 * @param cell The new state of the first extremity.
	 * @param b    The current state of the board.
	 * @throws IllegalCallerException   The extremity cannot be changed anymore.
	 * @throws IllegalArgumentException {@link #setFirstExtremity} can be called,
	 *                                  but the extremity cannot be set to this
	 *                                  <code>state</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void setFirstExtremity(MNKCellState cell, Board b) {
		if (cell != firstExtremity) {
			if (firstExtremity == null)
				throw new IllegalCallerException("The first extremity is out of the board.");
			if (cell == null)
				throw new IllegalArgumentException("The first extremity is not out of board.");
			if (firstExtremity != MNKCellState.FREE && cell != MNKCellState.FREE)
				throw new IllegalArgumentException("Can not ovveride a marked cell.");
			firstExtremity = cell;
			computeThreat(b);
		}
	}

	/**
	 * Sets the second extremity. Checks for weird behaviors, such as new cells
	 * appearing from nowhere and cells already marked being overwritten.
	 *
	 * @see #setFirstExtremity
	 * @param cell The new state of the second extremity.
	 * @param b    The current state of the board.
	 * @throws IllegalCallerException   The extremity cannot be changed anymore.
	 * @throws IllegalArgumentException {@link #setSecondExtremity} can be called,
	 *                                  but the extremity cannot be set to this
	 *                                  <code>state</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void setSecondExtremity(MNKCellState cell, Board b) {
		if (cell != secondExtremity) {
			if (secondExtremity == null)
				throw new IllegalCallerException("The second extremity is out of the board.");
			if (cell == null)
				throw new IllegalArgumentException("The second extremity is not out of board.");
			if (secondExtremity != MNKCellState.FREE && cell != MNKCellState.FREE)
				throw new IllegalArgumentException("Can not ovveride a marked cell.");
			secondExtremity = cell;
			computeThreat(b);
		}
	}

	/**
	 * Updates the current {@link Threat}.
	 *
	 * @param b The current state of the board.
	 * @throws NullPointerException b is <code>null</code>.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	private void computeThreat(Board b) {
		// There is no hole
		if (state == State.P1FULL || state == State.P2FULL)
			switch ((firstExtremity == MNKCellState.FREE ? 1 : 0) + (secondExtremity == MNKCellState.FREE ? 1 : 0)) {
			case 0:
				threat = Threat.THREE;
				break;
			case 1:
				threat = Threat.TWO;
				break;
			case 2:
				threat = Threat.ONE;
			}
		// There is just one hole
		else if (getFreeCells() == 1 && (state == State.P1PARTIAL || state == State.P2PARTIAL)
				&& b.getCellState(FIRSTCELL) != MNKCellState.FREE && b.getCellState(LASTCELL) != MNKCellState.FREE)
			switch ((firstExtremity == MNKCellState.FREE ? 1 : 0) + (secondExtremity == MNKCellState.FREE ? 1 : 0)) {
			case 0:
				threat = Threat.SIX;
				break;
			case 1:
				threat = Threat.FIVE;
				break;
			case 2:
				threat = Threat.FOUR;
			}
		else
			threat = null;
	}

	/**
	 * Returns a string representation of the object.
	 *
	 * @return A string representation of this object.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	@Override
	public String toString() {
		return firstExtremity + "{" + p1Cells + " - " + threat + " - " + p2Cells + "}" + secondExtremity;
	}

	/** Number of cells marked by the first {@link monkey.ai.Player Player}. */
	private int p1Cells = 0;
	/** Number of cells marked by the second {@link monkey.ai.Player Player}. */
	private int p2Cells = 0;
	/** Current {@link #State}. */
	private State state = State.EMPTY;
	/**
	 * State of the cell at the first extremity, or <code>null</code> if such a cell
	 * does not exist.
	 */
	private MNKCellState firstExtremity;
	/**
	 * State of the cell at the second extremity, or <code>null</code> if such a
	 * cell does not exist.
	 */
	private MNKCellState secondExtremity;
	/**
	 * If there is one, the current {@link Threat}, or <code>null</code> otherwise.
	 */
	private Threat threat = null;

}
