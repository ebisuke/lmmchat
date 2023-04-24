package jp.mochisuke.lmmchat.lmm;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.goal.EventNotificationGoal;
import jp.mochisuke.lmmchat.helper.Helper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid = LMMChat.MODID)
public class LMMEventReceiver {
    private static final Logger logger = LogUtils.getLogger();
    @SubscribeEvent
    public static void onGetProjectile(net.minecraftforge.event.entity.living.LivingGetProjectileEvent event){
        var entity=event.getEntity();
        if(entity instanceof TamableAnimal && entity.getClass().getName().contains("LittleMaidEntity")){
            //get goal
            EventNotificationGoal goal= (EventNotificationGoal) Helper.getGoal((TamableAnimal) entity, EventNotificationGoal.class);
            if(goal!=null){
                goal.onGetProjectile(event);
            }
        }
    }

    @SubscribeEvent
    public static void onHealEntity(net.minecraftforge.event.entity.living.LivingHealEvent event){
        var entity=event.getEntity();
        if(entity instanceof TamableAnimal && entity.getClass().getName().contains("LittleMaidEntity")){
            //get goal
            EventNotificationGoal goal= (EventNotificationGoal) Helper.getGoal((TamableAnimal) entity, EventNotificationGoal.class);
            if(goal!=null){
                goal.onHealEntity(event);
            }
        }
    }

    @SubscribeEvent
    public static void onHurtEntity(net.minecraftforge.event.entity.living.LivingHurtEvent event){
        var entity=event.getEntity();
        if(entity instanceof TamableAnimal && entity.getClass().getName().contains("LittleMaidEntity")){
            //get goal
            EventNotificationGoal goal= (EventNotificationGoal) Helper.getGoal((TamableAnimal) entity, EventNotificationGoal.class);
            if(goal!=null){
                goal.onHurtEntity(event);
            }
        }
        //check maid owner has hurted
        if(entity instanceof Player){
            //find nearby maids
            var world=entity.level;
            var pos=entity.blockPosition();
            var maids=world.getEntitiesOfClass(TamableAnimal.class, entity.getBoundingBox().inflate(50)  ,e->e.getClass().getName().contains("LittleMaidEntity"));
            for(var maid:maids){
                if(maid.getOwner()==entity) {
                    //get goal
                    EventNotificationGoal goal = (EventNotificationGoal) Helper.getGoal(maid, EventNotificationGoal.class);
                    if (goal != null) {
                        goal.onHurtOwner(event);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public static void onUsedTotem(net.minecraftforge.event.entity.living.LivingUseTotemEvent event){
        var entity=event.getEntity();
        if(entity instanceof TamableAnimal && entity.getClass().getName().contains("LittleMaidEntity")){
            //get goal
            EventNotificationGoal goal= (EventNotificationGoal) Helper.getGoal((TamableAnimal) entity, EventNotificationGoal.class);
            if(goal!=null){
                goal.onUsedTotem(event);
            }
        }
    }

    @SubscribeEvent
    public static void onDeath(net.minecraftforge.event.entity.living.LivingDeathEvent event){
        if(event.getSource()!=null) {
            var entity = event.getSource().getEntity();
            if (entity instanceof TamableAnimal && entity.getClass().getName().contains("LittleMaidEntity")) {
                //get goal
                EventNotificationGoal goal = (EventNotificationGoal) Helper.getGoal((TamableAnimal) entity, EventNotificationGoal.class);
                if (goal != null) {
                    goal.onKilled(event);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onExplodedEntity(net.minecraftforge.event.level.ExplosionEvent.Detonate event){
        var world=event.getLevel();
        var explosion=event.getExplosion();
        var exploder=explosion.getSourceMob();

        //find nearby maid
        var maidList=world.getEntitiesOfClass(TamableAnimal.class,
                explosion.getExploder().getBoundingBox().inflate(10),
                entity1 -> entity1.getClass().getName().contains("LittleMaidEntity"));
        for(var maid:maidList){
            //get goal
            EventNotificationGoal goal= (EventNotificationGoal) Helper.getGoal((TamableAnimal) maid, EventNotificationGoal.class);
            if(goal!=null){
                goal.onExplodedEntity(event);
            }
        }
    }

    @SubscribeEvent
    public static void onFishedItem(net.minecraftforge.event.entity.player.ItemFishedEvent event){
        var player=event.getEntity();
        var world=player.level;
        var pos=player.blockPosition();
        var maidList=world.getEntitiesOfClass(TamableAnimal.class,
                player.getBoundingBox().inflate(10),
                entity1 -> entity1.getClass().getName().contains("LittleMaidEntity"));
        for(var maid:maidList){
            //get goal
            EventNotificationGoal goal= (EventNotificationGoal) Helper.getGoal((TamableAnimal) maid, EventNotificationGoal.class);
            if(goal!=null){
                goal.onPlayerFishedItem(event);
            }
        }
    }

    @SubscribeEvent
    public static void onEnderManAngered(net.minecraftforge.event.entity.living.EnderManAngerEvent event){
        var enderman=event.getEntity();
        var world=enderman.level;
        var pos=enderman.blockPosition();
        var maidList=world.getEntitiesOfClass(TamableAnimal.class,
                enderman.getBoundingBox().inflate(10),
                entity1 -> entity1.getClass().getName().contains("LittleMaidEntity"));
        for(var maid:maidList){

            //tamed?
            if(!((TamableAnimal) maid).isTame()){
                continue;
            }

            //get goal

            EventNotificationGoal goal= (EventNotificationGoal) Helper.getGoal((TamableAnimal) maid, EventNotificationGoal.class);
            if(goal!=null){
                goal.onEnderManAngered(event);
            }
        }
    }

    @SubscribeEvent
    public static void onVillageSieged(net.minecraftforge.event.village.VillageSiegeEvent event){
        var world=event.getLevel();
        var pos=event.getAttemptedSpawnPos();
        var maidList=world.getEntitiesOfClass(TamableAnimal.class,
                new net.minecraft.world.phys.AABB(new BlockPos(pos.x,pos.y,pos.z)).inflate(150),
                entity1 -> entity1.getClass().getName().contains("LittleMaidEntity"));
        for(var maid:maidList){
            //get goal
            EventNotificationGoal goal= (EventNotificationGoal) Helper.getGoal((TamableAnimal) maid, EventNotificationGoal.class);
            if(goal!=null){
                goal.onVillageSieged(event);
            }
        }
    }

    @SubscribeEvent
    public void onZombieAid(net.minecraftforge.event.entity.living.ZombieEvent.SummonAidEvent event){
        var world=event.getLevel();
        var pos=new BlockPos(event.getX(),event.getY(),event.getZ());
        var maidList=world.getEntitiesOfClass(TamableAnimal.class,
                new net.minecraft.world.phys.AABB(pos).inflate(75),
                entity1 -> entity1.getClass().getName().contains("LittleMaidEntity"));
        for(var maid:maidList){
            //get goal
            EventNotificationGoal goal= (EventNotificationGoal) Helper.getGoal((TamableAnimal) maid, EventNotificationGoal.class);
            if(goal!=null){
                goal.onZombieAid(event);
            }
        }
    }



}
