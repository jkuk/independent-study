import java.util.HashSet;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Lexer {
	private DFA recognizer;

	public static void main(String[] args) {
		try {
			Constants.setDebugMode(args);
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
		recognizer = buildRecognizer(regularExpression);
	}

	private String buildRegularExpression(String tokenTypeFileName)
	throws FileNotFoundException, IOException {
		String regularExpression = parseRegularExpression(tokenTypeFileName);
		Constants.print("ORIGINAL: " + regularExpression);

		regularExpression = expandRanges(regularExpression);
		Constants.print("EXPANDED: " + regularExpression);

		regularExpression = convertToPostfix(regularExpression);
		Constants.print("POSTFIX: " + regularExpression);

		return regularExpression;
	}

	private String parseRegularExpression(String tokenTypeFileName)
	throws FileNotFoundException, IOException {
		BufferedReader tokenTypeFile = new BufferedReader(
			new FileReader(tokenTypeFileName)
		);

		String regularExpression = Constants.EPSILON;
		String line;
		while ((line = tokenTypeFile.readLine()) != null) {
			String splitLine[] = line.split(Constants.TAB + "");
			String expressionWithConcatenateSymbols
			= insertConcatenateSymbols(splitLine[0]);
			regularExpression
			+= expressionWithConcatenateSymbols + Constants.ALTERNATE;
		}
		tokenTypeFile.close();
		return regularExpression.substring(0, regularExpression.length() - 1);
	}

	private String insertConcatenateSymbols(String regularExpression) {
		String expressionWithConcatenateSymbols = "";
		char previousCharacter = regularExpression.charAt(0);
		expressionWithConcatenateSymbols += previousCharacter;
		for (int i = 1; i < regularExpression.length(); i++) {
			char character = regularExpression.charAt(i);
			if (isLetter(previousCharacter) && (isLetter(character))
			|| (previousCharacter == Constants.KLEENE && isLetter(character))
			|| (previousCharacter == Constants.KLEENE && isLeftBracket(character))
			|| (isRightBracket(previousCharacter) && isLeftBracket(character))
			|| (isLetter(previousCharacter) && isLeftBracket(character))
			|| (isRightBracket(previousCharacter) && isLetter(character))) {
				expressionWithConcatenateSymbols += Constants.CONCATENATE;
			}
			expressionWithConcatenateSymbols += character;
			previousCharacter = character;
		}
		return expressionWithConcatenateSymbols;
	}

	private boolean isLetter(char character) {
		return character != Constants.KLEENE
		&& character != Constants.ALTERNATE
		&& character != Constants.RANGE
		&& !isLeftBracket(character)
		&& !isRightBracket(character);
	}

	private boolean isLeftBracket(char character) {
		return character == Constants.LEFT_PARENTHESIS
		|| character == Constants.LEFT_BRACKET;
	}

	private boolean isRightBracket(char character) {
		return character == Constants.RIGHT_PARENTHESIS
		|| character == Constants.RIGHT_BRACKET;
	}

	private String expandRanges(String regularExpression) {
		String expandedExpression = "";
		for (int i = 0; i < regularExpression.length(); i++) {
			if (isRange(regularExpression, i)) {
				String expandedRange = expandRange(regularExpression, i);
				i += 4;
				expandedExpression += expandedRange;
			}
			else {
				expandedExpression += regularExpression.charAt(i);
			}
		}
		return expandedExpression;
	}

	private boolean isRange(String regularExpression, int i) {
		return i + 4 < regularExpression.length()
		&& regularExpression.charAt(i) == Constants.LEFT_BRACKET
		&& regularExpression.charAt(i + 2) == Constants.RANGE
		&& regularExpression.charAt(i + 4) == Constants.RIGHT_BRACKET;
	}

	private String expandRange(String regularExpression, int i) {
		int startCharacter = (int)regularExpression.charAt(i + 1);
		int endCharacter = (int)regularExpression.charAt(i + 3);
		String expandedRange = Constants.LEFT_PARENTHESIS + "";

		for (int j = startCharacter; j < endCharacter; j++) {
			expandedRange += (char)j + "" + Constants.ALTERNATE;
		}
		expandedRange += (char)endCharacter + "" + Constants.RIGHT_PARENTHESIS;

		regularExpression = regularExpression.substring(0, i)
		+ expandedRange + regularExpression.substring(
			i + 4, regularExpression.length()
		);
		return expandedRange;
	}

	private String convertToPostfix(String regularExpression) {
		ArrayDeque<Operator> operatorStack = new ArrayDeque<Operator>();
		String postfixExpression = Constants.EPSILON;

		for (int i = 0; i < regularExpression.length(); i++) {
			Character character = regularExpression.charAt(i);
			if (character == Constants.LEFT_PARENTHESIS) {
				operatorStack.push(new Operator(character));
			}
			else if (character == Constants.RIGHT_PARENTHESIS) {
				while (operatorStack.peek().getCharacter()
				!= Constants.LEFT_PARENTHESIS) {
					postfixExpression += operatorStack.pop();
				}
				operatorStack.pop();
			}
			else {
				Operator input = new Operator(character);
				if (input.isOperator()) {
					while (!operatorStack.isEmpty()
					&& operatorStack.peek().getCharacter() != Constants.LEFT_PARENTHESIS
					&& operatorStack.peek().hasGreaterOrEqualPrecedence(input)) {
						postfixExpression += operatorStack.pop();
					}
					operatorStack.push(input);
				}
				else {
					postfixExpression += character;
				}
			}
		}
		while (!operatorStack.isEmpty()) {
			postfixExpression += operatorStack.pop();
		}
		return postfixExpression;
	}

	private DFA buildRecognizer(String regularExpression) {
		NFA nfa = NFA.build(regularExpression);
		// Constants.print(nfa.toString());
		DFA dfa = DFA.build(nfa);
		// Constants.print(dfa.toString());
		// dfa = dfa.minimize();
		// Constants.print(dfa.toString());
		return dfa;
	}

	public ArrayList<Token> lex(String sourceCodeFileName)
	throws FileNotFoundException, IOException {
		Token token = null;
		ArrayList<Token> tokenList = new ArrayList<Token>();
		CustomReader customReader = new CustomReader(sourceCodeFileName);
		while ((token = getNextToken(customReader)) != null) {
			Constants.print(token.toString());
			tokenList.add(token);
		}
		return tokenList;
	}

	public Token getNextToken(CustomReader customReader) throws IOException {
		String word = customReader.getNextWord();
		if (word == null) {
			return null;
		}
		DFAState dfaState = recognizer.getFirstState();
		Token token = null;
		for (int i = 0, lastValidIndex = 0; i < word.length(); i++) {
			String character = word.charAt(i) + "";
			DFAState previous = dfaState;
			dfaState = dfaState.getTransition(character);
			if (dfaState == null) {
				if (token == null) {
					throw new IllegalArgumentException();
				}
				customReader.rollback(word.substring(lastValidIndex));
				return token;
			}
			if (dfaState.isAcceptingDFAState()) {
				token = new Token(word.substring(0, i + 1), "placeholder");
				lastValidIndex = i + 1;
			}
		}
		return token;
	}
}