package com.company.parsers;

import java.util.*;

public class PythonLexicalParser {
    private static final Set<String> KEYWORDS =
            Set.of("as", "assert", "break", "class", "continue", "def", "del", "elif", "else", "except", "finally", "for", "from", "if", "import", "lambda", "pass", "print", "return", "try", "while", "with", "and", "or", "not", "is", "in");

    // Keyword literals
//    private static final String FALSE = "False";
//    private static final String TRUE = "True";
//    private static final String NONE = "None";

    private static final Set<String> BOOLEAN_LITERALS =
            Set.of("True", "False", "None");

    private static final Set<String> OPERATORS =
            Set.of("+", "-", "*", "**", "/", "//", "%", "@", "<<", ">>", "&", "|",
                    "^", "~", ":=", "<", ">", "<=", ">=", "==", "!=");

    private static final Set<String> DELIMITERS =
            Set.of("(", ")", "[", "]", "{", "}", ",", ":", ".", ";", "@", "=", "->","-=",
                    "*=", "/=", "//=", "%=", "@=", "&=", "|=", "^=", ">>=", "<<=", "**=");

    private static final Set<String> STRING_PREFIXES =
            Set.of("r", "u", "R", "U", "f", "F", "fr", "Fr", "fR", "FR", "rf", "rF", "Rf", "RF", "b", "B", "br", "Br", "bR", "BR", "rb", "rB", "Rb", "RB");

    public static Token tokens = new Token();

    public static List<String> deleteComments(List<String> pythonCode){
        List<String> result = new ArrayList<>();

        for(String p : pythonCode){
            //удаление однострочных комментариев
            if(p.indexOf('#')!=-1){
                StringBuilder stringBuilder = new StringBuilder(p);
                String substring = " ";
                int index = p.indexOf('#');
                while(index != stringBuilder.length() - p.indexOf('#')){
                    stringBuilder.setCharAt(index++, ' ');
                }
                p = stringBuilder.toString();
            }
            result.add(p);
        }
        return result;
    }

    private void findTokenKeyword(StringBuilder buffer, char sm){
        if(KEYWORDS.contains(buffer)){
            tokens.setToken("KEYWORD");
            tokens.setWord(buffer.toString());
        }
    }

    private void findTokenBooleanLiteral(StringBuilder buffer, char sm){
        if(BOOLEAN_LITERALS.contains(buffer)){
            tokens.setToken("BOOLEAN_LITERAL");
            tokens.setWord(buffer.toString());
        }
    }

    public List<String> lexicalAnalyzer(List<String> pythonCode){
        StringBuilder buffer = new StringBuilder();
        for(String p : pythonCode){
            char[] charArray = p.toCharArray();
            for(char sm : charArray){

                //добавить проверку на строковый литерал
                if(Character.isLetter(sm)){
                    buffer.append(sm);
                    continue;
                }
                if(DELIMITERS.contains(sm)){
                    findTokenKeyword(buffer, sm);
                    findTokenBooleanLiteral(buffer, sm);

                    tokens.setToken("DELIMITER");
                    tokens.setWord(Character.toString(sm));
                    buffer.setLength(0);
                }

                if(OPERATORS.contains(sm)){
                    findTokenKeyword(buffer, sm);
                    findTokenBooleanLiteral(buffer, sm);

                    tokens.setToken("OPERATOR");
                    tokens.setWord(Character.toString(sm));
                    buffer.setLength(0);
                }
            }

        }
        return pythonCode;
    }
}
