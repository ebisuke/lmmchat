package jp.mochisuke.lmmchat.order;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.Block;

import java.util.List;

public class FindBlockOrder extends AIOrderBase{

    String blockname;

    public FindBlockOrder(Mob entity,VariablesContext context, List<Object> args) {

        super(entity,context, args);



    }

    @Override
    protected void startUp(Mob entity, VariablesContext context, List<Object> args) {
        blockname= (String) args.get(0);


    }

    @Override
    public void executeImpl() {
        //find nearby block
        var pos=entity.blockPosition();
        Block foundblock=null;
        int size=20;
        for(int x=pos.getX()-size/2;x<pos.getX()+size/2;x++){
            for(int y=pos.getY()-size/2;y<pos.getY()+size/2;y++){
                for(int z=pos.getZ()-size/2;z<pos.getZ()+size/2;z++){
                    var block=entity.getLevel().getBlockState(new BlockPos(x,y,z));
                    if(block.getBlock().getName().getString().contains(blockname)){
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
        //store
        val("x",pos.getX());
        val("y",pos.getY());
        val("z",pos.getZ());

        // reply
        this.notifyAI("Target block position location is "+pos.getX()+","+pos.getY()+","+pos.getZ());
    }

}
