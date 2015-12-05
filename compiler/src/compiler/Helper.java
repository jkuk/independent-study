package compiler;

public class Helper {
	private static boolean debugMode;
	private static int depth = 0;

	private Helper() {}

	public static void setDebugMode(String[] args) {
		setDebugMode(false);
		for (String arg : args) {
			if (arg.equals("-verbose")) {
				setDebugMode(true);
				break;
			}
		}
	}

	public static void setDebugMode(boolean mode) {
		debugMode = mode;
	}

	public static void print(String string) {
		if (debugMode) {
			System.out.println("=>" + string + "<=\n");
		}
	}

	public static void printEntering(String function) {
		printFunction("<<ENTERING", function);
		depth++;
	}

	public static void printExiting(String function) {
		printFunction(function, "EXITING>>");
		depth--;
	}

	public static void printFunction(String first, String second) {
		if (debugMode) {
			String string = "";
			for (int i = 0; i < depth; i++) {
				string += "  ";
			}
			string += first + " " + second;
			System.out.println(string);
		}
	}

	public static void resetDepth() {
		depth = 0;
	}
}
