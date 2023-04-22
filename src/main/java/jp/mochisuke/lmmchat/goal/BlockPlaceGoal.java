package jp.mochisuke.lmmchat.goal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

public class BlockPlaceGoal<T extends Mob> extends CallbackedGoal{
    protected final T entity;
    protected double x;
    protected double y;
    protected double z;
    protected ItemStack blockItem;
    public BlockPlaceGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public void stop() {
        super.stop();
        entity.getNavigation().stop();
        fail("interrupted");
    }

    public void activate(double x,double y,double z,ItemStack block) {
        super.activate();
        this.x=x;
        this.y=y;
        this.z=z;
        this.blockItem=block;
    }


    @Override
    public void tick() {
        //move per 100 ticks
        if(entity.tickCount%100==0){
            entity.getNavigation().moveTo(x,y,z,1.0);
        }

        //if arrived
        if(entity.distanceToSqr(x,y,z)<1.0){
            //place block
            BlockItem blockItem = (BlockItem) this.blockItem.getItem();
            entity.level.setBlockAndUpdate(entity.blockPosition().offset(x,y,z),blockItem.getBlock().defaultBlockState());
            //play sound
            entity.level.playSound(null,entity.blockPosition().offset(x,y,z),
                    blockItem.getBlock().getSoundType(

                            blockItem.getBlock().defaultBlockState(),entity.level,new BlockPos( x,y,z ),entity
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
