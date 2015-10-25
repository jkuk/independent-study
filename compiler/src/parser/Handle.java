package parser;

import java.util.ArrayList;
import java.util.List;

public class Handle {
	private List<Symbol> symbolList;

	public Handle(String symbolListString) {
		String[] symbolStringArray = symbolListString.trim().split(" ");
		symbolList = new ArrayList<Symbol>();
		for (String symbolString : symbolStringArray) {
			String symbol = symbolString.trim();
			if (Character.isUpperCase(symbol.charAt(0))) {
				symbolList.add(new NonTerminal(symbol));
			}
			else {
				symbolList.add(new Terminal(symbol));
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
		return symbolList.get(0);
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