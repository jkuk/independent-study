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

	public String getNextWord() throws IOException {
		while (currentLine.equals("")) {
			currentLine = bufferedReader.readLine();
			if (currentLine == null) {
				return null;
			}
		}
		String[] splitLine = currentLine.split("\\s");
		String word = null;
		currentLine = "";
		int i = 0;
		while (word == null) {
			if (!splitLine[i++].equals("")) {
				word = splitLine[i - 1];
			}
		}

		for (i = i; i < splitLine.length; i++) {
			currentLine += splitLine[i] + " ";
		}
		if (word == null) {
			return getNextWord();
		}
		return word;
	}

	public void rollback(String previous) {
		currentLine = previous + currentLine;
	}
}