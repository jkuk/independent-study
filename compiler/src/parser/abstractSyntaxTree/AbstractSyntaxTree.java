package parser.abstractSyntaxTree;
import parser.ParserConstants;

import java.util.ArrayDeque;
import java.util.LinkedList;

import java.util.Deque;

public class AbstractSyntaxTree {
	private LinkedList<Node> rootBranch;
	private SymbolTable symbolTable;

	public AbstractSyntaxTree(LinkedList<Node> rootBranch) {
		this.rootBranch = rootBranch;
		initializeDepthForEachNode();
		initializeSymbolTable();
		checkTypes();
	}

	private void initializeDepthForEachNode() {
		Deque<Node> nodeStack = new ArrayDeque<Node>();

		for (Node root : rootBranch) {
			nodeStack.push(root);
		}
		while (!nodeStack.isEmpty()) {
			Node node = nodeStack.pop();
			for (Node child : node.getChildren()) {
				child.setDepth(node.getDepth() + 1);
				nodeStack.push(child);
			}
		}
	}

	private void initializeSymbolTable() {
		// make this take in the operator.txt
		symbolTable = new SymbolTable();
		LinkedList<String> scopeNameList = new LinkedList<String>();
		Deque<Node> nodeStack = new ArrayDeque<Node>();
		Deque<Integer> depthStack = new ArrayDeque<Integer>();
		depthStack.push(-1);

		for (Node root : rootBranch) {
			nodeStack.push(root);
		}
		while (!nodeStack.isEmpty()) {
			Node node = nodeStack.pop();
			while (node.getDepth() <= depthStack.peek()) {
				depthStack.pop();
				scopeNameList.removeLast();
			}
			for (Node child : node.getChildren()) {
				nodeStack.push(child);
			}
			if (node.getAnnotation() == ParserConstants.CHECK_IDENTIFIER
			&& !symbolTable.contains(node.getLexeme())) {
				throw new IllegalArgumentException();
			}
			else if (node.getAnnotation() != ParserConstants.NONE) {
				String scope = symbolTable.addSymbol(node, scopeNameList);
				if (scope != null) {
					scopeNameList.addLast(scope);
					depthStack.push(node.getDepth());
				}
			}
		}
	}

	private void checkTypes() {

	}

	public SymbolTable getSymbolTable() {
		return symbolTable;
	}
	public String toString() {
		Deque<Node> nodeStack = new ArrayDeque<Node>();

		for (Node root : rootBranch) {
			nodeStack.push(root);
		}
		String string = "AST:\n";
		while (!nodeStack.isEmpty()) {
			System.out.println("tick");
			Node node = nodeStack.pop();
			for (Node child : node.getChildren()) {
				nodeStack.push(child);
			}
			for (int i = 0; i < node.getDepth(); i++) {
				string += "  ";
			}
			string += node + "\n";
		}
		return string;
	}
}
