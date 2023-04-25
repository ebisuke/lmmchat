package jp.mochisuke.lmmchat.order;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import org.slf4j.Logger;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class AIOrderParser {
    static final Logger logger = LogUtils.getLogger();

    public static String parsedRemnant(String orders){
        //remove ! lines
        StringBuilder sb=new StringBuilder();
        //period and japanese period recognizes as newline

        orders=orders.replaceAll("。", "。\n");


        String[] ordertext = orders.split("[\n|\\n]");
        for(String orderLine : ordertext) {
            if(!orderLine.startsWith("!") && !Objects.equals(orderLine.strip(), "")){
                sb.append(orderLine);
                sb.append("\n");
            }
        }

        return sb.toString();
    }
    public static List<AIOrderBase> parse(LivingEntity sender, VariablesContext context, String orders){
        //format
        //order_name arg1,arg2,arg3...
        Vector<AIOrderBase> parsed = new Vector<>();
        //period and japanese period recognizes as newline

        orders=orders.replaceAll("。", "。\n");

        //split by newline
        String[] ordertext = orders.split("[\n|\\n]");




        for(String orderLine : ordertext) {
            if(!orderLine.contains("!")){
                continue;
            }

            //remove before !
            orderLine = orderLine.substring(orderLine.indexOf("!")+1);

            //remove string from any non ascii character to line end
            orderLine=orderLine.replaceAll("[^\\x00-\\x7F]+", "");


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
            //concat after 1 by comma
            String argsString = Arrays.stream(orderNameAndArgs).reduce("", (a, b) -> a + "," + b);
            String[] args = argsString.split(",");
            //replace @s to owner id


            for(int i=0;i<args.length;i++){
                if(args[i].equals("@s")){
                    if(sender instanceof TamableAnimal){
                        String ownerid= String.valueOf(((TamableAnimal) sender).getOwner().getId());
                        args[i]=ownerid;
                    }else{
                        logger.error("sender is not tamable animal");
                    }

                }

            }

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
