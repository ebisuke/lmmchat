package jp.mochisuke.lmmchat.order;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.TamableAnimal;

import java.util.List;

public class OwnerIdOrder extends AIOrderBase{
    public OwnerIdOrder(Mob entity,VariablesContext context, List<Object> args) {
        super(entity, context, args);
    }

    @Override
    protected void startUp(Mob entity, VariablesContext context, List<Object> args) {

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
            notifyAI("no owner");
        }
    }
}
