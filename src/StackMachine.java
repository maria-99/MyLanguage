import StackMachineExceptions.*;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class StackMachine {
    private final List<Token> rpn;
    private final List<Variable> varTable = new ArrayList<>();
    private final List<Token> stack = new ArrayList<>();
    private final List<LnkdList> createdLists = new ArrayList<>();
    private final List<HashSet> createdHashSets = new ArrayList<>();
    int rpnIndex=0;
    Token currentToken;

    public StackMachine(List<Token> rpn) {
        this.rpn = rpn;
    }

    public List<Variable> execute() throws StructureNotFoundException,
            IncorrectStructureNameException, IncorrctVarNameException {

        while(rpnIndex<rpn.size()){
            currentToken = rpn.get(rpnIndex);
            evaluate(currentToken);
        }
        return varTable;
    }

    private void evaluate(Token currentToken) throws StructureNotFoundException,
            IncorrectStructureNameException,IncorrctVarNameException {
        if(currentToken.getLexem()==Lexem.VAR | currentToken.getLexem()==Lexem.DIGIT){
            stack.add(currentToken);
            rpnIndex++;
        }
        else if (currentToken.getLexem()==Lexem.OP){
            operation(currentToken.getValue());
            rpnIndex++;
        }
        else if (currentToken.getLexem()==Lexem.ASSIGN_OP){
            assign();
            rpnIndex++;
        }
        else if (currentToken.getLexem()==Lexem.JUMP){
            jump();
        }
        else if (currentToken.getLexem()==Lexem.LOGIC_OP){
            logicOperation(currentToken.getValue());
            rpnIndex++;
        }
        else if (currentToken.getLexem()==Lexem.LIST_KW){
            createList();
            rpnIndex++;
        }
        else if (currentToken.getLexem()==Lexem.HASH_SET_KW){
            createHashset();
            rpnIndex++;
        }
        else if (currentToken.getLexem()==Lexem.STRUCTURE_OP_KW){
            structureOp(currentToken.getValue());
            rpnIndex++;
        }
        else if (currentToken.getLexem()==Lexem.STRUCTURE_OP_2_KW){
            structureOp2();
            rpnIndex++;
        }
        else if (currentToken.getLexem()==Lexem.STRUCTURE_LOGIC_OP_KW){
            structureLogicOp();
            rpnIndex++;
        }
        else if (currentToken.getLexem()==Lexem.STRUCTURE_GETTER_OP_KW){
            structureGetOp();
            rpnIndex++;
        }
    }

    private void structureOp2() throws StructureNotFoundException, IncorrctVarNameException{
        Token value = getOperand();
        Token index = getOperand();
        Token listName = getOperand();
        setElement(listName.getValue(), translate(value), translate(index));
    }

    private void setElement(String listName, int value, int index) throws StructureNotFoundException {
        findList(listName).setElement(index, value);
    }

    private void structureGetOp() throws StructureNotFoundException,IncorrctVarNameException{
        Token index = getOperand();
        Token listName = getOperand();
        int got = findList(listName.getValue()) . getElement(translate(index)) . value;
        stack.add(new Token(Lexem.DIGIT,String.valueOf(got)));
    }

    private void structureLogicOp() throws StructureNotFoundException, IncorrctVarNameException{
        Token x = getOperand();
        Token name = getOperand();
        if(findHashSet(name.getValue()).contains(translate(x))){
            stack.add(new Token(Lexem.VAR,"true"));
        }
        else{
            stack.add(new Token(Lexem.VAR,"false"));
        }
    }

    private void createHashset() throws IncorrectStructureNameException {
        Token name = getOperand();
        for (HashSet currentHashset : createdHashSets){
            if (currentHashset.getName().equals(name.getValue())){
                throw new IncorrectStructureNameException("HashSet already exists with name: " + name.getValue());
            }
        }
        for (LnkdList currentList : createdLists){
            if (currentList.getName().equals(name.getValue())){
                throw new IncorrectStructureNameException("List already exists with name: " + name.getValue());
            }
        }
        HashSet newHashSet = new HashSet(name.getValue());
        createdHashSets.add(newHashSet);
    }

    private void structureOp(String Op) throws StructureNotFoundException, IncorrctVarNameException{
        Token value = getOperand();
        Token structureName = getOperand();
        if (Op.equals("add")) {
            addElement(structureName.getValue(), translate(value));
        } else if (Op.equals("remove")) {
            removeElement(structureName.getValue(), translate(value));
        }
    }


    private void removeElement(String listName, int value) throws StructureNotFoundException {
        LnkdList list = findList(listName);
        list.deleteElement(value);
    }

    private void addElement(String name, int value) throws StructureNotFoundException {
        try {
            findList(name).addElement(value);
        }catch (StructureNotFoundException e){
            try{
                findHashSet(name).addValue(value);
            }catch (StructureNotFoundException ee){
                throw new StructureNotFoundException("No list or hashset named: " + name);
            }
        }

    }

    private LnkdList findList(String name) throws StructureNotFoundException {
        LnkdList foundList = null;
        boolean found = false;
        for (LnkdList list : createdLists){
            if (list.getName().equals(name)){
                foundList=list;
                found = true;
            }
        }
        if (!found){
            throw new StructureNotFoundException("No such list!");
        }
        return foundList;
    }

    private HashSet findHashSet(String name) throws StructureNotFoundException{
        HashSet foundHashSet = null;
        boolean found = false;
        for (HashSet hashSet : createdHashSets){
            if (hashSet.getName().equals(name)){
                foundHashSet=hashSet;
                found = true;
            }
        }
        if (!found){
            throw new StructureNotFoundException("No such hashSet!");
        }
        return foundHashSet;
    }

    private void createList() throws IncorrectStructureNameException{
        Token name = getOperand();
        for (HashSet currentHashset : createdHashSets){
            if (currentHashset.getName().equals(name.getValue())){
                throw new IncorrectStructureNameException("hashset already exists with name: " + name.getValue());
            }
        }
        for (LnkdList currentList : createdLists){
            if (currentList.getName().equals(name.getValue())){
                throw new IncorrectStructureNameException("list already exists with name: " + name.getValue());
            }
        }
        LnkdList newList = new LnkdList(name.getValue());
        createdLists.add(newList);
    }

    private void jump() {
        Token address = getOperand();
        if(currentToken.getValue().equals("!")){
            rpnIndex=parseInt(address.getValue());
        }
        else if(currentToken.getValue().equals("!F")) {
            Token condition = getOperand();
            if (condition.getValue().equals("false")){
                rpnIndex=parseInt(address.getValue());
            }
            else {
                rpnIndex++;
            }
        }
    }

    private void logicOperation(String logicOp) throws IncorrctVarNameException{
        Token b = getOperand();
        Token a = getOperand();
        switch (logicOp) {
            case "<":
                lessThan(translate(a), translate(b));
                break;
            case ">":
                greaterThan(translate(a), translate(b));
                break;
            case "==":
                equalesTo(translate(a), translate(b));
                break;
            case "!=":
                notEqualTo(translate(a), translate(b));
                break;
            case "<=":
                lessOrEqual(translate(a), translate(b));
                break;
            case ">=":
                greaterOrEqual(translate(a), translate(b));
                break;
        }
    }

    private void lessOrEqual(int a, int b){
        if(a<=b){
            stack.add(new Token(Lexem.VAR,"true"));
        }
        else{
            stack.add(new Token(Lexem.VAR,"false"));
        }
    }

    private void greaterOrEqual(int a, int b){
        if(a>=b){
            stack.add(new Token(Lexem.VAR,"true"));
        }
        else{
            stack.add(new Token(Lexem.VAR,"false"));
        }
    }

    private void notEqualTo(int a, int b){
        if(a!=b){
            stack.add(new Token(Lexem.VAR,"true"));
        }
        else{
            stack.add(new Token(Lexem.VAR,"false"));
        }
    }

    private void equalesTo(int a, int b){
        if(a==b){
            stack.add(new Token(Lexem.VAR,"true"));
        }
        else{
            stack.add(new Token(Lexem.VAR,"false"));
        }
    }

    private void greaterThan(int a, int b){
        if(a>b){
            stack.add(new Token(Lexem.VAR,"true"));
        }
        else{
            stack.add(new Token(Lexem.VAR,"false"));
        }
    }

    private void lessThan(int a, int b){
        if(a<b){
            stack.add(new Token(Lexem.VAR,"true"));
        }
        else{
            stack.add(new Token(Lexem.VAR,"false"));
        }
    }

    private void assign() throws IncorrctVarNameException {
        Token b = getOperand();
        Token a = getOperand();
        addVar(a.getValue(),translate(b));
    }

    private void operation(String op) throws IncorrctVarNameException{
        Token b = getOperand();
        Token a = getOperand();
        if (op.equals("+")){
            add(translate(a),translate(b));
        }
        if (op.equals("-")){
            sub(translate(a),translate(b));
        }
        if (op.equals("*")){
            multiply(translate(a),translate(b));
        }
        if (op.equals("/")){
            divide(translate(a),translate(b));
        }
        if (op.equals("^")){
            raiseToThePower(translate(a),translate(b));
        }

    }

    private void add(int a, int b) {
        int sum=a+b;
        addToken(sum);
    }

    private void sub(int a, int b) {
        int difference=a-b;
        addToken(difference);
    }

    private void multiply(int a, int b) {
        int product=a*b;
        addToken(product);
    }

    private void divide(int a, int b) {
        int quotient=a/b;
        addToken(quotient);
    }

    private void raiseToThePower(int a, int b) {
        int answer = (int)Math.pow(a,b);
        addToken(answer);
    }

    private void addToken(int answer){
        Token newToken = new Token(Lexem.DIGIT, String.valueOf(answer));
        stack.add(newToken);

    }

    private void addVar(String varName, int varValue){
        boolean varFound=false;
        for(Variable var : varTable){
            if (var.getVariable().equals(varName)){
                varFound=true;
                var.changeValue(varValue);
            }
        }
        if(!varFound) {
            Variable var = new Variable(varName, varValue);
            varTable.add(var);
        }
    }

    private Token getOperand(){
        Token operand = stack.get(stack.size()-1);
        stack.remove(stack.size()-1);
        return operand;
    }

    private int translate(Token token) throws IncorrctVarNameException {
        int digit=0;
        if(token.getLexem()==Lexem.DIGIT){
            digit = parseInt(token.getValue());
        }
        if(token.getLexem()==Lexem.VAR){
            boolean varFound=false;
            for(Variable var : varTable){
                if (token.getValue().equals(var.getVariable())){
                    varFound=true;
                    digit=var.getValue();
                }
            }
            if(!varFound){
                throw new IncorrctVarNameException("No var created with name " + token.getValue());
            }
        }
        return digit;
    }

    public void getLists(){
        System.out.println("... Lists ... ");
        for (LnkdList list : createdLists){
            System.out.println("List " +list.getName());
            while(list.size>0){
                 System.out.println( list.first.value);
                 list.deleteElement(0);
            }
        }
    }

    public void getHashSets(){
        System.out.println("... HashSets ... ");
        for (HashSet hashSet : createdHashSets){
            System.out.println("hashSet " +hashSet.getName());
            for (int element : hashSet.getAllElements()) {
                System.out.println(element);
            }
        }
    }
}
