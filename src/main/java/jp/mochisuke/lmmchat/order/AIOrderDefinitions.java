package jp.mochisuke.lmmchat.order;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;

public class AIOrderDefinitions {
    public static HashMap<String,Class<AIOrderBase>> orders = new HashMap<>();

    public static void register(String name,Class<AIOrderBase> order){
        orders.put(name, order);
    }

    public static AIOrderBase createOrder(String orderName,List<Object> args){
        //get constructor
        try {
            Constructor<AIOrderBase> constructor = orders.get(orderName).getConstructor(List.class);
            //generate
            return constructor.newInstance(args);
        } catch (NoSuchMethodException | InvocationTargetException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static void initialize(){




    }

}
