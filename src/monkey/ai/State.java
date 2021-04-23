package monkey.ai;

interface State<Action, Utility extends Comparable<Utility>> {

	public Player player();

	public Action[] actions();

	public void result(Action a);

	public boolean terminalTest();

	public Utility utility(Player p);

	public Utility alpha();

	public Utility beta();

}
