package jp.mochisuke.lmmchat.order;

import net.objecthunter.exp4j.Expression;
import net.objecthunter.exp4j.ExpressionBuilder;

public class VariablesContext {
    //variable map
    protected java.util.Map<String, Double> variables = new java.util.HashMap<>();
    public void setVar(String name, Double value) {
        variables.put(name, value);
    }
    public Object getVar(String name) {
        return variables.get(name);
    }
    public Object getVar(String name,Object defaultValue) {
        //name is number?

        if (variables.containsKey(name)) {
            return variables.get(name);
        } else {
            return defaultValue;
        }
    }
    public Object eval(String arg){
        if (variables.containsKey(arg)) {
            return variables.get(arg);
        }else {
            Expression exp = new ExpressionBuilder(arg).build();
            exp.setVariables(variables);
            return exp.evaluate();
        }
    }

    public void clear(){
        variables.clear();
    }
}