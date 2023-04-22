package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.chat.ChatData;
import jp.mochisuke.lmmchat.chat.ChatGenerationRequest;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class EndOrder extends AIOrderBase{

    public EndOrder(Mob entity,VariablesContext context, List<Object> args) {
        super(entity,context, args);
    }

    @Override
    protected void startUp(Mob entity, VariablesContext context, List<Object> args) {

    }

    @Override
    public void executeImpl() {
        context.clear();
    }

    @Override
    public void onChatGenerated(ChatGenerationRequest request, ChatData response) {
        //nothing to do
    }
}
