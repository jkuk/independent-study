package compiler;

public class Helper {
	private static boolean debugMode;

	private Helper() {}

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