package monkey.mnk;

import monkey.util.Pair;

/**
 * A <code>Position</code> refers to a single cell of the {@link Board}. It
 * features bounds checking and helper operations.
 *
 * @author Gaia Clerici
 * @version 1.0
 * @since 1.0
 */
public class Position {

	/** Number of rows of the board. */
	public final int ROWSNUMBER;
	/** Number of columns of the board. */
	public final int COLUMNSNUMBER;

	/** Current row index. */
	private int row;
	/** Current column index */
	private int column;

	/**
	 * Implements bounds checking for rows.
	 *
	 * @see #validateColumn
	 * @param row Row index.
	 * @throws IndexOutOfBoundsException Row out of bounds.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	private void validateRow(int row) {
		// TODO Method stub
	}

	/**
	 * Implements bounds checking for columns.
	 *
	 * @see #validateRow
	 * @param column Column index.
	 * @throws IndexOutOfBoundsException Column out of bounds.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	private void validateColumn() {
		// TODO Method stub
	}

	/**
	 * Implements bounds checking for both rows and columns.
	 *
	 * @param row    Row index.
	 * @param column Column index.
	 * @throws IndexOutOfBoundsException Row and/or column out of bounds.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	private void validate() {
		// TODO Method stub
	}

	/**
	 * Constructs a new {@link Position} given the numbers of rwos and columns and
	 * its coordinates.
	 *
	 * @param rowsNumber    Number of rows in the grid.
	 * @param columnsNumber Number of columns in the grid.
	 * @param row           Row index (starting from zero).
	 * @param column        Column index (starting from zero).
	 * @throws NullPointerException      Null {@link Board}.
	 * @throws IndexOutOfBoundsException Referring to a {@link Position} outside of
	 *                                   <code>b</code>'s bounds.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Position(int rowsNumber, int columnsNumber, int row, int column) {
		// TODO Constructor stub
		ROWSNUMBER = COLUMNSNUMBER = 0;
	}

	/**
	 * Constructs a new {@link Position} given its parent {@link Board} and its
	 * coordinates.
	 *
	 * @param b      Parent {@link Board} whose extents are used for bounds
	 *               checking.
	 * @param row    Row index (starting from zero).
	 * @param column Column index (starting from zero).
	 * @throws NullPointerException      Null {@link Board}.
	 * @throws IndexOutOfBoundsException Referring to a {@link Position} outside of
	 *                                   <code>b</code>'s bounds.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Position(Board b, int row, int column) {
		// TODO Constructor stub
		ROWSNUMBER = COLUMNSNUMBER = 0;
	}

	/**
	 * Getter for row index.
	 *
	 * @see #getColumn
	 * @return Row index.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public int getRow() {
		// TODO Method stub
		return 0;
	}

	/**
	 * Setter for row index.
	 *
	 * @see #setColumn
	 * @param row Row index.
	 * @throws IndexOutOfBoundsException Row out of bounds.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void setRow(int row) {
		// TODO Method stub
	}

	/**
	 * Getter for column index.
	 *
	 * @see #getRow
	 * @return Column index.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public int getColumn() {
		// TODO Method stub
		return 0;
	}

	/**
	 * Setter for column index.
	 *
	 * @see #setRow
	 * @param column Row index.
	 * @throws IndexOutOfBoundsException Column out of bounds.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public void setColumn(int column) {
		// TODO Method stub
	}

	/**
	 * Computes the offset of this {@link Position} from <code>p</code>.
	 *
	 * @param p Starting point from which to compute the offset.
	 * @return A {@link monkey.util.Pair [Pair]} containing row and column offsets.
	 * @throws NullPointerException <code>p</code> is null.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Pair<Integer, Integer> offsetFrom(Position p) {
		// TODO Method stub
		return new Pair<Integer, Integer>(0, 0);
	}

	/**
	 * Moves this {@link Position} of the specified offset.
	 *
	 * @param offset The offset used to move this {@link Position}.
	 * @return A reference to this object after the update.
	 * @throws NullPointerException      <code>offset</code> is null.
	 * @throws IndexOutOfBoundsException Moving this {@link Position} outside of
	 *                                   <code>b</code>'s bounds.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Position move(Pair<Integer, Integer> offset) {
		// TODO Method stub
		return null;
	}

	/**
	 * Moves this {@link Position} of the specified row and column offsets.
	 *
	 * @param rows    The row offset used to move this {@link Position}.
	 * @param columns The column offset used to move this {@link Position}.
	 * @return A reference to this object after the update.
	 * @throws NullPointerException      <code>offset</code> is null.
	 * @throws IndexOutOfBoundsException Moving this {@link Position} outside of
	 *                                   <code>b</code>'s bounds.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Position move(int rows, int columns) {
		// TODO Method stub
		return null;
	}

}
