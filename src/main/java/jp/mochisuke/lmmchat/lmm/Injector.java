package jp.mochisuke.lmmchat.lmm;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.goal.AIGoalBase;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sistr.littlemaidrebirth.entity.LittleMaidEntity;
import org.slf4j.Logger;

import java.util.ArrayList;

@Mod.EventBusSubscriber(modid = LMMChat.MODID)
public class Injector {
    static final Logger logger = LogUtils.getLogger();
    @SubscribeEvent
    public static void onSpawn (net.minecraftforge.event.entity.EntityJoinLevelEvent event){
        if(event.getEntity().getClass().toString().contains("LittleMaidEntity")){
            logger.info("injecting goals");
            //inject goals
            var entity=(LittleMaidEntity)event.getEntity();
            int priority=5;
            ArrayList<AIGoalBase> goals=new ArrayList<>();
            goals.add(new jp.mochisuke.lmmchat.goal.BlockItemPickupGoal<LittleMaidEntity>(entity));
            goals.add(new jp.mochisuke.lmmchat.goal.BlockItemPutGoal<LittleMaidEntity>(entity));
            goals.add(new jp.mochisuke.lmmchat.goal.GiveItemGoal<LittleMaidEntity>(entity));
            goals.add(new jp.mochisuke.lmmchat.goal.MoveGoal<LittleMaidEntity>(entity));
            goals.add(new jp.mochisuke.lmmchat.goal.TakeItemGoal<LittleMaidEntity>(entity));
            goals.add(new jp.mochisuke.lmmchat.goal.BlockPlaceGoal<LittleMaidEntity>(entity));
            goals.add(new jp.mochisuke.lmmchat.goal.BlockInspectGoal<LittleMaidEntity>(entity));
            goals.add(new jp.mochisuke.lmmchat.goal.InteractGoal<LittleMaidEntity>(entity));
            goals.add(new jp.mochisuke.lmmchat.goal.FortifyGoal<LittleMaidEntity>(entity));
            goals.add(new jp.mochisuke.lmmchat.goal.CraftingGoal<LittleMaidEntity>(entity));

            var operator=new jp.mochisuke.lmmchat.goal.AIOperationGoal<LittleMaidEntity>(entity);
            entity.goalSelector.addGoal(0,operator);

            for(AIGoalBase goal:goals){
                entity.goalSelector.addGoal(priority,goal);
                goal.setCallback(operator);
            }
            entity.goalSelector.addGoal(8,new jp.mochisuke.lmmchat.goal.RandomTalkGoal<LittleMaidEntity>(entity));
            entity.goalSelector.addGoal(1,new jp.mochisuke.lmmchat.goal.StatusNotificationGoal<LittleMaidEntity>(entity));
            //entity.goalSelector.addGoal(3,new jp.mochisuke.lmmchat.goal.HurtNotificationGoal<LittleMaidEntity>(entity));
            entity.goalSelector.addGoal(3,new jp.mochisuke.lmmchat.goal.EventNotificationGoal<LittleMaidEntity>(entity));
            entity.goalSelector.addGoal(4,new jp.mochisuke.lmmchat.goal.HealOwnerGoal<LittleMaidEntity>(entity));
        }
    }
}
