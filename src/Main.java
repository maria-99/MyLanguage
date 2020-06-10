import LexerExceptions.UnknownSymbolException;
import ParserExceptions.*;
import StackMachineExceptions.IncorrctVarNameException;
import StackMachineExceptions.IncorrectStructureNameException;
import StackMachineExceptions.StructureNotFoundException;

import java.util.List;

//a=2;if(a>b){a=while(a>b){a=a+1;}}

//a=1; if(8<2){if(5<4){x=8;}else{y=1;}m=7;while(a<2){a=a+1;}}else{n=1;}

//HashSet a; a add 6; b=6; if( a contains b){z=1;}else{z=0;}

/* x = 2^5; y= x-5; z= x+y;" +
               "LinkedList a; LinkedList b; HashSet m; HashSet n;" +
                "c=0; while (c<5){a add (c+1); m add (x-c); c=c+1;} " +
                "if(m contains 67){ k=1; } else{l=1; if(m contains 28){yes = 6;} }" +
               "b add (a get 2); */

public class Main {
    public static void main(String[] args) throws UnknownSymbolException, NoMatchException, IncompleteInputException,
            AssignExprException, WhileExprException, IfExprException, ElseExprException,
            structureDeclarationException, structureOpExprException, StructureNotFoundException,
            IncorrectStructureNameException, IncorrctVarNameException {
        System.out.println("Start ...");

        String rawInput = "x = 2^5; y= x-5; z= x+y;" +
                "LinkedList a; LinkedList b; HashSet m; HashSet n;" +
                "c=0; while (c<5){a add (c+1); m add (x-c); c=c+1;} " +
                "if(m contains 67){ k=1; } else{l=1; if(m contains 28){yes = 6;} }" +
                "b add (a get 2); m add 30; ";

        Lexer lexer = new Lexer(rawInput);

        List<Token> tokens = lexer.tokens();

        /*for (Token token : tokens) {
            System.out.println(token.getLexem() + "  " + token.getValue());
        }*/

        Parser parser = new Parser( tokens );
        parser.lang();

        RPN rpn = new RPN( tokens );
        List<Token> rpnList = rpn.intoRpn();

        /*for (Token token : rpnList) {
            System.out.println(token.getLexem() + "  " + token.getValue());
        }*/

        System.out.println("... Variables ...");

        StackMachine stack = new StackMachine(rpnList);
        List<Variable> varTable = stack.execute();
        for (Variable var : varTable) {
            System.out.println(var.getVariable() + "  " + var.getValue());
        }
        stack.getLists();
        stack.getHashSets();
    }
}
