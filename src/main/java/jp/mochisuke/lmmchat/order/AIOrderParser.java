package jp.mochisuke.lmmchat.order;

import net.minecraft.world.entity.Mob;

import java.util.List;
import java.util.Vector;

public class AIOrderParser {
    public static List<AIOrderBase> parse(Mob sender,VariablesContext context, String orders){
        //format
        //order_name arg1,arg2,arg3...
        Vector<AIOrderBase> parsed = new Vector<>();
        //split by newline
        String[] ordertext = orders.split("\n");
        for(String orderLine : ordertext) {
            if(!orderLine.startsWith("!")){
                continue;
            }
            //remove !
            orderLine = orderLine.substring(1);
            //split by space
            String[] orderNameAndArgs = orderLine.split(" ");

            //get order name
            String orderName = orderNameAndArgs[0];

            //get args
            if(orderNameAndArgs.length == 1){
                //create order
                parsed.add(AIOrderDefinitions.createOrder(sender,orderName,context, List.of()));
                continue;
            }
            String[] args = orderNameAndArgs[1].split(",");

            //create order
            parsed.add(AIOrderDefinitions.createOrder(sender,orderName,context, List.of((Object[]) args)));
        }
        return parsed;


    }
}
