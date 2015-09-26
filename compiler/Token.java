public class Token {
	private String lexeme;
	private String type;

	public Token(String lexeme, String type) {
		this.lexeme = lexeme;
		this.type = type;
	}

	@Override
	public String toString() {
		return lexeme + ": " + type;
	}
}