
del .\compiler\src\compiler*.class .\compiler\src\lexer\*.class
javac .\compiler\src\compiler\*.java .\compiler\src\lexer\*.java -sourcepath .\compiler\src\lexer;.\compiler\src\compiler
java -cp compiler\src lexer.Lexer .\input\lexer\KleeneSourceCode.kd .\input\lexer\KleeneSymbols.txt -verbose
java -cp compiler\src lexer.Lexer .\input\lexer\AlternateSourceCode.kd .\input\lexer\AlternateSymbols.txt -verbose
java -cp compiler\src lexer.Lexer .\input\lexer\ConcatenateSourceCode.kd .\input\lexer\ConcatenateSymbols.txt -verbose
java -cp compiler\src lexer.Lexer .\input\lexer\TestSourceCode.kd .\input\lexer\TestSymbols.txt -verbose
java -cp compiler\src lexer.Lexer .\input\lexer\SourceCode.kd .\input\lexer\Symbols.txt -verbose