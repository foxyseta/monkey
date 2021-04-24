package monkey.mnk;

import monkey.util.Pair;

public class Position {

	public final int ROWSNUMBER, COLUMNSNUMBER;

	public Position(Board b, int row, int column) {
		// TODO Constructor stub
		ROWSNUMBER = COLUMNSNUMBER = 0;
	}

	public int getRow() {
		// TODO Method stub
		return 0;
	}

	public int setRow(int row) {
		// TODO Method stub
		return 0;
	}

	public void getColumn() {
		// TODO Method stub
	}

	public void setColumn(int column) {
		// TODO Method stub
	}

	public Pair<Integer, Integer> offsetFrom(Position p) {
		// TODO Method stub
		return new Pair<Integer, Integer>(0, 0);
	}

	public Position move(Pair<Integer, Integer> offset) {
		// TODO Method stub
		return null;
	}

	public Position move(int rows, int columns) {
		// TODO Method stub
		return null;
	}

}
