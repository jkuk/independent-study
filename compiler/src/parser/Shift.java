package parser;

public class Shift extends Action {
	private ParserState destinationState;

	public Shift(ParserState destinationState) {
		this.destinationState = destinationState;
	}

	public ParserState getDestinationState() {
		return destinationState;
	}

	@Override
	public String toString() {
		return "SHIFT: " + destinationState;
	}
}