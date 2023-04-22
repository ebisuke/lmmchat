package jp.mochisuke.lmmchat.order;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.List;

public class FindEntityOrder extends AIOrderBase{

    String entityname;

    public FindEntityOrder(Mob entity,VariablesContext context, List<Object> args) {

        super(entity,context, args);




    }

    @Override
    protected void startUp(Mob entity, VariablesContext context, List<Object> args) {
        entityname= (String) args.get(0);

    }

    @Override
    public void executeImpl() {
        //find nearby entity
        var nearentities=entity.getLevel().getNearbyEntities(LivingEntity.class, TargetingConditions.forNonCombat(), entity,
                entity.getBoundingBox().inflate(30));

        if(nearentities.size()==0){
            //no entity found
            this.notifyAI("No entity found");
            return;
        }
        //pick first
        var target=nearentities.stream().filter(e->e.getName().getString().contains(entityname)).findFirst().orElse(null);

        if(target==null){
            //no entity found
            this.notifyAI("No entity found");
            return;
        }
        //get target id
        var targetid=target.getId();

        // reply
        this.notifyAI("Target id is "+targetid);

        //store
        val("id",targetid);
        //pos
        val("x",target.getBlockX());
        val("y",target.getBlockY());
        val("z",target.getBlockZ());



    }

}
