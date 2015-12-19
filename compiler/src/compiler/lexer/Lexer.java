package compiler.lexer;
import utility.Helper;

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
			Helper.setDebugMode(args);
			Lexer lexer = new Lexer(args[1]);
			long startTime = System.currentTimeMillis();
			lexer.lex(args[0]);
			long finishTime = System.currentTimeMillis();
			Helper.print("LEX [" + (finishTime - startTime) + "ms]: ");
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
		long startTime = System.currentTimeMillis();
		String regularExpression = parseRegularExpression(tokenTypeFileName);
		long finishTime = System.currentTimeMillis();
		Helper.print("ORIGINAL [" + (finishTime - startTime)
		+ "ms]: " + regularExpression);

		startTime = System.currentTimeMillis();
		regularExpression = expandRanges(regularExpression);
		finishTime = System.currentTimeMillis();
		Helper.print("EXPANDED [" + (finishTime - startTime)
		+ "ms]: " + regularExpression);

		startTime = System.currentTimeMillis();
		regularExpression = convertToPostfix(regularExpression);
		finishTime = System.currentTimeMillis();
		Helper.print("POSTFIX [" + (finishTime - startTime)
		+ "ms]: " + regularExpression);

		return regularExpression;
	}

	private String parseRegularExpression(String tokenTypeFileName)
	throws FileNotFoundException, IOException {
		BufferedReader tokenTypeFile = new BufferedReader(
			new FileReader(tokenTypeFileName)
		);

		String regularExpression = LexerConstants.EPSILON;
		String line;
		while ((line = tokenTypeFile.readLine()) != null) {
			String splitLine[] = line.split(LexerConstants.TAB + "");
			String expressionWithConcatenateSymbols
			= insertConcatenateSymbols(splitLine[0]);
			regularExpression
			+= expressionWithConcatenateSymbols + LexerConstants.ALTERNATE;
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
			|| (previousCharacter == LexerConstants.KLEENE && isLetter(character))
			|| (previousCharacter == LexerConstants.KLEENE && isLeftBracket(character))
			|| (isRightBracket(previousCharacter) && isLeftBracket(character))
			|| (isLetter(previousCharacter) && isLeftBracket(character))
			|| (isRightBracket(previousCharacter) && isLetter(character))) {
				expressionWithConcatenateSymbols += LexerConstants.CONCATENATE;
			}
			expressionWithConcatenateSymbols += character;
			previousCharacter = character;
		}
		return expressionWithConcatenateSymbols;
	}

	private boolean isLetter(char character) {
		return character != LexerConstants.KLEENE
		&& character != LexerConstants.ALTERNATE
		&& character != LexerConstants.RANGE
		&& !isLeftBracket(character)
		&& !isRightBracket(character);
	}

	private boolean isLeftBracket(char character) {
		return character == LexerConstants.LEFT_PARENTHESIS
		|| character == LexerConstants.LEFT_BRACKET;
	}

	private boolean isRightBracket(char character) {
		return character == LexerConstants.RIGHT_PARENTHESIS
		|| character == LexerConstants.RIGHT_BRACKET;
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
		&& regularExpression.charAt(i) == LexerConstants.LEFT_BRACKET
		&& regularExpression.charAt(i + 2) == LexerConstants.RANGE
		&& regularExpression.charAt(i + 4) == LexerConstants.RIGHT_BRACKET;
	}

	private String expandRange(String regularExpression, int i) {
		int startCharacter = (int)regularExpression.charAt(i + 1);
		int endCharacter = (int)regularExpression.charAt(i + 3);
		String expandedRange = LexerConstants.LEFT_PARENTHESIS + "";

		for (int j = startCharacter; j < endCharacter; j++) {
			expandedRange += (char)j + "" + LexerConstants.ALTERNATE;
		}
		expandedRange += (char)endCharacter + ""
		+ LexerConstants.RIGHT_PARENTHESIS;

		regularExpression = regularExpression.substring(0, i)
		+ expandedRange + regularExpression.substring(
			i + 4, regularExpression.length()
		);
		return expandedRange;
	}

	private String convertToPostfix(String regularExpression) {
		ArrayDeque<Operator> operatorStack = new ArrayDeque<Operator>();
		String postfixExpression = LexerConstants.EPSILON;

		for (int i = 0; i < regularExpression.length(); i++) {
			Character character = regularExpression.charAt(i);
			if (character == LexerConstants.LEFT_PARENTHESIS) {
				operatorStack.push(new Operator(character));
			}
			else if (character == LexerConstants.RIGHT_PARENTHESIS) {
				while (operatorStack.peek().getCharacter()
				!= LexerConstants.LEFT_PARENTHESIS) {
					postfixExpression += operatorStack.pop();
				}
				operatorStack.pop();
			}
			else {
				Operator input = new Operator(character);
				if (input.isOperator()) {
					while (!operatorStack.isEmpty()
					&& operatorStack.peek().getCharacter() != LexerConstants.LEFT_PARENTHESIS
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
		long startTime = System.currentTimeMillis();
		NFA nfa = NFA.build(regularExpression);
		long finishTime = System.currentTimeMillis();
		Helper.print("NFA [" + (finishTime - startTime) + "ms]: ");

		startTime = System.currentTimeMillis();
		DFA dfa = DFA.build(nfa);
		finishTime = System.currentTimeMillis();
		Helper.print("DFA [" + (finishTime - startTime) + "ms]: ");
		// Helper.print(dfa.toString());
		// dfa = dfa.minimize();
		// Helper.print(dfa.toString());
		return dfa;
	}

	public ArrayDeque<Token> lex(String sourceCodeFileName)
	throws FileNotFoundException, IOException {
		Token token = null;
		ArrayDeque<Token> tokenQueue = new ArrayDeque<Token>();
		CustomReader customReader = new CustomReader(sourceCodeFileName);
		while ((token = getNextToken(customReader)) != null) {
			tokenQueue.offer(token);
		}
		return tokenQueue;
	}

	public Token getNextToken(CustomReader customReader) throws IOException {
		String lexeme = customReader.getNextLexeme();
		if (lexeme == null) {
			return null;
		}
		DFAState dfaState = recognizer.getFirstState();
		Token token = null;
		for (int i = 0, lastValidIndex = 0; i < lexeme.length(); i++) {
			String character = lexeme.charAt(i) + "";
			DFAState previous = dfaState;
			dfaState = dfaState.getTransition(character);
			if (dfaState == null) {
				if (token == null) {
					throw new IllegalArgumentException();
				}
				customReader.rollback(lexeme.substring(lastValidIndex));
				return token;
			}
			if (dfaState.isAcceptingDFAState()) {
				token = createToken(lexeme.substring(0, i + 1));
				lastValidIndex = i + 1;
			}
		}
		return token;
	}

	public Token createToken(String lexeme) {
		String type;
		if (lexeme.equals(";")) {
			type = "EndOfLine";
		}
		else if (lexeme.equals(":")) {
			type = "AssignmentOperator";
		}
		else if (lexeme.equals("_")) {
			type = "AlphabeticBinaryOperator";
		}
		else if (lexeme.equals("~")) {
			type = "NumericUnaryOperator";
		}
		else if (lexeme.equals("+")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals("-")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals("*")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals("/")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals("%")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals("&")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals("|")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals("#")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals("=")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals("<")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals(">")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals("<=")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals(">=")) {
			type = "NumericBinaryOperator";
		}
		else if (lexeme.equals(".")) {
			type = "ArraySymbol";
		}
		else if (lexeme.equals("[")) {
			type = "OpeningBracket";
		}
		else if (lexeme.equals("]")) {
			type = "ClosingBracket";
		}
		else if (lexeme.equals("^")) {
			type = "ReturnSymbol";
		}
		else if (lexeme.equals("?")) {
			type = "IfSymbol";
		}
		else if (lexeme.equals("!")) {
			type = "WhileSymbol";
		}
		else if (lexeme.charAt(0) == '\'') {
			type = "String";
			lexeme = lexeme.substring(1, lexeme.length() - 1);
		}
		else if (lexeme.charAt(0) == '"') {
			type = "String";
			lexeme = lexeme.substring(1, lexeme.length() - 1);
		}
		else if (Character.isUpperCase(lexeme.charAt(0))) {
			type = "IdentifierUpper";
		}
		else if (Character.isLowerCase(lexeme.charAt(0))) {
			type = "IdentifierLower";
		}
		else if (Character.isDigit(lexeme.charAt(0))) {
			type = "Number";
		}
		else {
			type = "Invalid";
		}

		return new Token(lexeme, type);
	}
}
