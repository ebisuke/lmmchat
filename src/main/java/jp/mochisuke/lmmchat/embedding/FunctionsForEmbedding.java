package jp.mochisuke.lmmchat.embedding;

import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class FunctionsForEmbedding {
    public static String nearbyEntities(TamableAnimal maid ,Object[] tag){
        //get nearby entities
        Level world = maid.level();
        Class<LivingEntity> cl= (Class<LivingEntity>) tag[1];
        var entities = world.getEntitiesOfClass(cl, maid.getBoundingBox().inflate(30));
        StringBuilder sb = new StringBuilder();
        int limit=10;
        //return the list of entities and poses
        sb.append("nearby "+tag[0].toString()+" entities:"+entities.size()+"\n");
        for(var entity: entities){
            sb.append(entity.getDisplayName().getString());
            sb.append(":");
            sb.append(entity.getX());
            sb.append(",");
            sb.append(entity.getY());
            sb.append(",");
            sb.append(entity.getZ());
            sb.append("\n");
            limit--;
            if(limit<=0){
                return sb.toString();
            }
        }
        return sb.toString();
    }

    public static String nearbyBlocks(TamableAnimal maid ,Object[] tag){
        //get nearby chest blocks
        Level world = maid.level();
        var blockpos= maid.blockPosition();
        int size=20;
        int limit=10;
        Class<Block> b= (Class<Block>)tag[0];
        StringBuilder sb = new StringBuilder();
        for(int x= blockpos.getX()-size/2;x< blockpos.getX()+size+2;x++){
            for(int y= blockpos.getY()-size/2;y< blockpos.getY()+size+2;y++){
                for(int z= blockpos.getZ()-size/2;z< blockpos.getZ()+size+2;z++){
                    var block = world.getBlockState(new BlockPos(x,y,z));
                    if(block.getBlock().getClass()==b){
                        sb.append(String.format("%s at (%d,%d,%d)\n",block.getBlock().getName().getString(),x,y,z));
                        limit--;
                        if(limit<=0){
                            return sb.toString();
                        }
                    }
                }
            }
        }
        return sb.toString();
    }

    public static String inspectInventory(LivingEntity maid,Object[] args){
        StringBuilder sb = new StringBuilder();
        sb.append("inventory:\n");
        var inventory= Helper.getInventoryContainer(maid);
        for(int i=0;i<inventory.getContainerSize();i++){
            var stack=inventory.getItem(i);
            if(stack.isEmpty()){
                continue;
            }
            sb.append(stack.getDisplayName().getString());
            sb.append(" x");
            sb.append(stack.getCount());
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String getTime(LivingEntity maid,Object[] args){
        return String.format("now minecraft time:"+Helper.getDateAsTimeFormat()+"\n")+
                String.format("now real time:"+ LocalDateTime.now().format(
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME));

    }

}
