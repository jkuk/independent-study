del .\compiler\src\compiler\*.class .\compiler\src\lexer\*.class
javac .\compiler\src\compiler\*.java .\compiler\src\lexer\*.java -sourcepath .\compiler\src\lexer;.\compiler\src\compiler

del .\compiler\src\parser\*.class .\compiler\tst\parser\*.class .\compiler\src\parser\abstractSyntaxTree\*.class .\compiler\src\parser\parseTree\*.class
javac -cp .\compiler\src -sourcepath .\compiler\src\parser\parseTree;.\compiler\src\parser\abstractSyntaxTree;.\compiler\src\parser;.\compiler\tst\parser .\compiler\src\parser\parseTree\*.java .\compiler\src\parser\abstractSyntaxTree\*.java .\compiler\src\parser\*.java .\compiler\tst\parser\*.java

del .\compiler\src\generator\*class .\generator\tst\generator\*.class
javac -cp .\compiler\src -sourcepath .\compiler\src\generator;.\compiler\tst\generator .\compiler\src\generator\*.java .\compiler\tst\generator\*.java

java -cp compiler\src;compiler\tst generator.TranslatorIntegrationTests
