// package compiler.parser;
// import compiler.lexer.Token;
//
// import java.util.ArrayDeque;
// import java.util.ArrayList;
// import java.util.HashMap;
//
// import java.util.Deque;
// import java.util.Map;
//
// public class ParserUnitTests {
// 	private static int testNumber = 0;
// 	public static void main(String[] args) {
// 		// printResult(ParserSimpleInvalidTest());
// 		// printResult(ParserSimpleValidTest());
// 		// printResult(ParserComplexInvalidTest());
// 		// printResult(ParserComplexValidTest());
// 		// printResult(ParserActualInvalidTest());
// 		// printResult(ParserActualValidTest());
// 		visualTest();
// 	}
//
// 	public static void visualTest() {
// 		// AbstractSyntaxTree tree;
// 		// Token token;
// 		// Deque<Token> tokenQueue = new ArrayDeque<Token>();
// 		// Parser parser = new Parser("./input/compiler/parser/ComplexGrammar.txt");
// 		//
// 		// token = new Token("1", "Identifier");
// 		// tokenQueue.offer(token);
// 		// token = new Token("2", "Identifier");
// 		// tokenQueue.offer(token);
// 		// token = new Token("3", "Identifier");
// 		// tokenQueue.offer(token);
// 		//
// 		// tree = parser.parse(tokenQueue);
// 		// System.out.println("\nTREE:\n" + tree);
// 		// System.out.println("Symbol Table: " + tree.getSymbolTable());
// 		//
// 		// token = new Token("op1", "Operator");
// 		// tokenQueue.offer(token);
// 		// token = new Token("2", "Identifier");
// 		// tokenQueue.offer(token);
// 		// token = new Token("3", "Identifier");
// 		// tokenQueue.offer(token);
// 		//
// 		// tree = parser.parse(tokenQueue);
// 		// System.out.println("\nTREE:\n" + tree);
// 		// System.out.println("Symbol Table: " + tree.getSymbolTable());
// 		//
// 		// token = new Token("if1", "IfSymbol");
// 		// tokenQueue.offer(token);
// 		// token = new Token("2", "Identifier");
// 		// tokenQueue.offer(token);
// 		// token = new Token("exp3", "Expressions");
// 		// tokenQueue.offer(token);
// 		// token = new Token("return4", "ReturnExpression");
// 		// tokenQueue.offer(token);
// 		//
// 		// tree = parser.parse(tokenQueue);
// 		// System.out.println("\nTREE:\n" + tree);
// 		// System.out.println("Symbol Table: " + tree.getSymbolTable());
// 		//
// 		// token = new Token("while1", "WhileSymbol");
// 		// tokenQueue.offer(token);
// 		// token = new Token("2", "Identifier");
// 		// tokenQueue.offer(token);
// 		// token = new Token("exp3", "Expressions");
// 		// tokenQueue.offer(token);
// 		//
// 		// tree = parser.parse(tokenQueue);
// 		// System.out.println("\nTREE:\n" + tree);
// 		// System.out.println("Symbol Table: " + tree.getSymbolTable());
// 		//
// 		//
// 		// // token = new Token("id1", "Identifier");
// 		// // tokenQueue.offer(token);
// 		// // token = new Token("int2", "DataType");
// 		// // tokenQueue.offer(token);
// 		// // token = new Token("id1", "IdentifierLower");
// 		// // tokenQueue.offer(token);
// 		// //
// 		// // tree = parser.parse(tokenQueue);
// 		// // System.out.println("\nTREE:\n" + tree);
// 		// // System.out.println("Symbol Table: " + tree.getSymbolTable());
// 		//
// 		// token = new Token("1", "Identifier");
// 		// tokenQueue.offer(token);
// 		// token = new Token("int2", "DataType");
// 		// tokenQueue.offer(token);
// 		//
// 		// tree = parser.parse(tokenQueue);
// 		// System.out.println("\nTREE:\n" + tree);
// 		// System.out.println("Symbol Table: " + tree.getSymbolTable());
// 		//
// 		// token = new Token(".1", "ArraySymbol");
// 		// tokenQueue.offer(token);
// 		// token = new Token("id2", "Identifier");
// 		// tokenQueue.offer(token);
// 		// token = new Token("3", "Evaluation");
// 		// tokenQueue.offer(token);
// 		//
// 		// tree = parser.parse(tokenQueue);
// 		// System.out.println("\nTREE:\n" + tree);
// 		// System.out.println("Symbol Table: " + tree.getSymbolTable());
// 		//
// 		// token = new Token("arr1", "Identifier");
// 		// tokenQueue.offer(token);
// 		// token = new Token(".2", "ArraySymbol");
// 		// tokenQueue.offer(token);
// 		// token = new Token("int3", "DataType");
// 		// tokenQueue.offer(token);
// 		// token = new Token("4", "Evaluation");
// 		// tokenQueue.offer(token);
// 		//
// 		// tree = parser.parse(tokenQueue);
// 		// System.out.println("\nTREE:\n" + tree);
// 		// System.out.println("Symbol Table: " + tree.getSymbolTable());
// 		//
// 		// // token = new Token("id1", "Identifier");
// 		// // tokenQueue.offer(token);
// 		// // token = new Token("id2", "IdentifierLower");
// 		// // tokenQueue.offer(token);
// 		// // token = new Token("id3", "IdentifierLower");
// 		// // tokenQueue.offer(token);
// 		// //
// 		// // tree = parser.parse(tokenQueue);
// 		// // System.out.println("\nTREE:\n" + tree);
// 		// // System.out.println("Symbol Table: " + tree.getSymbolTable());
// 		//
// 		// token = new Token("fun1", "Identifier");
// 		// tokenQueue.offer(token);
// 		// token = new Token("int2", "DataType");
// 		// tokenQueue.offer(token);
// 		// token = new Token("par3", "Identifier");
// 		// tokenQueue.offer(token);
// 		// token = new Token("int3.1", "DataType");
// 		// tokenQueue.offer(token);
// 		// token = new Token("exp4", "Expressions");
// 		// tokenQueue.offer(token);
// 		// token = new Token("ret5", "ReturnExpression");
// 		// tokenQueue.offer(token);
// 		//
// 		// tree = parser.parse(tokenQueue);
// 		// System.out.println("\nTREE:\n" + tree);
// 		// System.out.println("Symbol Table: " + tree.getSymbolTable());
// 	}
//
// 	//
// 	// public static boolean ParserSimpleInvalidTest() {
// 	// 	try {
// 	// 		Deque<Token> tokenQueue = new ArrayDeque<Token>();
// 	// 		Token token = new Token("Asdf", "identifierUpper");
// 	// 		tokenQueue.offer(token);
// 	// 		Parser parser = new Parser("./input/compiler/parser/SimpleGrammar.txt");
// 	// 		ParseTree parseTree = parser.parse(tokenQueue);
// 	//
// 	// 		return parseTree == null;
// 	// 	}
// 	// 	catch (Exception e) {
// 	// 		e.printStackTrace();
// 	// 	}
// 	// 	return false;
// 	// }
// 	//
// 	public static boolean ParserSimpleValidTest() {
// 		try {
// 			Deque<Token> tokenQueue = new ArrayDeque<Token>();
// 			Token token = new Token("asdF", "IdentifierLower");
// 			tokenQueue.offer(token);
// 			token = new Token("String", "DataType");
// 			tokenQueue.offer(token);
// 			Parser parser = new Parser("./input/compiler/parser/SimpleGrammar.txt");
// 			AbstractSyntaxTree parseTree = parser.parse(tokenQueue);
//
// 			// ParseTreeNode expectedNode
// 			// = new ParseTreeNode(new Token("asdF", "IdentifierLower"));
// 			// Deque<ParseTreeNode> expectedQueue
// 			// = new ArrayDeque<ParseTreeNode>();
// 			// expectedQueue.offer(expectedNode);
// 			// ParseTree expectedTree = new ParseTree(
// 			// 	new ParseTreeNode(ParserConstants.GOAL, expectedQueue)
// 			// );
//
// 			System.out.println(parseTree);
// 			// System.out.println(expectedTree);
// 			// return parseTree.equals(expectedTree);
// 			return true;
// 		}
// 		catch (Exception e) {
// 			e.printStackTrace();
// 		}
// 		return false;
// 	}
// 	//
// 	// public static boolean ParserComplexInvalidTest() {
// 	// 	try {
// 	// 		Deque<Token> tokenQueue = new ArrayDeque<Token>();
// 	// 		Token token = new Token("asdf", "identifierLower");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		token = new Token("AsdF", "identifierUpper");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		token = new Token("aSdF", "identifierLower");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		token = new Token("aSDFASDF", "identifierLower");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		Parser parser = new Parser("./input/compiler/parser/ComplexGrammar.txt");
// 	// 		ParseTree parseTree = parser.parse(tokenQueue);
// 	// 		ParseTreeNode actualNode = parseTree.getRoot();
// 	//
// 	// 		Deque<ParseTreeNode> expectedQueue
// 	// 		= new ArrayDeque<ParseTreeNode>();
// 	//
// 	// 		ParseTreeNode expectedNode = new ParseTreeNode(
// 	// 			new Terminal("aSDFASDF", "identifierLower")
// 	// 		);
// 	// 		expectedQueue.offer(expectedNode);
// 	//
// 	// 		expectedNode = new ParseTreeNode(
// 	// 			new Terminal("aSdF", "identifierLower"), expectedQueue
// 	// 		);
// 	// 		expectedQueue.offer(expectedNode);
// 	//
// 	// 		expectedNode = new ParseTreeNode(
// 	// 			new Terminal("AsdF", "identifierUpper"), expectedQueue
// 	// 		);
// 	// 		expectedQueue.offer(expectedNode);
// 	//
// 	// 		expectedNode = new ParseTreeNode(
// 	// 			new Terminal("asdf", "identifierLower"), expectedQueue
// 	// 		);
// 	// 		expectedQueue.offer(expectedNode);
// 	//
// 	// 		expectedNode = new ParseTreeNode(
// 	// 			new NonTerminal("Goal"), expectedQueue
// 	// 		);
// 	//
// 	// 		return actualNode.equals(expectedNode);
// 	// 	}
// 	// 	catch (Exception e) {
// 	// 		e.printStackTrace();
// 	// 	}
// 	// 	return false;
// 	// }
// 	//
// 	// public static boolean ParserComplexValidTest() {
// 	// 	try {
// 	// 		Deque<Token> tokenQueue = new ArrayDeque<Token>();
// 	// 		Token token = new Token("asdf", "identifierLower");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		token = new Token("AsdF", "identifierUpper");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		token = new Token("aSdF", "identifierLower");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		token = new Token("aSDFASDF", "identifierLower");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		Parser parser = new Parser("./input/compiler/parser/ComplexGrammar.txt");
// 	// 		ParseTree parseTree = parser.parse(tokenQueue);
// 	//
// 	// 		ParseTreeNode expectedNode
// 	// 		= new ParseTreeNode(new NonTerminal("Identifiers"));
// 	//
// 	// 		ParseTreeNode parentNode = expectedNode;
// 	// 		ParseTreeNode childNode
// 	// 		= new ParseTreeNode(new Token("aSDFASDF", "identifierLower"));
// 	// 		parentNode.getChildren().add(childNode);
// 	// 		childNode = new ParseTreeNode(new NonTerminal("Identifiers"));
// 	// 		parentNode.getChildren().add(childNode);
// 	//
// 	// 		parentNode = childNode;
// 	// 		childNode
// 	// 		= new ParseTreeNode(new Token("aSdF", "identifierLower"));
// 	// 		parentNode.getChildren().add(childNode);
// 	// 		childNode = new ParseTreeNode(new NonTerminal("Identifiers"));
// 	// 		parentNode.getChildren().add(childNode);
// 	//
// 	// 		parentNode = childNode;
// 	// 		childNode
// 	// 		= new ParseTreeNode(new Token("AsdF", "identifierUpper"));
// 	// 		parentNode.getChildren().add(childNode);
// 	// 		childNode = new ParseTreeNode(new NonTerminal("Identifiers"));
// 	// 		parentNode.getChildren().add(childNode);
// 	//
// 	// 		parentNode = childNode;
// 	// 		childNode
// 	// 		= new ParseTreeNode(new Token("asdf", "identifierLower"));
// 	// 		parentNode.getChildren().add(childNode);
// 	//
// 	// 		ParseTree expectedTree = new ParseTree(expectedNode);
// 	// 		System.out.println(parseTree);
// 	// 		System.out.println(expectedTree);
// 	//
// 	// 		return parseTree.equals(expectedTree);
// 	// 	}
// 	// 	catch (Exception e) {
// 	// 		e.printStackTrace();
// 	// 	}
// 	// 	return false;
// 	// }
//
// 	// public static boolean ParserComplexValidTest() {
// 	// 	try {
// 	// 		Deque<Token> tokenQueue = new ArrayDeque<Token>();
// 	// 		Token token = new Token("asdf", "identifierLower");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		token = new Token("AsdF", "identifierUpper");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		token = new Token("aSdF", "identifierLower");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		token = new Token("aSDFASDF", "identifierLower");
// 	// 		tokenQueue.offer(token);
// 	//
// 	// 		Parser parser = new Parser("./input/compiler/parser/ComplexGrammar.txt");
// 	// 		ParseTree parseTree = parser.parse(tokenQueue);
// 	//
// 	// 		ParseTreeNode expectedNode
// 	// 		= new ParseTreeNode(new NonTerminal("Goal"));
// 	//
// 	// 		ParseTreeNode parentNode = expectedNode;
// 	// 		ParseTreeNode childNode
// 	// 		= new ParseTreeNode(new Token("asdf", "identifierLower"));
// 	// 		parentNode.getChildren().add(childNode);
// 	//
// 	// 		parentNode = childNode;
// 	// 		childNode
// 	// 		= new ParseTreeNode(new Token("AsdF", "identifierUpper"));
// 	// 		parentNode.getChildren().add(childNode);
// 	//
// 	// 		parentNode = childNode;
// 	// 		childNode
// 	// 		= new ParseTreeNode(new Token("aSdF", "identifierLower"));
// 	// 		parentNode.getChildren().add(childNode);
// 	//
// 	// 		parentNode = childNode;
// 	// 		childNode
// 	// 		= new ParseTreeNode(new Token("aSDFASDF", "identifierLower"));
// 	// 		parentNode.getChildren().add(childNode);
// 	//
// 	// 		ParseTree expectedTree = new ParseTree(expectedNode);
// 	//
// 	// 		return parseTree.equals(expectedTree);
// 	// 	}
// 	// 	catch (Exception e) {
// 	// 		e.printStackTrace();
// 	// 	}
// 	// 	return false;
// 	// }
// 	//
// 	// public static boolean ParserActualInvalidTest() {
// 	// 	return false;
// 	// }
// 	//
// 	// public static boolean ParserActualValidTest() {
// 	// 	return false;
// 	// }
//
// 	public static void printResult(boolean succeeded) {
// 		System.out.println("Test #" + testNumber++ + " " + succeeded);
// 	}
// }
