package parser.abstractSyntaxTree;
import parser.ParserConstants;

public class Variable {
	private String name;
	private String dataType;

	public Variable(Node node) {
		name = node.getLexeme();
		// System.out.println(node);
		// System.out.println(node.get(ParserConstants.DATA_TYPE_BRANCH));
		// System.out.println(node.get(ParserConstants.DATA_TYPE_BRANCH)
		// .get(ParserConstants.DATA_TYPE_INDEX));
		dataType = node.getBranch(ParserConstants.DATA_TYPE_BRANCH)
		.get(ParserConstants.DATA_TYPE_INDEX).getLexeme();
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}
}
