package jp.mochisuke.lmmchat.helper;

import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.goal.AIGoalBase;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Tuple;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Helper {

    public static Supplier<Component> wrapSupplier(Component supplier){
        Supplier<Component> componentSupplier=()->{
            try {
                return supplier;
            }catch (Exception e){
                return Component.nullToEmpty(e.getMessage());
            }
        };
        return componentSupplier;
    }
    public static Container getInventoryContainer(Entity entity){
        //use reflection
        var fields=entity.getClass().getDeclaredFields();
        for(var field:fields){
            if(field.getType().equals(Container.class)){
                field.setAccessible(true);
                try {
                    return (Container) field.get(entity);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        //check littlemaidmob inventory
        if(entity.getClass().getName().contains("LittleMaidEntity")){
            try {
                var method=entity.getClass().getMethod("getInventory");
                return (Container) method.invoke(entity);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    public static String getDateAsTimeFormat(){
        var time= LMMChat.getDayTime();
        var days=LMMChat.getElapsedDays();

        var hour=time/1000;
        var minute=(time%1000)*60/1000;
        // time 0==6:00
        hour+=6;
        if(hour>=24){
            hour-=24;
        }
        if(days<=1) {
            return String.format("%d day %02d:%02d", days, hour, minute);
        }else{
            return String.format("%d days %02d:%02d",days, hour, minute);
        }
    }
    public static Goal getGoal(Mob entity, Class<? extends AIGoalBase> goalClass){
        var m=entity.goalSelector. getAvailableGoals().stream().filter(g-> Objects.equals(g.getGoal().getClass(),goalClass)).findFirst();
        if(m.isPresent()){
            var goal=m.get().getGoal();
            if(goal.getClass().equals(goalClass)){
                return goal;
            }
        }
        return null;
    }
    public static ArrayList<ItemStack> findEdibleItems(Container inventory){
        var list=new ArrayList<ItemStack>();
        for(int i=0;i<inventory.getContainerSize();i++){
            var stack=inventory.getItem(i);
            if(stack.isEdible()){
                list.add(stack);
            }
        }
        return list;
    }
    public static ArrayList<ItemStack> findPotionItems(Container inventory){
        var list=new ArrayList<ItemStack>();
        for(int i=0;i<inventory.getContainerSize();i++){
            var stack=inventory.getItem(i);
            //is potion?

            if(stack.getItem() instanceof PotionItem){
                list.add(stack);
            }
        }
        return list;
    }
    public static Player getOwner(TamableAnimal entity){
        Player owner=null;
        if(entity.getOwner() instanceof Player){
            owner=(Player) entity.getOwner();
        }else{
            //byuuid
            var uuid=entity.getOwnerUUID();
            if(uuid!=null){

                owner=LMMChat.findPlayerByUUID(uuid);


            }
        }

        return owner;
    }
    public static int countItem(Container inventory, Predicate<ItemStack> filter){
        int count=0;
        for(int i=0;i<inventory.getContainerSize();i++){
            var stack=inventory.getItem(i);
            if(filter.test(stack)){
                count+=stack.getCount();
            }
        }
        return count;
    }
    public static void consumeItem(Container container, Item item,int count){
        for(int i=0;i<container.getContainerSize();i++){
            var stack=container.getItem(i);
            if(stack.getItem().equals(item)){
                if(stack.getCount()<=count){
                    container.removeItem(i,stack.getCount());
                    count-=stack.getCount();
                }else{
                    container.removeItem(i,count);
                    count=0;
                }
                if(count<=0){
                    break;
                }
            }
        }
    }

    public static Tuple<Integer,ItemStack> findItemStack(Container inventory, String itemName) {
        for(int i=0;i<inventory.getContainerSize();i++){
            var stack=inventory.getItem(i);
            if(stack.getDisplayName().getString().toLowerCase().contains(itemName)){
                return new Tuple<>(i,stack);
            }
        }
        return null;
    }
    public static boolean addItem(Container inventory, ItemStack stack){

        //backup for prevent failing
        Map<Integer,ItemStack> backup=new HashMap<>();
        for(int i=0;i<inventory.getContainerSize();i++){
            var s=inventory.getItem(i);
            if(!s.isEmpty()){
                backup.put(i,s.copy());
            }
        }

        for(int i=0;i<inventory.getContainerSize();i++){
            var s=inventory.getItem(i);
            if(s.isEmpty()){
                inventory.setItem(i,stack);
                return true;
            }else if(ItemStack.isSameItem(s,stack) && s.getCount()+stack.getCount()<=s.getMaxStackSize()){
                s.setCount(s.getCount()+stack.getCount());
                return true;
            }else if(ItemStack.isSameItem(s,stack) && s.getCount()+stack.getCount()>s.getMaxStackSize()){
                var newStack=stack.copy();
                newStack.setCount(s.getCount()+stack.getCount()-s.getMaxStackSize());
                s.setCount(s.getMaxStackSize());
                stack=newStack;
            }
        }
        //rollback
        for(var entry:backup.entrySet()){
            inventory.setItem(entry.getKey(),entry.getValue());
        }
        return false;
    }

}
