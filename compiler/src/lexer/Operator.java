package lexer;

public class Operator {
	private Character character;
	private int precedence;

	public Operator(Character character) {
		this.character = character;
		if (character == LexerConstants.KLEENE) {
			precedence = 4;
		}
		else if (character == LexerConstants.CONCATENATE) {
			precedence = 3;
		}
		else if (character == LexerConstants.ALTERNATE) {
			precedence = 2;
		}
		else if (character == LexerConstants.LEFT_PARENTHESIS) {
			precedence = 1;
		}
		else {
			precedence = LexerConstants.NON_OPERATOR;
		}
	}

	public char getCharacter() {
		return character;
	}

	public boolean hasGreaterOrEqualPrecedence(Operator operator) {
		if (character == LexerConstants.LEFT_PARENTHESIS
		|| operator.getCharacter() == LexerConstants.LEFT_PARENTHESIS) {
			return false;
		}
		return precedence >= operator.getPrecedence();
	}

	public int getPrecedence() {
		return precedence;
	}

	public boolean isOperator() {
		return precedence != LexerConstants.NON_OPERATOR;
	}

	@Override
	public String toString() {
		return character + "";
	}
}