package compiler.parser;

public class ParserConstants {
	/*
	███████ ██    ██ ███    ███ ██████   ██████  ██      ███████
	██       ██  ██  ████  ████ ██   ██ ██    ██ ██      ██
	███████   ████   ██ ████ ██ ██████  ██    ██ ██      ███████
	     ██    ██    ██  ██  ██ ██   ██ ██    ██ ██           ██
	███████    ██    ██      ██ ██████   ██████  ███████ ███████
	*/
	public static final NonTerminal GOAL = new NonTerminal("goal");
	public static final Terminal EPSILON = new Terminal("Epsilon");
	public static final Terminal EOF = new Terminal("EOF");

	/*
	██ ███    ██ ███████ ████████ ██████  ██    ██  ██████ ████████ ██  ██████  ███    ██ ███████
	██ ████   ██ ██         ██    ██   ██ ██    ██ ██         ██    ██ ██    ██ ████   ██ ██
	██ ██ ██  ██ ███████    ██    ██████  ██    ██ ██         ██    ██ ██    ██ ██ ██  ██ ███████
	██ ██  ██ ██      ██    ██    ██   ██ ██    ██ ██         ██    ██ ██    ██ ██  ██ ██      ██
	██ ██   ████ ███████    ██    ██   ██  ██████   ██████    ██    ██  ██████  ██   ████ ███████
	*/
	public static final String NULL = "null";
	public static final String LIST = "list";
	public static final String EMPTY = "empty";
	public static final String EVALUATION = "evaluation";
	public static final String CONDITIONAL = "conditional";
	public static final String LOOP = "loop";
	public static final String DECLARE_OR_INITIALIZE = "declare_or_initialize";
	public static final String CALL_NUMBER = "call_number";
	public static final String CALL_STRING = "call_string";
	public static final String CALL_VARIABLE = "call_variable";
	public static final String DECLARE_VARIABLE = "declare_variable";
	public static final String INITIALIZE_VARIABLE = "initialize_variable";
	public static final String DECLARE_AND_INITIALIZE_VARIABLE = "declare_and_initialize_variable";
	public static final String CALL_ARRAY = "call_array";
	public static final String DECLARE_ARRAY = "declare_array";
	public static final String INITIALIZE_ARRAY = "initialize_array";
	public static final String DECLARE_AND_INITIALIZE_ARRAY = "declare_and_initialize_array";
	public static final String CALL_FUNCTION = "call_function";
	public static final String CALL_FUNCTION_NO_ARGUMENTS = "call_function_no_arguments";
	public static final String DECLARE_FUNCTION = "declare_function";
	public static final String INITIALIZE_FUNCTION = "initialize_function";
	public static final String DECLARE_AND_INITIALIZE_FUNCTION = "declare_and_initialize_function";
	public static final String DECLARE_FUNCTION_NO_PARAMETERS = "declare_function_no_parameters";
	public static final String INITIALIZE_FUNCTION_NO_PARAMETERS = "initialize_function_no_parameters";
	public static final String DECLARE_AND_INITIALIZE_FUNCTION_NO_PARAMETERS = "declare_and_initialize_function_no_parameters";

	/*
	 █████  ███    ██ ███    ██  ██████  ████████  █████  ████████ ██  ██████  ███    ██ ███████
	██   ██ ████   ██ ████   ██ ██    ██    ██    ██   ██    ██    ██ ██    ██ ████   ██ ██
	███████ ██ ██  ██ ██ ██  ██ ██    ██    ██    ███████    ██    ██ ██    ██ ██ ██  ██ ███████
	██   ██ ██  ██ ██ ██  ██ ██ ██    ██    ██    ██   ██    ██    ██ ██    ██ ██  ██ ██      ██
	██   ██ ██   ████ ██   ████  ██████     ██    ██   ██    ██    ██  ██████  ██   ████ ███████
	*/
	public static final char NONE = 'a';
	public static final char NUMBER_CALL = 'b';
	public static final char STRING_CALL = 'c';
	public static final char DECLARATION_OR_INITIALIZATION = 'd';

	public static final char VARIABLE_CALL = 'e';
	public static final char VARIABLE_DECLARATION = 'f';
	public static final char VARIABLE_INITIALIZATION = 'g';
	public static final char VARIABLE_DECLARATION_AND_INITIALIZATION = 'h';

	public static final char ARRAY_CALL = 'i';
	public static final char ARRAY_DECLARATION = 'j';
	public static final char ARRAY_INITIALIZATION = 'k';
	public static final char ARRAY_DECLARATION_AND_INITIALIZATION = 'l';

	public static final char FUNCTION_CALL = 'm';
	public static final char FUNCTION_DECLARATION = 'n';
	public static final char FUNCTION_INITIALIZATION = 'o';
	public static final char FUNCTION_DECLARATION_AND_INITIALIZATION = 'p';

/*
██       ██████   ██████  █████  ████████ ██  ██████  ███    ██ ███████
██      ██    ██ ██      ██   ██    ██    ██ ██    ██ ████   ██ ██
██      ██    ██ ██      ███████    ██    ██ ██    ██ ██ ██  ██ ███████
██      ██    ██ ██      ██   ██    ██    ██ ██    ██ ██  ██ ██      ██
███████  ██████   ██████ ██   ██    ██    ██  ██████  ██   ████ ███████
*/
	public static final int DATA_TYPE_BRANCH = 0;
	public static final int ARGUMENTS_BRANCH = 0;
	public static final int PARAMETERS_BRANCH = 1;
	public static final int EXPRESSIONS_BRANCH = 2;

	public static final int DATA_TYPE_INDEX = 0;
	public static final int DIMENSION_INDEX = 1;

	private ParserConstants() {}
}
