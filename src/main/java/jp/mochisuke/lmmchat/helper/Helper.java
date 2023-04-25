package jp.mochisuke.lmmchat.helper;

import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.goal.AIGoalBase;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.PotionItem;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Objects;

public class Helper {
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
}
