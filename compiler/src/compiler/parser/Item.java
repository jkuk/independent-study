package compiler.parser;

import java.util.List;
import java.util.ArrayList;

public class Item {
	private NonTerminal reduction;
	private List<Symbol> symbolsSeen;
	private Symbol nextSymbol;
	private List<Symbol> suffix;
	private Terminal lookAheadSymbol;

	public Item(
		NonTerminal reduction, Handle handle, Terminal lookAheadSymbol
	) {
		this(reduction, new ArrayList<Symbol>(), handle, lookAheadSymbol);
	}

	public Item(
		NonTerminal reduction,
		List<Symbol> symbolsSeen,
		Handle handle,
		Terminal lookAheadSymbol
	) {
		this(
			reduction,
			symbolsSeen,
			handle.getFirstSymbol(),
			new ArrayList<Symbol>(),
			lookAheadSymbol
		);
		List<Symbol> symbolList = handle.getSymbolList();
		for (int i = 1; i < symbolList.size(); i++) {
			suffix.add(symbolList.get(i));
		}
	}

	public Item(
		NonTerminal reduction,
		List<Symbol> symbolsSeen,
		Symbol nextSymbol,
		List<Symbol> suffix,
		Terminal lookAheadSymbol
	) {
		this.reduction = reduction;
		this.symbolsSeen = symbolsSeen;
		this.nextSymbol = nextSymbol;
		if (ParserConstants.EPSILON.equals(nextSymbol)) {
			this.nextSymbol = null;
		}
		this.suffix = suffix;
		this.lookAheadSymbol = lookAheadSymbol;
	}

	public NonTerminal getReduction() {
		return reduction;
	}

	public List<Symbol> getSymbolsSeen() {
		return symbolsSeen;
	}

	public Symbol getNextSymbol() {
		return nextSymbol;
	}

	public List<Symbol> getSuffix() {
		return suffix;
	}

	public Terminal getLookAheadSymbol() {
		return lookAheadSymbol;
	}

	public Symbol getNextSymbolOrLookAhead() {
		if (nextSymbol == null) {
			return lookAheadSymbol;
		}
		return nextSymbol;
	}

	public Handle getHandle() {
		List<Symbol> symbolList = new ArrayList<Symbol>(symbolsSeen);
		if (nextSymbol != null) {
			symbolList.add(nextSymbol);
		}
		for (Symbol symbol : suffix) {
			symbolList.add(symbol);
		}
		return new Handle(symbolList);
	}

	public Item recognizeNextSymbol() {
		List<Symbol> symbolsSeen = new ArrayList<Symbol>(this.symbolsSeen);
		if (nextSymbol != null) {
			symbolsSeen.add(nextSymbol);
		}
		if (suffix.size() == 0) {
			return new Item(
				reduction, symbolsSeen, null, new ArrayList<Symbol>(), lookAheadSymbol
			);
		}
		return new Item(
			reduction,
			symbolsSeen,
			suffix.get(0),
			suffix.subList(1, suffix.size()),
			lookAheadSymbol
		);
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object == this) return true;
		if (object instanceof Item) {
			Item that = (Item)object;
			return equals(reduction, that.getReduction())
			&& equals(symbolsSeen, that.getSymbolsSeen())
			&& equals(nextSymbol, that.getNextSymbol())
			&& equals(suffix, that.getSuffix())
			&& equals(lookAheadSymbol, that.getLookAheadSymbol());
		}
		return false;
	}

	private boolean equals(Object object, Object that) {
		if (object == null) {
			if (that == null) {
				return true;
			}
			return false;
		}
		return object.equals(that);
	}

	@Override
	public int hashCode() {
		return reduction.hashCode();
	}

	@Override
	public String toString() {
		return reduction + "->"
		+ symbolsSeen + nextSymbol + suffix + "." + lookAheadSymbol;
	}
}
