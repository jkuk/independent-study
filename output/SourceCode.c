
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

string generatedVariable0;
generatedVariable0.length = getLength("asdf");
generatedVariable0.lexeme = "asdf";
string generatedVariable1;
generatedVariable1.length = getLength("qwert");
generatedVariable1.lexeme = "qwert";
string generatedVariable2;
generatedVariable2.length = getLength("dofjsdfjoisdjf");
generatedVariable2.lexeme = "dofjsdfjoisdjf";
string generatedVariable3;
generatedVariable3.length = getLength("swoop");
generatedVariable3.lexeme = "swoop";

int main() {
int a = 1;
a = ((a - 4) + (5 * 2));
string b = generatedVariable0;
b = concatenate(concatenate(b, b), generatedVariable1);
int arr1[3];
int fasd() {
a = 7;
return 1;
}
int fun1() {
if ((4 < 5)) {
if ((4 < 5)) {
b = generatedVariable2;

}
while ((0 == 1)) {
a = 3;

}
b = generatedVariable3;
return b;

}
return 5;
}
return 1;
}
