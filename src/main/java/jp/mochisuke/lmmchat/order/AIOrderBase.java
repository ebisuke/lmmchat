package jp.mochisuke.lmmchat.order;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChat;
import jp.mochisuke.lmmchat.LMMChatConfig;
import jp.mochisuke.lmmchat.chat.ChatData;
import jp.mochisuke.lmmchat.chat.ChatGenerationCallback;
import jp.mochisuke.lmmchat.chat.ChatGenerationRequest;
import jp.mochisuke.lmmchat.chat.ChatPreface;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import org.slf4j.Logger;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public abstract class AIOrderBase implements ChatGenerationCallback {
    public static final Logger logger= LogUtils.getLogger();
    protected final Mob entity;
    protected final VariablesContext context;
    protected List<Object> args;
    public AIOrderBase(Mob entity,VariablesContext context, List<Object> args) {
        this.entity = entity;
        this.context = context;
        this.args = args;
    }
    protected abstract void startUp(Mob entity,VariablesContext context, List<Object> args);
    protected int val(String o){
        //name is number?
        if (o.matches("(|-)[0-9]+")) {
            //return number
            return Integer.parseInt(o);
        } else
        if (context.variables.containsKey(o)) {
            return (int) context.variables.get(o);
        } else {
            throw new RuntimeException("variable not found:"+o);
        }
    }
    protected void val(String o,int value){
        //name is number?
        if (o.matches("(|-)[0-9]+")) {
            //return number
            throw new RuntimeException("variable name is number:"+o);
    }
        context.variables.put(o,value);
    }
    public interface Generator{
        AIOrderBase generate(Mob entity,VariablesContext context, List<Object> args);
    }
    public void execute(){
        startUp(entity,context,args);
        executeImpl();
    }
    protected abstract void executeImpl();

    public void onSuccess(){
        //nothing to do
    }
    public void onFailed(String reason){
        //nothing to do

    }

    public void notifyAI(String message){
        ChatPreface preface = new ChatPreface(LMMChatConfig.getPreface());
        var req=new ChatGenerationRequest(null,entity,true,false,message,
                Minecraft.getInstance().player.getLevel().getGameTime(),0, preface);
        req.setCallback(this);
        LMMChat.chatThread.PushRequest(req);
    }
    public void sendChat(String message,LivingEntity sendto){
        LMMChat.addChatMessage(
                this.entity,sendto,false,false,message,0
        );
    }
    public void sendChatFromAssitant(String message,LivingEntity sendto){
        LMMChat.addChatMessage(
                this.entity,sendto,true,false,message,0
        );
    }
    @Override
    public void onChatGenerated(ChatGenerationRequest request, ChatData response) {
        //nothing to do
        logger.info("AIOrderBase.onChatGenerated:REQ:"+response.getCallerMessage()+"RESP:"+response.getCalleeMessage());

        // send chat
        sendChatFromAssitant(response.getCalleeMessage(),request.getCaller());


    }

    public void activateGoal(Class type, Object ... args)
    {
        this.entity.goalSelector.getAvailableGoals().stream().filter(g->g.getGoal().getClass()==type).findFirst().ifPresent(g->{
            Method m = null;
            try {
                m = type.getMethod("activate");
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
            try {
                m.invoke(g.getGoal(),args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
