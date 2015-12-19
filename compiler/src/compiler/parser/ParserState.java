package compiler.parser;

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
			for (Item item : closure) {
				Symbol nextSymbol = item.getNextSymbol();
				if (nextSymbol instanceof NonTerminal) {
					for (Handle handle : productionMap.get(nextSymbol)) {
						for (Terminal lookAheadSymbol
						: buildLookAheadSymbolSet(
							item.getSuffix(), item.getLookAheadSymbol(), productionMap)
						) {

							if (ParserConstants.EPSILON
							.equals(handle.getFirstSymbol())) {
								List<Symbol> symbolList = new ArrayList<Symbol>(item.getSymbolsSeen());
								symbolList.addAll(item.getSuffix());
								Handle epsilonNonTerminalHandle
								= new Handle(item.getSuffix());
								itemSet.add(
									new Item(
										(NonTerminal)nextSymbol,
										epsilonNonTerminalHandle,
										lookAheadSymbol
									)
								);
							}
							else {
								itemSet.add(
									new Item(
										(NonTerminal)nextSymbol,
										handle,
										lookAheadSymbol
									)
								);
							}
						}
					}
				}
			}
			closure.addAll(itemSet);
		}
		return new ParserState(closure);
	}

	private Set<Terminal> buildLookAheadSymbolSet(
		List<Symbol> suffix,
		Terminal lookAheadSymbol,
		Map<NonTerminal, ArrayList<Handle>> productionMap
	) {
		Set<ArrayList<Symbol>> processeduffixSet
		= new HashSet<ArrayList<Symbol>>();
		Set<Terminal> lookAheadSymbolSet
		= buildTerminalPrefixSet(suffix, productionMap, processeduffixSet);
		if (lookAheadSymbolSet.remove(ParserConstants.EPSILON)) {
			lookAheadSymbolSet.add(lookAheadSymbol);
		}
		return lookAheadSymbolSet;
	}

	private Set<Terminal> buildTerminalPrefixSet(
		List<Symbol> suffix,
		Map<NonTerminal, ArrayList<Handle>> productionMap,
		Set<ArrayList<Symbol>> processedSuffixSet
	) {
		Set<Terminal> terminalPrefixSet = new HashSet<Terminal>();
		terminalPrefixSet.add(ParserConstants.EOF);
		boolean allContainEpsilon = true;
		for (Symbol symbol : suffix) {
			terminalPrefixSet.addAll(buildTerminalPrefixSet(symbol, productionMap, processedSuffixSet));
			boolean containsEpsilon
			= terminalPrefixSet.remove(ParserConstants.EPSILON);

			allContainEpsilon = allContainEpsilon && containsEpsilon;
			if (!containsEpsilon) {
				break;
			}
		}
		if (allContainEpsilon) {
			terminalPrefixSet.add(ParserConstants.EPSILON);
		}
		return terminalPrefixSet;
	}

	private Set<Terminal> buildTerminalPrefixSet(
		Symbol symbol,
		Map<NonTerminal, ArrayList<Handle>> productionMap,
		Set<ArrayList<Symbol>> processedSuffixSet
	) {
		Set<Terminal> terminalPrefixSet = new HashSet<Terminal>();
		if (symbol instanceof Terminal) {
			terminalPrefixSet.add((Terminal)symbol);
			return terminalPrefixSet;
		}
		for (Handle handle : productionMap.get(symbol)) {
			if (processedSuffixSet.add(
				new ArrayList<Symbol>(handle.getSymbolList())
			)) {
				terminalPrefixSet.addAll(
					buildTerminalPrefixSet(
						handle.getSymbolList(),
						productionMap,
						processedSuffixSet
					)
				);
			}
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
