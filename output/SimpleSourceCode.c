
/**
 * This is C code generated by the CayDiJay Compiler.
 **/

#DEFINE True = 1;
#DEFINE False = 0;

typedef struct {
    int length;
    char *sequence;
} string;

int getLength(char *str) {
    int i = 0;
    while (str[i] != 0) {
        i++;
    }
    return i;
}

string concatenate(string first, string second) {
    string newString;
    newString.length = first.length + second.length;
    newString.sequence = (char *)malloc(sizeof(char) * newString.length);

    newString.sequence[0] = first.sequence;
    newString.sequence[first.length] = second.sequence;
    return newString;
}

string generatedVariable4;
generatedVariable4.length = getLength("b");
generatedVariable4.lexeme = "b";
string generatedVariable5;
generatedVariable5.length = getLength("c");
generatedVariable5.lexeme = "c";
string generatedVariable6;
generatedVariable6.length = getLength("dofjsdfjoisdjf");
generatedVariable6.lexeme = "dofjsdfjoisdjf";
string generatedVariable7;
generatedVariable7.length = getLength("dofjsdfjoisdjf");
generatedVariable7.lexeme = "dofjsdfjoisdjf";
string generatedVariable8;
generatedVariable8.length = getLength("dofjsdfjoisdjf");
generatedVariable8.lexeme = "dofjsdfjoisdjf";
string generatedVariable9;
generatedVariable9.length = getLength("dofjsdfjoisdjf");
generatedVariable9.lexeme = "dofjsdfjoisdjf";
string generatedVariable10;
generatedVariable10.length = getLength("this");
generatedVariable10.lexeme = "this";

int a = 1;
a = 2;
a = (a + 4);
a = (!5);
string b = generatedVariable4;
string d = concatenate(b, generatedVariable5);
int arr1[3];
string arr2[5][7];
int val = arr1[1];
string val1 = arr2[a][2];
void fun1() {
int a = 7;
}
int fasd() {
string b = generatedVariable6;
return 5;
}
string qwert(int argc, string argv) {
string b = generatedVariable7;
return b;
}
if ((4 < 5)) {
b = generatedVariable8;
return b;

}
if ((((2 == 3) > 4) < 5)) {
b = generatedVariable9;

}
while ((0 > 1)) {
a = fasd();

}
while ((0 > 1)) {
b = qwert(5, generatedVariable10);

}