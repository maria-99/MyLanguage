public class Variable {
    private final String variable;
    private int value;

    public Variable(String variable, int value)
    {
        this.variable = variable;
        this.value = value;
    }

    public String getVariable()
    {
        return variable;
    }

    public int getValue()
    {
        return value;
    }

    public void changeValue(int newValue){
        this.value = newValue;
    }
}
