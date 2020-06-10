import LexerExceptions.UnknownSymbolException;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class Lexer {

    public String inputCopy;

    Lexer (String rawInput){
        inputCopy = rawInput;
    }

    public List<Token> tokens() throws UnknownSymbolException {

        List<Token> tokens = new ArrayList<>();
        boolean lexemFound;
        String lastValueFound="";
        Lexem lastLexemFound=null;


        while (!inputCopy.isEmpty()) {

            lexemFound = false;

            for (Lexem lexem : Lexem.values()) {
                Matcher matcher = (lexem.getPattern()).matcher(inputCopy);

                if (matcher.find()) {
                    lexemFound = true;
                    lastValueFound= matcher.group();
                    lastLexemFound=lexem;
                    Token token = new Token(lexem, matcher.group());

                    if(lexem!=Lexem.SPACE) {
                        tokens.add(token);
                    }

                    inputCopy = inputCopy.substring(matcher.end());
                }
            }

            if (!lexemFound){
                throw new UnknownSymbolException("Unknown symbol after " + lastLexemFound +" "+ lastValueFound );
            }
        }


        return tokens;
    }


}
