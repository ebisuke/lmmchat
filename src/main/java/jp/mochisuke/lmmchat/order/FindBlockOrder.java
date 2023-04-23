package jp.mochisuke.lmmchat.order;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class FindBlockOrder extends AIOrderBase{

    String blockname;

    public FindBlockOrder(LivingEntity entity, VariablesContext context, List<Object> args) {

        super(entity,context, args);



    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        blockname= (String) args.get(0);


    }

    @Override
    protected boolean isImmediate() {
        return true;
    }

    @Override
    public void executeImpl() {
        //find nearby block
        var pos=entity.blockPosition();
        int xx=0,yy=0,zz=0;
        Block foundblock=null;
        int size=40;
        for(int x=pos.getX()-size/2;x<pos.getX()+size/2;x++){
            for(int y=pos.getY()-size/2;y<pos.getY()+size/2;y++){
                for(int z=pos.getZ()-size/2;z<pos.getZ()+size/2;z++){
                    var block=entity.getLevel().getBlockState(new BlockPos(x,y,z));
                    if(block.getBlock().getDescriptionId().contains(blockname)){
                        //found
                        xx=x;
                        yy=y;
                        zz=z;
                        foundblock=block.getBlock();
                    }
                }
            }
        }
        if(foundblock==null){
            //no block found
            throw new RuntimeException("No block found");
        }
        pos=new BlockPos(xx,yy,zz);
        //check blockentity
        var blockentity=entity.getLevel().getBlockEntity(pos);
        if(blockentity==null){
            //no blockentity
            throw new RuntimeException("No blockentity found:"+entity.getLevel().getBlockState(pos).getBlock().getDescriptionId());
        }
        //store
        val("x",pos.getX());
        val("y",pos.getY());
        val("z",pos.getZ());

        // reply
        this.notifyAI(blockentity.getBlockState().getBlock().getName().getString()+" position is "+pos.getX()+","+pos.getY()+","+pos.getZ());
    }

}
