package compiler.generator;
import compiler.parser.AbstractSyntaxTree;
import compiler.parser.AbstractSyntaxTreeNode;
import compiler.parser.ParserConstants;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.BufferedWriter;
import java.io.FileWriter;

import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

public class Translator {
	private String initializations;
	private HashMap<String, String> translationMap;
	private String stringInitializations;
	private String generatedVariable;
	private int variableCounter;

	private final String END_OF_LINE = ";\n";

	public Translator(
		String initializationsFileName, String translationsFileName
	) {
		try {
			initializeInitializations(initializationsFileName);
			initializeTranslationMap(translationsFileName);
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		generatedVariable = "generatedVariable";
		variableCounter = 0;
	}

	private void initializeInitializations(String initializationsFileName)
	throws FileNotFoundException, IOException {
		BufferedReader initializationsFile = new BufferedReader(
			new FileReader(initializationsFileName)
		);
		initializations = "";
		for (String line = ""; line != null;
		line = initializationsFile.readLine()) {
			initializations += line + "\n";
		}
		initializationsFile.close();
	}

	private void initializeTranslationMap(String translationsFileName)
	throws FileNotFoundException, IOException {
		BufferedReader translationsFile = new BufferedReader(
			new FileReader(translationsFileName)
		);

		translationMap = new HashMap<String, String>();
		String line;
		String symbol = null;
		String instruction = "";
		while ((line = translationsFile.readLine()) != null) {
			if (line.length() > 2
			&& line.charAt(0) == '-'
			&& line.charAt(1) == '>') {
				symbol = line.substring(2).trim();
				instruction = "";
			}
			else if (line.length() >= 2
			&& line.charAt(line.length() - 2) == '<'
			&& line.charAt(line.length() - 1) == '-') {
				instruction += line.substring(0, line.length() - 2);
				translationMap.put(symbol, instruction);
			}
			else {
				instruction += line + '\n';
			}
		}
		translationsFile.close();
	}

	public String translate(AbstractSyntaxTree tree) {
		stringInitializations = "";
		String compiledCode = translate(tree.getRootBranch());
		return initializations + "\n"
		+ stringInitializations + "\n"
		+ compiledCode;
	}

	private String translate(LinkedList<AbstractSyntaxTreeNode> branch) {
		String string = "";
		for (AbstractSyntaxTreeNode node : branch) {
			string += translate(node);
		}
		return string;
	}

	private String translate(AbstractSyntaxTreeNode node) {
		if (node == null) {
			return "";
		}
		String translation = translateAnnotation(node);
		if (translation != null) {
			return translation;
		}
		return translateSymbol(node);
	}

	private String translateAnnotation(AbstractSyntaxTreeNode node) {
		char annotation = node.getAnnotation();
		if (annotation == ParserConstants.NONE) {
			return null;
		}
		else if (annotation == ParserConstants.NUMBER_CALL
		|| annotation == ParserConstants.VARIABLE_CALL) {
			if (node.getLexeme().equals("String")) {
				return "string ";
			}
			if (node.getLexeme().equals("Integer")) {
				return "int ";
			}
			if (node.getLexeme().equals("None")) {
				return "void ";
			}
			return node.getLexeme();
		}
		else if (annotation == ParserConstants.STRING_CALL) {
			stringInitializations += translateStringCall(node);
			return generatedVariable + variableCounter++;
		}
		else if (annotation == ParserConstants.DECLARATION_OR_INITIALIZATION) {
			return translateDeclarationOrInitialization(node);
		}
		else if (annotation == ParserConstants.VARIABLE_DECLARATION) {
			return translateVariableDeclaration(node);
		}
		else if (annotation == ParserConstants.VARIABLE_INITIALIZATION) {
			return translateVariableInitialization(node);
		}
		else if (annotation
		== ParserConstants.VARIABLE_DECLARATION_AND_INITIALIZATION) {
			return translateVariableDeclarationAndInitialization(node);
		}
		else if (annotation == ParserConstants.ARRAY_CALL) {
			return translateArrayCall(node);
		}
		if (annotation == ParserConstants.ARRAY_DECLARATION) {
			return translateArrayDeclaration(node);
		}
		else if (annotation == ParserConstants.FUNCTION_CALL) {
			return translateFunctionCall(node);
		}
		else if (annotation
		== ParserConstants.FUNCTION_DECLARATION_AND_INITIALIZATION) {
			return translateFunctionDeclarationAndInitialization(node);
		}
		return null;
	}

	private String translateStringCall(AbstractSyntaxTreeNode node) {
		String lexeme = node.getLexeme();
		String newVariable = generatedVariable + variableCounter;
		return "string " + newVariable + END_OF_LINE
		+ newVariable + ".length = getLength(\"" + lexeme + "\")" + END_OF_LINE
		+ newVariable + ".lexeme = \"" + lexeme + "\"" + END_OF_LINE;
	}

	private String translateDeclarationOrInitialization(AbstractSyntaxTreeNode node) {
		AbstractSyntaxTreeNode next = node.get(0);
		char annotation = next.getAnnotation();
		if (annotation == ParserConstants.VARIABLE_DECLARATION) {
			return translateVariableDeclaration(next);
		}
		else if (annotation == ParserConstants.VARIABLE_INITIALIZATION) {
			return translateVariableInitialization(node);
		}
		else if (annotation
		== ParserConstants.VARIABLE_DECLARATION_AND_INITIALIZATION) {
			return translateVariableDeclarationAndInitialization(node);
		}
		else if (annotation == ParserConstants.ARRAY_DECLARATION) {
			return translateArrayDeclaration(next);
		}
		else if (annotation
		== ParserConstants.FUNCTION_DECLARATION_AND_INITIALIZATION) {
			return translateFunctionDeclarationAndInitialization(next);
		}
		return "";
	}

	private String translateVariableDeclaration(AbstractSyntaxTreeNode node) {
		return translate(node.get(0)) + node.getLexeme();
	}

	private String translateVariableInitialization(
		AbstractSyntaxTreeNode node
	) {
		return node.get(0).getLexeme() + " = "
		+ translate(node.getBranch(0).get(1)) + END_OF_LINE;
	}

	private String translateVariableDeclarationAndInitialization(
		AbstractSyntaxTreeNode node
	) {
		return translateVariableDeclaration(node.get(0)) + " = "
		+ translate(node.getBranch(0).get(1)) + END_OF_LINE;
	}

	private String translateArrayCall(AbstractSyntaxTreeNode node) {
		String array = "";
		AbstractSyntaxTreeNode operator = node;
		while (operator.get(0) != null) {
			array += "[" + translate(operator.getBranch(0).get(1)) + "]";
			operator = operator.get(0);
		}
		return operator.getLexeme() + array;
	}

	private String translateArrayDeclaration(AbstractSyntaxTreeNode node) {
		String array = "";
		AbstractSyntaxTreeNode operator = node.get(0);
		while (operator.get(0) != null) {
			array = "[" + translate(operator.getBranch(0).get(1)) + "]" + array;
			operator = operator.get(0);
		}
		return translate(operator) + node.getLexeme() + array + END_OF_LINE;
	}

	private String translateFunctionCall(AbstractSyntaxTreeNode node) {
		String function = node.getLexeme().toLowerCase() + "(";
		LinkedList<AbstractSyntaxTreeNode> argumentsBranch
		= node.getBranch(ParserConstants.ARGUMENTS_BRANCH);
		int i;
		for (i = 0; i < argumentsBranch.size() - 1; i++) {
			function += translate(argumentsBranch.get(i))+ ", ";
		}
		if (argumentsBranch.size() > 0) {
			function += translate(argumentsBranch.get(i));
		}
		function += ")";
		return function;
	}

	private String translateFunctionDeclarationAndInitialization(
		AbstractSyntaxTreeNode node
	) {
		String returnType
		= node.get(ParserConstants.DATA_TYPE_BRANCH).getLexeme();
		String signature = node.getLexeme().toLowerCase() + "(";
		LinkedList<AbstractSyntaxTreeNode> parametersBranch
		= node.getBranch(ParserConstants.PARAMETERS_BRANCH);
		int i;
		for (i = 0; i < parametersBranch.size() - 1; i++) {
			signature += translate(parametersBranch.get(i)) + ", ";
		}
		if (parametersBranch.size() > 0) {
			signature += translate(parametersBranch.get(i));
		}
		signature += ")";

		LinkedList<AbstractSyntaxTreeNode> expressionsBranch
		= node.getBranch(ParserConstants.EXPRESSIONS_BRANCH);
		String expressions = " {\n";
		for (i = 0; i < expressionsBranch.size(); i++) {
			expressions += translate(expressionsBranch.get(i));
		}
		expressions += "}\n";
		return translate(node.get(ParserConstants.DATA_TYPE_BRANCH))
		+ signature + expressions;
	}

	private String translateSymbol(AbstractSyntaxTreeNode node) {
		String string = "";
		String instructions = translationMap.get(node.getLexeme());
		if (instructions == null) {
			return "";
		}
		for (int i = 0; i < instructions.length(); i++) {
			char instruction = instructions.charAt(i);
			if (instruction == '\\') {
				if (instructions.charAt(i + 1) == 'b') {
					i++;
					String numberString = "";
					while (i + 1 < instructions.length()
					&& Character.isDigit(instructions.charAt(i + 1))) {
						numberString += instructions.charAt(++i);
					}
					string += translate(
						node.getBranch(Integer.parseInt(numberString))
					);
				}
				else {
					String numberString = "";
					while (i + 1 < instructions.length()
					&& Character.isDigit(instructions.charAt(i + 1))) {
						numberString += instructions.charAt(++i);
					}
					string += translate(
						node.getBranch(0).get(Integer.parseInt(numberString))
					);
				}
			}
			else {
				string += instruction;
			}
		}
		return string;
	}
}
