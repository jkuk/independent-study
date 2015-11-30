package parser.parseTree;

public class Reduce extends Action {
	private NonTerminal reduction;
	private Handle handle;

	public Reduce(NonTerminal reduction, Handle handle) {
		this.reduction = reduction;
		this.handle = handle;
	}

	public NonTerminal getReduction() {
		return reduction;
	}

	public Handle getHandle() {
		return handle;
	}

	@Override
	public String toString() {
		return "REDUCE: " + reduction + "->" + handle;
	}
}
