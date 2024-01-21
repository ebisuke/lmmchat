package jp.mochisuke.lmmchat.goal;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.LMMChatConfig;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import org.slf4j.Logger;

import java.util.Random;

public class RandomTalkGoal<T extends TamableAnimal> extends Goal {
    static final Logger logger= LogUtils.getLogger();
    T entity;
    long cooldown=0;
    public RandomTalkGoal(T entity) {
        this.entity = entity;
    }
    @Override
    public boolean canUse() {
        return entity.isTame();
    }

    @Override
    public void tick() {
        if(cooldown>0){
            cooldown--;
            return;
        }
        if(entity.isTame()){
            Random random=new Random();
            if(LMMChatConfig.getRandomTalkChance()>random.nextDouble()){
                //talk trigger

                //find nearest player
                var player=entity.getCommandSenderWorld().getNearestPlayer(entity, 10);
                if(player!=null){
                    logger.info("RandomTalkGoal:talk to player");
                    LMMChat.addChatMessage(entity,player,false,false, LMMChatConfig.getRandomTalkPrompt(),0);

                }
                else {
                    //alternatively find nearest entity
                    var entity2=entity.getCommandSenderWorld().getNearestEntity(LivingEntity.class,
                            TargetingConditions.forNonCombat(),entity,entity.getX(),entity.getY(),entity.getZ(),entity.getBoundingBox().inflate(10));
                    if(entity2!=null) {
                        logger.info("RandomTalkGoal:talk to entity");
                        LMMChat.addChatMessage(entity, entity2, false, false, LMMChatConfig.getRandomTalkPrompt(), 0);
                    }else{
                        logger.info("RandomTalkGoal:talk to nobody");
                        return;
                    }
                }




                cooldown= LMMChatConfig.getRandomTalkCooldown();
            }
        }
    }
}
