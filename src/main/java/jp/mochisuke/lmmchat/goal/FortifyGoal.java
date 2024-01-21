package jp.mochisuke.lmmchat.goal;

import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FortifyGoal<T extends TamableAnimal> extends AIGoalBase {
    protected final T entity;
    protected long remainingTime;
    protected final long initialRemainingTime=20*40*60;
    protected boolean navigateFlag=false;
    public FortifyGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public void stop() {
        super.stop();
        entity.getNavigation().stop();
    }
    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.MOVE,Flag.LOOK);
    }

    @Override
    public boolean canContinueToUse() {
        return super.canUse();
    }

    @Override
    public boolean isInterruptable() {
        return false;
    }

    @Override
    public boolean canUse() {
        return active && entity.getOwner()!=null ;
    }

    public void setFortify(boolean on) {
        if(on){
            active=true;
            remainingTime=initialRemainingTime;
        }else{
            stop();
            active=false;
        }
    }
    public void setup(double x, double y, double z) {

        super.activate();
    }

    @Override
    public boolean requiresUpdateEveryTick() {
        return true;
    }

    @Override
    public void tick() {
        if(!canUse()) {
            return;
        }

        if(entity.tickCount%40==1){
            if(entity.getOwner()==null){
                //stop
                fail("no owner");
                return;
            }
            //check nearby enemies
            Level level=entity.level();
            if(level.getNearbyEntities(Monster.class, TargetingConditions.forCombat(),entity, entity.getBoundingBox().inflate(20.0)).size()>0){
                //reset timer
                remainingTime=initialRemainingTime;
            }
            remainingTime--;
            if(remainingTime<=0){
                //stop
                setFortify(false);
                success();
                return;
            }

            //guard owner
            Vec3 pos=new Vec3(entity.getOwner().getX(),entity.getOwner().getY(),entity.getOwner().getZ());

            //if arrived
            if(entity.distanceToSqr(pos)<2.0){
                if(navigateFlag) {
                    entity.getNavigation().stop();
                    navigateFlag=false;
                }
                //look direction of owner
                entity.getLookControl().setLookAt(entity.getOwner(), 10.0F, (float) entity.getMaxHeadYRot());
            }else{
                entity.getNavigation().moveTo(pos.x,pos.y,pos.z,1.0);
                navigateFlag=true;
            }
        }


    }
}
