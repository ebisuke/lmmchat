package jp.mochisuke.lmmchat.helper;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;

import java.lang.reflect.InvocationTargetException;

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
}
