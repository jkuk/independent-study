package compiler.parser;

import java.util.LinkedList;

import java.util.List;

public class Function extends Variable {
	private LinkedList<String> parameterTypeList;
	private Scope scope;

	public Function(AbstractSyntaxTreeNode node) {
		super(node);
		parameterTypeList = new LinkedList<String>();
		for (AbstractSyntaxTreeNode parameterNode
		: node.getBranch(ParserConstants.PARAMETERS_BRANCH)) {
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

	public Function(
		String symbol, String returnType, LinkedList<String> parameterTypeList
	) {
		super(symbol, returnType);
		this.parameterTypeList = parameterTypeList;
	}

	public Scope getScope() {
		return scope;
	}

	public String toString() {
		return super.toString() + " " + parameterTypeList + " " + scope;
	}
}
