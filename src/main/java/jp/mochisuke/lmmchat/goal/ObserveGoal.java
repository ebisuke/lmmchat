package jp.mochisuke.lmmchat.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

import java.util.EnumSet;

public class ObserveGoal<T extends PathfinderMob> extends AIGoalBase{
    protected final T entity;
    private int x;
    private int y;
    private int z;
    private BlockState watchingBlockState;
    public ObserveGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return active;
    }

    @Override
    public boolean canContinueToUse() {
        return super.canContinueToUse();
    }

    public void setUp(int x,int y,int z){
        this.x=x;
        this.y=y;
        this.z=z;
        super.activate();
    }
    @Override
    public boolean isInterruptable() {
        return true;
    }

    @Override
    public void start() {
        super.start();
        entity.getNavigation().moveTo(x,y,z,1.0);
    }

    @Override
    public void stop() {
        if(active){
            fail("interrupted");
        }
        entity.setShiftKeyDown(false);
        active= false;
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }


    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.MOVE,Flag.LOOK,Flag.JUMP,Flag.TARGET);
    }


    @Override
    public void tick() {
        if(!canUse()){
            return;
        }
        if(entity.tickCount%20==0){
            entity.getNavigation().moveTo(x,y,z,1.0);

            BlockState block=entity.level().getBlockState(new BlockPos(x,y,z));

            //check maid can see block and near block
            if(block.isAir() || !block.isPathfindable(entity.level(), new BlockPos(x,y,z), PathComputationType.LAND)){
                fail("block is air or not pathfindable");
                return;
            }

            if(entity.distanceToSqr(x,y,z)>4 && entity.getNavigation().isDone()) {
                entity.getNavigation().moveTo(x, y, z, 1.0);
            }
            if(entity.distanceToSqr(x,y,z)<=4){
                entity.getNavigation().stop();
                // crouching
                entity.setShiftKeyDown(true);
                //look at block
                entity.getLookControl().setLookAt(x,y,z);
                if(watchingBlockState==null){
                    watchingBlockState=block;
                }else {
                    //wait for change blockstate
                    BlockState nowBlockState=entity.level().getBlockState(new BlockPos(x,y,z));
                    if (!nowBlockState.equals(watchingBlockState)) {
                        success();
                    }
                }


            }else{
                entity.setShiftKeyDown(false);
                watchingBlockState=null;
            }
        }

    }

}
