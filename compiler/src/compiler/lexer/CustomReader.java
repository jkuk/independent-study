package compiler.lexer;

import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CustomReader {
	private BufferedReader bufferedReader;
	private String currentLine;

	public CustomReader(String fileName) throws FileNotFoundException {
		bufferedReader = new BufferedReader(new FileReader(fileName));
		currentLine = "";
	}

	public String getNextLexeme() throws IOException {
		while (currentLine.equals("")) {
			currentLine = bufferedReader.readLine();
			if (currentLine == null) {
				bufferedReader.close();
				return null;
			}
		}
		String[] splitLine = currentLine.split("\\s");
		String lexeme = null;
		currentLine = "";
		int i = 0;
		while (lexeme == null) {
			if (!splitLine[i++].equals("")) {
				lexeme = splitLine[i - 1];
			}
		}

		for (i = i; i < splitLine.length; i++) {
			currentLine += splitLine[i] + " ";
		}
		if (lexeme == null) {
			return getNextLexeme();
		}
		return lexeme;
	}

	public void rollback(String previous) {
		currentLine = previous + currentLine;
	}
}
