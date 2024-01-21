package jp.mochisuke.lmmchat.embedding;

import org.antlr.v4.misc.Utils;

public class EmbeddingAnswer implements Cloneable {
    private final Object answer;
    private final Object[] args;

    public EmbeddingAnswer(Object answer,Object[] args){
        this.answer = answer;
        this.args = args;
        assert (answer instanceof Utils.Func0<?> || answer instanceof String);
    }

    public boolean isFunction(){
        return answer instanceof Utils.Func0<?>;
    }
    @Override
    public String toString(){
        if(isFunction()){
            return ((Utils.Func0<?>)answer).exec().toString();
        }else{
            return (String)answer;
        }
    }

    @Override
    public EmbeddingAnswer clone(){
        return new EmbeddingAnswer(answer,args);
    }

    public Object getAnswer(){
        return answer;
    }

    public Object[] getArgs(){
        return args;
    }
}
