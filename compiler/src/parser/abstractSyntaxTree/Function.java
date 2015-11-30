package parser.abstractSyntaxTree;
import parser.ParserConstants;

import java.util.LinkedList;

import java.util.List;

public class Function extends Variable {
	private LinkedList<String> parameterTypeList;
	private Scope scope;

	public Function(Node node) {
		super(node);
		parameterTypeList = new LinkedList<String>();
		for (Node parameterNode
		: node.getBranch(ParserConstants.PARAMETERS_BRANCH)) {
			System.out.println("looking at parameterNode:  " + parameterNode);
			parameterTypeList.add(
				parameterNode.get(ParserConstants.DATA_TYPE_INDEX)
				.getLexeme()
			);
		}
		setDataType(
			node.getBranch(ParserConstants.DATA_TYPE_BRANCH)
			.get(ParserConstants.DATA_TYPE_INDEX).getLexeme()
		);
		scope = new Scope(node.getLexeme());
	}

	public Scope getScope() {
		return scope;
	}
}
