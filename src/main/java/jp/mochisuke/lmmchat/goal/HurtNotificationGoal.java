package jp.mochisuke.lmmchat.goal;

import jp.mochisuke.lmmchat.LMMChat;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.goal.Goal;

public class HurtNotificationGoal<T extends TamableAnimal> extends Goal {
    T entity;

    int cooldown=0;
    float prevHealth=0;
    public HurtNotificationGoal(T entity) {
        this.entity = entity;
    }

    @Override
    public boolean canUse() {
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return canUse();
    }

    @Override
    public void tick() {
        if(cooldown>0){
            cooldown--;
            return;
        }
            float hp = entity.getHealth();
            if(hp>prevHealth){
                prevHealth=hp;
            }else if(hp<prevHealth) {
                float damage = prevHealth - hp;
                prevHealth = hp;


                String msg = String.format("メイドさんは %.0f のダメージを受けました。残りHPは %.0f/%.0f です。 (%.0f%%)", damage, (float) hp, entity.getMaxHealth(), (hp / entity.getMaxHealth() * 100));
                LMMChat.addChatMessage(null, entity, true, false, msg, 0);
                cooldown = 400;
            }


    }
}
