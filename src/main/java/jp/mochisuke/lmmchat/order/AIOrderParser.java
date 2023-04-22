package jp.mochisuke.lmmchat.order;

import java.util.List;
import java.util.Vector;

public class AIOrderParser {
    public static List<AIOrderBase> parse(String orders){
        //format
        //order_name arg1,arg2,arg3...
        Vector<AIOrderBase> parsed = new Vector<>();
        //split by newline
        String[] ordertext = orders.split("\n");
        for(String orderLine : ordertext) {
            //split by space
            String[] orderNameAndArgs = orderLine.split(" ");

            //get order name
            String orderName = orderNameAndArgs[0];

            //get args
            String[] args = orderNameAndArgs[1].split(",");

            //create order
            parsed.add(AIOrderDefinitions.createOrder(orderName, List.of(args)));
        }
        return parsed;


    }
}
