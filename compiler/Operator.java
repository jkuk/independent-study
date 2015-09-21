public class Operator {
	private final char ALTERNATE = '|';
	private final char CONCATENATE = ' ';
	private final char KLEENE = '*';
	private final char LEFT_PARENTHESIS = '(';
	private final int NON_OPERATOR = 0;

	private Character character;
	private int precedence;

	public Operator(Character character) {
		this(character, null);
	}

	public Operator(Character character, Character previousCharacter) {
		this.character = character;
		if (character == LEFT_PARENTHESIS) {
			precedence = 1;
		}
		else if (character == KLEENE) {
			precedence = 4;
		}
		else if (character == ALTERNATE) {
			precedence = 2;
		}
		else if (previousCharacter != null
		&& previousCharacter != LEFT_PARENTHESIS
		&& previousCharacter != KLEENE
		&& previousCharacter != ALTERNATE) {
			this.character = CONCATENATE;
			precedence = 3;
		}
		else {
			this.character = character;
			precedence = NON_OPERATOR;
		}
	}

	public char getCharacter() {
		return character;
	}

	public boolean hasGreaterOrEqualPrecedence(Operator operator) {
		if (character == LEFT_PARENTHESIS
		|| operator.getCharacter() == LEFT_PARENTHESIS) {
			return false;
		}
		return precedence >= operator.getPrecedence();
	}

	public int getPrecedence() {
		return precedence;
	}

	public boolean isOperator() {
		return precedence != NON_OPERATOR;
	}

	@Override
	public String toString() {
		return character + "";
	}
}