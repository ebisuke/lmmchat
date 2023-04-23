package jp.mochisuke.lmmchat.order;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;

import java.util.List;

public class OwnerIdOrder extends AIOrderBase{
    public OwnerIdOrder(LivingEntity entity, VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {

    }

    @Override
    protected boolean isImmediate() {
       return true;
    }

    @Override
    public void executeImpl() {
        TamableAnimal animal=(TamableAnimal)entity;
        if(animal.getOwner()!=null) {
            notifyAI("owner id:" + animal.getOwner().getId()+" location:"+animal.getOwner().blockPosition());
            //store
            val("id",animal.getOwner().getId());
            val("x",animal.getOwner().blockPosition().getX());
            val("y",animal.getOwner().blockPosition().getY());
            val("z",animal.getOwner().blockPosition().getZ());
        }else{
            throw new RuntimeException("No owner");
        }
    }
}
