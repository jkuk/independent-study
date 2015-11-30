package parser.abstractSyntaxTree;
import parser.ParserConstants;

import java.util.LinkedList;

public class SymbolTable {
	private Scope globalScope;

	public SymbolTable() {
		globalScope = new Scope("");
	}

	public String addSymbol(Node node, LinkedList<String> scopeNameList) {
		Scope scope = getScope(scopeNameList);
		if (scope.contains(node.getLexeme())) {
			if (node.getAnnotation() != ParserConstants.NONE) {
				throw new IllegalArgumentException();
			}
		}
		else {
			switch (node.getAnnotation()) {
				case ParserConstants.NEW_VARIABLE:
				scope.addVariable(node);
				break;

				case ParserConstants.NEW_ARRAY:
				scope.addArray(node);
				break;

				case ParserConstants.NEW_FUNCTION:
				scope.addFunction(node);
				return node.getLexeme();

				default:
				throw new IllegalArgumentException();
			}
		}
		return null;
	}

	private Scope getScope(LinkedList<String> scopeNameList) {
		Scope scope = globalScope;
		for (String scopeName : scopeNameList) {
			scope = scope.getScope(scopeName);
		}
		return scope;
	}

	public boolean contains(String name) {
		return globalScope.contains(name);
	}

	public String toString() {
		return globalScope.toString();
	}
}
