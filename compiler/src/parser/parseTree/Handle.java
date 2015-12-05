package parser.parseTree;

import java.util.ArrayList;
import java.util.List;

public class Handle {
	private List<Symbol> symbolList;

	public Handle(String symbolListString) {
		symbolList = new ArrayList<Symbol>();
		if (!symbolListString.isEmpty()) {
			String[] symbolStringArray = symbolListString.trim().split(" ");
			for (String symbolString : symbolStringArray) {
				String symbol = symbolString.trim();
				if (Character.isUpperCase(symbol.charAt(0))) {
					symbolList.add(new Terminal(symbol));
				}
				else {
					symbolList.add(new NonTerminal(symbol));
				}
			}
		}
	}

	public Handle(List<Symbol> symbolList) {
		this.symbolList = symbolList;
	}

	public int getSize() {
		return symbolList.size();
	}

	public List<Symbol> getSymbolList() {
		return symbolList;
	}

	public Symbol getFirstSymbol() {
		if (symbolList.isEmpty()) {
			return null;
		}
		return symbolList.get(0);
	}

	public Handle removeFirstSymbol() {
		if (symbolList.size() <= 1) {
			return new Handle(new ArrayList<Symbol>());
		}
		return new Handle(symbolList.subList(1, symbolList.size()));
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object == this) return true;
		if (object instanceof Handle) {
			Handle that = (Handle)object;
			if (getSize() == that.getSize()) {
				for (int i = 0; i < getSize(); i++) {
					if (!symbolList.get(i)
					.equals(that.getSymbolList().get(i))) {
						return false;
					}
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return symbolList.get(symbolList.size() - 1).hashCode();
	}

	@Override
	public String toString() {
		return symbolList.toString();
	}
}
