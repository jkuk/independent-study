package parser;
import parser.abstractSyntaxTree.*;
import parser.parseTree.*;
import parser.ParserConstants;
import lexer.Token;

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
import java.util.Map;
import java.util.Set;

import java.util.Arrays;

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
			initializeProductionMapAndAbstractSyntaxTreeReductionTable(
				grammarFileName
			);
			System.out.println("Production map: " + productionMap);
			System.out.println(
				"Abstract syntax tree node reduction table: "
				+ abstractSyntaxTreeReductionTable
			);

			initializeInitialState();
			System.out.println("Initial State: " + initialState);

			initializeCanonicalCollection();
			System.out.println("Canonical Collection: " + canonicalCollection);
			System.out.println(
				"Parser State Transition Map: "
				+ parserStateTransitionMap
			);

			initializeActionTable();
			System.out.println("Action table: " + actionTable);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeProductionMapAndAbstractSyntaxTreeReductionTable(
		String grammarFileName
	)
	throws FileNotFoundException, IOException {
		System.out.println("Starting initializeProductionMap: ");
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
		System.out.println("Finishing initializeProductionMap: ");
	}

	private void initializeInitialState() {
		System.out.println("\tStarting initializeInitialState: ");
		initialState = new ParserState(
			ParserConstants.GOAL,
			productionMap.get(ParserConstants.GOAL),
			ParserConstants.EOF
		).buildClosure(productionMap);
		System.out.println("\tFinishing initializeInitialState: ");
	}

	private void initializeCanonicalCollection() {
		System.out.println("\t\tStarting initializeCanonicalCollection: ");
		canonicalCollection = new HashSet<ParserState>();
		canonicalCollection.add(initialState);

		Deque<ParserState> unmarkedParserStateStack
		= new ArrayDeque<ParserState>();
		unmarkedParserStateStack.push(initialState);

		parserStateTransitionMap
		= new HashMap<ParserState, HashMap<Symbol, ParserState>>();

		initializeCanonicalCollection(unmarkedParserStateStack);
		System.out.println("\t\tFinishing initializeCanonicalCollection: ");
	}

	private void initializeCanonicalCollection(
		Deque<ParserState> unmarkedParserStateStack
	) {
		System.out.println("\t\t\tStarting initializeCanonicalCollection: ");
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
		System.out.println("\t\t\tFinishing initializeCanonicalCollection: ");
	}

	private Set<Symbol> buildNextSymbolSet(ParserState parserState) {
		System.out.println("\t\t\t\tStarting buildNextSymbolSet: ");
		Set<Symbol> nextSymbolSet = new HashSet<Symbol>();
		for (Item item : parserState.getItemSet()) {
			if (item.getNextSymbol() == null) {
				nextSymbolSet.add(item.getLookAheadSymbol());
			}
			else {
				Symbol nextSymbol = item.getNextSymbol();
				// java.util.List<Symbol> suffix = item.getSuffix();
				// int i = 0;
				// while (nextSymbol.equals(ParserConstants.EPSILON)
				// && i < suffix.size()) {
				// 	nextSymbol = suffix.get(i++);
				// }
				// if (i == suffix.size()) {
				// 	nextSymbol = item.getLookAheadSymbol();
				// }
				System.out.println("SHOULD NOT BE EPSILON:  " + nextSymbol);
				nextSymbolSet.add(nextSymbol);
				// nextSymbolSet.add(item.getNextSymbol());
			}
		}
		System.out.println("\t\t\t\tFinishing buildNextSymbolSet: ");
		System.out.println("PARSER STATE: " + parserState);
		System.out.println("ITEM SET:  " + parserState.getItemSet());
		System.out.println("NEXT SYMBOL SET: " + nextSymbolSet);
		return nextSymbolSet;
	}

	private void addParserStateToCanonicalCollection(
		Deque<ParserState> unmarkedParserStateStack,
		ParserState parserState,
		Symbol symbol
	) {
		System.out.println("\t\t\t\t\tStarting addParserStateToCanonicalCollection: ");
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
		System.out.println("SYMBOL SHOULD BE SOMETHING:  " + symbol);
		System.out.println("DeSTINATION:  " + destinationState);
		parserStateTransitionMap.get(parserState).put(symbol, destinationState);
		System.out.println("\t\t\t\t\tFinishing addParserStateToCanonicalCollection: ");
	}

	private void initializeActionTable() {
		actionTable	= new HashMap<ParserState, HashMap<Symbol, Action>>();
		for (ParserState parserState : canonicalCollection) {
			for (Item item : parserState.getItemSet()) {
				addActionToActionTable(parserState, item);
			}
		}
	}

	private void addActionToActionTable(ParserState parserState, Item item) {
		HashMap<Symbol, Action> symbolActionMap = actionTable.get(parserState);
		if (symbolActionMap == null) {
			symbolActionMap = new HashMap<Symbol, Action>();
			actionTable.put(parserState, symbolActionMap);
		}

		Symbol nextSymbol = item.getNextSymbol();
		if (nextSymbol == null /*|| nextSymbol.equals(ParserConstants.EPSILON)*/) {
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
				if (nextSymbol.equals(ParserConstants.EPSILON)) {
					nextSymbol = item.getLookAheadSymbol();
				}

				symbolActionMap.put(nextSymbol, new Shift(destinationState));
			}
		}
	}

	public AbstractSyntaxTree parse(Deque<Token> tokenQueue) {
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
			System.out.println("\nACTION STUFF");
			System.out.println(parserState);
			System.out.println(actionTable.get(parserState));
			System.out.println(lookAheadNode.getSymbol());
			System.out.println(action);
			try {
				shiftOrReduce(tokenQueue, action);
			}
			catch (IllegalArgumentException e) {
				e.printStackTrace();
				return null;
			}
		}
		Reduce reduceAction = (Reduce)action;
		reduce(reduceAction.getReduction(), reduceAction.getHandle());
		System.out.println("Parse Tree: " + new ParseTree(parseTreeNodeStack.pop()));
		System.out.println("SIZE: " + abstractSyntaxTreeNodeListStack.size());
		return new AbstractSyntaxTree(abstractSyntaxTreeNodeListStack.pop());
		// return new ParseTree(parseTreeNodeStack.pop());
	}

	private ParseTreeNode buildNextParseTreeNode(Deque<Token> tokenQueue) {
		return tokenQueue.peek() == null ?
		new ParseTreeNode(ParserConstants.EOF)
		: new ParseTreeNode(tokenQueue.poll());
	}

	private void shiftOrReduce(Deque<Token> tokenQueue, Action action) {
		if (action instanceof Accept) {
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
	}

	private void shift(ParserState destinationState, Deque<Token> tokenQueue) {
		parseTreeNodeStack.push(lookAheadNode);
		parserStateStack.push(destinationState);
		int lineNumber = -1;
		abstractSyntaxTreeNodeListStack.push(
			Node.buildSingleton(
				lookAheadNode.getLexeme(), lineNumber
			)
		);
		lookAheadNode = buildNextParseTreeNode(tokenQueue);
	}

	private void reduce(NonTerminal reduction, Handle handle) {
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
	}

	private void reduceAbstractSyntaxTreeNodeListStack(String instruction) {
		String[] splitInstruction = instruction.split(" ");
		LinkedList<Node>[] nodeArray
		= new LinkedList[splitInstruction.length - 1];
		int[] nodeIndexArray = new int[splitInstruction.length - 1];

		for (int i = 1; i < splitInstruction.length; i++) {
			nodeArray[splitInstruction.length - i - 1] = abstractSyntaxTreeNodeListStack.pop();
			nodeIndexArray[i - 1] = Integer.parseInt(splitInstruction[i]);
		}

		LinkedList<Node> nodeList
		= buildNodeList(splitInstruction, nodeArray, nodeIndexArray);
		if (nodeList != null) {
			abstractSyntaxTreeNodeListStack.push(nodeList);
		}
	}

	private LinkedList<Node> buildNodeList(
		String[] splitInstruction,
		LinkedList<Node>[] nodeArray,
		int[] nodeIndexArray
	) {
		String type = splitInstruction[0];
		switch (type) {
			case ParserConstants.LIST:
			if (nodeIndexArray.length == 0) {
				return new LinkedList<Node>();
			}
			LinkedList<Node> listBranch = nodeArray[nodeIndexArray[0]];
			LinkedList<Node> nodeBranch = nodeArray[nodeIndexArray[1]];
			return Node.extendListWithNode(listBranch, nodeBranch);

			case ParserConstants.NULL:
			return null;

			case ParserConstants.EVALUATION:
			Node symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			LinkedList<Node> argumentsBranch = new LinkedList<Node>();
			for (int i = 1; i < splitInstruction.length - 1; i++) {
				argumentsBranch.addAll(nodeArray[nodeIndexArray[i]]);
			}
			return Node.buildSingleton(symbolNode, argumentsBranch);

			case ParserConstants.CONDITIONAL:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			LinkedList<Node> evaluationsBranch = nodeArray[nodeIndexArray[1]];
			System.out.println("evaluations branch:" + evaluationsBranch);
			LinkedList<Node> expressionsBranch = nodeArray[nodeIndexArray[2]];
			System.out.println("expressions branch:" + expressionsBranch);
			expressionsBranch.addAll(nodeArray[nodeIndexArray[3]]);
			return Node.buildSingleton(
				symbolNode, evaluationsBranch, expressionsBranch
			);

			case ParserConstants.LOOP:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			evaluationsBranch = nodeArray[nodeIndexArray[1]];
			expressionsBranch = nodeArray[nodeIndexArray[2]];
			return Node.buildSingleton(
				symbolNode, evaluationsBranch, expressionsBranch
			);

			case ParserConstants.VARIABLE:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			// symbolNode.setAnnotation(ParserConstants.NEW_VARIABLE);
			LinkedList<Node> dataTypeBranch = nodeArray[nodeIndexArray[1]];
			LinkedList<Node> variableBranch = Node.buildSingleton(
				symbolNode, ParserConstants.NEW_VARIABLE, dataTypeBranch
			);
			if (nodeIndexArray.length == 2) {
				return variableBranch;
			}
			evaluationsBranch = nodeArray[nodeIndexArray[2]];
			return Node.extendListWithNode(variableBranch, evaluationsBranch);

			case ParserConstants.ARRAY:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			// symbolNode.setAnnotation(ParserConstants.NEW_ARRAY);
			Node operatorNode = nodeArray[nodeIndexArray[1]].get(0);
			LinkedList<Node> dimensionsBranch = nodeArray[nodeIndexArray[2]];
			evaluationsBranch = nodeArray[nodeIndexArray[3]];
			LinkedList<Node> operatorBranch = Node.buildSingleton(
				operatorNode, dimensionsBranch, evaluationsBranch
			);
			return Node.buildSingleton(symbolNode, ParserConstants.NEW_ARRAY, operatorBranch);

			case ParserConstants.FUNCTION:
			symbolNode = nodeArray[nodeIndexArray[0]].get(0);
			symbolNode.setAnnotation(ParserConstants.NEW_FUNCTION);
			LinkedList<Node> returnTypeBranch = nodeArray[nodeIndexArray[1]];
			LinkedList<Node> parametersBranch = nodeArray[nodeIndexArray[2]];
			expressionsBranch = nodeArray[nodeIndexArray[3]];
			expressionsBranch.addAll(nodeArray[nodeIndexArray[4]]);
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
			return null;

			default: return null;
		}
	}
}
