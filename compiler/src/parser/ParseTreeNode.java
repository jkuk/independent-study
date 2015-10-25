package parser;
import lexer.Token;

import java.util.ArrayDeque;
import java.util.ArrayList;

import java.util.Deque;
import java.util.List;

public class ParseTreeNode {
	private Symbol symbol;
	private String lexeme;
	private List<ParseTreeNode> children;

	public ParseTreeNode(Token token) {
		this(new Terminal(token.getType()));
		lexeme = token.getLexeme();
	}

	public ParseTreeNode(Symbol symbol) {
		this(symbol, new ArrayDeque<ParseTreeNode>());
	}
	
	public ParseTreeNode(
		Symbol symbol, Deque<ParseTreeNode> parseTreeNodeStack
	) {
		this.symbol = symbol;
		children = new ArrayList<ParseTreeNode>();
		while (!parseTreeNodeStack.isEmpty()) {
			children.add(parseTreeNodeStack.poll());
		}
	}
	
	public Symbol getSymbol() {
		return symbol;
	}

	public List<ParseTreeNode> getChildren() {
		return children;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object == this) return true;
		if (object instanceof ParseTreeNode) {
			ParseTreeNode that = (ParseTreeNode)object;
			if (symbol.equals(that.getSymbol())
			&& children.size() == that.getChildren().size()) {
				for (int i = 0; i < children.size(); i++) {
					if (!children.get(i).equals(that.getChildren().get(i))) {
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
		return symbol.hashCode();
	}

	@Override
	public String toString() {
		return symbol + ": " + lexeme;
	}
}