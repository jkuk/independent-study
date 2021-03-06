del .\compiler\src\utility\*.class
javac .\compiler\src\utility\*.java

del .\compiler\src\compiler\lexer\*.class .\compiler\tst\compiler\lexer\*.class
javac -cp .\compiler\src -sourcepath .\compiler\src\compiler\lexer;.\compiler\tst\compiler\lexer .\compiler\src\compiler\lexer\*.java .\compiler\tst\compiler\lexer\*.java

del .\compiler\src\compiler\parser\*.class .\compiler\tst\compiler\parser\*.class
javac -cp .\compiler\src -sourcepath .\compiler\src\compiler\parser;.\compiler\tst\compiler\parser .\compiler\src\compiler\parser\*.java .\compiler\tst\compiler\parser\*.java

java -cp compiler\src;compiler\tst compiler.parser.ParserUnitTests
