package jp.mochisuke.lmmchat.goal;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.sistr.littlemaidrebirth.entity.util.HasInventory;
import org.slf4j.Logger;

import java.util.EnumSet;

public class BlockItemPutGoal <T extends PathfinderMob & HasInventory> extends AIGoalBase {

    static final Logger logger= LogUtils.getLogger();
    protected final T entity;

    private BaseContainerBlockEntity blockEntity;

    private ItemStack putItemStack;


    //target slot index range
    private int minSlotIndex=-1;
    private int maxSlotIndex=-1;

    private int pathFindingRetry=0;


    public BlockItemPutGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.TARGET,Flag.MOVE);
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
        logger.info("blockitemputgoal start");
        pathFindingRetry=0;
    }
    public void setup(int x, int y, int z, ItemStack putItemStack, int minslotindex, int maxslotindex) {
        this.blockEntity = (BaseContainerBlockEntity) this.entity.level().getBlockEntity(new BlockPos(x,y,z));
        if(this.blockEntity==null) {
            fail("no such block");
            return;
        }
        // check block is not air
        if(this.blockEntity.getBlockState().isAir()) {
            fail("block is air");
            return;
        }
        this.putItemStack = putItemStack;
        if(this.putItemStack==null){
            //pick something
            for(int i=0;i<entity.getInventory().getContainerSize();i++){
                var item = entity.getInventory().getItem(i);
                if(item.getCount()>0){
                    this.putItemStack = item;
                    break;
                }
            }
        }
        if(this.putItemStack==null){
            fail("no item");
            return;
        }
        this.minSlotIndex = minslotindex;
        this.maxSlotIndex = maxslotindex;
        super.activate();

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
    public boolean canUse() {
        return this.active && this.blockEntity!=null;
    }
    @Override
    public void tick() {
        if(!canUse()) {
            return;
        }
        if(putItemStack.isEmpty() || putItemStack.getCount() <= 0) {
            fail("no such item");
            return;
        }
        //walk per 60 ticks
        if (this.entity.tickCount % 60 == 1) {
            // can  reach to block?
            if(!this.entity.getNavigation().moveTo(this.blockEntity.getBlockPos().getX(),
                    this.blockEntity.getBlockPos().getY(), this.blockEntity.getBlockPos().getZ(),1)){
                //can not reach
                pathFindingRetry++;
                if(pathFindingRetry>10) {
                    fail("can not reach");
                    return;
                }
            }else{
                pathFindingRetry=0;

            }
        }

        // check distance to block
        if (this.entity.distanceToSqr(this.blockEntity.getBlockPos().getX(), this.blockEntity.getBlockPos().getY(), this.blockEntity.getBlockPos().getZ()) <4.0) {
            // attempt to put item

            var mn = minSlotIndex;
            var mx = maxSlotIndex;

            if (mn < 0 || mx < 0) {
                mn = 0;
                mx = blockEntity.getContainerSize()-1;
            }
            //fit
            mn=Math.max(mn,0);
            mx=Math.min(mx,blockEntity.getContainerSize()-1);

            for (int i = mn; i <= mx; i++) {
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
                            return;
                        }
                    }
                } else if (item.isEmpty()) {
                    //put item
                    var putItem = putItemStack.copy();
                    putItem.setCount(
                            //store up to minecraft limit
                            putItemStack.getCount()
                    );
                    blockEntity.setItem(i, putItem);
                    putItemStack.shrink(putItemStack.getCount() );

                    if (putItemStack.isEmpty() || putItemStack.getCount() <= 0) {
                        success();
                        return;
                    }

                }

            }
        }
        if(putItemStack.isEmpty() || putItemStack.getCount() <= 0) {
            fail("no such item");
        }
    }
}
