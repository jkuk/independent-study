del *.class
javac Lexer.java
java Lexer ..\input\KleeneSourceCode.kd ..\input\KleeneSymbols.txt -verbose
java Lexer ..\input\AlternateSourceCode.kd ..\input\AlternateSymbols.txt -verbose
java Lexer ..\input\ConcatenateSourceCode.kd ..\input\ConcatenateSymbols.txt -verbose
java Lexer ..\input\TestSourceCode.kd ..\input\TestSymbols.txt -verbose
java Lexer ..\input\SourceCode.kd ..\input\Symbols.txt -verbose