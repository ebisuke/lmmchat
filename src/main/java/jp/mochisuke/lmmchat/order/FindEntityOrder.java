package jp.mochisuke.lmmchat.order;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import java.util.List;

public class FindEntityOrder extends AIOrderBase{

    String entityname;

    public FindEntityOrder(LivingEntity entity,VariablesContext context, List<Object> args) {

        super(entity,context, args);




    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        if(args.size()>0) {
            entityname = (String) args.get(0);
        }else{
            entityname=null;
        }
    }

    @Override
    protected boolean isImmediate() {
        return true;
    }

    @Override
    public void executeImpl() {
        //find nearby entity
        List<LivingEntity> nearentities;
        if(entityname==null)
            //monster only
            nearentities= entity.getLevel().getNearbyEntities(LivingEntity.class, TargetingConditions.forNonCombat(), entity,
                    entity.getBoundingBox().inflate(40)).stream().filter(e->e instanceof net.minecraft.world.entity.monster.Monster).toList();
        else
            //filter
            nearentities= entity.getLevel().getNearbyEntities(LivingEntity.class, TargetingConditions.forNonCombat(), entity,
                    entity.getBoundingBox().inflate(40)).stream().filter(e->e.getDisplayName().getString().contains(entityname)).toList();

        if(nearentities.size()==0){
            //no entity found
            this.notifyAI("No entity found");
            return;
        }
        //pick first
        var target=nearentities.stream().filter(e->e.getDisplayName().getString().contains(entityname)).toList();

        if(target.isEmpty()){
            //no entity found
            throw new RuntimeException("No entity found");
        }
        var pos=entity.blockPosition();
        //sort by distance
        target.sort((a,b)->{
            var apos=a.blockPosition();
            var bpos=b.blockPosition();
            var adist=apos.distSqr(pos);
            var bdist=bpos.distSqr(pos);
            return (int)(adist-bdist);
        });


        boolean omitted=false;
        //spoil
        if(target.size()>10){
            target=target.subList(0,10);
            omitted=true;
        }
        String message;
        if (entityname==null){
            message="Found "+target.size()+" entities (monster only):\n";
        }else{
            message ="Found "+target.size()+" entities:\n";
        }

        for(var t: target) {
            //get target id
            var targetid = t.getId();
            var pos2=t.blockPosition();
            // reply
            message += "id:" + targetid + " name:" + t.getDisplayName().getString() + " pos:" + pos2.getX() + "," + pos2.getY() + "," + pos2.getZ() +" hp:"+t.getHealth()+"/"+t.getMaxHealth()+"\n";


        }
        if(omitted){
            message+="(omitted)";
        }

        var firstTarget=target.get(0);
        val("id", firstTarget.getId());
        //pos
        val("x", firstTarget.getBlockX());
        val("y", firstTarget.getBlockY());
        val("z", firstTarget.getBlockZ());

        this.notifyAI(message);
    }

}
