package jp.mochisuke.lmmchat.helper;

import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;

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
        return null;
    }
}
