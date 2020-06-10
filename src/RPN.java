import java.util.ArrayList;
import java.util.List;

public class RPN {

    private final List<Token> tokens;
    private final List<Token> rpn = new ArrayList<>();
    private final List<Token> tempStack = new ArrayList<>();
    int index;

    public RPN(List<Token> tokens) {
        this.tokens = tokens;
    }

    public List<Token> intoRpn (){

        for (index = 0; index<tokens.size(); index++) {
            Token currentToken = tokens.get(index);
            sort(currentToken);
        }
        return rpn;
    }


    private void sort (Token currentToken){

        Lexem currentLexem = currentToken.getLexem();
        if (currentLexem== Lexem.VAR | currentLexem==Lexem.DIGIT){
            rpn.add(currentToken);
        }
        else if (currentLexem== Lexem.OP){
            sortOp(currentToken);
        }
        else if (currentLexem == Lexem.L_B | currentLexem == Lexem.L_CB){
            tempStack.add(currentToken);
        }
        else if (currentLexem == Lexem.R_B){
            sortRB();
        }
        else if (currentLexem == Lexem.ASSIGN_OP ){
            tempStack.add(currentToken);
        }
        else if (currentLexem == Lexem.SC){
            sortSC();
        }
        else if (currentLexem == Lexem.LOGIC_OP){
            tempStack.add(currentToken);
        }
        else if (currentLexem == Lexem.IF_KW){
            tempStack.add(currentToken);
        }
        else if (currentLexem == Lexem.R_CB){
            sortRCB();
        }
        else if (currentLexem == Lexem.ELSE_KW){
            addUnconditionalJump();
        }
        else if (currentLexem == Lexem.WHILE_KW){
            sortWhile(currentToken);
        }
        else if (currentLexem == Lexem.HASH_SET_KW || currentLexem == Lexem.LIST_KW){
            tempStack.add(currentToken);
        }
        else if (currentLexem == Lexem.STRUCTURE_OP_KW
                || currentLexem == Lexem.STRUCTURE_OP_2_KW
                || currentLexem == Lexem.STRUCTURE_LOGIC_OP_KW
                || currentLexem == Lexem.STRUCTURE_GETTER_OP_KW ){
            tempStack.add(currentToken);
        }
    }

    private void sortWhile(Token currentToken) {
        Token jump = new Token(Lexem.JUMP, "!");
        Token pointer = new Token (Lexem.DIGIT, String.valueOf(rpn.size()));
        tempStack.add(jump);
        tempStack.add(pointer);
        tempStack.add(currentToken);
    }

    private void addUnconditionalJump() {
        Token jump = new Token(Lexem.JUMP, "!");
        Token pointer = new Token(Lexem.POINTER, "#");
        rpn.add(pointer);
        rpn.add(jump);
    }

    private void sortRCB() {
        tempStack.remove(tempStack.size() - 1);  //removing LCB
        if(!tempStack.isEmpty() && tempStack.get(tempStack.size()-1).getLexem()==Lexem.WHILE_KW){
            tempStack.remove(tempStack.size()-1);
            rpn.add(tempStack.get(tempStack.size()-1));
            tempStack.remove(tempStack.size()-1);
            rpn.add(tempStack.get(tempStack.size()-1));
            tempStack.remove(tempStack.size()-1);
        }
        int i = rpn.size();
        while (i>0 && rpn.get(i - 1).getLexem() != Lexem.POINTER) {  //searching for empty pointer
           i--;
        }
        rpn.remove(i - 1);                               //delete empty pointer
        Token pointerValue;
        if (index + 1< tokens.size() &&tokens.get(index + 1).getLexem() == Lexem.ELSE_KW) {
            pointerValue = new Token(Lexem.DIGIT, String.valueOf(rpn.size() + 3));
        } else {
            pointerValue = new Token(Lexem.DIGIT, String.valueOf(rpn.size() + 1));
        }
        rpn.add(i - 1, pointerValue);                    //add digit which is gonna work as a pointer

    }

    private void sortSC() {
        while (!tempStack.isEmpty() && tempStack.get(tempStack.size()-1).getLexem()!= Lexem.L_CB){
            rpn.add(tempStack.get(tempStack.size()-1));
            tempStack.remove(tempStack.size()-1);
        }
    }

    private void sortRB() {
        while (tempStack.get(tempStack.size() - 1).getLexem() != Lexem.L_B) { //if our token is right bracket until we find the left
            rpn.add(tempStack.get(tempStack.size() - 1));                   //we add the existing ops form the stack to rpn
            tempStack.remove(tempStack.size() - 1);                    //and delete them from the stack
        }
        tempStack.remove(tempStack.size() - 1);                        // when finally reaches left bracket -> deletes it and ignores skips the right one

        if (tempStack.get(tempStack.size()-1).getLexem()==Lexem.IF_KW || tempStack.get(tempStack.size()-1).getLexem()==Lexem.WHILE_KW){
            if (tempStack.get(tempStack.size()-1).getLexem()==Lexem.IF_KW){
                tempStack.remove(tempStack.size() - 1);
            }
            Token jump = new Token(Lexem.JUMP, "!F");
            Token pointer = new Token(Lexem.POINTER, "#");
            rpn.add(pointer);
            rpn.add(jump);
        }
    }

    private void sortOp(Token currentToken) {
        int priority = getPriority(currentToken);
        if (tempStack.isEmpty() | getPriority(tempStack.get(tempStack.size() - 1)) < priority) {
            tempStack.add(currentToken);
        } else  if (getPriority(tempStack.get(tempStack.size() - 1)) >= priority) {
                rpn.add(tempStack.get(tempStack.size() - 1));
                tempStack.remove(tempStack.size() - 1);
                sortOp(currentToken);
            }
    }


    private int getPriority (Token token){
        int priority=0;
        String value;
        value = token.getValue();
        if (value.equals("+") | value.equals("-")){
            priority = 1;
        }
        if (value.equals("*") | value.equals("/")){
            priority = 2;
        }
        if (value.equals("^")){
            priority = 3;
        }
        return priority;
    }
}
