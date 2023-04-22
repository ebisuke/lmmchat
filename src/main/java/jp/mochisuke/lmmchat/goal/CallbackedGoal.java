package jp.mochisuke.lmmchat.goal;

import net.minecraft.world.entity.ai.goal.Goal;

public abstract class  CallbackedGoal extends Goal {
    protected Callback callback;
    boolean active = false;
    public CallbackedGoal() {
    }

    @Override
    public boolean canUse() {
        return active;
    }
    public boolean canContinueToUse() {
        return canUse();
    }
    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    public void activate() {
        active = true;
    }

    protected void success() {
        if (callback != null) {
            callback.onSuccess();
            callback=null;
        }
    }
    protected void fail(String reason) {
        if (callback != null) {
            callback.onFailed(reason);
            callback=null;
        }
    }

    public interface Callback {
        void onSuccess();
        void onFailed(String reason);
    }
}
