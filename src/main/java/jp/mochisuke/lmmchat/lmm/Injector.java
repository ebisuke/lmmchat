package jp.mochisuke.lmmchat.lmm;

import jp.mochisuke.lmmchat.LMMChat;
import net.minecraftforge.fml.common.Mod;
import net.sistr.littlemaidrebirth.entity.LittleMaidEntity;

@Mod.EventBusSubscriber(modid = LMMChat.MODID)
public class Injector {
    public static void onSpawn(net.minecraftforge.event.entity.EntityJoinLevelEvent event){
        if(event.getEntity().getClass().toString().contains("LittleMaidEntity")){
            //inject goals
            var entity=(LittleMaidEntity)event.getEntity();
            int priority=5;



            entity.goalSelector.addGoal(priority,new jp.mochisuke.lmmchat.goal.AIOperationGoal<LittleMaidEntity>(entity));
            entity.goalSelector.addGoal(priority,new jp.mochisuke.lmmchat.goal.BlockItemPickupGoal<LittleMaidEntity>(entity));
            entity.goalSelector.addGoal(priority,new jp.mochisuke.lmmchat.goal.BlockItemPutGoal<LittleMaidEntity>(entity));
            entity.goalSelector.addGoal(priority,new jp.mochisuke.lmmchat.goal.GiveItemGoal<LittleMaidEntity>(entity));

            entity.goalSelector.addGoal(priority,new jp.mochisuke.lmmchat.goal.TakeItemGoal<LittleMaidEntity>(entity));
            entity.goalSelector.addGoal(priority,new jp.mochisuke.lmmchat.goal.RandomTalkGoal<LittleMaidEntity>(entity));
            entity.goalSelector.addGoal(1,new jp.mochisuke.lmmchat.goal.StatusNotificationGoal<LittleMaidEntity>(entity));
            entity.goalSelector.addGoal(priority,new jp.mochisuke.lmmchat.goal.BlockPlaceGoal<LittleMaidEntity>(entity));

        }
    }
}
