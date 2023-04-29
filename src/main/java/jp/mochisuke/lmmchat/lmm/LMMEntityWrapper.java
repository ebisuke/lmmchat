package jp.mochisuke.lmmchat.lmm;

import net.minecraft.nbt.CompoundTag;
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
    public void setNBTMeta(String key,Object value){

        if(value instanceof Integer){
            entity.getPersistentData().putInt(key,(Integer) value);
        }else if(value instanceof String){
            entity.getPersistentData().putString(key,(String) value);
        }else if(value instanceof Boolean){
            entity.getPersistentData().putBoolean(key,(Boolean) value);
        }else if(value instanceof Float){
            entity.getPersistentData().putFloat(key,(Float) value);
        }else if(value instanceof Double){
            entity.getPersistentData().putDouble(key,(Double) value);
        }else if(value instanceof Byte){
            entity.getPersistentData().putByte(key,(Byte) value);
        }else if(value instanceof Short){
            entity.getPersistentData().putShort(key,(Short) value);
        }else if(value instanceof Long){
            entity.getPersistentData().putLong(key,(Long) value);
        }else if(value instanceof byte[]){
            entity.getPersistentData().putByteArray(key,(byte[]) value);
        }else if(value instanceof int[]){
            entity.getPersistentData().putIntArray(key,(int[]) value);
        }else if(value instanceof long[]){
            entity.getPersistentData().putLongArray(key,(long[]) value);
        }else if(value instanceof CompoundTag){
            entity.getPersistentData().put(key,(CompoundTag) value);
        }else{
            throw new IllegalArgumentException("value must be primitive or CompoundTag");
        }
    }
    public int getNBTMetaInt(String key){
        return entity.getPersistentData().getInt(key);
    }
    public String getNBTMetaString(String key){
        return entity.getPersistentData().getString(key);
    }
    public boolean getNBTMetaBoolean(String key){
        return entity.getPersistentData().getBoolean(key);
    }
    public float getNBTMetaFloat(String key){
        return entity.getPersistentData().getFloat(key);
    }
    public double getNBTMetaDouble(String key){
        return entity.getPersistentData().getDouble(key);
    }
    public byte getNBTMetaByte(String key){
        return entity.getPersistentData().getByte(key);
    }
    public short getNBTMetaShort(String key){
        return entity.getPersistentData().getShort(key);
    }
    public long getNBTMetaLong(String key){
        return entity.getPersistentData().getLong(key);
    }
    public byte[] getNBTMetaByteArray(String key){
        return entity.getPersistentData().getByteArray(key);
    }
    public int[] getNBTMetaIntArray(String key){
        return entity.getPersistentData().getIntArray(key);
    }
    public long[] getNBTMetaLongArray(String key){
        return entity.getPersistentData().getLongArray(key);
    }
    public CompoundTag getNBTMetaCompoundTag(String key){
        return entity.getPersistentData().getCompound(key);
    }


    public LMMChatMode getChatMode(){
        int value= entity.getPersistentData().getInt("lmmchatmode");
        return LMMChatMode.values()[value];
    }
    public void setChatMode(LMMChatMode mode){
        entity.getPersistentData().putInt("lmmchatmode",mode.ordinal());
    }
}
