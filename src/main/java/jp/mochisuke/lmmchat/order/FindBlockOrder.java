package jp.mochisuke.lmmchat.order;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.List;

public class FindBlockOrder extends AIOrderBase{

    String blockname;

    public FindBlockOrder(LivingEntity entity, VariablesContext context, List<Object> args) {

        super(entity,context, args);



    }

    @Override
    protected void startUp(LivingEntity entity, VariablesContext context, List<Object> args) {
        blockname= (String) args.get(0);
        assert blockname!=null && blockname.length()>0;

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
        int size=40;
        ArrayList<Tuple<Vec3i,BlockState>> blocks=new ArrayList<>();
        for(int x=pos.getX()-size/2;x<pos.getX()+size/2;x++){
            for(int y=pos.getY()-size/2;y<pos.getY()+size/2;y++){
                for(int z=pos.getZ()-size/2;z<pos.getZ()+size/2;z++){
                    var block=entity.level().getBlockState(new BlockPos(x,y,z));
                    if(block.getBlock().getName().getString().toLowerCase().contains(blockname)){
                        //found
                        xx=x;
                        yy=y;
                        zz=z;
                        blocks.add(new Tuple<>(new Vec3i(x,y,z),block));
                    }
                }
            }
        }
        if(blocks.size()==0){
            //no block found
            throw new RuntimeException("No block found");
        }
        Vec3i entitypos=entity.blockPosition();
        //sort by distance
        blocks.sort((at,bt)->{

            var a=at.getA();
            var b=bt.getA();
            int adist=(a.getX()-entitypos.getX())*(a.getX()-entitypos.getX())+(a.getY()-entitypos.getY())*(a.getY()-entitypos.getY())+(a.getZ()-entitypos.getZ())*(a.getZ()-entitypos.getZ());
            int bdist=(b.getX()-entitypos.getX())*(b.getX()-entitypos.getX())+(b.getY()-entitypos.getY())*(b.getY()-entitypos.getY())+(b.getZ()-entitypos.getZ())*(b.getZ()-entitypos.getZ());
            return adist-bdist;

        });


        String message="";
        message+="Found "+blocks.size()+" blocks:";

        if(blocks.size()>10){
            blocks= new ArrayList<>(blocks.subList(0,10));
            message+="(showing 10)";
        }
        int idx=0;



        for(var block:blocks){
            xx= block.getA().getX();
            yy= block.getA().getY();
            zz= block.getA().getZ();
            message+= idx+":"+ block.getB().getBlock().getName().getString()+":"+String.format("%d,%d,%d",xx,yy,zz)+"\n";
            idx++;
        }
        xx= blocks.get(0).getA().getX();
        yy= blocks.get(0).getA().getY();
        zz= blocks.get(0).getA().getZ();
        //blockentity

        //store variables for nearest block
        val("x",xx);
        val("y",yy);
        val("z",zz);

        // reply
        this.notifyAI(message);
    }

}
