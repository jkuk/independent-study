package parser;
import parser.parseTree.NonTerminal;
import parser.parseTree.Terminal;

public class ParserConstants {
	public static final NonTerminal GOAL = new NonTerminal("goal");
	public static final Terminal EOF = new Terminal("EOF");
	public static final Terminal EPSILON = new Terminal("Epsilon");

	public static final String LIST = "list";
	public static final String EMPTY = "empty";
	public static final String NULL = "null";
	public static final String EVALUATION = "evaluation";
	public static final String CONDITIONAL = "conditional";
	public static final String LOOP = "loop";
	public static final String VARIABLE = "variable";
	public static final String ARRAY = "array";
	public static final String FUNCTION = "function";
	public static final String IDENTIFIER = "identifier";

	public static final char NONE = '0';
	public static final char NEW_VARIABLE = '1';
	public static final char NEW_ARRAY = '2';
	public static final char NEW_FUNCTION = '3';
	public static final char CHECK_IDENTIFIER = '4';

	public static final int DATA_TYPE_BRANCH = 0;
	public static final int PARAMETERS_BRANCH = 1;

	public static final int DATA_TYPE_INDEX = 0;
	public static final int DIMENSION_INDEX = 1;

	private ParserConstants() {}
}
