package parser.parseTree;
import parser.ParserConstants;

import java.util.ArrayList;
import java.util.HashSet;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class ParserState {
	private Set<Item> itemSet;

	public ParserState(
		NonTerminal reduction, List<Handle> handleList, Terminal lookAheadSymbol
	) {
		this();
		for (Handle handle : handleList) {
			itemSet.add(new Item(reduction, handle, lookAheadSymbol));
		}
	}

	public ParserState() {
		this(new HashSet<Item>());
	}

	public ParserState(Set<Item> itemSet) {
		this.itemSet = itemSet;
	}

	public Set<Item> getItemSet() {
		return itemSet;
	}


	public ParserState buildClosure(
		Map<NonTerminal, ArrayList<Handle>> productionMap
	) {
		Set<Item> closure = new HashSet<Item>();
		closure.addAll(itemSet);

		int previousSize = -1;
		while (closure.size() != previousSize) {
			previousSize = closure.size();
			Set<Item> itemSet = new HashSet<Item>();
			for (Item item : closure) { // change this into a worklist -- unprocessed items
				Symbol nextSymbol = item.getNextSymbol();
				if (nextSymbol instanceof NonTerminal) {
					List<Symbol> suffix
					= new ArrayList<Symbol>(item.getSuffix());
					suffix.add(item.getLookAheadSymbol());

					for (Handle handle : productionMap.get(nextSymbol)) {
						for (Terminal lookAheadSymbol
						: buildLookAheadSymbolSet(suffix, productionMap)) {

							if (ParserConstants.EPSILON
							.equals(handle.getFirstSymbol())) {
								Handle epsilonNonTerminalHandle
								= item.getHandle().removeFirstSymbol();

								if (lookAheadSymbol
								.equals(ParserConstants.EPSILON)) {
									itemSet.add(
										new Item(
											(NonTerminal)nextSymbol,
											epsilonNonTerminalHandle,
											item.getLookAheadSymbol()
										)
									);
								}
								else {
									itemSet.add(
										new Item(
											(NonTerminal)nextSymbol,
											epsilonNonTerminalHandle,
											lookAheadSymbol
										)
									);
								}
							}
							else {
								itemSet.add(
									new Item(
										(NonTerminal)nextSymbol,
										handle,
										lookAheadSymbol
									)
								);
								// itemSet.addAll(
								// 	buildItemsInClosure(
								// 		(NonTerminal)nextSymbol, item, productionMap
								// 	)
								// );
							}
						}
					}
				}
			}
			closure.addAll(itemSet);
		}
		return new ParserState(closure);
	}

	// private Set<Item> buildItemsInClosure(
	// 	NonTerminal reduction,
	// 	Item item,
	// 	Map<NonTerminal, ArrayList<Handle>> productionMap
	// ) {
	// 	List<Symbol> suffix
	// 	= new ArrayList<Symbol>(item.getSuffix());
	// 	suffix.add(item.getLookAheadSymbol());
	// 	Set<Item> itemSet = new HashSet<Item>();
	// 	for (Handle handle : productionMap.get(reduction)) {
	// 		for (Terminal lookAheadSymbol
	// 		: buildLookAheadSymbolSet(suffix, productionMap)) {
	// 			if (ParserConstants.EPSILON.equals(handle.getFirstSymbol())) {
	// 				compiler.Helper.print("EPSILON: " + lookAheadSymbol);
	// 				for (Handle handle : productionMap.get(reduction)) {
	// 					if (ParserConstants.EPSILON.equals(handle.getFirstSymbol())) {
	// 				}
	// 				if lookAheadSymbol.equals(ParserConstants.EPSILON)) {
	// 					itemSet.add(new Item(reduction, something, item.getLookAheadSymbol));
	// 				}
	// 				else {
	//
	// 					itemSet.add(new Item(reduction, something, lookAheadSymbol));
	// 				}
	// 			}
	// 			// if i come across an epsilon handle
	// 			// replace the item handle with null
	// 			// if (reduction.toString().equals("list")) {
	// 			// 	compiler.Helper.print("here : " + new Item(reduction, handle, lookAheadSymbol));
	// 			// }
	// 			else {
	// 				itemSet.add(new Item(reduction, handle, lookAheadSymbol));
	// 			}
	// 		}
	// 	}
	// 	// itemSet.addAll();
	// 	// return if handle is epsilon
	// 	return itemSet;
	// }

	private Set<Terminal> buildLookAheadSymbolSet(
		List<Symbol> symbolList,
		Map<NonTerminal, ArrayList<Handle>> productionMap
	) {
		Set<Terminal> lookAheadSymbolSet = new HashSet<Terminal>();
		boolean allContainEpsilon = true;
		for (Symbol symbol : symbolList) {
			Set<Terminal> terminalPrefixSet
			= buildTerminalPrefixSet(symbol, productionMap);
			boolean containsEpsilon
			= terminalPrefixSet.remove(ParserConstants.EPSILON);

			lookAheadSymbolSet.addAll(terminalPrefixSet);
			allContainEpsilon = allContainEpsilon && containsEpsilon;
			if (!containsEpsilon) {
				break;
			}
		}
		if (allContainEpsilon) {
			lookAheadSymbolSet.add(ParserConstants.EPSILON);
		}
		return lookAheadSymbolSet;
	}

	private Set<Terminal> buildTerminalPrefixSet(
		Symbol symbol, Map<NonTerminal, ArrayList<Handle>> productionMap
	) {
		Set<Terminal> terminalPrefixSet = new HashSet<Terminal>();
		if (symbol instanceof Terminal) {
			terminalPrefixSet.add((Terminal)symbol);
			return terminalPrefixSet;
		}
		System.out.println(productionMap);
		System.out.println(symbol);
		System.out.println(productionMap.get(symbol));
		for (Handle handle : productionMap.get(symbol)) {
			terminalPrefixSet.addAll(
				buildTerminalPrefixSet(handle.getFirstSymbol(), productionMap)
			);
		}
		return terminalPrefixSet;
	}

	public ParserState transition(
		Symbol symbol, Map<NonTerminal, ArrayList<Handle>> productionMap
	) {
		Set<Item> destinationState = new HashSet<Item>();
		for (Item item : itemSet) {
			if (symbol.equals(item.getNextSymbol())) {
				destinationState.add(item.recognizeNextSymbol());
			}
		}
		return new ParserState(destinationState).buildClosure(productionMap);
	}

	@Override
	public boolean equals(Object object) {
		if (this == null) return false;
		if (this == object) return true;
		if (this instanceof ParserState) {
			ParserState that = (ParserState)object;
			for (Item item : itemSet) {
				if (!that.getItemSet().contains(item)) {
					return false;
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return itemSet.size();
	}

	@Override
	public String toString() {
		return itemSet.toString();
	}
}
