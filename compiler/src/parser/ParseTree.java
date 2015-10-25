package parser;

import java.util.Deque;
import java.util.ArrayDeque;

public class ParseTree {
	private ParseTreeNode root;

	public ParseTree(ParseTreeNode node) {
		root = node;
	}

	public ParseTreeNode getRoot() {
		return root;
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) return false;
		if (object == this) return true;
		if (object instanceof ParseTree) {
			ParseTree that = (ParseTree)object;
			return root.equals(that.getRoot());
		}
		return false;
	}

	@Override
	public String toString() {
		String string = "";
		Deque<ParseTreeNode> nodeQueue = new ArrayDeque<ParseTreeNode>();
		nodeQueue.offer(root);
		int count = 1;
		int depth = 0;
		int nextCount = root.getChildren().size();
		while (!nodeQueue.isEmpty()) {
			ParseTreeNode node = nodeQueue.poll();
			for (int i = 0; i < depth; i++) {
				string += "  ";
			}
			string += node + "\n";

			if (--count == 0) {
				depth++;
				count = nextCount;
				nextCount = 0;
			}
			for (ParseTreeNode child : node.getChildren()) {
				nodeQueue.offer(child);
				nextCount += child.getChildren().size();
			}
		}
		return string;
	}
}