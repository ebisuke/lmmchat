package jp.mochisuke.lmmchat.goal;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

import java.util.EnumSet;

public class InteractGoal<T extends TamableAnimal> extends AIGoalBase {
    protected final T entity;
    protected double x;
    protected double y;
    protected double z;
    public InteractGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public void stop() {
        super.stop();
        entity.getNavigation().stop();
    }
    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.MOVE);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canUse();
    }

    @Override
    public boolean canUse() {
        return active ;
    }

    public void setup(double x, double y, double z) {

        this.x=x;
        this.y=y;
        this.z=z;
        super.activate();
    }


    @Override
    public void tick() {
        if(!canUse()) {
            return;
        }
        //move per 100 ticks
        if(entity.tickCount%60==1){
            entity.getNavigation().moveTo(x,y,z,1.0);
        }

        //if arrived
        if(entity.distanceToSqr(x,y,z)<4.0){
            //interact
            BlockState block=entity.getLevel().getBlockState(entity.blockPosition());
            if(block.isAir()){
                entity.getNavigation().stop();
                fail("No block");
            }
            //use block
            Block b=block.getBlock();
            Level level=entity.getLevel();

            //interact using r-click

            BlockHitResult result=new BlockHitResult(entity.position(),entity.getDirection(),entity.blockPosition(),false);
            InteractionResult r= b.use(block,level,entity.blockPosition(),(Player)entity.getOwner(),
                    net.minecraft.world.InteractionHand.MAIN_HAND,result);

            if(r.shouldSwing()){
                entity.swing(entity.getUsedItemHand());
            }

            if(r.consumesAction()){
                success();
            }


        }
    }
}
