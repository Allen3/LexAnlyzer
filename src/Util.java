/**
 * Created by Allen on 2016/3/20.
 */
public class Util {
    public final static int SEPARATOR = 0;
    public final static int LETTER = 1;
    public final static int NUMBER = 2;
    public final static int LESS = 3;
    public final static int GREATER = 4;
    public final static int EQUAL = 5;
    public final static int NOT = 6;
    public final static int ADD = 7;
    public final static int SUB = 8;
    public final static int MUL = 9;
    public final static int DIV = 10;
    public final static int LPAR = 11;
    public final static int RPAR = 12;
    public final static int COM = 13;
    public final static int SEM = 14;
    public final static int LBR = 15;
    public final static int RBR = 16;
    public final static int ILLEGAL = 17;

    public static boolean isLetter(char character) {
        return (character >= 'A' && character <= 'Z') ||
                (character >= 'a' && character <= 'z');
    }

    public static boolean isNumber(char character) {
        return character >= '0' && character <= '9';
    }

    public static boolean isSeparator(char character) {
        return character == '\n' || character == '\r' || character == ' ';
    }

    public static boolean isSignLSS(char character) {
        return character == '<';
    }

    public static boolean isSignGRT(char character) {
        return character == '>';
    }

    public static boolean isSignEQL(char character) {
        return character == '=';
    }

    public static boolean isSignNOT(char character) {
        return character == '!';
    }

    public static boolean isSignADD(char character) {
        return character == '+';
    }

    public static boolean isSignSUB(char character) {
        return character == '-';
    }

    public static boolean isSignMUL(char character) {
        return character == '*';
    }

    public static boolean isSignDIV(char character) {
        return character == '/';
    }

    public static boolean isSignLPAR(char character) {
        return character == '(';
    }

    public static boolean isSignRPAR(char character) {
        return character == ')';
    }

    public static boolean isSignCOM(char character) {
        return character == ',';
    }

    public static boolean isSignSEM(char character) {
        return character == ';';
    }

    public static boolean isSignLBR(char character) {
        return character == '{';
    }

    public static boolean isSignRBR(char character) {
        return character == '}';
    }

    public static int checkCharType(char ch) {
        if (isSeparator(ch)) {
            return SEPARATOR;
        } else if (isLetter(ch)) {
            return LETTER;
        } else if (isNumber(ch)) {
            return NUMBER;
        } else if (isSignLSS(ch)) {
            return LESS;
        } else if (isSignGRT(ch)) {
            return GREATER;
        } else if (isSignEQL(ch)) {
            return EQUAL;
        } else if (isSignNOT(ch)) {
            return NOT;
        } else if (isSignADD(ch)) {
            return ADD;
        } else if (isSignSUB(ch)) {
            return SUB;
        } else if (isSignMUL(ch)) {
            return MUL;
        } else if (isSignDIV(ch)) {
            return DIV;
        } else if (isSignLPAR(ch)) {
            return LPAR;
        } else if (isSignRPAR(ch)) {
            return RPAR;
        } else if (isSignCOM(ch)) {
            return COM;
        } else if (isSignSEM(ch)) {
            return SEM;
        } else if (isSignLBR(ch)) {
            return LBR;
        } else if (isSignRBR(ch)) {
            return RBR;
        } else {
            return ILLEGAL;
        }
    }
}
