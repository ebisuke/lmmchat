package jp.mochisuke.lmmchat.goal;

import jp.mochisuke.lmmchat.lmm.LMMEntityWrapper;
import jp.mochisuke.lmmchat.lmm.PseudoPlayer;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class ComputerCraftModeGoal<T extends TamableAnimal> extends AIGoalBase{
    protected final T entity;
    protected final LMMEntityWrapper wrapper;
    BlockPos blockPos;
    Player player;
    public ComputerCraftModeGoal(T entity) {
        this.entity = entity;
        wrapper = LMMEntityWrapper.of((TamableAnimal) entity);

    }

    @Override
    public boolean canUse() {
        return wrapper.getChatMode()==jp.mochisuke.lmmchat.lmm.LMMChatMode.COMPUTERCRAFT;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public boolean isInterruptable() {
        return super.isInterruptable();
    }

    @Override
    public void start() {
        super.start();
        blockPos=new BlockPos(
                wrapper.getNBTMetaInt("lmmchat:computerx"),
                wrapper.getNBTMetaInt("lmmchat:computery"),
                wrapper.getNBTMetaInt("lmmchat:computerz")
        );
        BlockState block=entity.level.getBlockState(blockPos);
        player=new PseudoPlayer(entity.level,blockPos);
        //interact computer
        var result=entity.level.getBlockState(blockPos).getBlock().use(block,entity.level,blockPos,player, InteractionHand.MAIN_HAND,null);
        if(result.shouldSwing()){
            entity.swing(InteractionHand.MAIN_HAND);
        }

    }

    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.MOVE,Flag.LOOK);
    }

    @Override
    public void stop() {
        super.stop();
        wrapper.setChatMode(jp.mochisuke.lmmchat.lmm.LMMChatMode.NORMAL);
        entity.setShiftKeyDown(false);
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    public void processMessage(String message){
        //TODO
    }

    @Override
    public void tick() {
        //get block
        BlockState block=entity.level.getBlockState(blockPos);

        //if entity is not near computer ,stop
        if(entity.distanceToSqr(blockPos.getX(),blockPos.getY(),blockPos.getZ())>4){
            stop();
            fail("computer is too far");
        }
        //look at computer
        entity.lookAt(EntityAnchorArgument.Anchor.EYES,new Vec3(blockPos.getX(),blockPos.getY(),blockPos.getZ()));
        //crouch
        entity.setShiftKeyDown(true);
        //if block not a computer or turtle ,stop
        if(!block.getBlock().getClass().getName().contains("BlockPeripheral")){

        }else{
            //cancel
            stop();
            fail("computer is removed");
        }
    }

}
