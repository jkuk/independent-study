package compiler;
import compiler.lexer.Lexer;
import compiler.parser.Parser;
import compiler.generator.Translator;
import utility.Helper;

import java.io.FileNotFoundException;
import java.io.IOException;

import java.io.BufferedWriter;
import java.io.FileWriter;

public class Compiler {
	public static void main(String[] args) {
		try {
			Lexer lexer = new Lexer("./input/compiler/lexer/Symbols.txt");
			Parser parser = new Parser(
				"./input/compiler/parser/Grammar.txt",
				"./input/compiler/parser/Operators.txt"
			);
			Translator translator = new Translator(
				"./input/compiler/generator/Initializations.txt",
				"./input/compiler/generator/Translations.txt"
			);

			String compiledCode = translator.translate(
				parser.parse(
					lexer.lex("./input/compiler/generator/SourceCode.kdj")
				)
			);
			BufferedWriter bufferedWriter = new BufferedWriter(
				new FileWriter("./output/SourceCode.c")
			);
			bufferedWriter.write(compiledCode);
			bufferedWriter.close();

			compiledCode = translator.translate(
				parser.parse(
					lexer.lex("./input/compiler/generator/SimpleSourceCode.kdj")
				)
			);
			bufferedWriter = new BufferedWriter(
				new FileWriter("./output/SimpleSourceCode.c")
			);
			bufferedWriter.write(compiledCode);
			bufferedWriter.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
}
