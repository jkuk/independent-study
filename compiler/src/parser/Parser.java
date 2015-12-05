package parser;
import parser.abstractSyntaxTree.*;
import parser.parseTree.*;
import parser.ParserConstants;
import lexer.Token;
import compiler.Helper;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Parser {
	private Map<NonTerminal, ArrayList<Handle>> productionMap;
	private Map<NonTerminal, HashMap<Handle, String>>
	abstractSyntaxTreeReductionTable;
	private Set<ParserState> canonicalCollection;
	private ParserState initialState;
	private Map<ParserState, HashMap<Symbol, Action>> actionTable;
	private Map<ParserState, HashMap<Symbol, ParserState>>
	parserStateTransitionMap;

	private Deque<ParseTreeNode> parseTreeNodeStack;
	private Deque<ParserState> parserStateStack;
	private ParseTreeNode lookAheadNode;
	private ParserState parserState;
	private Deque<LinkedList<Node>>
	abstractSyntaxTreeNodeListStack;

	public Parser(String grammarFileName) {
		try {
			Helper.setDebugMode(true);
			Helper.resetDepth();
			initializeProductionMapAndAbstractSyntaxTreeReductionTable(
				grammarFileName
			);
			Helper.print("Production map: " + productionMap);
			Helper.print(
				"Abstract syntax tree node reduction table: "
				+ abstractSyntaxTreeReductionTable
			);

			initializeInitialState();
			Helper.print("Initial State: " + initialState);

			initializeCanonicalCollection();
			Helper.print("Canonical Collection: " + canonicalCollection);
			Helper.print(
				"Parser State Transition Map: " + parserStateTransitionMap
			);

			initializeActionTable();
			Helper.print("Action table: " + actionTable);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeProductionMapAndAbstractSyntaxTreeReductionTable(
		String grammarFileName
	)
	throws FileNotFoundException, IOException {
		Helper.printEntering(
			"initializeProductionMapAndAbstractSyntaxTreeReductionTable"
		);
		BufferedReader grammarFile = new BufferedReader(
			new FileReader(grammarFileName)
		);
		productionMap
		= new HashMap<NonTerminal, ArrayList<Handle>>();
		abstractSyntaxTreeReductionTable
		= new HashMap<NonTerminal, HashMap<Handle, String>>();

		String line;
		while ((line = grammarFile.readLine()) != null) {
			if (line.trim().isEmpty()) {
				continue;
			}
			String[] splitLine = line.split("->|\\|");
			NonTerminal reduction = new NonTerminal(splitLine[0]);
			ArrayList<Handle> handleList = new ArrayList<Handle>();
			for (int i = 1; i < splitLine.length; i++) {
				Handle handle = new Handle(splitLine[i]);
				handleList.add(handle);
			}
			productionMap.put(reduction, handleList);

			line = grammarFile.readLine();
			splitLine = line.split(":|\\|");
			HashMap<Handle, String> handleInstructionMap
			= new HashMap<Handle, String>();
			abstractSyntaxTreeReductionTable.put(
				reduction, handleInstructionMap
			);
			for (int i = 1; i < splitLine.length; i++) {
				if (!splitLine[i].trim().equals("null")) {
					handleInstructionMap.put(handleList.get(i - 1),
					splitLine[i].trim());
				}
			}
		}
		Helper.printExiting(
			"initializeProductionMapAndAbstractSyntaxTreeReductionTable"
		);
	}

	private void initializeInitialState() {
		Helper.printEntering("initializeInitialState");
		initialState = new ParserState(
			ParserConstants.GOAL,
			productionMap.get(ParserConstants.GOAL),
			ParserConstants.EOF
		).buildClosure(productionMap);
		Helper.printExiting("initializeInitialState");
	}

	private void initializeCanonicalCollection() {
		Helper.printEntering("initializeCanonicalCollection");
		canonicalCollection = new HashSet<ParserState>();
		canonicalCollection.add(initialState);

		Deque<ParserState> unmarkedParserStateStack
		= new ArrayDeque<ParserState>();
		unmarkedParserStateStack.push(initialState);

		parserStateTransitionMap
		= new HashMap<ParserState, HashMap<Symbol, ParserState>>();

		initializeCanonicalCollection(unmarkedParserStateStack);
		Helper.printExiting("initializeCanonicalCollection");
	}

	private void initializeCanonicalCollection(
		Deque<ParserState> unmarkedParserStateStack
	) {
		Helper.printEntering("initializeCanonicalCollection");
		int previousSize = 0;
		while (canonicalCollection.size() != previousSize) {
			previousSize = canonicalCollection.size();
			while (!unmarkedParserStateStack.isEmpty()) {
				ParserState parserState = unmarkedParserStateStack.pop();

				for (Symbol symbol : buildNextSymbolSet(parserState)) {
					addParserStateToCanonicalCollection(
						unmarkedParserStateStack,
						parserState,
						symbol
					);
				}
			}
		}
		Helper.printExiting("initializeCanonicalCollection");
	}

	private Set<Symbol> buildNextSymbolSet(ParserState parserState) {
		Helper.printEntering("buildNextSymbolSet");
		Set<Symbol> nextSymbolSet = new HashSet<Symbol>();
		for (Item item : parserState.getItemSet()) {
			if (item.getNextSymbol() == null) {
				nextSymbolSet.add(item.getLookAheadSymbol());
			}
			else {
				nextSymbolSet.add(item.getNextSymbol());
			}
		}
		Helper.printExiting("buildnextSymbolSet");
		return nextSymbolSet;
	}

	private void addParserStateToCanonicalCollection(
		Deque<ParserState> unmarkedParserStateStack,
		ParserState parserState,
		Symbol symbol
	) {
		Helper.printEntering("addParserStateToCanonicalCollection");
		ParserState destinationState
		= parserState.transition(symbol, productionMap);
		if (canonicalCollection.add(destinationState)) {
			unmarkedParserStateStack.push(destinationState);
		}
		if (parserStateTransitionMap.get(parserState) == null) {
			parserStateTransitionMap.put(
				parserState, new HashMap<Symbol, ParserState>()
			);
		}
		parserStateTransitionMap.get(parserState).put(symbol, destinationState);
		Helper.printExiting("addParserStateToCanonicalCollection");
	}

	private void initializeActionTable() {
		Helper.printEntering("initializeActionTable");
		actionTable	= new HashMap<ParserState, HashMap<Symbol, Action>>();
		for (ParserState parserState : canonicalCollection) {
			for (Item item : parserState.getItemSet()) {
				addActionToActionTable(parserState, item);
			}
		}
		Helper.printExiting("initializeActionTable");
	}

	private void addActionToActionTable(ParserState parserState, Item item) {
		Helper.printEntering("addActionToActionTable");
		HashMap<Symbol, Action> symbolActionMap = actionTable.get(parserState);
		if (symbolActionMap == null) {
			symbolActionMap = new HashMap<Symbol, Action>();
			actionTable.put(parserState, symbolActionMap);
		}

		Symbol nextSymbol = item.getNextSymbol();
		/*
		List<Symbol> suffix = item.getSuffix();
		int i = 0;
		while (nextSymbol.equals(ParserConstants.EPSILON)
		&& i < suffix.size()) {
			nextSymbol = suffix.get(i++);
		}
		if (nextSymbol.equals(ParserConstants.EPSILON)) {
			nextSymbol = item.getLookAheadSymbol();
		}*/
		if (nextSymbol == null/*
		|| nextSymbol.equals(ParserConstants.EPSILON)
		|| nextSymbol.equals(ParserConstants.EOF)*/) {
			Symbol lookAheadSymbol = item.getLookAheadSymbol();
			if (item.getReduction().equals(ParserConstants.GOAL)) {
				if (lookAheadSymbol.equals(ParserConstants.EOF)) {
					symbolActionMap.put(
						ParserConstants.EOF,
						new Accept(item.getReduction(), item.getHandle())
					);
				}
			}
			else {
				symbolActionMap.put(
					lookAheadSymbol,
					new Reduce(item.getReduction(), item.getHandle())
				);
			}
		}
		else if (nextSymbol instanceof Terminal) {
			ParserState destinationState
			= parserStateTransitionMap.get(parserState).get(nextSymbol);
			if (destinationState != null) {
				// List<Symbol> suffix = item.getSuffix();
				// int i = 0;
				// while (nextSymbol.equals(ParserConstants.EPSILON)
				// && i < suffix.size()) {
				// 	nextSymbol = suffix.get(i++);
				// }
				// if (nextSymbol.equals(ParserConstants.EPSILON)) {
				// 	nextSymbol = item.getLookAheadSymbol();
				// }
				// if (nextSymbol.equals(ParserConstants.EPSILON)
				// || nextSymbol.equals(ParserConstants.EOF)) {
				// 	symbolActionMap.put(ParserConstants.EOF, new Reduce(item.getReduction(), item.getHandle()));
				// }
				// else {
					symbolActionMap.put(nextSymbol, new Shift(destinationState));
				// }
			}
		}
		else {
			ParserState destinationState
			= parserStateTransitionMap.get(parserState).get(nextSymbol);
			if (destinationState != null) {

				symbolActionMap.put(nextSymbol, new Shift(destinationState));
			}
		}
		Helper.printExiting("addActionToActionTable");
	}

	public AbstractSyntaxTree parse(Deque<Token> tokenQueue) {
		Helper.printEntering("parse");
		parseTreeNodeStack = new ArrayDeque<ParseTreeNode>();
		parserStateStack = new ArrayDeque<ParserState>();
		abstractSyntaxTreeNodeListStack =
		new ArrayDeque<LinkedList<Node>>();

		parserStateStack.push(initialState);
		lookAheadNode = buildNextParseTreeNode(tokenQueue);

		int counter = 0;
		Action action = null;
		while (!(action instanceof Accept)) {
			parserState = parserStateStack.peek();
			action
			= actionTable.get(parserState).get(lookAheadNode.getSymbol());
			// Helper.print("ACTION STUFF");
			// Helper.print(parserState + "");
			// Helper.print(actionTable.get(parserState) + "");
			// Helper.print(lookAheadNode.getSymbol() + "");
			// Helper.print(action + "");
			try {
				takeAction(tokenQueue, action);
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			}
		}
		Reduce reduceAction = (Reduce)action;
		reduce(reduceAction.getReduction(), reduceAction.getHandle());
		Helper.printExiting("parse");
		Helper.print("Parse Tree: " + new ParseTree(parseTreeNodeStack.pop()));
		return new AbstractSyntaxTree(abstractSyntaxTreeNodeListStack.pop());
	}

	private ParseTreeNode buildNextParseTreeNode(Deque<Token> tokenQueue) {
		Helper.printEntering("buildNextParseTreeNode");
		Helper.printExiting("buildNextParseTreeNode");
		return tokenQueue.peek() == null ?
		new ParseTreeNode(ParserConstants.EOF)
		: new ParseTreeNode(tokenQueue.poll());
	}

	private void takeAction(Deque<Token> tokenQueue, Action action) {
		Helper.printEntering("takeAction");
		// Helper.print(abstractSyntaxTreeNodeListStack.toString());
		if (action instanceof Accept) {
			Helper.printExiting("takeAction");
			return;
		}
		else if (action == null) {
			throw new IllegalArgumentException();
		}
		else if (action instanceof Shift) {
			Shift shiftAction = (Shift)action;
			shift(shiftAction.getDestinationState(), tokenQueue);
		}
		else if (action instanceof Reduce) {
			Reduce reduceAction = (Reduce)action;
			reduce(reduceAction.getReduction(), reduceAction.getHandle());
		}
		// Helper.print(abstractSyntaxTreeNodeListStack.toString());
		Helper.printExiting("takeAction");
	}

	private void shift(ParserState destinationState, Deque<Token> tokenQueue) {
		Helper.printEntering("shift");
		parseTreeNodeStack.push(lookAheadNode);
		parserStateStack.push(destinationState);
		int lineNumber = -1;
		// abstractSyntaxTreeNodeListStack.push(
		// 	Node.buildSingleton(
		// 		lookAheadNode.getLexeme(), lineNumber
		// 	)
		// );
		abstractSyntaxTreeNodeListStack.offerLast(
			Node.buildSingleton(
				lookAheadNode.getLexeme(), lineNumber
			)
		);
		lookAheadNode = buildNextParseTreeNode(tokenQueue);
		Helper.printExiting("shift");
	}

	private void reduce(NonTerminal reduction, Handle handle) {
		Helper.printEntering("reduce");
		Deque<ParseTreeNode> children = new ArrayDeque<ParseTreeNode>();
		int[] nodeArray = new int[handle.getSize()];
		for (int i = 0; i < handle.getSize(); i++) {
			children.offer(parseTreeNodeStack.pop());
			parserStateStack.pop();
		}
		parseTreeNodeStack.push(new ParseTreeNode(reduction, children));
		ParserState parserState
		= parserStateTransitionMap.get(parserStateStack.peek()).get(reduction);
		if (parserState != null) {
			parserStateStack.push(parserState);
		}
		String instruction
		= abstractSyntaxTreeReductionTable.get(reduction).get(handle);
		if (instruction != null) {
			reduceAbstractSyntaxTreeNodeListStack(instruction);
		}
		Helper.printExiting("reduce");
	}

	private void reduceAbstractSyntaxTreeNodeListStack(String instruction) {
		Helper.printEntering("reduceAbstractSyntaxTreeNodeListStack");
		String[] splitInstruction = instruction.split(" ");
		LinkedList<Node>[] nodeArray
		= new LinkedList[splitInstruction.length - 1];
		int[] nodeIndexArray = new int[splitInstruction.length - 1];

		for (int i = 1; i < splitInstruction.length; i++) {
			nodeArray[splitInstruction.length - i - 1] = abstractSyntaxTreeNodeListStack.pollLast();
			// nodeArray[i - 1] = abstractSyntaxTreeNodeListStack.pollLast();

			nodeIndexArray[i - 1] = Integer.parseInt(splitInstruction[i]);

				// nodeIndexArray[i - 1] = Integer.parseInt(splitInstruction[i]);
		}

		LinkedList<Node> nodeList
		= buildNodeList(splitInstruction, nodeArray, nodeIndexArray);
		if (nodeList != null) {
			// abstractSyntaxTreeNodeListStack.push(nodeList);
			abstractSyntaxTreeNodeListStack.offerLast(nodeList);
		}
		Helper.printExiting("reduceAbstractSyntaxTreeNodeListStack");
	}

	private LinkedList<Node> buildNodeList(
		String[] splitInstruction,
		LinkedList<Node>[] nodeArray,
		int[] nodeIndexArray
	) {
		Helper.printEntering("buildNodeList");
		String type = splitInstruction[0];
		switch (type) {
			case ParserConstants.LIST:
			if (nodeIndexArray.length == 0) {
				Helper.printExiting("buildNodeList");
				return new LinkedList<Node>();
			}
			LinkedList<Node> listBranch = nodeArray[nodeIndexArray[0]];
			LinkedList<Node> nodeBranch = nodeArray[nodeIndexArray[1]];
			Helper.printExiting("buildNodeList");
			return Node.extendListWithNode(listBranch, nodeBranch);

			case ParserConstants.NULL:
			Helper.printExiting("buildNodeList");
			return null;

			case ParserConstants.EVALUATION:
			Node symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			LinkedList<Node> argumentsBranch = new LinkedList<Node>();
			for (int i = 1; i < splitInstruction.length - 1; i++) {
				argumentsBranch.addAll(nodeArray[nodeIndexArray[i]]);
			}
			// for (int i = splitInstruction.length - 1; i > 0 ; i--) {
			// 	argumentsBranch.addAll(nodeArray[nodeIndexArray[i]]);
			// }
			Helper.printExiting("buildNodeList");
			return Node.buildSingleton(symbolNode, argumentsBranch);

			case ParserConstants.CONDITIONAL:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			LinkedList<Node> evaluationsBranch = nodeArray[nodeIndexArray[1]];
			LinkedList<Node> expressionsBranch = nodeArray[nodeIndexArray[2]];
			expressionsBranch.addAll(nodeArray[nodeIndexArray[3]]);
			Helper.printExiting("buildNodeList");
			return Node.buildSingleton(
				symbolNode, evaluationsBranch, expressionsBranch
			);

			case ParserConstants.LOOP:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			evaluationsBranch = nodeArray[nodeIndexArray[1]];
			expressionsBranch = nodeArray[nodeIndexArray[2]];
			Helper.printExiting("buildNodeList");
			return Node.buildSingleton(
				symbolNode, evaluationsBranch, expressionsBranch
			);

			case ParserConstants.VARIABLE:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			LinkedList<Node> dataTypeBranch = nodeArray[nodeIndexArray[1]];
			LinkedList<Node> variableBranch = Node.buildSingleton(
				symbolNode, ParserConstants.NEW_VARIABLE, dataTypeBranch
			);
			if (nodeIndexArray.length == 2) {
				Helper.printExiting("buildNodeList: nodeIndexArray.length == 2");
				return variableBranch;
			}
			evaluationsBranch = nodeArray[nodeIndexArray[2]];
			Helper.printExiting("buildNodeList");
			return Node.extendListWithNode(variableBranch, evaluationsBranch);

			case ParserConstants.ARRAY:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			Node operatorNode = nodeArray[nodeIndexArray[1]].get(0);
			LinkedList<Node> dimensionsBranch = nodeArray[nodeIndexArray[2]];
			evaluationsBranch = nodeArray[nodeIndexArray[3]];
			LinkedList<Node> operatorBranch = Node.buildSingleton(
				operatorNode, dimensionsBranch, evaluationsBranch
			);
			Helper.printExiting("buildNodeList");
			return Node.buildSingleton(
				symbolNode, ParserConstants.NEW_ARRAY, operatorBranch
			);

			case ParserConstants.FUNCTION:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			symbolNode.setAnnotation(ParserConstants.NEW_FUNCTION);
			LinkedList<Node> returnTypeBranch = nodeArray[nodeIndexArray[1]];
			LinkedList<Node> parametersBranch = nodeArray[nodeIndexArray[2]];
			expressionsBranch = nodeArray[nodeIndexArray[3]];
			expressionsBranch.addAll(nodeArray[nodeIndexArray[4]]);
			Helper.printExiting("buildNodeList");
			return Node.buildSingleton(
				symbolNode,
				ParserConstants.NEW_FUNCTION,
				returnTypeBranch,
				parametersBranch,
				expressionsBranch
			);

			case ParserConstants.IDENTIFIER:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			symbolNode.setAnnotation(ParserConstants.CHECK_IDENTIFIER);
			Helper.printExiting("buildNodeList");
			return nodeArray[nodeIndexArray[0]];

			default:
			Helper.printExiting("buildNodeList");
			return null;
		}
	}
}
