package jp.mochisuke.lmmchat.goal;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import org.slf4j.Logger;

public abstract class AIGoalBase extends Goal {
    static final Logger logger = LogUtils.getLogger();
    protected Callback callback;
    boolean active = false;
    public AIGoalBase() {
    }

    @Override
    public boolean canUse() {
        return active;
    }
    public boolean canContinueToUse() {
        return canUse();
    }

    public void activate() {
        active = true;
        start();

    }

    protected void success() {
        logger.info(this.getClass().getName()+" success");
        active = false;
        if(callback!=null)
            callback.onSuccess();


    }
    protected void fail(String reason) {
        logger.info(this.getClass().getName()+" fail:"+reason);
        active = false;
        if(callback!=null)
            callback.onFailed(reason);
    }

    public void setCallback(Callback aiOrderBase) {
        this.callback=aiOrderBase;
    }

    public interface Callback {
        void onSuccess();
        void onFailed(String reason);
    }
}
