package jp.mochisuke.lmmchat.goal;

import jp.mochisuke.lmmchat.helper.Helper;
import jp.mochisuke.lmmchat.lmm.LMMEntityWrapper;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

public class TeleportOwnerGoal<T extends TamableAnimal> extends AIGoalBase{
    protected final T entity;
    int cooldown = 0;

    public TeleportOwnerGoal(T entity) {
        this.entity = entity;
    }

    public void setUp(){
        activate();
    }
    @Override
    public boolean canUse() {
        return entity.isTame();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void activate() {
        super.activate();
    }

    @Override
    public void start() {
    }

    @Override
    public void stop() {
    }
    public boolean isCooldown(){
        return cooldown>0;
    }

    @Override
    public void tick() {
        if(cooldown > 0){
            cooldown--;
            return;
        }
        if(!active){
            return;
        }

        //find owner
        Player owner = Helper.getOwner(entity);
        if(owner == null){
            fail("owner not found");
            return;
        }
        //count sugars
        Container container=Helper.getInventoryContainer(entity);
        int sugarCount = Helper.countItem(container,(itemStack)->itemStack.getItem().equals(Items.SUGAR));
        int consumeHp= (int) Math.min(entity.getHealth()-1, Math.max(5,30-sugarCount));

        if(consumeHp>0){
            if(entity.getHealth()<=5){
                fail("not enough health");
                return;
            }
            DamageSource damageSource = DamageSource.MAGIC;
            entity.hurt(damageSource,consumeHp);
        }
        //consume sugars
        sugarCount=Math.min(30,sugarCount);
        Helper.consumeItem(container,Items.SUGAR,sugarCount);
        //teleport
        //same level?
        if(owner.level.dimension().equals(entity.level.dimension())) {
            entity.teleportTo(owner.getX(), owner.getY(), owner.getZ());
        }else{
            entity.changeDimension(owner.getServer().getLevel(owner.level.dimension()));
            entity.teleportTo(owner.getX(), owner.getY(), owner.getZ());
        }
        LMMEntityWrapper wrapper = LMMEntityWrapper.of(entity);
        wrapper.setWait(false);
        //enderman teleport sound
        entity.playSound(ForgeRegistries.SOUND_EVENTS.getValue(new net.minecraft.resources.ResourceLocation("entity.enderman.teleport")),1,1);
        cooldown=6000;
        success();
    }

}
