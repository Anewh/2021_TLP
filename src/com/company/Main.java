package com.company;

import com.company.parsers.PythonLexicalParser;
import com.company.parsers.Token;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Vector;

public class Main {
    public static void main(String[] args) {
        final String path = "src\\resources\\example.txt";
        List<String> pythonCode = readFile(path);

        PythonLexicalParser parser = new PythonLexicalParser();
        pythonCode = parser.deleteComments(pythonCode);
        for(Token t : parser.lexicalAnalyzer(pythonCode)){
            System.out.println(t.getToken() + " " + t.getWord() + " ");
        }
    }

    public static List<String> readFile(String path){
        List<String> pyCode = new Vector<String>();
        try(FileReader reader = new FileReader(path)) {
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = null;
            while((line = bufferedReader.readLine()) != null ) {
                pyCode.add(line);
            }
        }
        catch(IOException ex){
            System.out.println(ex.getMessage());
        }
        return pyCode;
    }
}
