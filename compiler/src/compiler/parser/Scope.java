package compiler.parser;

import java.util.LinkedList;
import java.util.HashMap;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;
import java.util.List;

public class Scope {
	private String name;
	private Map<String, Variable> variableMap;
	private Map<String, Array> arrayMap;
	private Map<String, Function> functionMap;
	private Set<String> dataTypeMap;

	public Scope(String name) {
		this.name = name;
		variableMap = new HashMap<String, Variable>();
		arrayMap = new HashMap<String, Array>();
		functionMap = new HashMap<String, Function>();
		dataTypeMap = new HashSet<String>();
		dataTypeMap.add("String");
		dataTypeMap.add("Integer");
		dataTypeMap.add("None");
	}

	public Scope getScope(String name) {
		return functionMap.get(name).getScope();
	}

	public boolean addVariable(AbstractSyntaxTreeNode node) {
		if (contains(node.getLexeme())) {
			return false;
		}
		variableMap.put(node.getLexeme(), new Variable(node));
		return true;
	}

	public boolean addArray(AbstractSyntaxTreeNode node) {
		if (contains(node.getLexeme())) {
			return false;
		}
		arrayMap.put(node.getLexeme(), new Array(node));
		return true;
	}

	public boolean addFunction(AbstractSyntaxTreeNode node) {
		if (contains(node.getLexeme())) {
			return false;
		}
		functionMap.put(node.getLexeme(), new Function(node));
		return true;
	}

	public boolean addOperator(
		String symbol, String returnType, LinkedList<String> parameterTypeList
	) {
		if (contains(symbol)) {
			return false;
		}
		functionMap.put(
			symbol, new Function(symbol, returnType, parameterTypeList)
		);
		return true;
	}

	public boolean contains(String name) {
		return variableMap.containsKey(name)
		|| arrayMap.containsKey(name)
		|| functionMap.containsKey(name)
		|| dataTypeMap.contains(name);
	}

	public String toString() {
		return name + ":\n\t" + variableMap + "\n\t" + arrayMap + "\n\t" + functionMap;
	}
}
