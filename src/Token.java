
public class Token {

    private final Lexem Lexem;
    private final String Value;

    Token (Lexem L, String V){
        this.Lexem = L;
        this.Value = V;
    }

    public Lexem getLexem(){
        return Lexem;
    }

    public String getValue(){
        return Value;
    }

}
