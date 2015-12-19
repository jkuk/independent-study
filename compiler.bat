del .\compiler\src\utility\*.class
javac .\compiler\src\utility\*.java

del .\compiler\src\compiler\lexer\*.class .\compiler\tst\compiler\lexer\*.class
javac -cp .\compiler\src -sourcepath .\compiler\src\compiler\lexer;.\compiler\tst\compiler\lexer .\compiler\src\compiler\lexer\*.java .\compiler\tst\compiler\lexer\*.java

del .\compiler\src\compiler\parser\*.class .\compiler\tst\compiler\parser\*.class
javac -cp .\compiler\src -sourcepath .\compiler\src\compiler\parser;.\compiler\tst\compiler\parser .\compiler\src\compiler\parser\*.java .\compiler\tst\compiler\parser\*.java

del .\compiler\src\compiler\generator\*.class .\compiler\tst\compiler\generator\*.class
javac -cp .\compiler\src -sourcepath .\compiler\src\compiler\generator;.\compiler\tst\compiler\generator .\compiler\src\compiler\generator\*.java .\compiler\tst\compiler\generator\*.java

del .\compiler\src\compiler\*.class .\compiler\tst\compiler\*.class
javac -cp .\compiler\src -sourcepath .\compiler\src\compiler;.\compiler\tst\compiler .\compiler\src\compiler\*.java .\compiler\tst\compiler\*.java

java -cp compiler\src;compiler\tst compiler.Compiler
