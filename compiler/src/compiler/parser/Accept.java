package compiler.parser;

public class Accept extends Reduce {
	public Accept(NonTerminal reduction, Handle handle) {
		super(reduction, handle);
	}

	@Override
	public String toString() {
		return "ACCEPT";
	}
}
