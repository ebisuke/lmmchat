package jp.mochisuke.lmmchat.goal;

import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.LMMChatConfig;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistry;

public class ConcentrateGoal<T extends TamableAnimal> extends AIGoalBase{
    protected T entity;

    private long concentrateTime = 0;

    private long intervalTime=0;

    public void resetConcentrateTime() {
        concentrateTime = LMMChatConfig.getConcentrateTime();
        intervalTime = 0;

    }
    public ConcentrateGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canUse();
    }

    @Override
    public void tick(){
        super.tick();
        if(concentrateTime>0) {
            concentrateTime--;
            intervalTime++;
            if (intervalTime >= LMMChatConfig.getConcentratePromptInterval()) {
                intervalTime = 0;

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
                if(entity.getOwner()==null) return;
                float ownerHp = entity.getOwner().getHealth();
                float ownerMaxHp = entity.getOwner().getMaxHealth();

                float distance = entity.distanceTo(entity.getOwner());

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
                String message = "Status Notification: \n";
                message += String.format("Now is %s. your hp:%.0f/%.0f,owner hp:%.0f/%.0f,distance %.0f,location %.1f,%.1f,%.1f," +
                                "nearby enemies:%d,sugar salary:%d "+statusEffect,
                        Helper.getDateAsTimeFormat(),hp, maxHp, ownerHp, ownerMaxHp, distance, x, y, z,  enemies,sugarcount);

                LMMChat.addChatMessage(null, entity, true, false, message, 0);


            }
        }else{
            intervalTime=0;
        }
    }


}
