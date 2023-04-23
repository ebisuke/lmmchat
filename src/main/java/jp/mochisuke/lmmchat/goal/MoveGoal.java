package jp.mochisuke.lmmchat.goal;

import net.minecraft.world.entity.Mob;

import java.util.EnumSet;

public class MoveGoal<T extends Mob> extends AIUnitGoalBase {
    protected final T entity;
    protected double x;
    protected double y;
    protected double z;
    public MoveGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public void stop() {
        super.stop();
        entity.getNavigation().stop();
        fail("interrupted");
    }
    @Override
    public EnumSet<Flag> getFlags() {
        return EnumSet.of(Flag.MOVE);
    }

    public void setup(double x,double y,double z) {

        this.x=x;
        this.y=y;
        this.z=z;
        super.activate();
    }


    @Override
    public void tick() {
        if(!canUse()) {
            return;
        }
        //move per 100 ticks
        if(entity.tickCount%60==1){
            entity.getNavigation().moveTo(x,y,z,1.0);
        }

        //if arrived
        if(entity.distanceToSqr(x,y,z)<1.0){
            success();
        }
    }
}
