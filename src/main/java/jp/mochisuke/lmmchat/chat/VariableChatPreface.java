package jp.mochisuke.lmmchat.chat;

import net.minecraft.world.entity.Entity;

public class VariableChatPreface implements IChatPreface {
    private final String message;
    private final Entity owner;
    private final Entity maid;
    private final Entity caller;
    public VariableChatPreface(String message, Entity owner,Entity caller, Entity maid) {
        this.owner=owner;
        this.maid=maid;
        this.message = message;
        this.caller=caller;
    }
    @Override
    public String getMessage() {
        String tempStr=message;
        tempStr=tempStr.replace("{ownerid}",owner != null ? owner.getId()+"" : "notfound");
        tempStr=tempStr.replace("{maidid}",maid != null ? maid.getId()+"" : "notfound");
        tempStr=tempStr.replace("{callerid}",caller != null ? caller.getId()+"" : "notfound");
        tempStr=tempStr.replace("{ownername}",owner != null ? owner.getName().getString()+"" : "notfound");
        tempStr=tempStr.replace("{maidname}",maid != null ? maid.getName().getString()+"" : "notfound");
        tempStr=tempStr.replace("{callername}",caller != null ? caller.getName().getString()+"" : "notfound");
        return tempStr;
    }
}
