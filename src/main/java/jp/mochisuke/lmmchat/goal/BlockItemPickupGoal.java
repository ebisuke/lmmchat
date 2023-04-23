package jp.mochisuke.lmmchat.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.sistr.littlemaidrebirth.entity.util.HasInventory;

import java.util.EnumSet;

public class BlockItemPickupGoal<T extends PathfinderMob & HasInventory> extends AIGoalBase {


    protected final T entity;
    private BaseContainerBlockEntity blockEntity;
    private int pathFindingRetry=0;
    private ItemStack wantsItemStack;

    //target slot index range
    private int minSlotIndex=-1;
    private int maxSlotIndex=-1;

    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.TARGET,Flag.MOVE);
    }


    public BlockItemPickupGoal(T entity) {
        this.entity = entity;
        assert this.wantsItemStack!=null && this.minSlotIndex<0 && this.maxSlotIndex<0;
    }

    public void setup(int x, int y, int z, ItemStack wantsItemStack, int minslotindex, int maxslotindex) {

        this.blockEntity = (BaseContainerBlockEntity) this.entity.level.getBlockEntity(new BlockPos(x,y,z));
        //null or air
        if(this.blockEntity==null || this.blockEntity.getBlockState().isAir()) {
            fail("block not found");
            return;
        }
        this.wantsItemStack=wantsItemStack;
        this.minSlotIndex=minslotindex;
        this.maxSlotIndex=maxslotindex;
        pathFindingRetry=0;
        super.activate();
    }

    @Override
    public void start() {
        // walk to block
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
    }

    @Override
    public boolean isInterruptable() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public boolean canUse() {


        return this.active;
    }

    @Override
    public void tick() {
        if(!canUse()) {
            return;
        }
        //walk per 60 ticks
        if (this.entity.tickCount % 60 == 1) {
            if(!this.entity.getNavigation().moveTo(this.blockEntity.getBlockPos().getX(),
                    this.blockEntity.getBlockPos().getY(), this.blockEntity.getBlockPos().getZ(),1)){
                pathFindingRetry++;
                if(pathFindingRetry>10){
                    fail("pathfinding failed");
                }
            }else{
                pathFindingRetry=0;
            }
        }

        // check distance to block
        if(this.entity.distanceToSqr(this.blockEntity.getBlockPos().getX(),this.blockEntity.getBlockPos().getY(),this.blockEntity.getBlockPos().getZ())<4.0){
            // attempt to pick up item

            var mn=minSlotIndex;
            var mx=maxSlotIndex;

            if(mn<0 || mx<0){
                mn=0;
                mx=blockEntity.getContainerSize()-1;
            }
            //fit
            mn=Math.max(0,mn);
            mx=Math.min(blockEntity.getContainerSize()-1,mx);
            boolean picked=false;
            for(int i=mn;i<=mx;i++){
                var item=blockEntity.getItem(i);
                if(this.wantsItemStack==null || item.getItem()==this.wantsItemStack.getItem()){
                    picked=true;

                    // pick up item
                    for(int j=0;j<entity.getInventory().getContainerSize();j++){
                        if(entity.getInventory().getItem(j).isEmpty()){

                            var pickedUpItem=blockEntity.removeItem(i,item.getCount());

                            // put item in inventory
                            entity.getInventory().setItem(j,pickedUpItem);
                            success();
                            return;
                        }
                    }
                    //fail
                    fail("your inventory is full.");
                    break;
                }
            }
            if(!picked){
                fail("item not found");
            }

        }
    }
}
