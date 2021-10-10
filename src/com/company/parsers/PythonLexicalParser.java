package com.company.parsers;

import java.util.*;
import java.util.stream.Collectors;

public class PythonLexicalParser {
    private static final Set<String> KEYWORDS =
            Set.of("as", "assert", "break", "class", "continue", "def", "del", "elif", "else", "except", "finally", "for", "from", "if", "import", "lambda", "pass", "print", "return", "try", "while", "with", "and", "or", "not", "is", "in");

    private static final Set<String> BOOLEAN_LITERALS =
            Set.of("True", "False", "None");

    private static final Set<String> OPERATORS =
            Set.of("+", "-", "*", "**", "/", "//", "%", "@", "<<", ">>", "&", "|",
                    "^", "~", ":=", "<", ">", "<=", ">=", "==", "!=");

    private static final Set<String> DELIMITERS =
            Set.of("(", ")", "[", "]", "{", "}", ",", ":", ".", ";", "@", "=", "->","-=",
                    "*=", "/=", "//=", "%=", "@=", "&=", "|=", "^=", ">>=", "<<=", "**=");

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

    private boolean findTokenKeyword(StringBuilder buffer){
        Token token = new Token();
        if(KEYWORDS.contains(buffer.toString())){
            token.setToken("KEYWORD");
            token.setWord(buffer.toString());
            this.tokens.add(token);

            buffer.setLength(0);
            return true;
        }
        return false;
    }

    private boolean findTokenBooleanLiteral(StringBuilder buffer){
        Token token = new Token();
        if(BOOLEAN_LITERALS.contains(buffer.toString())){
            token.setToken("BOOLEAN_LITERAL");
            token.setWord(buffer.toString());
            this.tokens.add(token);

            buffer.setLength(0);
            return true;
        }
        return false;
    }

    private void ohIdHereWeGoAgain(StringBuilder buffer){
        if(buffer.toString() == " " || buffer.toString() == "" || buffer.length() == 0) return;
        Token token = new Token();
        token.setToken("ID");
        token.setWord(buffer.toString());
        this.tokens.add(token);
        buffer.setLength(0);
    }

    private void addLiteral(StringBuilder literalBuffer, boolean flag, String currentToken){
        Token token = new Token();
        token.setToken(currentToken);
        token.setWord(literalBuffer.toString());
        this.tokens.add(token);
        flag = false;
        literalBuffer.setLength(0);
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

            for(int i = 0;i<charArray.length; i++){

                if(charArray[i] == '\'' || charArray[i] == '"'){
                    if(STRING_PREFIXES.contains(buffer.toString())) addLiteral(buffer, true , "STRING_PREFIXES");
                    if(!isStringLiteral){
                        escapedQuote = charArray[i];
                        isStringLiteral = true;
                        literalBuffer.append(charArray[i]);
                    }
                    else if(escapedQuote == charArray[i]){
                        isStringLiteral = false;
                        literalBuffer.append(charArray[i]);
                        addLiteral(literalBuffer, isStringLiteral, "STRING_LITERAL");
                    }
                    continue;
                }

                if(isStringLiteral){
                    literalBuffer.append(charArray[i]);
                    continue;
                }

                if(Character.isLetter(charArray[i])){
                    buffer.append(charArray[i]);
                    continue;
                }

                if((!Character.isDigit(charArray[i]) && charArray[i]!='.') && canBeNumberLiteral && literalBuffer.length() != 0) addLiteral(literalBuffer, canBeNumberLiteral, "NUMBER_LITERAL");

                if(Character.isDigit(charArray[i]) || (literalBuffer.length() != 0 && charArray[i] == '.') || charArray[i] == '-'){
                    canBeNumberLiteral = true;
                    literalBuffer.append(charArray[i]);
                }

                if(DELIMITERS.contains(Character.toString(charArray[i])) && !canBeNumberLiteral){
                    findTokenKeyword(buffer);
                    findTokenBooleanLiteral(buffer);

                    Token token = new Token();
                    token.setToken("DELIMITER");
                    token.setWord(Character.toString(charArray[i]));

                    ohIdHereWeGoAgain(buffer);
                    //findHardLiteral(p.toCharArray());
                    this.tokens.add(token);
                }

                if(OPERATORS.contains(Character.toString(charArray[i]))){
                    findTokenKeyword(buffer);
                    findTokenBooleanLiteral(buffer);

                    Token token = new Token();
                    token.setToken("OPERATOR");
                    token.setWord(Character.toString(charArray[i]));

                    ohIdHereWeGoAgain(buffer);
                    this.tokens.add(token);
                }

                if(UNRECOGNIZED.contains(Character.toString(charArray[i]))){
                    Token token = new Token();
                    token.setToken("UNRECOGNIZED");
                    token.setWord(Character.toString(charArray[i]));
                    this.tokens.add(token);
                }
            }
            if(isStringLiteral){literalBuffer.append(" "); continue;}
            if(findTokenBooleanLiteral(buffer) || findTokenKeyword(buffer)) isFounded = true;
            if(!isFounded){ ohIdHereWeGoAgain(buffer);}
            buffer.setLength(0);

            if(literalBuffer.length()!=0) if(canBeNumberLiteral)addLiteral(literalBuffer, canBeNumberLiteral, "NUMBER_LITERAL");
            literalBuffer.setLength(0);
        }
        return tokens;
    }
}
