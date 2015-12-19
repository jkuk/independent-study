package compiler.parser;
import compiler.lexer.Token;
import utility.Helper;

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
	private Deque<LinkedList<AbstractSyntaxTreeNode>>
	abstractSyntaxTreeNodeListStack;

	private String operatorsFileName;

	public Parser(String grammarFileName, String operatorsFileName) {
		try {
			Helper.setDebugMode(true);
			Helper.resetDepth();
			initializeProductionMapAndAbstractSyntaxTreeReductionTable(
				grammarFileName
			);
			this.operatorsFileName = operatorsFileName;
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
				handleInstructionMap.put(handleList.get(i - 1),
				splitLine[i].trim());
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
		if (nextSymbol == null) {
			Symbol lookAheadSymbol = item.getLookAheadSymbol();
			if (item.getReduction().equals(ParserConstants.GOAL)
			&& lookAheadSymbol.equals(ParserConstants.EOF)) {
				symbolActionMap.put(
					ParserConstants.EOF,
					new Accept(item.getReduction(), item.getHandle())
				);
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
				symbolActionMap.put(nextSymbol, new Shift(destinationState));
			}
		}
		Helper.printExiting("addActionToActionTable");
	}

	public AbstractSyntaxTree parse(Deque<Token> tokenQueue)
	throws FileNotFoundException, IOException {
		Helper.printEntering("parse");
		parseTreeNodeStack = new ArrayDeque<ParseTreeNode>();
		parserStateStack = new ArrayDeque<ParserState>();
		abstractSyntaxTreeNodeListStack =
		new ArrayDeque<LinkedList<AbstractSyntaxTreeNode>>();

		parserStateStack.push(initialState);
		lookAheadNode = buildNextParseTreeNode(tokenQueue);

		int counter = 0;
		Action action = null;
		while (!(action instanceof Accept)) {
			parserState = parserStateStack.peek();
			action
			= actionTable.get(parserState).get(lookAheadNode.getSymbol());
			try {
				takeAction(tokenQueue, action);
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			}
			// System.out.println(lookAheadNode.getSymbol());
			// System.out.println(action);
			// System.out.println();
		}
		Helper.printExiting("parse");
		System.out.println("Parse Tree: " + new ParseTree(parseTreeNodeStack.pop()));
		AbstractSyntaxTree tree = new AbstractSyntaxTree(
			abstractSyntaxTreeNodeListStack.pop(), operatorsFileName
		);
		System.out.println("Abstract Syntax Tree: " + tree);
		return tree;
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
		Helper.printExiting("takeAction");
	}

	private void shift(ParserState destinationState, Deque<Token> tokenQueue) {
		Helper.printEntering("shift");
		parseTreeNodeStack.push(lookAheadNode);
		parserStateStack.push(destinationState);
		int lineNumber = -1;
		abstractSyntaxTreeNodeListStack.offerLast(
			AbstractSyntaxTreeNode.buildSingleton(
				lookAheadNode.getLexeme(), lineNumber
			)
		);
		lookAheadNode = buildNextParseTreeNode(tokenQueue);
		Helper.printExiting("shift");
	}

	private void reduce(NonTerminal reduction, Handle handle) {
		Helper.printEntering("reduce");
		Deque<ParseTreeNode> children = new ArrayDeque<ParseTreeNode>();
		int size = handle.getSize();
		int[] nodeArray = new int[size];
		for (int i = 0; i < size; i++) {
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
		if (!instruction.equals("null")) {
			reduceAbstractSyntaxTreeNodeListStack(instruction, size);
		}
		Helper.printExiting("reduce");
	}

	private void reduceAbstractSyntaxTreeNodeListStack(
		String instruction, int size
	) {
		Helper.printEntering("reduceAbstractSyntaxTreeNodeListStack");
		String[] splitInstruction = instruction.split(" ");
		LinkedList<AbstractSyntaxTreeNode>[] nodeArray
		= new LinkedList[size];
		int[] nodeIndexArray = new int[splitInstruction.length - 1];

		for (int i = 0; i < size; i++) {
			nodeArray[size - i - 1] = abstractSyntaxTreeNodeListStack.pollLast();
		}

		for (int i = 1; i < splitInstruction.length; i ++) {
			nodeIndexArray[i - 1] = Integer.parseInt(splitInstruction[i]);
		}

		LinkedList<AbstractSyntaxTreeNode> nodeList
		= buildNodeList(splitInstruction, nodeArray, nodeIndexArray);
		if (nodeList == null) {
			abstractSyntaxTreeNodeListStack.offerLast(nodeList);
		}
		else {
			abstractSyntaxTreeNodeListStack.offerLast(nodeList);
		}
		Helper.printExiting("reduceAbstractSyntaxTreeNodeListStack");
	}

	private LinkedList<AbstractSyntaxTreeNode> buildNodeList(
		String[] splitInstruction,
		LinkedList<AbstractSyntaxTreeNode>[] nodeArray,
		int[] nodeIndexArray
	) {
		Helper.printEntering("buildNodeList");
		String type = splitInstruction[0];
		switch (type) {
			case ParserConstants.LIST:
			if (nodeIndexArray.length == 0) {
				Helper.printExiting("buildNodeList");
				return new LinkedList<AbstractSyntaxTreeNode>();
			}
			if (nodeIndexArray.length == 1) {
				Helper.printExiting("buildNodeList");
				return nodeArray[nodeIndexArray[0]];
			}
			LinkedList<AbstractSyntaxTreeNode> listBranch
			= nodeArray[nodeIndexArray[0]];
			LinkedList<AbstractSyntaxTreeNode> nodeBranch
			= nodeArray[nodeIndexArray[1]];
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.extendListWithNode(listBranch, nodeBranch);

			case ParserConstants.EVALUATION:
			AbstractSyntaxTreeNode symbolNode
			= nodeArray[nodeIndexArray[0]].get(0);
			LinkedList<AbstractSyntaxTreeNode> argumentsBranch = new LinkedList<AbstractSyntaxTreeNode>();
			for (int i = 1; i < splitInstruction.length - 1; i++) {
				argumentsBranch.addAll(nodeArray[nodeIndexArray[i]]);
			}
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.buildSingleton(symbolNode, argumentsBranch);

			case ParserConstants.CONDITIONAL:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			LinkedList<AbstractSyntaxTreeNode> evaluationsBranch
			= nodeArray[nodeIndexArray[1]];
			LinkedList<AbstractSyntaxTreeNode> expressionsBranch
			= nodeArray[nodeIndexArray[2]];
			if (nodeIndexArray.length == 4) {
				expressionsBranch.addAll(nodeArray[nodeIndexArray[3]]);
			}
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.buildSingleton(
				symbolNode, evaluationsBranch, expressionsBranch
			);

			case ParserConstants.LOOP:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			evaluationsBranch = nodeArray[nodeIndexArray[1]];
			expressionsBranch = nodeArray[nodeIndexArray[2]];
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.buildSingleton(
				symbolNode, evaluationsBranch, expressionsBranch
			);

			case ParserConstants.CALL_NUMBER:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			symbolNode.setAnnotation(ParserConstants.NUMBER_CALL);
			Helper.printExiting("buildNodeList");
			return nodeArray[nodeIndexArray[0]];

			case ParserConstants.CALL_STRING:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			symbolNode.setAnnotation(ParserConstants.STRING_CALL);
			Helper.printExiting("buildNodeList");
			return nodeArray[nodeIndexArray[0]];

			case ParserConstants.DECLARE_OR_INITIALIZE:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			symbolNode.setAnnotation(
				ParserConstants.DECLARATION_OR_INITIALIZATION
			);
			argumentsBranch = new LinkedList<AbstractSyntaxTreeNode>();
			for (int i = 1; i < splitInstruction.length - 1; i++) {
				argumentsBranch.addAll(nodeArray[nodeIndexArray[i]]);
			}
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.buildSingleton(
				symbolNode, argumentsBranch
			);

			case ParserConstants.CALL_VARIABLE:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			symbolNode.setAnnotation(ParserConstants.VARIABLE_CALL);
			Helper.printExiting("buildNodeList");
			return nodeArray[nodeIndexArray[0]];

			case ParserConstants.DECLARE_VARIABLE:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			LinkedList<AbstractSyntaxTreeNode> dataTypeBranch
			= nodeArray[nodeIndexArray[1]];
			dataTypeBranch.get(0).setAnnotation(ParserConstants.NONE);
			LinkedList<AbstractSyntaxTreeNode> variableBranch
			= AbstractSyntaxTreeNode.buildSingleton(
				symbolNode, ParserConstants.VARIABLE_DECLARATION, dataTypeBranch
			);
			Helper.printExiting("buildNodeList");
			return variableBranch;

			case ParserConstants.INITIALIZE_VARIABLE:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			variableBranch = AbstractSyntaxTreeNode.buildSingleton(
				symbolNode, ParserConstants.VARIABLE_INITIALIZATION
			);

			evaluationsBranch = nodeArray[nodeIndexArray[1]];
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.extendListWithNode(
				variableBranch, evaluationsBranch
			);

			case ParserConstants.DECLARE_AND_INITIALIZE_VARIABLE:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			dataTypeBranch = nodeArray[nodeIndexArray[1]];
			dataTypeBranch.get(0).setAnnotation(ParserConstants.NONE);
			variableBranch = AbstractSyntaxTreeNode.buildSingleton(
				symbolNode,
				ParserConstants.VARIABLE_DECLARATION_AND_INITIALIZATION,
				dataTypeBranch
			);
			evaluationsBranch = nodeArray[nodeIndexArray[2]];
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.extendListWithNode(
				variableBranch,
				evaluationsBranch
			);

			case ParserConstants.CALL_ARRAY:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			argumentsBranch = new LinkedList<AbstractSyntaxTreeNode>();
			for (int i = 1; i < splitInstruction.length - 1; i++) {
				argumentsBranch.addAll(nodeArray[nodeIndexArray[i]]);
			}
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.buildSingleton(
				symbolNode,
				ParserConstants.ARRAY_CALL,
				argumentsBranch
			);

			case ParserConstants.DECLARE_ARRAY:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			LinkedList<AbstractSyntaxTreeNode> dimensionsBranch
			= nodeArray[nodeIndexArray[1]];
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.buildSingleton(
				symbolNode, ParserConstants.ARRAY_DECLARATION, dimensionsBranch
			);

			case ParserConstants.CALL_FUNCTION:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			argumentsBranch = new LinkedList<AbstractSyntaxTreeNode>();
			for (int i = 1; i < splitInstruction.length - 1; i++) {
				argumentsBranch.addAll(nodeArray[nodeIndexArray[i]]);
			}
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.buildSingleton(
				symbolNode,
				ParserConstants.FUNCTION_CALL,
				argumentsBranch
			);

			case ParserConstants.CALL_FUNCTION_NO_ARGUMENTS:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			argumentsBranch = new LinkedList<AbstractSyntaxTreeNode>();
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.buildSingleton(
				symbolNode,
				ParserConstants.FUNCTION_CALL,
				argumentsBranch
			);

			case ParserConstants.DECLARE_AND_INITIALIZE_FUNCTION:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			LinkedList<AbstractSyntaxTreeNode> returnTypeBranch
			= nodeArray[nodeIndexArray[1]];
			LinkedList<AbstractSyntaxTreeNode> parametersBranch
			= nodeArray[nodeIndexArray[2]];
			expressionsBranch = nodeArray[nodeIndexArray[3]];
			if (nodeIndexArray.length == 5) {
				expressionsBranch.addAll(nodeArray[nodeIndexArray[4]]);
			}
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.buildSingleton(
				symbolNode,
				ParserConstants.FUNCTION_DECLARATION_AND_INITIALIZATION,
				returnTypeBranch,
				parametersBranch,
				expressionsBranch
			);

			case ParserConstants.DECLARE_AND_INITIALIZE_FUNCTION_NO_PARAMETERS:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			returnTypeBranch = nodeArray[nodeIndexArray[1]];
			parametersBranch = new LinkedList<AbstractSyntaxTreeNode>();
			expressionsBranch = nodeArray[nodeIndexArray[2]];
			if (nodeIndexArray.length == 4) {
				expressionsBranch.addAll(nodeArray[nodeIndexArray[3]]);
			}
			Helper.printExiting("buildNodeList");
			return AbstractSyntaxTreeNode.buildSingleton(
				symbolNode,
				ParserConstants.FUNCTION_DECLARATION_AND_INITIALIZATION,
				returnTypeBranch,
				parametersBranch,
				expressionsBranch
			);

			default:
			Helper.printExiting("buildNodeList");
			return null;
		}
	}
}
