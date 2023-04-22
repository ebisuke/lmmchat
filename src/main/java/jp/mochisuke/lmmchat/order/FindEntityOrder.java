package jp.mochisuke.lmmchat.order;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.List;

public class FindEntityOrder extends AIOrderBase{

    String entityname;

    public FindEntityOrder(Mob entity, List<Object> args) {

        super(entity, args);

        entityname= (String) args.get(0);



    }

    @Override
    public void execute() {
        //find nearby entity
        var nearentities=entity.getLevel().getNearbyEntities(LivingEntity.class, TargetingConditions.forNonCombat(), entity,
                entity.getBoundingBox().inflate(30));

        if(nearentities.size()==0){
            //no entity found
            this.notifyAI("No entity found");
            return;
        }
        //pick first
        var target=nearentities.stream().filter(e->e.getName().getString().equals(entityname)).findFirst().orElse(null);

        if(target==null){
            //no entity found
            this.notifyAI("No entity found");
            return;
        }
        //get target id
        var targetid=target.getEncodeId();

        // reply
        this.notifyAI("Target id is "+targetid);

    }

}
