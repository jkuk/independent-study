del .\compiler\src\utility\*.class
javac .\compiler\src\utility\*.java

del .\compiler\src\compiler\lexer\*.class .\compiler\tst\compiler\lexer\*.class
javac -cp .\compiler\src -sourcepath .\compiler\src\compiler\lexer;.\compiler\tst\compiler\lexer .\compiler\src\compiler\lexer\*.java .\compiler\tst\compiler\lexer\*.java

java -cp compiler\src compiler.lexer.Lexer .\input\compiler\lexer\KleeneSourceCode.kd .\input\compiler\lexer\KleeneSymbols.txt -verbose
java -cp compiler\src compiler.lexer.Lexer .\input\compiler\lexer\AlternateSourceCode.kd .\input\compiler\lexer\AlternateSymbols.txt -verbose
java -cp compiler\src compiler.lexer.Lexer .\input\compiler\lexer\ConcatenateSourceCode.kd .\input\compiler\lexer\ConcatenateSymbols.txt -verbose
java -cp compiler\src compiler.lexer.Lexer .\input\compiler\lexer\TestSourceCode.kd .\input\compiler\lexer\TestSymbols.txt -verbose
java -cp compiler\src compiler.lexer.Lexer .\input\compiler\lexer\SourceCode.kd .\input\compiler\lexer\Symbols.txt -verbose
