package com.company.parsers;

import java.util.*;

public class PythonLexicalParser {
    private static final Set<String> KEYWORDS =
            Set.of("as", "assert", "break", "class", "continue", "def", "del", "elif", "else", "except", "finally", "for", "from", "if", "import", "lambda", "pass", "print", "return", "try", "while", "with");

    private static final Set<String> KEYWORD_OPERATORS =
            Set.of("and", "or", "not", "is", "in");

    // Keyword literals
    private static final String FALSE = "False";
    private static final String TRUE = "True";
    private static final String NONE = "None";

    private static final Set<String> OPERATORS =
            Set.of(".", "+", "-", "*", "/", "%", "**", "//", ":", "=", "+=", "-=", "*=", "/=", "%=", "//=", "**=", "&=", "|=", "^=", ">>=", "<<=", "==", "!=", ">", "<", ">=", "<=", "& ", "|", "^", "\\", "~", "<<", ">>", "(", ")", "[", "]", "{", "}");

    private static final Set<String> STRING_PREFIXES =
            Set.of("r", "u", "R", "U", "f", "F", "fr", "Fr", "fR", "FR", "rf", "rF", "Rf", "RF", "b", "B", "br", "Br", "bR", "BR", "rb", "rB", "Rb", "RB");


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

            //исключение docstring?
            //Boolean isCurrentComment = false;

        }
        return result;
    }

    public static List<String> lexicalAnalyzer(List<String> pythonCode){
        ArrayList<String> buffer = new ArrayList<>();

        for(String p : pythonCode){
            char[] charArray = p.toCharArray();

            //b = p.split(" ");
            //for(String k : b){
            //
            //}
        }
        return pythonCode;
    }
}
