import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Lexer {
	private char LEFT_PARENTHESIS = '(';
	private char RIGHT_PARENTHESIS = ')';
	private char LEFT_BRACKET = '[';
	private char RIGHT_BRACKET = ']';
	private char RANGE = '-';
	private char CONCATENATE = ' ';
	private char ALTERNATE = '|';
	private char KLEENE = '*';
	private String EPSILON = "";
	private char TAB = '\t';
	private char ESCAPE_CHARACTER = '\\';

	private HashSet<Character> alphabet;
	private DFA recognizer;
	private int id;

	private static boolean debugMode;

	public static void main(String[] args) {
		try {
			setDebugMode(args);
			Lexer lexer = new Lexer(args[1]);
			lexer.lex(args[0]);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Lexer(String tokenTypeFileName)
	throws FileNotFoundException, IOException {
		String regularExpression = buildRegularExpression(tokenTypeFileName);
		id = 0;
		recognizer = buildRecognizer(regularExpression);
	}

	private String buildRegularExpression(String tokenTypeFileName)
	throws FileNotFoundException, IOException {
		String regularExpression = parseRegularExpression(tokenTypeFileName);
		print("ORIGINAL: " + regularExpression);

		regularExpression = expandRanges(regularExpression);
		print("EXPANDED: " + regularExpression);

		regularExpression = convertToPostfix(regularExpression);
		print("POSTFIX: " + regularExpression);

		return regularExpression;
	}

	private String parseRegularExpression(String tokenTypeFileName)
	throws FileNotFoundException, IOException {
		BufferedReader tokenTypeFile = new BufferedReader(
			new FileReader(tokenTypeFileName)
		);

		String regularExpression = EPSILON;
		String line;
		while ((line = tokenTypeFile.readLine()) != null) {
			String splitLine[] = line.split(TAB + "");
			regularExpression += splitLine[0] + ALTERNATE;
		}
		tokenTypeFile.close();
		return regularExpression.substring(0, regularExpression.length() - 1);
	}

	private String expandRanges(String regularExpression) {
		String expandedExpression = "";
		for (int i = 0; i < regularExpression.length(); i++) {
			if (i + 4 < regularExpression.length()
			&& regularExpression.charAt(i) == LEFT_BRACKET
			&& regularExpression.charAt(i + 2) == RANGE
			&& regularExpression.charAt(i + 4) == RIGHT_BRACKET) {

				int startCharacter = (int)regularExpression.charAt(i + 1);
				int endCharacter = (int)regularExpression.charAt(i + 3);
				String expandedRange = LEFT_PARENTHESIS + "";

				for (int j = startCharacter; j < endCharacter; j++) {
					expandedRange += (char)j + "" + ALTERNATE;
				}
				expandedRange += (char)endCharacter + "" + RIGHT_PARENTHESIS;

				regularExpression = regularExpression.substring(0, i)
				+ expandedRange + regularExpression.substring(
					i + 4, regularExpression.length()
				);
				i += expandedRange.length();
				expandedExpression += expandedRange;
			}
			else {
				expandedExpression += regularExpression.charAt(i);
			}
		}
		return expandedExpression;
	}

	private String convertToPostfix(String regularExpression) {
		ArrayDeque<Operator> operatorStack = new ArrayDeque<Operator>();
		Character previousCharacter = null;
		String postfixExpression = EPSILON;

		for (int i = 0; i < regularExpression.length(); i++) {
			Character character = regularExpression.charAt(i);
			if (character == LEFT_PARENTHESIS) {
				operatorStack.push(new Operator(character, previousCharacter));
			}
			else if (character == RIGHT_PARENTHESIS) {
				while (operatorStack.peek().getCharacter()
				!= LEFT_PARENTHESIS) {
					postfixExpression += operatorStack.pop();
				}
				operatorStack.pop();
			}
			else {
				Operator input = new Operator(character, previousCharacter);
				if (input.isOperator()) {
					while (!operatorStack.isEmpty()
					&& operatorStack.peek().getCharacter() != LEFT_PARENTHESIS
					&& operatorStack.peek().hasGreaterOrEqualPrecedence(input)) {
						postfixExpression += operatorStack.pop();
					}
					if (input.getCharacter() == CONCATENATE) {
						postfixExpression += character;
					}
					operatorStack.push(input);
				}
				else {
					postfixExpression += character;
				}
			}
			previousCharacter = character;
		}
		while (!operatorStack.isEmpty()) {
			postfixExpression += operatorStack.pop();
		}
		return postfixExpression;
	}

	private DFA buildRecognizer(String regularExpression) {
		alphabet = new HashSet<Character>();
		NFA nfa = buildNFA(regularExpression);
		DFA dfa = convertToDFA(nfa);
		// dfa = minimizeDFA();
		return dfa;
	}

	private NFA buildNFA(String postfixExpression) {
		ArrayDeque<NFA> nfaStack = new ArrayDeque<NFA>();
		Character character = null;
		Character previousCharacter = null;
		for (int i = 0; i < postfixExpression.length(); i++) {
			character = postfixExpression.charAt(i);
			Operator input = new Operator(character);
			if (character == ESCAPE_CHARACTER) {
				character = postfixExpression.charAt(++i);
				nfaStack.push(generateCharacterNFA(character));
			}
			else if (input.isOperator()) {
				nfaStack.push(
					generateOperatorNFA(character, nfaStack)
				);
			}
			else {
				nfaStack.push(generateCharacterNFA(character));
			}
			previousCharacter = character;
		}
		nfaStack.peek().getLastState().makeAcceptingState();
		return nfaStack.pop();
	}

	private NFA generateOperatorNFA(Character operator, ArrayDeque<NFA> nfaStack) {
		if (operator == KLEENE) {
			return kleene(nfaStack.pop());
		}
		else if (operator == CONCATENATE) {
			return concatenate(nfaStack.pop(), nfaStack.pop());
		}
		else {
			return alternate(nfaStack.pop(), nfaStack.pop());
		}
	}

	private NFA concatenate(NFA b, NFA a) {
		a.getLastState().addTransition(EPSILON, b.getFirstState());
		return a;
	}

	private NFA kleene(NFA a) {
		State firstState = new State(id++);
		State lastState = new State(id++);

		firstState.addTransition(EPSILON, a.getFirstState());
		firstState.addTransition(EPSILON, lastState);
		a.getLastState().addTransition(EPSILON, lastState);
		a.getLastState().addTransition(EPSILON, a.getFirstState());

		a.addFirstState(firstState);
		a.addLastState(lastState);
		return a;
	}

	private NFA alternate(NFA b, NFA a) {
		State firstState = new State(id++);
		State lastState = new State(id++);

		firstState.addTransition(EPSILON, a.getFirstState());
		firstState.addTransition(EPSILON, b.getFirstState());
		a.getLastState().addTransition(EPSILON, lastState);
		b.getLastState().addTransition(EPSILON, lastState);

		a.addFirstState(firstState);
		a.addLastState(lastState);
		return a;
	}

	private NFA generateCharacterNFA(Character character) {
		alphabet.add(character);

		State firstState = new State(id++);
		State lastState = new State(id++);

		firstState.addTransition(character + "", lastState);

		NFA nfa = new NFA(firstState, lastState);
		return nfa;
	}

	private HashSet<State> closure(String character, HashSet<State> stateSet) {
		HashSet<State> reachableStateSet = new HashSet<State>();
		ArrayDeque<State> uncheckedStateStack = new ArrayDeque<State>();
		uncheckedStateStack.addAll(stateSet);

		while (!uncheckedStateStack.isEmpty()) { // starts as for each
			State startState = uncheckedStateStack.pop();
			State transitionState = startState.getTransition(character);
			if (transitionState != null // if there is a transition
			&& !reachableStateSet.contains(transitionState)) { // and the list of reachable states does not contain the state
				reachableStateSet.add(transitionState); // add it
				uncheckedStateStack.push(transitionState); // and add it to the list of states to check
			}
		}
		return reachableStateSet;
	}

	private DFA convertToDFA(NFA nfa) {
		DFA dfaTransitionTable = new DFA();

		HashSet<State> dfaState = new HashSet<State>();
		dfaState.add(nfa.getFirstState());
		closure(EPSILON, dfaState);
		ArrayDeque<HashSet<State>> uncheckedDFAStateStack
		= new ArrayDeque<HashSet<State>>();
		uncheckedDFAStateStack.push(dfaState);
		HashSet<HashSet<State>> checkedDFAStateSet = new HashSet<HashSet<State>>();

		while (!uncheckedDFAStateStack.isEmpty()) {
			HashSet<State> uncheckedDFAState = uncheckedDFAStateStack.pop();
			for (Character character : alphabet) {
				dfaState = closure(character + "", uncheckedDFAState);
				dfaState = closure(EPSILON, dfaState);

				dfaTransitionTable.put(uncheckedDFAState, character + "", dfaState);
				if (!checkedDFAStateSet.contains(dfaState)) {
					uncheckedDFAStateStack.push(dfaState);
				}
				checkedDFAStateSet.add(uncheckedDFAState);
			}
		}
		return dfaTransitionTable;
	}

	public ArrayList<String> lex(String sourceCodeFileName)
	throws FileNotFoundException, IOException {
		BufferedReader bufferedReader = new BufferedReader(
			new FileReader(sourceCodeFileName)
		);
		return new ArrayList<String>();
	}

	private static void setDebugMode(String[] args) {
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

	private void print(String string) {
		if (debugMode) {
			System.out.println("\t====>\n" + string + "\n<====\n");
		}
	}
}