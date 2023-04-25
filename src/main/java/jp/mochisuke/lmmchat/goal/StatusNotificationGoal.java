package jp.mochisuke.lmmchat.goal;

import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class StatusNotificationGoal<T extends TamableAnimal> extends Goal {
    T entity;
    public StatusNotificationGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return entity.isTame();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        //per 3600 ticks
        if(entity.tickCount%3600==1) {
            if (entity.isTame()) {
                float hp = entity.getHealth();
                float maxHp = entity.getMaxHealth();

                double x, y, z;
                x = entity.getX();
                y = entity.getY();
                z = entity.getZ();

                //biome
                var biome = entity.level.getBiome(entity.blockPosition());
                ForgeRegistry<Biome> biomeRegistry = (ForgeRegistry<Biome>) ForgeRegistries.BIOMES;
                String biomeName;
                if(biomeRegistry.getResourceKey(biome.get()).isEmpty()){
                    biomeName = "-";
                }else{
                    biomeName = biomeRegistry.getResourceKey(biome.get()).get().toString();
                }
                //nearby enemy
                int enemies = entity.level.getEntitiesOfClass(Monster.class, entity.getBoundingBox().inflate(20)).size();
                //owner hp/maxhp

                Container inventory= Helper.getInventoryContainer(entity);

                int sugarcount=0;
                for(int i=0;i<inventory.getContainerSize();i++){
                    if(inventory.getItem(i).getDescriptionId().contains("sugar")){
                        sugarcount+=inventory.getItem(i).getCount();
                    }
                }

                String statusEffect="";
                for(var effect:entity.getActiveEffects()){
                    statusEffect+=effect.getEffect().getDescriptionId()+":"+effect.getAmplifier()+",";
                }
                if(statusEffect.length()>0){
                    statusEffect="effects:"+statusEffect.substring(0,statusEffect.length()-1);

                }
                Player owner=Helper.getOwner(entity);
                String ownerStatus="";
                if(owner!=null) {
                    float ownerHp = owner.getHealth();
                    float ownerMaxHp = owner.getMaxHealth();
                    String distance;
                    //same level?
                    if(owner.level==entity.level){
                        distance = String.format("distance %.0f",entity.distanceTo(owner));
                    }else{
                        distance="owner is in different dimension";
                    }

                    ownerStatus = String.format("owner hp:%.0f/%.0f,%s", ownerHp, ownerMaxHp, distance);


                }else{
                    ownerStatus="owner:unknown";
                }

                String message = "Status Notification: \n";
                message += String.format("Now is %s. your hp:%.0f/%.0f,location %.1f,%.1f,%.1f,%s," +
                                "nearby enemies:%d,sugar salary:%d "+statusEffect,
                        Helper.getDateAsTimeFormat(),hp, maxHp, x, y, z, ownerStatus, enemies,sugarcount);
                LMMChat.addChatMessage(null, entity, true, false, message, 0);


                LMMChat.addChatMessage(null, entity, true, false, message, 0);

            }
        }
    }
}
