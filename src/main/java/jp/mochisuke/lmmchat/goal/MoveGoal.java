package jp.mochisuke.lmmchat.goal;

import net.minecraft.world.entity.Mob;

public class MoveGoal<T extends Mob> extends CallbackedGoal{
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

    public void activate(double x,double y,double z) {
        super.activate();
        this.x=x;
        this.y=y;
        this.z=z;
    }


    @Override
    public void tick() {
        //move per 100 ticks
        if(entity.tickCount%100==0){
            entity.getNavigation().moveTo(x,y,z,1.0);
        }

        //if arrived
        if(entity.distanceToSqr(x,y,z)<1.0){
            success();
        }
    }
}
