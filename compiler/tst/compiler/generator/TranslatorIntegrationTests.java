package compiler.generator;
import compiler.lexer.Token;
import compiler.parser.Parser;
import compiler.parser.AbstractSyntaxTree;
import compiler.parser.NonTerminal;
import compiler.parser.ParseTree;
import compiler.parser.ParseTreeNode;
import compiler.parser.Symbol;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Deque;
import java.util.Map;

public class TranslatorIntegrationTests {
	public static void main(String[] args) {
		test();
	}

	public static void test() {
		Translator translator = new Translator(
			"./input/compiler/generator/Initializations.txt",
			"./input/compiler/generator/Translations.txt"
		);


		//
		//
		// :
		//
		// _
		//
		// ~
		//
		// +
		//
		// -
		//
		// *
		//
		// /
		//
		// %
		//
		// &
		//
		// |
		//
		// #
		//
		// =
		//
		// <
		//
		// >
		// <=
		//
		// >=
		// [
		//
		// ]
		// ^
		// ?
		// !
		// void
		//
		// String

	}
}
