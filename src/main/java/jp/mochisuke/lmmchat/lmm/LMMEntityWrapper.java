package jp.mochisuke.lmmchat.lmm;

import net.minecraft.world.entity.TamableAnimal;

import java.lang.reflect.InvocationTargetException;

public class LMMEntityWrapper {
    private final TamableAnimal entity;

    public LMMEntityWrapper(TamableAnimal entity) {
        this.entity = entity;
    }

    public TamableAnimal get() {
        return entity;
    }
    public static LMMEntityWrapper of(TamableAnimal entity){
        return new LMMEntityWrapper(entity);
    }
    public void setWait(boolean wait) {
        //use reflection
        try {
            //get setWait method
            var method = entity.getClass().getMethod("setWait", boolean.class);
            //invoke method
        method.invoke(entity, wait);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
