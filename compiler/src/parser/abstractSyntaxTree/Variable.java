package parser.abstractSyntaxTree;
import parser.ParserConstants;

public class Variable {
	private String name;
	private String dataType;

	public Variable(Node node) {
		name = node.getLexeme();
		dataType = node.getBranch(ParserConstants.DATA_TYPE_BRANCH)
		.get(ParserConstants.DATA_TYPE_INDEX).getLexeme();
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String toString() {
		return name + ": " + dataType;
	}
}
