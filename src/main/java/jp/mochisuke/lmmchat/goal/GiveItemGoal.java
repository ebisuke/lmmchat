package jp.mochisuke.lmmchat.goal;

import net.minecraft.world.Container;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.sistr.littlemaidrebirth.entity.util.HasInventory;

import java.lang.reflect.Field;
import java.util.EnumSet;

public class GiveItemGoal <T extends PathfinderMob & HasInventory>  extends AIGoalBase {

    protected final T entity;

    private LivingEntity targetEntity;

    private ItemStack giveItemStack;

    private int itemCount;
    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.TARGET,Flag.MOVE);
    }

    public GiveItemGoal(T entity) {
        this.entity = entity;
    }

    public void setup(LivingEntity targetEntity, ItemStack giveItemStack,int itemCount){

        this.targetEntity=targetEntity;
        this.giveItemStack=giveItemStack;
        this.itemCount=itemCount;
        super.activate();
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }

    @Override
    public void start() {
        if(this.targetEntity==null || this.giveItemStack==null || this.itemCount<=0){
        }

    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if(!canUse()) {
            return;
        }
        //follow to target per 60 ticks
        if(this.entity.tickCount%60==1){
            this.entity.getNavigation().moveTo(this.targetEntity.getX(),this.targetEntity.getY(),this.targetEntity.getZ(),1);
        }
        // is target near?
        if(this.entity.distanceTo(this.targetEntity)<4){
            //give item

            //get inventory using java reflection
            Container container=null;

            //get container typed fields
            Field[] fields = targetEntity.getClass().getDeclaredFields();

            //find container field
            for(Field field:fields){
                if(field.getType().equals(Container.class)){
                    field.setAccessible(true);
                    try {
                        container=(Container)field.get(targetEntity);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            //found?
            if(container==null){
                fail("target entity has no inventory");
                return;
            }


            int remain=itemCount;
            if(this.giveItemStack==null){
                //pick something
                for(int slot=0;slot<container.getContainerSize();slot++ ){
                    ItemStack itemStack=container.getItem(slot);
                    if(!itemStack.isEmpty()){
                        //found item
                        this.giveItemStack=itemStack;
                        break;
                    }
                }
                if(this.giveItemStack==null){
                    //not found item
                    fail("not found item");
                    return;
                }
            }
            if(remain==-1){
                //give all
                remain=this.giveItemStack.getCount();
            }
            //find empty slot or stack item
            for(int slot=0;slot<container.getContainerSize();slot++ ){
                ItemStack itemStack=container.getItem(slot);
                if(itemStack.isEmpty()){
                    //empty slot
                    container.setItem(slot,giveItemStack);
                    success();
                    return;
                }else if(ItemStack.isSameItem(itemStack,giveItemStack)){
                    //stack item
                    int stackSize=itemStack.getCount()+remain;
                    if(stackSize<=itemStack.getMaxStackSize()){
                        //stackable
                        itemStack.setCount(stackSize);
                        container.setItem(slot,itemStack);
                        success();
                        return;
                    }else{
                        //not stackable
                        remain-=itemStack.getMaxStackSize();
                        itemStack.setCount(itemStack.getMaxStackSize());
                        container.setItem(slot,itemStack);
                        giveItemStack.setCount(remain);
                    }
                }
            }
            // not found empty slot or stack item
            fail("not found empty slot or stack item");
        }
    }
}
