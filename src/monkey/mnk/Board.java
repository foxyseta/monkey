package monkey.mnk;

import monkey.ai.Player;

public class Board implements monkey.ai.State<Board, Position, Integer> {

	final public int M, N, K, SIZE, VICTORYUTILITY, DRAWUTILITY, LOSSUTILITY;

	public Board(int m, int n, int k) {
		M = N = K = SIZE = VICTORYUTILITY = DRAWUTILITY = LOSSUTILITY = 0;
	}

	@Override
	public Player player() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Position[] actions() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Board result(Position a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Board revert() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean terminalTest() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Integer utility(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer initial_alpha(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Integer initial_beta(Player p) {
		// TODO Auto-generated method stub
		return null;
	}

}
