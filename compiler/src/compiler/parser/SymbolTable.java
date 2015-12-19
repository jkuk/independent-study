package compiler.parser;

import java.util.LinkedList;
import java.util.List;

public class SymbolTable {
	private Scope globalScope;

	public SymbolTable() {
		globalScope = new Scope("");
	}

	public String addSymbol(
		AbstractSyntaxTreeNode node, LinkedList<String> scopeNameList
	) {
		Scope scope = getScope(scopeNameList);
		if (scope.contains(node.getLexeme())) {
			if (node.getAnnotation() != ParserConstants.NONE) {
				throw new IllegalArgumentException();
			}
		}
		else {
			switch (node.getAnnotation()) {
				case ParserConstants.VARIABLE_DECLARATION:
				case ParserConstants.VARIABLE_DECLARATION_AND_INITIALIZATION:
				scope.addVariable(node);
				break;

				case ParserConstants.ARRAY_DECLARATION:
				case ParserConstants.ARRAY_DECLARATION_AND_INITIALIZATION:
				scope.addArray(node);
				break;

				case ParserConstants.FUNCTION_DECLARATION:
				case ParserConstants.FUNCTION_DECLARATION_AND_INITIALIZATION:
				scope.addFunction(node);
				return node.getLexeme();

				default:
				throw new IllegalArgumentException();
			}
		}
		return null;
	}

	public void addOperator(
		String symbol, String returnType, LinkedList<String> parameterTypeList
	) {
		globalScope.addOperator(symbol, returnType, parameterTypeList);
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

	public boolean contains(String name, LinkedList<String> scopeNameList) {
		Scope scope = globalScope;
		if (scope.contains(name)) {
			return true;
		}
		for (String scopeName : scopeNameList) {
			scope = scope.getScope(scopeName);
			if (scope == null) {
				return false;
			}
			if (scope.contains(name)) {
				return true;
			}
		}
		return false;
	}

	public String toString() {
		return globalScope.toString();
	}
}
