package jp.mochisuke.lmmchat.goal;

import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.sistr.littlemaidrebirth.entity.util.HasInventory;

public class BlockItemPutGoal <T extends PathfinderMob & HasInventory> extends CallbackedGoal {
    protected final T entity;

    private BaseContainerBlockEntity blockEntity;

    private ItemStack putItemStack;


    //target slot index range
    private int minSlotIndex=-1;
    private int maxSlotIndex=-1;


    public BlockItemPutGoal(T entity) {
        this.entity = entity;
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
    public void start() {
        // walk to block
        this.entity.moveTo(this.blockEntity.getBlockPos().getX(),this.blockEntity.getBlockPos().getY(),this.blockEntity.getBlockPos().getZ());

    }
    public void activate(int x, int y, int z, ItemStack putItemStack, int minslotindex, int maxslotindex) {
        this.blockEntity = (BaseContainerBlockEntity) this.entity.level.getBlockEntity(this.entity.blockPosition().offset(x,y,z));


        this.putItemStack = putItemStack;
        this.minSlotIndex = minslotindex;
        this.maxSlotIndex = maxslotindex;
        this.active = false;
    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
        fail("interrupted");
    }
    @Override
    public boolean requiresUpdateEveryTick() {
        return super.requiresUpdateEveryTick();
    }

    @Override
    public void tick() {
        if(putItemStack.isEmpty() || putItemStack.getCount() <= 0) {
            fail("no such item");
            return;
        }
        // check distance to block
        if (this.entity.distanceToSqr(this.blockEntity.getBlockPos().getX(), this.blockEntity.getBlockPos().getY(), this.blockEntity.getBlockPos().getZ()) < 1.0) {
            // attempt to put item

            var mn = minSlotIndex;
            var mx = maxSlotIndex;

            if (mn < 0 || mx < 0) {
                mn = 0;
                mx = blockEntity.getContainerSize();
            }

            for (int i = mn; i < mx; i++) {
                var item = blockEntity.getItem(i);

                //check slot
                if (item.is(putItemStack.getItem())) {
                    //check count
                    if (item.getCount() >= putItemStack.getCount()) {
                        //put item
                        var putItem = putItemStack.copy();
                        putItem.setCount(
                                //store up to minecraft limit
                                Math.min(item.getMaxStackSize() - item.getCount(), putItemStack.getCount())
                        );
                        blockEntity.setItem(i, putItem);
                        putItemStack.shrink(putItemStack.getCount() - putItem.getCount());

                        if (putItemStack.isEmpty() || putItemStack.getCount() <= 0) {
                            success();
                            break;
                        }
                    }
                } else if (item.isEmpty()) {
                    //put item
                    var putItem = putItemStack.copy();
                    putItem.setCount(
                            //store up to minecraft limit
                            Math.min(putItemStack.getMaxStackSize() - putItemStack.getCount(), putItemStack.getCount())
                    );
                    blockEntity.setItem(i, putItem);
                    putItemStack.shrink(putItemStack.getCount() - putItem.getCount());

                    if (putItemStack.isEmpty() || putItemStack.getCount() <= 0) {
                        success();
                        break;
                    }

                }

            }
        }
        int itemcount=0;
        for(int i=minSlotIndex;i<maxSlotIndex;i++){
            if(entity.getInventory().getItem(i).is(putItemStack.getItem())){
                itemcount+=entity.getInventory().getItem(i).getCount();

            }
        }
        if ( itemcount<putItemStack.getCount()) {
            fail("no such item");
        }
    }
}
