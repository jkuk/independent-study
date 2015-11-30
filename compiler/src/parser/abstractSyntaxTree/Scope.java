package parser.abstractSyntaxTree;
import parser.ParserConstants;

import java.util.LinkedList;
import java.util.HashMap;

import java.util.Map;

public class Scope {
	private String name;
	private Map<String, Variable> variableMap;
	private Map<String, Array> arrayMap;
	private Map<String, Function> functionMap;

	public Scope(String name) {
		this.name = name;
		variableMap = new HashMap<String, Variable>();
		arrayMap = new HashMap<String, Array>();
		functionMap = new HashMap<String, Function>();
	}

	public Scope getScope(String name) {
		return functionMap.get(name).getScope();
	}

	public boolean addVariable(Node node) {
		if (contains(node.getLexeme())) {
			return false;
		}
		variableMap.put(node.getLexeme(), new Variable(node));
		return true;
	}

	public boolean addArray(Node node) {
		if (contains(node.getLexeme())) {
			return false;
		}
		arrayMap.put(node.getLexeme(), new Array(node));
		return true;
	}

	public boolean addFunction(Node node) {
		if (contains(node.getLexeme())) {
			return false;
		}
		functionMap.put(node.getLexeme(), new Function(node));
		return true;
	}

	public boolean contains(String name) {
		return variableMap.containsKey(name)
		|| arrayMap.containsKey(name)
		|| functionMap.containsKey(name);
	}

	public String toString() {
		return name + ":\n" + variableMap + "\n" + arrayMap + "\n" + functionMap;
	}
}
