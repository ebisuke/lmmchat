package jp.mochisuke.lmmchat.order;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class FindBlockOrder extends AIOrderBase{

    String blockname;

    public FindBlockOrder(Mob entity, List<Object> args) {

        super(entity, args);

        blockname= (String) args.get(0);



    }

    @Override
    public void execute() {
        //find nearby block
        var pos=entity.blockPosition();
        Block foundblock=null;
        int size=20;
        for(int x=pos.getX()-size/2;x<pos.getX()+size/2;x++){
            for(int y=pos.getY()-size/2;y<pos.getY()+size/2;y++){
                for(int z=pos.getZ()-size/2;z<pos.getZ()+size/2;z++){
                    var block=entity.getLevel().getBlockState(new BlockPos(x,y,z));
                    if(block.getBlock().getName().getString().equals(blockname)){
                        //found
                        foundblock=block.getBlock();
                    }
                }
            }
        }
        if(foundblock==null){
            //no block found
            this.notifyAI("No block found");
            return;
        }

        // reply
        this.notifyAI("Target block position location is "+pos.getX()+","+pos.getY()+","+pos.getZ());
    }

}
