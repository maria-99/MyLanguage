/*
 * VAR -> [a-z]+
 * ASSIGN_OP -> =
 * DIGIT -> 0 | ([1-9][0-9]*) ???
 * OP -> + | - | * | /
 * SC -> ;
 * WHILE_KW -> while
 * IF_KW -> if
 * ELSE_KW -> else
 * L_CB -> {
 * R_CB -> }
 * L_B -> (
 * R_B -> )
 * LOGIC_OP -> < | > | == | <= | >= | !=
 */
import java.util.regex.Pattern;

public enum Lexem {

    WHILE_KW("^while"),
    IF_KW("^if"),
    ELSE_KW("^else"),
    LIST_KW("^LinkedList"),
    HASH_SET_KW("^HashSet"),
    STRUCTURE_OP_KW("^(add|remove)"),
    STRUCTURE_OP_2_KW("^(set)"),
    STRUCTURE_LOGIC_OP_KW("^contains"),
    STRUCTURE_GETTER_OP_KW("^get"),
    VAR("^[a-z]+"),
    DIGIT("^(0|[1-9][0-9]*)"),
    LOGIC_OP("^(<=|>=|<|>|==|!=)"),
    ASSIGN_OP("^="),
    OP("^((\\+)|-|(\\*)|/|(\\^))"),
    SC("^;"),
    L_CB("^\\{"),
    R_CB("^\\}"),
    L_B("^\\("),
    R_B("^\\)"),
    SPACE("^(\\s)+"),
    JUMP("^(!|!F)"),
    POINTER("^#");


    public Pattern pattern;

    Lexem (String regex){
        this.pattern = Pattern.compile(regex);
    }

    public Pattern getPattern() {
        return pattern;
    }


}
