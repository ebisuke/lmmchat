package jp.mochisuke.lmmchat.order;

import net.minecraft.world.entity.LivingEntity;

import java.util.HashMap;
import java.util.List;

public class AIOrderDefinitions {
    //generator map
    public static HashMap<String, AIOrderBase.Generator> orders = new HashMap<>();

    public static void register(String name,AIOrderBase.Generator order){
        orders.put(name, order);
    }

    public static AIOrderBase createOrder(LivingEntity mob, String orderName, VariablesContext context, List<Object> args){
        //get constructor
        if(!orders.containsKey(orderName)){
            return null;
        }
        var gen = orders.get(orderName).generate(mob,context,args);
        //generate
        return gen;
    }

    public static void initialize(){


        //register orders
        register("place", BlockPlaceOrder::new);
        register("end", EndOrder::new);
        register("move", MoveOrder::new);
        register("findowner", OwnerIdOrder::new);
        register("findentity", FindEntityOrder::new);
        register("findblock", FindBlockOrder::new);
        register("takeitem", ItemTakeOrder::new);
        register("giveitem", ItemGiveOrder::new);
        register("put", BlockItemPutOrder::new);
        register("pick", BlockItemPickupOrder::new);
        register("pos", PositionOrder::new);
        register("inspect", BlockInspectOrder::new);
        register("concentrate", ConcentrateOrder::new);
        register("swap", SwapHandOrder::new);
        register("check", CheckItemOrder::new);
        register("wield", WieldOrder::new);
        register("interact", InteractOrder::new);
        register("fortify", FortifyOrder::new);
        register("craft", CraftingOrder::new);
        register("healowner", HealOwnerOrder::new);
        register("emerg", TeleportOwnerOrder::new);
        register("observe", ObserveOrder::new);
        //register("cc", TransitionComputerCraftModeOrder::new);
        //register("exit", TransitionNormalModeOrder::new);

    }

}
