public class Operator {
	private Character character;
	private int precedence;

	public Operator(Character character) {
		this.character = character;
		if (character == Constants.KLEENE) {
			precedence = 4;
		}
		else if (character == Constants.CONCATENATE) {
			precedence = 3;
		}
		else if (character == Constants.ALTERNATE) {
			precedence = 2;
		}
		else if (character == Constants.LEFT_PARENTHESIS) {
			precedence = 1;
		}
		else {
			precedence = Constants.NON_OPERATOR;
		}
	}

	public char getCharacter() {
		return character;
	}

	public boolean hasGreaterOrEqualPrecedence(Operator operator) {
		if (character == Constants.LEFT_PARENTHESIS
		|| operator.getCharacter() == Constants.LEFT_PARENTHESIS) {
			return false;
		}
		return precedence >= operator.getPrecedence();
	}

	public int getPrecedence() {
		return precedence;
	}

	public boolean isOperator() {
		return precedence != Constants.NON_OPERATOR;
	}

	@Override
	public String toString() {
		return character + "";
	}
}