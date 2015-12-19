# independent-study

There are two input files: SimpleSourceCode.kdj and SourceCode.kdj
	./input/compiler/generator/SimpleSourceCode.kdj
	./input/compiler/generator/SourceCode.kdj"

Their outputs are:
	./output/SimpleSourceCode.c
	./output/SourceCode.c

./compiler.bat is the main script I've been using for running an integration test.
All of the unit tests are broken because so much of the code has changed
and I haven't been updating them.
Running the compiler is set to display the Parse Tree and Abstract Syntax Tree

SimpleSourceCode checks these features in this order:
	variable declaration and initialization
	variable initialization
	variable initialization with binary numeric operator
	variable initialization with unary numeric operator

	string declaration and initialization
	string declaration and initialization with binary string operator

	1D array declaration
	2D array declaration
	1D array indexing
	2D array indexing

	no arg function definition with no return type
	no arg function definition with return type
	function with args definition with return type
	these functions declare new variables which share names with another variable outside of scope

	if statement with return value
	if statement without return value

	while loop with a previously defined no arg function call inside
	while loop with a previously defined function with args call inside
It finishes compiling and if any string is removed in a way that doesn't fit the grammar,
the compiler will throw an error (without much explanation).
All symbols in my language are translated into their corresponding C symbol.
Expressions are translated to a syntactically correct C expression.
I visually verified this in the output file.

SourceCode starts with a main function (which turns into the C int main() function).
From there, it nests variable declarations and initializations, function definitions,
if statements, while loops without compilation errors.
Same as before, it finishes compiling, and I visually verified that it looks the way it should.

Notes:
Strings are a basic data type in my language (no characters).
There's a concatenation operator built into the language.
This is done by prepending some helper functions to the top of the compiled code.
Also, any time a hard coded string is used, the compiler hoists the definition
to the top of the file, and replaces the instance of the hard coded string
with a compiler-generated variable that references a custom string struct.
This allows for strings to be used anywhere in the code as a basic data type.

The symbol table uses functions for new scopes.
Variables defined in if statements and loops are not hoisted.

What's working:
Lexer (mostly)
Parser (mostly)
Translator (mostly)

What's not:
Lexer -
	sometimes gets messed up if you don't place spaces between strings in the source code.
	not sure what causes this
Parser -
	can't handle epsilon grammar rules.
	need to redesign my abstract syntax tree reductions to accommodate for epsilon
	I over looked this in the initial design
	type checking - not implemented yet, but the start of it is there, but commented out
Translator -
	can't have a function where the only expression is a return expression
	forgot to specify this in the grammar

If you want to see a list of things that I wanted to do, but ran out of time for,
look at the to.do file.
