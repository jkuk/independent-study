package compiler.parser;

import java.util.ArrayDeque;
import java.util.LinkedList;

import java.util.Deque;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class AbstractSyntaxTree {
	private LinkedList<AbstractSyntaxTreeNode> rootBranch;
	private SymbolTable symbolTable;

	public AbstractSyntaxTree(
		LinkedList<AbstractSyntaxTreeNode> rootBranch,
		String operatorsFileName
	) throws FileNotFoundException, IOException {
		this.rootBranch = rootBranch;
		initializeDepthForEachNode();
		initializeSymbolTable();
		// checkTypes(operatorsFileName);
	}

	private void initializeDepthForEachNode() {
		Deque<AbstractSyntaxTreeNode> nodeStack
		= new ArrayDeque<AbstractSyntaxTreeNode>();
		for (AbstractSyntaxTreeNode root : rootBranch) {
			nodeStack.push(root);
		}
		while (!nodeStack.isEmpty()) {
			AbstractSyntaxTreeNode node = nodeStack.pop();
			for (AbstractSyntaxTreeNode child : node.getChildren()) {
				child.setDepth(node.getDepth() + 1);
				nodeStack.push(child);
			}
		}
	}

	private void initializeSymbolTable() {
		symbolTable = new SymbolTable();
		LinkedList<String> scopeNameList = new LinkedList<String>();
		Deque<AbstractSyntaxTreeNode> nodeStack
		= new ArrayDeque<AbstractSyntaxTreeNode>();
		Deque<Integer> depthStack = new ArrayDeque<Integer>();
		depthStack.push(-1);

		for (int i = rootBranch.size() - 1; i >= 0; i--) {
			nodeStack.push(rootBranch.get(i));
		}
		while (!nodeStack.isEmpty()) {
			AbstractSyntaxTreeNode node = nodeStack.pop();
			while (node.getDepth() <= depthStack.peekLast()) {
				depthStack.pop();
				scopeNameList.removeLast();
			}
			LinkedList<AbstractSyntaxTreeNode> children = node.getChildren();
			for (int i = children.size() - 1; i >= 0; i--) {
				nodeStack.push(children.get(i));
			}
			char annotation = node.getAnnotation();
			if ((annotation == ParserConstants.VARIABLE_CALL
			|| annotation == ParserConstants.VARIABLE_INITIALIZATION)
			&& !symbolTable.contains(node.getLexeme(), scopeNameList)) {
				throw new IllegalArgumentException();
			}
			else if (annotation == ParserConstants.VARIABLE_DECLARATION
			|| annotation == ParserConstants.VARIABLE_DECLARATION_AND_INITIALIZATION
			|| annotation == ParserConstants.ARRAY_DECLARATION
			|| annotation == ParserConstants.ARRAY_DECLARATION_AND_INITIALIZATION
			|| annotation == ParserConstants.FUNCTION_DECLARATION
			|| annotation == ParserConstants.FUNCTION_DECLARATION_AND_INITIALIZATION) {
				String scope = symbolTable.addSymbol(node, scopeNameList);
				if (scope != null) {
					scopeNameList.addLast(scope);
					depthStack.push(node.getDepth());
				}
			}
		}
	}

	private void checkTypes(String operatorsFileName)
	throws FileNotFoundException, IOException {
		addOperatorsToSymbolTable(operatorsFileName);
		// for (int i = rootBranch.size() - 1; i >= 0; i--) {
		// 	nodeStack.push(rootBranch.get(i));
		// }
		// while (!nodeStack.isEmpty()) {
		// 	AbstractSyntaxTreeNode node = nodeStack.pop();
		// 	char annotation = node.getAnnotation();
		// 	if (annotation == ParserConstants.INITIALIZE_VARIABLE
		// 	|| annotation == ParserConstants.DECLARE_AND_INITIALIZE_VARIABLE
		// 	|| annotation == ParserConstants.CALL_ARRAY
		// 	|| annotation == ParserConstants.INITIALIZE_ARRAY
		// 	|| annotation == ParserConstants.DECLARE_AND_INITIALIZE_ARRAY
		// 	|| annotation == ParserConstants.INITIALIZE_FUNCTION
		// 	|| annotation == ParserConstants.DECLARE_AND_INITIALIZE_FUNCTION
		// 	|| annotation == ParserConstants.CALL_FUNCTION
		// 	|| annotation == ParserConstants.CALL_FUNCTION_NO_ARGUMENTS) {
		// 		checkType();
		// 	}
		// 	else {
		// 		LinkedList<AbstractSyntaxTreeNode> children = node.getChildren();
		// 		for (int i = children.size() - 1; i >= 0; i--) {
		// 			nodeStack.push(children.get(i));
		// 		}
		// 	}
		// }
	}

	// private String checkType() {
	// 	if (annotation == ParserConstants.CALL_NUMBER
	// 	|| annotation == ParserConstants.CALL_STRING) {
	// 		return type;
	// 	}
	// 	else if (annotation == ParserConstants.CALL_VARIABLE) {
	// 		return symbolTable.getType();
	// 	}
	// 	char annotation = node.getAnnotation();
	// 	List<String> parameterTypeList = symbolTable.get(operator).getParameterTypeList();
	// 	throw new IllegalArgumentException();
	// }

	private void addOperatorsToSymbolTable(String operatorsFileName)
	throws FileNotFoundException, IOException {
		BufferedReader bufferedReader = new BufferedReader(
			new FileReader(operatorsFileName)
		);
		String line;
		while ((line = bufferedReader.readLine()) != null) {
			String[] splitLine = line.split(" ");
			String symbol = splitLine[0];
			String returnType = splitLine[1];
			LinkedList<String> parameterTypeList = new LinkedList<String>();
			for (int i = 2; i < splitLine.length; i++) {
				parameterTypeList.add(splitLine[i]);
			}
			symbolTable.addOperator(symbol, returnType, parameterTypeList);
		}
	}

	public LinkedList<AbstractSyntaxTreeNode> getRootBranch() {
		return rootBranch;
	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}

	public String toString() {
		Deque<AbstractSyntaxTreeNode> nodeStack = new ArrayDeque<AbstractSyntaxTreeNode>();
		for (int i = rootBranch.size() - 1; i >= 0; i--) {
			nodeStack.push(rootBranch.get(i));
		}
		String string = "";
		while (!nodeStack.isEmpty()) {
			AbstractSyntaxTreeNode node = nodeStack.pop();
			LinkedList<AbstractSyntaxTreeNode> children = node.getChildren();
			for (int i = children.size() - 1; i >= 0; i--) {
				nodeStack.push(children.get(i));
			}
			for (int i = 0; i < node.getDepth(); i++) {
				string += "  ";
			}
			string += node + "\n";
		}
		return string;
	}
}
