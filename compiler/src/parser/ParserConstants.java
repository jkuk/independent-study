package parser;

public class ParserConstants {
	public static final NonTerminal GOAL = new NonTerminal("Goal");
	public static final Terminal EOF = new Terminal("eof");
	public static final Terminal EPSILON = new Terminal("epsilon");

	private ParserConstants() {}
}