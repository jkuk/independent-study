package parser;
import lexer.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Parser {
	private Map<NonTerminal, ArrayList<Handle>> productionMap;
	private Set<ParserState> canonicalCollection;
	private ParserState initialState;
	private Map<ParserState, HashMap<Symbol, Action>> actionTable;
	private Map<ParserState, HashMap<Symbol, ParserState>>
	parserStateTransitionMap;

	private Deque<ParseTreeNode> parseTreeNodeStack;
	private Deque<ParserState> parserStateStack;
	private ParseTreeNode lookAheadNode;
	private ParserState parserState;

	public Parser(String grammarFileName) {
		try {
			initializeProductionMap(grammarFileName);
			System.out.println("Production map: " + productionMap);

			initializeInitialState();
			System.out.println("Initial State: " + initialState);
			
			initializeCanonicalCollection();
			System.out.println("Canonical Collection: " + canonicalCollection);
			System.out.println("Parser State Transition Map: " + parserStateTransitionMap);
			initializeActionTable();
			System.out.println("Action table: " + actionTable);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void initializeProductionMap(String grammarFileName)
	throws FileNotFoundException, IOException {
		System.out.println("Starting initializeProductionMap: ");
		BufferedReader grammarFile = new BufferedReader(
			new FileReader(grammarFileName)
		);
		productionMap
		= new HashMap<NonTerminal, ArrayList<Handle>>();
		String line;
		while ((line = grammarFile.readLine()) != null) {
			String[] lines = line.split("->|\\|");
			NonTerminal reduction = new NonTerminal(lines[0]);
			ArrayList<Handle> handleList = new ArrayList<Handle>();
			for (int i = 1; i < lines.length; i++) {
				Handle handle = new Handle(lines[i]);
				handleList.add(handle);
			}
			productionMap.put(reduction, handleList);
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
				nextSymbolSet.add(item.getNextSymbol());
			}
		}
		System.out.println("\t\t\t\tFinishing buildNextSymbolSet: ");
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
		if (nextSymbol == null) {
			Symbol lookAheadSymbol = item.getLookAheadSymbol();
			if (item.getReduction().equals(ParserConstants.GOAL)) {
				if (lookAheadSymbol.equals(ParserConstants.EOF)) {
					symbolActionMap.put(ParserConstants.EOF, new Accept());
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
				symbolActionMap.put(nextSymbol, new Shift(destinationState));
			}
		}
	}

	public ParseTree parse(Deque<Token> tokenQueue) {
		parseTreeNodeStack = new ArrayDeque<ParseTreeNode>();
		parserStateStack = new ArrayDeque<ParserState>();
		parserStateStack.push(initialState);
		lookAheadNode = buildNextParseTreeNode(tokenQueue);

		int counter = 0;
		Action action = null;
		while (!(action instanceof Accept)) {
			parserState = parserStateStack.peek();
			action
			= actionTable.get(parserState).get(lookAheadNode.getSymbol());
			System.out.println();
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
		return new ParseTree(parseTreeNodeStack.pop());
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
		lookAheadNode = buildNextParseTreeNode(tokenQueue);
	}

	private void reduce(NonTerminal reduction, Handle handle) {
		Deque<ParseTreeNode> children = new ArrayDeque<ParseTreeNode>();
		for (int i = 0; i < handle.getSize(); i++) {
			children.offer(parseTreeNodeStack.pop());
			parserStateStack.pop();
		}
		parseTreeNodeStack.push(new ParseTreeNode(reduction, children));
		parserStateStack.push(
			parserStateTransitionMap.get(parserStateStack.peek()).get(reduction)
		);
	}
}