package jp.mochisuke.lmmchat.goal;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import org.slf4j.Logger;

import java.util.EnumSet;

public class BlockInspectGoal<T extends PathfinderMob > extends AIGoalBase {

    static final Logger logger= LogUtils.getLogger();
    protected final T entity;

    private BaseContainerBlockEntity blockEntity;



    //target slot index range
    private int pathFindingRetry=0;


    public BlockInspectGoal(T entity) {
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
        pathFindingRetry=0;
    }
    public void setup(int x, int y, int z) {
        this.blockEntity = (BaseContainerBlockEntity) this.entity.level.getBlockEntity(new BlockPos(x,y,z));
        if(this.blockEntity==null) {
            fail("no such block");
            return;
        }
        // check block is not air
        if(this.blockEntity.getBlockState().isAir()) {
            fail("block is air");
            return;
        }
        super.activate();

    }

    @Override
    public void stop() {
        this.entity.getNavigation().stop();
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
            // can  reach to block?
            if(!this.entity.getNavigation().moveTo(this.blockEntity.getBlockPos().getX(),
                    this.blockEntity.getBlockPos().getY(), this.blockEntity.getBlockPos().getZ(), 1)){
                // can not reach to block
                if(pathFindingRetry++>10){
                    fail("can not reach to block");
                }
            }else{
                pathFindingRetry=0;
            }
        }

        // check distance to block
        if (this.entity.distanceToSqr(this.blockEntity.getBlockPos().getX(), this.blockEntity.getBlockPos().getY(), this.blockEntity.getBlockPos().getZ()) <4.0) {
            // attempt to put item

            // dump inventory
            String dump="";
            for(int idx=0;idx<blockEntity.getContainerSize();idx++){
                ItemStack itemStack=blockEntity.getItem(idx);
                if(itemStack.isEmpty() ||itemStack.getCount()==0){
                    dump += "Slot:" + idx + " (empty)\n";
                }else {
                    dump += "Slot:" + idx + " " + itemStack.getDescriptionId() + " x" + itemStack.getCount() + "\n";
                }
            }
            logger.info("dump inventory\n"+dump);
            //notify ai
            LMMChat.addChatMessage(null, entity, true, false, dump, 0);
            success();
        }
    }
}
