package parser;
import lexer.Token;
import parser.Symbol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Deque;
import java.util.Map;

public class ParserUnitTests {
	private static int testNumber = 0;
	public static void main(String[] args) {
		// printResult(ParserSimpleInvalidTest());
		// printResult(ParserSimpleValidTest());
		// printResult(ParserComplexInvalidTest());
		printResult(ParserComplexValidTest());
		// printResult(ParserActualInvalidTest());
		// printResult(ParserActualValidTest());
	}

	public static boolean ParserSimpleInvalidTest() {
		try {
			Deque<Token> tokenQueue = new ArrayDeque<Token>();
			Token token = new Token("Asdf", "identifierUpper");
			tokenQueue.offer(token);
			Parser parser = new Parser("./input/parser/SimpleGrammar.txt");
			ParseTree parseTree = parser.parse(tokenQueue);

			return parseTree == null;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static boolean ParserSimpleValidTest() {
		try {
			Deque<Token> tokenQueue = new ArrayDeque<Token>();
			Token token = new Token("asdF", "identifierLower");
			tokenQueue.offer(token);
			Parser parser = new Parser("./input/parser/SimpleGrammar.txt");
			ParseTree parseTree = parser.parse(tokenQueue);

			ParseTreeNode expectedNode
			= new ParseTreeNode(new Token("asdF", "identifierLower"));
			Deque<ParseTreeNode> expectedQueue
			= new ArrayDeque<ParseTreeNode>();
			expectedQueue.offer(expectedNode);
			ParseTree expectedTree = new ParseTree(
				new ParseTreeNode(ParserConstants.GOAL, expectedQueue)
			);

			System.out.println(parseTree);
			System.out.println(expectedTree);
			return parseTree.equals(expectedTree);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	// 
	// public static boolean ParserComplexInvalidTest() {
	// 	try {
	// 		Deque<Token> tokenQueue = new ArrayDeque<Token>();
	// 		Token token = new Token("asdf", "identifierLower");
	// 		tokenQueue.offer(token);
	// 
	// 		token = new Token("AsdF", "identifierUpper");
	// 		tokenQueue.offer(token);
	// 
	// 		token = new Token("aSdF", "identifierLower");
	// 		tokenQueue.offer(token);
	// 
	// 		token = new Token("aSDFASDF", "identifierLower");
	// 		tokenQueue.offer(token);
	// 
	// 		Parser parser = new Parser("./input/parser/ComplexGrammar.txt");
	// 		ParseTree parseTree = parser.parse(tokenQueue);
	// 		ParseTreeNode actualNode = parseTree.getRoot();
	// 
	// 		Deque<ParseTreeNode> expectedQueue
	// 		= new ArrayDeque<ParseTreeNode>();
	// 
	// 		ParseTreeNode expectedNode = new ParseTreeNode(
	// 			new Terminal("aSDFASDF", "identifierLower")
	// 		);
	// 		expectedQueue.offer(expectedNode);
	// 
	// 		expectedNode = new ParseTreeNode(
	// 			new Terminal("aSdF", "identifierLower"), expectedQueue
	// 		);
	// 		expectedQueue.offer(expectedNode);
	// 
	// 		expectedNode = new ParseTreeNode(
	// 			new Terminal("AsdF", "identifierUpper"), expectedQueue
	// 		);
	// 		expectedQueue.offer(expectedNode);
	// 
	// 		expectedNode = new ParseTreeNode(
	// 			new Terminal("asdf", "identifierLower"), expectedQueue
	// 		);
	// 		expectedQueue.offer(expectedNode);
	// 
	// 		expectedNode = new ParseTreeNode(
	// 			new NonTerminal("Goal"), expectedQueue
	// 		);
	// 
	// 		return actualNode.equals(expectedNode);
	// 	}
	// 	catch (Exception e) {
	// 		e.printStackTrace();
	// 	}
	// 	return false;
	// }
	// 
	public static boolean ParserComplexValidTest() {
		try {
			Deque<Token> tokenQueue = new ArrayDeque<Token>();
			Token token = new Token("asdf", "identifierLower");
			tokenQueue.offer(token);

			token = new Token("AsdF", "identifierUpper");
			tokenQueue.offer(token);

			token = new Token("aSdF", "identifierLower");
			tokenQueue.offer(token);

			token = new Token("aSDFASDF", "identifierLower");
			tokenQueue.offer(token);

			Parser parser = new Parser("./input/parser/ComplexGrammar.txt");
			ParseTree parseTree = parser.parse(tokenQueue);

			ParseTreeNode expectedNode
			= new ParseTreeNode(new NonTerminal("Identifiers"));

			ParseTreeNode parentNode = expectedNode;
			ParseTreeNode childNode
			= new ParseTreeNode(new Token("aSDFASDF", "identifierLower"));
			parentNode.getChildren().add(childNode);
			childNode = new ParseTreeNode(new NonTerminal("Identifiers"));
			parentNode.getChildren().add(childNode);
			
			parentNode = childNode;
			childNode
			= new ParseTreeNode(new Token("aSdF", "identifierLower"));
			parentNode.getChildren().add(childNode);
			childNode = new ParseTreeNode(new NonTerminal("Identifiers"));
			parentNode.getChildren().add(childNode);

			parentNode = childNode;
			childNode
			= new ParseTreeNode(new Token("AsdF", "identifierUpper"));
			parentNode.getChildren().add(childNode);
			childNode = new ParseTreeNode(new NonTerminal("Identifiers"));
			parentNode.getChildren().add(childNode);

			parentNode = childNode;
			childNode
			= new ParseTreeNode(new Token("asdf", "identifierLower"));
			parentNode.getChildren().add(childNode);

			ParseTree expectedTree = new ParseTree(expectedNode);
			System.out.println(parseTree);
			System.out.println(expectedTree);

			return parseTree.equals(expectedTree);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	// public static boolean ParserComplexValidTest() {
	// 	try {
	// 		Deque<Token> tokenQueue = new ArrayDeque<Token>();
	// 		Token token = new Token("asdf", "identifierLower");
	// 		tokenQueue.offer(token);
	// 
	// 		token = new Token("AsdF", "identifierUpper");
	// 		tokenQueue.offer(token);
	// 
	// 		token = new Token("aSdF", "identifierLower");
	// 		tokenQueue.offer(token);
	// 
	// 		token = new Token("aSDFASDF", "identifierLower");
	// 		tokenQueue.offer(token);
	// 
	// 		Parser parser = new Parser("./input/parser/ComplexGrammar.txt");
	// 		ParseTree parseTree = parser.parse(tokenQueue);
	// 
	// 		ParseTreeNode expectedNode
	// 		= new ParseTreeNode(new NonTerminal("Goal"));
	// 
	// 		ParseTreeNode parentNode = expectedNode;
	// 		ParseTreeNode childNode
	// 		= new ParseTreeNode(new Token("asdf", "identifierLower"));
	// 		parentNode.getChildren().add(childNode);
	// 
	// 		parentNode = childNode;
	// 		childNode
	// 		= new ParseTreeNode(new Token("AsdF", "identifierUpper"));
	// 		parentNode.getChildren().add(childNode);
	// 
	// 		parentNode = childNode;
	// 		childNode
	// 		= new ParseTreeNode(new Token("aSdF", "identifierLower"));
	// 		parentNode.getChildren().add(childNode);
	// 
	// 		parentNode = childNode;
	// 		childNode
	// 		= new ParseTreeNode(new Token("aSDFASDF", "identifierLower"));
	// 		parentNode.getChildren().add(childNode);
	// 
	// 		ParseTree expectedTree = new ParseTree(expectedNode);
	// 
	// 		return parseTree.equals(expectedTree);
	// 	}
	// 	catch (Exception e) {
	// 		e.printStackTrace();
	// 	}
	// 	return false;
	// }
	// 
	// public static boolean ParserActualInvalidTest() {
	// 	return false;
	// }
	// 
	// public static boolean ParserActualValidTest() {
	// 	return false;
	// }

	public static void printResult(boolean succeeded) {
		System.out.println("Test #" + testNumber++ + " " + succeeded);
	}
}