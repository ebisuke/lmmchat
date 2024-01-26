package jp.mochisuke.lmmchat.packets;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChatController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.function.Supplier;

public class SynthesisPacket {
    private static final Logger LOGGER = LogUtils.getLogger();

    public int sourceId;

    public int speakerId;
    public String text;
    public UUID target;


    public SynthesisPacket(FriendlyByteBuf buf)
    {
        this.sourceId = buf.readInt();
        this.speakerId = buf.readInt();
        this.text = buf.readUtf(32767);
        this.target = buf.readUUID();
    }
    public SynthesisPacket(int sourceId, int speakerId,String text,UUID target){
        this.sourceId=sourceId;
        this.speakerId=speakerId;
        this.text=text;
        this.target=target;

    }
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(this.sourceId);
        buf.writeInt(this.speakerId);
        buf.writeUtf(this.text);
        buf.writeUUID(this.target);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
                {
                    LOGGER.info("Received synthesis packet from server");
                    LMMChatController.synthesis(this.sourceId, this.speakerId, this.text);
                }
        );
        ctx.get().setPacketHandled(true);

    }
}
