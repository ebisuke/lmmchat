package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;

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
        Player owner= Helper.getOwner(animal);
        if(owner!=null) {
            notifyAI("owner id:" + owner.getId()+" location:"+
                    owner.blockPosition().getX()+","+owner.blockPosition().getY()+","+owner.blockPosition().getZ());
            //store
            val("id",owner.getId());
            val("x",owner.blockPosition().getX());
            val("y",owner.blockPosition().getY());
            val("z",owner.blockPosition().getZ());
        }else{
            throw new RuntimeException("No owner");
        }
    }
}
