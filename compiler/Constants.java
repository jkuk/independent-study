public final class Constants {
	public static final char LEFT_PARENTHESIS = '(';
	public static final char RIGHT_PARENTHESIS = ')';
	public static final char LEFT_BRACKET = '[';
	public static final char RIGHT_BRACKET = ']';
	public static final char RANGE = '-';
	public static final char CONCATENATE = ' ';
	public static final char ALTERNATE = '|';
	public static final char KLEENE = '*';
	public static final String EPSILON = "";
	public static final char TAB = '\t';
	public static final char ESCAPE_CHARACTER = '\\';
	public static final int NON_OPERATOR = 0;

	private static boolean debugMode;

	private Constants() {}

	public static void setDebugMode(String[] args) {
		debugMode = false;
		if (args.length >= 3) {
			for (String arg : args) {
				if (arg.equals("-verbose")) {
					debugMode = true;
					break;
				}
			}
		}
	}

	public static void print(String string) {
		if (debugMode) {
			System.out.println("=> " + string + "<=\n");
		}
	}
}