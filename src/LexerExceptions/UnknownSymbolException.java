package LexerExceptions;

public class UnknownSymbolException extends Exception{
    public UnknownSymbolException(String errorMessage){
        super(errorMessage);
    }
}
