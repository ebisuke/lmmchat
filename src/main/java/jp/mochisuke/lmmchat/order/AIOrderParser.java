package jp.mochisuke.lmmchat.order;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.LivingEntity;
import org.slf4j.Logger;

import java.util.List;
import java.util.Vector;

public class AIOrderParser {
    static final Logger logger = LogUtils.getLogger();

    public static String parsedRemnant(String orders){
        //remove ! lines
        //after ! will remove
        var lines=orders.split("[\n|\\n]");
        StringBuilder sb=new StringBuilder();
        for(var line:lines){
            if(line.contains("!")){
                sb.append(line.substring(0,line.indexOf("!")));
            }else{
                sb.append(line);
            }
            sb.append("\n");
        }
        return sb.toString();
    }
    public static List<AIOrderBase> parse(LivingEntity sender, VariablesContext context, String orders){
        //format
        //order_name arg1,arg2,arg3...
        Vector<AIOrderBase> parsed = new Vector<>();
        //split by newline
        String[] ordertext = orders.split("[\n|\\n]");




        for(String orderLine : ordertext) {
            if(!orderLine.contains("!")){
                continue;
            }

            //remove before !
            orderLine = orderLine.substring(orderLine.indexOf("!")+1);
            //remove {}
            orderLine = orderLine.replaceAll("[{|}]", "");
            //remove ""
            orderLine = orderLine.replaceAll("\"", "");
            //remove ''
            orderLine = orderLine.replaceAll("'", "");
            //remove ~
            orderLine = orderLine.replaceAll("~", "");
            //remove @
            orderLine = orderLine.replaceAll("@", "");
            //remove $
            orderLine = orderLine.replaceAll("\\$", "");
            //remove #
            orderLine = orderLine.replaceAll("#", "");
            //remove %

            orderLine = orderLine.replaceAll("%", "");

            //split by space
            String[] orderNameAndArgs = orderLine.split(" ");

            //get order name
            String orderName = orderNameAndArgs[0];

            //get args
            if(orderNameAndArgs.length == 1){
                //create order
                var ret=AIOrderDefinitions.createOrder(sender,orderName,context, List.of());
                if(ret!=null){
                    parsed.add(ret);
                }
                continue;
            }
            String[] args = orderNameAndArgs[1].split(",");

            //create order
            var ret=AIOrderDefinitions.createOrder(sender,orderName,context, List.of((Object[]) args));
            if(ret!=null){
                parsed.add(ret);
            }
        }
        logger.info("parsed order:"+parsed.size());
        //set intermediate flag
//        for(int i=0;i<parsed.size()-1;i++){
//            parsed.get(i).setIntermediate(true);
//        }
        return parsed;


    }
}
