package jp.mochisuke.lmmchat.order;

import jp.mochisuke.lmmchat.chat.ChatData;
import jp.mochisuke.lmmchat.chat.ChatGenerationRequest;
import net.minecraft.world.entity.Mob;

import java.util.List;

public class EndOrder extends AIOrderBase{

    public EndOrder(Mob entity, List<Object> args) {
        super(entity, args);
    }

    @Override
    public void execute() {
        //do nothing
    }

    @Override
    public void onChatGenerated(ChatGenerationRequest request, ChatData response) {
        //nothing to do
    }
}
