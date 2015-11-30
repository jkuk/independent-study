package parser.abstractSyntaxTree;
import parser.ParserConstants;

import java.util.LinkedList;

import java.util.List;

public class Array extends Variable {
	private List<Integer> dimensionList;

	public Array(Node node) {
		super(node);
		String dataType = "";
		dimensionList = new LinkedList<Integer>();

		Node dataTypeNode
		= node.get(ParserConstants.DATA_TYPE_BRANCH);
		while (dataTypeNode != null) {
			dataType += dataTypeNode.getLexeme();
			Node evaluationNode
			= dataTypeNode.get(ParserConstants.DIMENSION_INDEX);
			if (evaluationNode != null) {
				dimensionList.add(Integer.parseInt(evaluationNode.getLexeme()));
			}

			dataTypeNode
			= dataTypeNode.get(ParserConstants.DATA_TYPE_BRANCH);
		}
		setDataType(dataType);
	}

	public String toString() {
		return super.toString() + " " + dimensionList;
	}
}