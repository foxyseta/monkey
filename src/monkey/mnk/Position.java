package monkey.mnk;

import java.lang.reflect.InaccessibleObjectException;

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
		if ((row > ROWSNUMBER) || (row < 0))
			throw new IndexOutOfBoundsException("This row isn't valid");
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
	private void validateColumn(int column) {
		if ((column > COLUMNSNUMBER) || (column < 0))
			throw new IndexOutOfBoundsException("This row isn't valid");
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
	private void validate(int row, int column) {
		validateRow(row);
		validateColumn(column);
	}

	/**
	 * Constructs a new {@link Position} given the numbers of rwos and columns and
	 * its coordinates.
	 *
	 * @param rowsNumber    Number of rows in the grid.
	 * @param columnsNumber Number of columns in the grid.
	 * @param row           Row index (starting from zero).
	 * @param column        Column index (starting from zero).
	 * @throws IndexOutOfBoundsException Referring to a {@link Position} outside of
	 *                                   <code>b</code>'s bounds.
	 * @author Gaia Clerici
	 * @version 1.0
	 * @since 1.0
	 */
	public Position(int rowsNumber, int columnsNumber, int row, int column) {
		if (rowsNumber < 0 || columnsNumber < 0)
			throw new IndexOutOfBoundsException("rowsNumber or columnsNumber aren't valid");
		ROWSNUMBER = rowsNumber;
		COLUMNSNUMBER = columnsNumber;
		validate(row, column);
		this.row = row;
		this.column = column;
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
		if (b == null)
			throw new NullPointerException("b is null");
		ROWSNUMBER = b.M;
		COLUMNSNUMBER = b.N;
		validate(row, column);
		this.row = row;
		this.column = column;
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
		return row;
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
		validateRow(row);
		this.row = row;
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
		return column;
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
		validateColumn(column);
		this.column = column;
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
		return new Pair<Integer, Integer>(ROWSNUMBER - p.getRow(), COLUMNSNUMBER - p.getColumn());
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
		if (offset == null)
			throw new NullPointerException("The offset is null");
		validate(row + offset.getKey(), column + offset.getValue());
		row = row + offset.getKey();
		column = column + offset.getValue();
		return this;
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
		validate(row + rows, column + columns);
		Pair<Integer, Integer> offset = new Pair<Integer, Integer>(rows, columns);
		return move(offset);
	}

}
