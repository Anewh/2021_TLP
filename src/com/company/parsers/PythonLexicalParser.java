package com.company.parsers;

import java.util.*;
import java.util.stream.Collectors;

public class PythonLexicalParser {
    private static final Set<String> KEYWORDS =
            Set.of("as", "assert", "break", "class", "continue", "def", "del", "elif", "else", "except", "finally", "for", "from", "if", "import", "lambda", "pass", "print", "return", "try", "while", "with", "and", "or", "not", "is", "in");

    private static final Set<String> BOOLEAN_LITERALS =
            Set.of("True", "False", "None");

    private static final Set<String> OPERATORS =
            Set.of("+", "-", "*", "**", "/", "//", "%", "<<", ">>", "&", "|",
                    "^", "~", ":=", "<", ">", "<=", ">=", "==", "!=");

    private static final Set<String> DELIMITERS =
            Set.of("(", ")", "[", "]", "{", "}", ",", ":", ".", ";", "=", "->","-=",
                    "*=", "/=", "//=", "%=", "&=", "|=", "^=", ">>=", "<<=", "**=");

    private static final Set<String> UNRECOGNIZED = Set.of("$", "?", "`");

    private static final Set<String> STRING_PREFIXES =
            Set.of("r", "u", "R", "U", "f", "F", "fr", "Fr", "fR", "FR", "rf", "rF", "Rf", "RF", "b", "B", "br", "Br", "bR", "BR", "rb", "rB", "Rb", "RB");

    public static List<Token> tokens = new LinkedList<Token>();

    public static List<String> deleteComments(List<String> pythonCode){
        List<String> result = new ArrayList<>();

        for(String p : pythonCode){
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

    private void addToken(StringBuilder literalBuffer, String currentToken){
        Token token = new Token();
        token.setToken(currentToken);
        token.setWord(literalBuffer.toString());
        this.tokens.add(token);
        literalBuffer.setLength(0);
    }

    private Token parseSymbolToken(char[] chars, int i){
        int start = i;
        String buffer = "";
        Token token = new Token();
        for (; i < chars.length; i++) {
            final String s = String.valueOf(chars, start, i-start+1);
            if (DELIMITERS.contains(s)) {
                token.setWord(s);
                token.setToken("DELIMITER");
            }
            else if (OPERATORS.contains(s)) {
                token.setWord(s);
                token.setToken("OPERATOR");
            }
            else
                return token;
        }
        return token;
    }

    public List<Token> lexicalAnalyzer(List<String> resource){
        StringBuilder buffer = new StringBuilder();
        StringBuilder literalBuffer = new StringBuilder();
        boolean isFounded;
        char escapedQuote = ' ';
        boolean isStringLiteral = false;
        boolean canBeNumberLiteral = false;

        List<String> pythonCode = resource.stream().flatMap(str -> Arrays.stream(str.split(" "))).collect(Collectors.toList());

        for(String p : pythonCode){
            isFounded = false;
            canBeNumberLiteral = false;
            char[] charArray = p.toCharArray();

            for(int i = 0; i < charArray.length; i++){
                //строковый литерал
                char c = charArray[i];
                if(c == '\'' || c == '"'){
                    if(STRING_PREFIXES.contains(buffer.toString())) addToken(buffer, "STRING_PREFIXES");
                    if(!isStringLiteral){
                        escapedQuote = c;
                        isStringLiteral = true;
                        literalBuffer.append(c);
                    }
                    else if(escapedQuote == c){
                        isStringLiteral = false;
                        literalBuffer.append(c);
                        addToken(literalBuffer, "STRING_LITERAL");
                        isStringLiteral = false;
                    }
                    continue;
                }

                if(isStringLiteral){
                    literalBuffer.append(c);
                    continue;
                }

                //любая буквенная лексема
                if(Character.isLetter(c)){
                    buffer.append(c);
                    continue;
                }

                //численный литерал
                if((!Character.isDigit(c) && c !='.') && canBeNumberLiteral && literalBuffer.length() != 0){
                    addToken(literalBuffer, "NUMBER_LITERAL");
                    canBeNumberLiteral = false;
                }
                if(Character.isDigit(c) || (literalBuffer.length() != 0 && c == '.') || c == '-'){
                    canBeNumberLiteral = true;
                    literalBuffer.append(c);
                }

                //разделители
                if(DELIMITERS.contains(Character.toString(c)) && !canBeNumberLiteral){
                    if(KEYWORDS.contains(buffer.toString())) addToken(buffer, "KEYWORD");
                    if(BOOLEAN_LITERALS.contains(buffer.toString())) addToken(buffer, "BOOLEAN_LITERALS");

                    Token token = new Token();
                    token.setToken("DELIMITER");
                    token.setWord(Character.toString(c));

                    if(!(buffer.toString().equals(" ")) && !(buffer.length() == 0)) addToken(buffer, "ID");
                    this.tokens.add(token);
                }

                //операторы
                if(OPERATORS.contains(Character.toString(c))){
                    if(KEYWORDS.contains(buffer.toString())) addToken(buffer, "KEYWORD");
                    if(BOOLEAN_LITERALS.contains(buffer.toString())) addToken(buffer,  "BOOLEAN_LITERALS");

                    Token token = new Token();
                    token.setToken("OPERATOR");
                    token.setWord(Character.toString(c));

                    if(!(buffer.toString().equals(" ")) && !(buffer.length() == 0)) addToken(buffer, "ID");
                    this.tokens.add(token);
                }

                //неведомая хреновина
                if(UNRECOGNIZED.contains(Character.toString(c))){
                    Token token = new Token();
                    token.setToken("UNRECOGNIZED");
                    token.setWord(Character.toString(c));
                    this.tokens.add(token);
                }
            }

            //если строковый литерал разбился на несколько строк
            if(isStringLiteral){
                literalBuffer.append(" ");
                continue;
            }

            //если буквенный литерал это последнее что написано в строке
            if(KEYWORDS.contains(buffer.toString())){
                addToken(buffer, "KEYWORD");
                isFounded = true;
            }
            else if(BOOLEAN_LITERALS.contains(buffer.toString())){
                addToken(buffer, "BOOLEAN_LITERALS");
                isFounded = true;
            }
            else if(!(buffer.toString().equals(" ")) && !(buffer.toString().equals("")) && !(buffer.length() == 0)) addToken(buffer, "ID");
            buffer.setLength(0);

            //если строковый литерал вынесен в отдельную строку
            if(literalBuffer.length()!=0){
                addToken(literalBuffer, "NUMBER_LITERAL");
            }
            literalBuffer.setLength(0);
        }
        return tokens;
    }
}
