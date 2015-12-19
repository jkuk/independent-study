package compiler.parser;

public class Variable {
	private String name;
	private String dataType;

	public Variable(AbstractSyntaxTreeNode node) {
		name = node.getLexeme();
		dataType = node.getBranch(ParserConstants.DATA_TYPE_BRANCH)
		.get(ParserConstants.DATA_TYPE_INDEX).getLexeme();
	}

	public Variable(String name, String dataType) {
		this.name = name;
		this.dataType = dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String toString() {
		return name + ": " + dataType;
	}
}
