del .\compiler\src\compiler*.class .\compiler\src\lexer\*.class
javac .\compiler\src\compiler\*.java .\compiler\src\lexer\*.java -sourcepath .\compiler\src\lexer;.\compiler\src\compiler

del .\compiler\src\parser\*.class .\compiler\tst\parser\*.class
javac -cp .\compiler\src -sourcepath .\compiler\src\parser;.\compiler\tst\parser .\compiler\src\parser\*.java .\compiler\tst\parser\*.java
java -cp compiler\src;compiler\tst parser.ParserUnitTests