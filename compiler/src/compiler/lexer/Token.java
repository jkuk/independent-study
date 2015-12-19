package compiler.lexer;

public class Token {
	private String lexeme;
	private String type;

	public Token(String lexeme, String type) {
		this.lexeme = lexeme;
		this.type = type;
	}

	public String getLexeme() {
		return lexeme;
	}

	public String getType() {
		return type;
	}

	@Override
	public String toString() {
		return lexeme + ": " + type;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object == this) return true;
		if (object instanceof Token) {
			Token that = (Token)object;
			return lexeme.equals(that.getLexeme())
			&& type.equals(that.getType());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}
}
