package compiler.parser;

import java.util.LinkedList;

public class AbstractSyntaxTreeNode {
	private String lexeme;
	private int lineNumber;
	private LinkedList<AbstractSyntaxTreeNode>[] branchArray;
	private char annotation;
	private int depth;

	public static LinkedList<AbstractSyntaxTreeNode> buildSingleton(
		AbstractSyntaxTreeNode symbolNode,
		LinkedList<AbstractSyntaxTreeNode>... branchArray
	) {
		return buildSingleton(symbolNode, symbolNode.getAnnotation(), branchArray);
	}

	public static LinkedList<AbstractSyntaxTreeNode> buildSingleton(
		AbstractSyntaxTreeNode symbolNode,
		char annotation,
		LinkedList<AbstractSyntaxTreeNode>... branchArray
	) {
		LinkedList<AbstractSyntaxTreeNode> nodeList
		= new LinkedList<AbstractSyntaxTreeNode>();
		AbstractSyntaxTreeNode node
		= new AbstractSyntaxTreeNode(symbolNode, branchArray);
		node.setAnnotation(annotation);
		nodeList.add(node);
		return nodeList;
	}

	protected AbstractSyntaxTreeNode(AbstractSyntaxTreeNode symbolNode, LinkedList<AbstractSyntaxTreeNode>... branchArray) {
		this(symbolNode);
		this.branchArray = branchArray;
	}

	protected AbstractSyntaxTreeNode(AbstractSyntaxTreeNode node) {
		this(node.getLexeme(), node.getLineNumber());
	}

	public static LinkedList<AbstractSyntaxTreeNode> buildSingleton(
		String lexeme, int lineNumber
	) {
		LinkedList<AbstractSyntaxTreeNode> nodeList
		= new LinkedList<AbstractSyntaxTreeNode>();
		nodeList.add(new AbstractSyntaxTreeNode(lexeme, lineNumber));
		return nodeList;
	}

	protected AbstractSyntaxTreeNode(String lexeme, int lineNumber) {
		this.lexeme = lexeme;
		this.lineNumber = lineNumber;
		branchArray = new LinkedList[0];
		annotation = ParserConstants.NONE;
		depth = 0;
	}

	public static LinkedList<AbstractSyntaxTreeNode> extendListWithNode(
		LinkedList<AbstractSyntaxTreeNode> list,
		LinkedList<AbstractSyntaxTreeNode> node
	) {
		list.addAll(node);
		return list;
	}

	public void setAnnotation(char annotation) {
		this.annotation = annotation;
	}

	public void setDepth(int depth) {
		this.depth = depth;
	}

	public String getLexeme() {
		return lexeme;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public char getAnnotation() {
		return annotation;
	}

	public int getDepth() {
		return depth;
	}

	public LinkedList<AbstractSyntaxTreeNode> getBranch(int i) {
		return branchArray[i];
	}

	public AbstractSyntaxTreeNode get(int i) {
		if (i >= branchArray.length) {
			return null;
		}
		return branchArray[i].get(0);
	}

	public LinkedList<AbstractSyntaxTreeNode> getChildren() {
		LinkedList<AbstractSyntaxTreeNode> children
		= new LinkedList<AbstractSyntaxTreeNode>();
		for (LinkedList<AbstractSyntaxTreeNode> branch : branchArray) {
			children.addAll(branch);
		}
		return children;
	}

	public String toString() {
		return lexeme + ":" + annotation + "@" +depth;
	}
}
