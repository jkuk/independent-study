package parser.abstractSyntaxTree;
import parser.ParserConstants;

import java.util.List;
import java.util.LinkedList;

public class Array extends Variable {
	private List<Integer> dimensions;
	public Array(Node node) {
		super(node);
		String dataType = "";
		dimensions = new LinkedList<Integer>();

		Node dataTypeNode
		= node.get(ParserConstants.DATA_TYPE_BRANCH);
		while (dataTypeNode != null) {
			dataType += dataTypeNode.getLexeme();
			// System.out.println("INSIDE: " + dataTypeNode.get(ParserConstants.DIMENSION_INDEX));
			Node evaluationNode
			= dataTypeNode.get(ParserConstants.DIMENSION_INDEX);
			if (evaluationNode != null) {
				dimensions.add(Integer.parseInt(evaluationNode.getLexeme()));
			}

			dataTypeNode
			= dataTypeNode.get(ParserConstants.DATA_TYPE_BRANCH);
		}
		setDataType(dataType);
	}
}
