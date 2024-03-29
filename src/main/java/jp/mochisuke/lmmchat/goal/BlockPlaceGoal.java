package jp.mochisuke.lmmchat.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.EnumSet;

public class BlockPlaceGoal<T extends Mob> extends AIGoalBase {
    protected final T entity;
    protected int x;
    protected int y;
    protected int z;
    protected ItemStack blockItem;
    int pathFindingRetry=0;
    public BlockPlaceGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public void stop() {
        super.stop();
        entity.getNavigation().stop();
    }

    public void setup(int  x,int y,int z,ItemStack block) {

        this.x=x;
        this.y=y;
        this.z=z;
        this.blockItem=block;
        pathFindingRetry=0;
        super.activate();
    }
    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.TARGET,Flag.MOVE);
    }

    @Override
    public void tick() {
        if(!canUse()) {
            return;
        }
        //move per 100 ticks
        if(entity.tickCount%60==1){
            if(!entity.getNavigation().moveTo(x, y, z, 1)){
                pathFindingRetry++;
                if(pathFindingRetry>10){
                    fail("pathfinding failed");
                }
            }else{
                pathFindingRetry=0;
            }
        }

        //if arrived
        if(entity.distanceToSqr(x,y,z)<5.0){
            //place block
            BlockItem blockItem = (BlockItem) this.blockItem.getItem();
            entity.level().setBlockAndUpdate(entity.blockPosition().offset(x,y,z),blockItem.getBlock().defaultBlockState());
            //play sound
            entity.level().playSound(null,entity.blockPosition().offset(x,y,z),
                    blockItem.getBlock().getSoundType(

                            blockItem.getBlock().defaultBlockState(),entity.level(),new BlockPos( x,y,z ),entity
                    ).getPlaceSound(),entity.getSoundSource(),1.0f,1.0f);
            //swing hand
            entity.swing(InteractionHand.MAIN_HAND);
            //consume item
            this.blockItem.shrink(1);
            //finish
            success();
        }
    }
}
