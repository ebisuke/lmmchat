package jp.mochisuke.lmmchat.order;

public class VariablesContext {
    //variable map
    protected java.util.Map<String, Object> variables = new java.util.HashMap<>();


    public void setVar(String name, Object value) {
        variables.put(name, value);
    }
    public Object getVar(String name) {
        return variables.get(name);
    }
    public Object getVar(String name,Object defaultValue) {
        //name is number?
        if (name.matches("(|-)[0-9]+")) {
            //return number
            return Integer.parseInt(name);
        } else
        if (variables.containsKey(name)) {
            return variables.get(name);
        } else {
            return defaultValue;
        }
    }

    public void clear(){
        variables.clear();
    }
}