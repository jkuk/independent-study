package parser.abstractSyntaxTree;
import parser.ParserConstants;

import java.util.LinkedList;

public class Node {
	private String lexeme;
	private int lineNumber;
	private LinkedList<Node>[] branchArray;
	private char annotation;
	private int depth;

	public static LinkedList<Node> buildSingleton(
		Node symbolNode, LinkedList<Node>... branchArray
	) {
		// LinkedList<Node> nodeList = new LinkedList<Node>();
		// Node node = new Node(symbolNode, branchArray);
		// nodeList.add(node);
		// return nodeList;
		return buildSingleton(symbolNode, symbolNode.getAnnotation(), branchArray);
	}

	public static LinkedList<Node> buildSingleton(
		Node symbolNode, char annotation, LinkedList<Node>... branchArray
	) {
		LinkedList<Node> nodeList = new LinkedList<Node>();
		Node node = new Node(symbolNode, branchArray);
		node.setAnnotation(annotation);
		nodeList.add(node);
		return nodeList;
	}

	protected Node(Node symbolNode, LinkedList<Node>... branchArray) {
		this(symbolNode);
		// this.branchArray = new LinkedList<Node>[branchArray.length];
		// for (int i = 0; i < branchArray.length; i++) {
		// 	this.branchArray[i] = branchArray[i];
		// }
		this.branchArray = branchArray;
		System.out.println("branch array: " + java.util.Arrays.toString(branchArray));
	}

	protected Node(Node node) {
		this(node.getLexeme(), node.getLineNumber());
	}

	public static LinkedList<Node> buildSingleton(
		String lexeme, int lineNumber
	) {
		LinkedList<Node> nodeList = new LinkedList<Node>();
		nodeList.add(new Node(lexeme, lineNumber));
		return nodeList;
	}

	protected Node(String lexeme, int lineNumber) {
		this.lexeme = lexeme;
		this.lineNumber = lineNumber;
		branchArray = new LinkedList[0];
		annotation = ParserConstants.NONE;
		depth = 0;
	}

	public static LinkedList<Node> extendListWithNode(
		LinkedList<Node> list, LinkedList<Node> node
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

	public LinkedList<Node> getBranch(int i) {
		return branchArray[i];
	}

	public Node get(int i) {
		if (i >= branchArray.length) {
			return null;
		}
		return branchArray[i].get(0);
	}

	public LinkedList<Node> getChildren() {
		System.out.println("getting children for: " + lexeme);
		LinkedList<Node> children = new LinkedList<Node>();
		for (LinkedList<Node> branch : branchArray) {
			System.out.println("branch: " + branch);
			children.addAll(branch);
		}
		System.out.println("children: " + children);
		return children;
	}

	public String toString() {
		return lexeme + ": " + lineNumber + ":" + annotation + ":" +depth;
	}
}
