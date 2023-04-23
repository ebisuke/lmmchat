package jp.mochisuke.lmmchat.goal;

import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.ai.goal.Goal;
import org.slf4j.Logger;

import java.util.ArrayList;

public abstract class AIUnitGoalBase extends Goal {
    static final Logger logger = LogUtils.getLogger();
    protected ArrayList<Callback> callback=new ArrayList<>();
    boolean active = false;
    public AIUnitGoalBase() {
    }

    @Override
    public boolean canUse() {
        return active;
    }
    public boolean canContinueToUse() {
        return canUse();
    }
    public void addListener(Callback callback) {
        this.callback.add(callback);
    }
    public void removeListener(Callback callback) {
        this.callback.remove(callback);
    }
    public void activate() {
        active = true;
        start();

    }

    protected void success() {
        logger.info(this.getClass().getName()+" success");
        for (Callback cb:callback)
        {
            cb.onSuccess();
        }
        callback.clear();
        active = false;
    }
    protected void fail(String reason) {
        logger.info(this.getClass().getName()+" fail"+reason);
        for (Callback cb:callback)
        {
            cb.onFailed(reason);
        }
        callback.clear();
        active = false;
    }

    public interface Callback {
        void onSuccess();
        void onFailed(String reason);
    }
}
