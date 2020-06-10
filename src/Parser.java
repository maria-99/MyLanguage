import ParserExceptions.*;

import java.util.ArrayList;
import java.util.List;

public class Parser {

    private final List<Token> tokens;
    int i;
    boolean error=false;
    List<Integer> rollbackBody = new ArrayList<>();
    int logicRollback;


    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void lang() throws NoMatchException, IncompleteInputException, AssignExprException, WhileExprException, IfExprException, ElseExprException, structureOpExprException, structureDeclarationException {
        i = 0;
        do  {
            expr();
        }while (i < tokens.size());
        System.out.println("Parsing completed.");
    }

    private void expr() throws NoMatchException, IncompleteInputException, AssignExprException, WhileExprException, IfExprException, ElseExprException, structureDeclarationException, structureOpExprException {
        rollbackBody.add(i);
        try {
            assignExpr();
        } catch (NoMatchException e) {
            i = rollbackBody.get(rollbackBody.size() - 1);
            try {
                structureOpExpr();
            } catch (NoMatchException e6) {

                try {
                    whileExpr();
                } catch (NoMatchException ee) {
                    // i=rollbackBody.get(rollbackBody.size()-1);
                    try {
                        ifExpr();
                    } catch (NoMatchException e3) {
                        try {
                            structureDeclarationExpr();
                        } catch (NoMatchException e4) {
                            throw new NoMatchException("No expression starts with " + getCurrentToken().getLexem() + " at " + getCurrentToken().getValue());
                        }

                    }
                }
            }
        }
       /* catch (AssignExprException NotAssignExpr){
            i=rollbackBody.get(rollbackBody.size()-1);
            try{
                structureOpExpr();
            }
            catch (structureOpExprException NotStrucOp){
                throw new structureOpExprException(NotAssignExpr.getMessage() + " or " + NotStrucOp.getMessage());
            }
        }*/
        // rollbackBody.remove(rollbackBody.size()-1);
    }

    private void assignExpr() throws NoMatchException, IncompleteInputException, AssignExprException{
        var();
        assignOp();
        try {
            valueExpr();
            sC();
        } catch (NoMatchException e) {
            throw new AssignExprException(e.getMessage());
        }
    }



    private void structureOpExpr() throws NoMatchException,IncompleteInputException, structureOpExprException {
        var();
        try {
            try {
                structureOp();
            }catch (NoMatchException e){
                structureOp2Param();
                valueExpr();
            }
            valueExpr();

            sC();
        } catch (NoMatchException e) {
            throw new structureOpExprException(e.getMessage());
        }
    }

    private void structureOp2Param() throws NoMatchException, IncompleteInputException{
        match(getCurrentToken(),Lexem.STRUCTURE_OP_2_KW);
    }

    private void structureOp() throws NoMatchException,IncompleteInputException {
        match(getCurrentToken(), Lexem.STRUCTURE_OP_KW);
    }

    private void structureDeclarationExpr() throws NoMatchException,IncompleteInputException,structureDeclarationException{
        structureType();
        try{
            var();
            sC();
        }catch (NoMatchException e){
            throw new structureDeclarationException(e.getMessage());
        }
    }

    private void structureType() throws NoMatchException,IncompleteInputException{
        try {
            listKw();
        }
        catch(NoMatchException e) {
            try {
                hashsetKw();
            }
            catch (NoMatchException ee){
                throw new NoMatchException(e.getMessage() + ee.getMessage());
            }
        }
    }

    private void hashsetKw() throws NoMatchException,IncompleteInputException{
        match(getCurrentToken(),Lexem.HASH_SET_KW);
    }

    private void listKw() throws NoMatchException,IncompleteInputException{
        match(getCurrentToken(), Lexem.LIST_KW);
    }

    private void ifExpr() throws NoMatchException, IncompleteInputException, AssignExprException, WhileExprException, IfExprException, ElseExprException, structureOpExprException, structureDeclarationException{
        ifKw();
        try {
            head();
            body();
        }
        catch (NoMatchException e){
            throw new IfExprException(e.getMessage());
        }
        try{
            elseExpr();
        }
        catch (NoMatchException | IncompleteInputException ignored){
            }
    }

    private void elseExpr() throws NoMatchException, IncompleteInputException, AssignExprException, WhileExprException, IfExprException, ElseExprException, structureOpExprException,structureDeclarationException{
        elseKw();
        try {
            body();
        }
        catch (NoMatchException e){
            throw new ElseExprException(e.getMessage());
        }
    }

    private void elseKw()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.ELSE_KW);
    }

    private void ifKw()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.IF_KW);
    }

    private void whileExpr() throws NoMatchException, IncompleteInputException, AssignExprException, WhileExprException, IfExprException, ElseExprException, structureOpExprException, structureDeclarationException{
        whileKw();
        try {
            head();
            body();
        }
        catch (NoMatchException e){
            throw new WhileExprException(e.getMessage());
        }
    }

    private void sC()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.SC);
    }

    private void body() throws NoMatchException, IncompleteInputException, AssignExprException, WhileExprException, IfExprException, ElseExprException, structureDeclarationException, structureOpExprException {

        lCB();
        expr();
        while (!error){
            //rollbackBody.add(i);//add new nb into the rollback stack for current body
            try {
                expr();
            }
            catch (NoMatchException er){
                error=true;
                //this.i =rollbackBody.get(rollbackBody.size()-1);
            }
            //rollbackBody.remove(rollbackBody.size()-1); //remove current
        }

        error=false;
        rCB();

    }

    private void lCB()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.L_CB);
    }

    private void rCB()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.R_CB);
    }

    private void head()throws NoMatchException, IncompleteInputException {
        lB();
        logicExpr();
        rB();
    }

    private void rB()throws NoMatchException, IncompleteInputException{
        match(getCurrentToken(), Lexem.R_B);
    }

    private void logicExpr()throws NoMatchException, IncompleteInputException {
        logicRollback = i;
        try {
            var();
            structureLogicOp();
            value();
        }catch(NoMatchException e){
            i=logicRollback;
            try {
                value();
                logicOp();
                value();
            }catch (NoMatchException ee){
                throw new NoMatchException(e.getMessage() + " OR " + ee.getMessage() +" at " + i);
            }

        }
    }

    private void structureLogicOp() throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(),Lexem.STRUCTURE_LOGIC_OP_KW);
    }

    private void logicOp()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.LOGIC_OP);
    }

    private void lB()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.L_B);
    }

    private void whileKw()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.WHILE_KW);
    }

    private void var()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.VAR);
    }


    private void assignOp()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.ASSIGN_OP);
    }

    private void digit()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.DIGIT);
    }

    private void op()throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(), Lexem.OP);
    }

    private void valueExpr()throws NoMatchException, IncompleteInputException{
        try {
            value();
        }catch(NoMatchException e1){
            try {
                lB();
                valueExpr();
                rB();
            }catch (NoMatchException e2){throw new NoMatchException(e1.getMessage() +" OR "+ e2.getMessage());}
        }
        while (!error){
            try {
                op();
                valueExpr();
            }
            catch (NoMatchException e){
                error=true;

            }
        }
        error=false;

    }

    private void value()throws NoMatchException, IncompleteInputException {
        try{
            digit();
        }
        catch (NoMatchException varNotFound){
            try {
                var();
                if (getCurrentToken().getLexem()==Lexem.STRUCTURE_GETTER_OP_KW) {
                    structureGetOp();
                    value();
                }
            }
            catch (NoMatchException digitNotFound){
                throw new NoMatchException(varNotFound.getMessage() + " OR " + digitNotFound.getMessage());

            }
        }
    }

    private void structureGetOp() throws NoMatchException, IncompleteInputException {
        match(getCurrentToken(),Lexem.STRUCTURE_GETTER_OP_KW);
    }


    private void match(Token t, Lexem l) throws NoMatchException{
        if (t.getLexem()==l){
            i++;

        }
        else{
            throw new NoMatchException("expecting " + l + " intead of " + t.getLexem() + " at " + t.getValue());
        }

    }

    private Token getCurrentToken() throws IncompleteInputException{
        if(i>=tokens.size()){
            throw new IncompleteInputException("Incomplete input");

            }
        return tokens.get(i);

    }

}
