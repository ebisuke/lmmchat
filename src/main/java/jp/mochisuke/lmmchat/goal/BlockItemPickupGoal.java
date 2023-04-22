package jp.mochisuke.lmmchat.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.sistr.littlemaidrebirth.entity.util.HasInventory;

public class BlockItemPickupGoal<T extends PathfinderMob & HasInventory> extends CallbackedGoal {


    protected final T entity;
    private BaseContainerBlockEntity blockEntity;

    private ItemStack wantsItemStack;

    //target slot index range
    private int minSlotIndex=-1;
    private int maxSlotIndex=-1;






    public BlockItemPickupGoal(T entity) {
        this.entity = entity;
        assert this.wantsItemStack!=null && this.minSlotIndex<0 && this.maxSlotIndex<0;
    }

    public void activate(int x, int y, int z, ItemStack wantsItemStack, int minslotindex, int maxslotindex) {
        super.activate();
        this.blockEntity = (BaseContainerBlockEntity) this.entity.level.getBlockEntity(this.entity.blockPosition().offset(x,y,z));

        this.wantsItemStack=wantsItemStack;
        this.minSlotIndex=minslotindex;
        this.maxSlotIndex=maxslotindex;
    }

    @Override
    public void start() {
        // walk to block
        this.entity.moveTo(this.blockEntity.getBlockPos().getX(),this.blockEntity.getBlockPos().getY(),this.blockEntity.getBlockPos().getZ());

    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
        fail("interrupted");
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
        // check done flag and inventory is full
        boolean isFull=true;
        for(int i=minSlotIndex;i<maxSlotIndex;i++){
            if(entity.getInventory().getItem(i).isEmpty()){
                isFull=false;
                break;
            }
        }

        if (active || false/*isFull*/ ) {
            return false;
        }
        return true;
    }

    @Override
    public void tick() {
        // check distance to block
        if(this.entity.distanceToSqr(this.blockEntity.getBlockPos().getX(),this.blockEntity.getBlockPos().getY(),this.blockEntity.getBlockPos().getZ())<1.0){
            // attempt to pick up item

            var mn=minSlotIndex;
            var mx=maxSlotIndex;

            if(mn<0 || mx<0){
                mn=0;
                mx=blockEntity.getContainerSize();
            }

            for(int i=mn;i<mx;i++){
                var item=blockEntity.getItem(i);
                if(item.getItem()==this.wantsItemStack.getItem()){
                    // pick up item
                    for(int j=minSlotIndex;j<maxSlotIndex;j++){
                        if(entity.getInventory().getItem(j).isEmpty()){

                            var pickedUpItem=blockEntity.removeItem(i,1);

                            // put item in inventory
                            entity.getInventory().setItem(j,pickedUpItem);
                            success();
                            break;
                        }
                    }
                    //fail
                    fail("your inventory is full.");
                    break;
                }
            }
        }
    }
}
