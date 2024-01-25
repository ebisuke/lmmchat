package jp.mochisuke.lmmchat.packets;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.LMMChatController;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.slf4j.Logger;

import java.util.UUID;
import java.util.function.Supplier;

public class SynthesisPacket {
    private static final Logger LOGGER = LogUtils.getLogger();

    public int sourceId;

    public BlockPos sourcePos;
    public String text;
    public UUID target;


    public SynthesisPacket(FriendlyByteBuf buf)
    {
        this.sourceId = buf.readInt();
        this.sourcePos = buf.readBlockPos();
        this.text = buf.readUtf(32767);
        this.target = buf.readUUID();
    }
    public SynthesisPacket(int sourceId, BlockPos pos,String text,UUID target){
        this.sourceId=sourceId;
        this.sourcePos=pos;
        this.text=text;
        this.target=target;

    }
    public void encode(FriendlyByteBuf buf)
    {
        buf.writeInt(this.sourceId);
        buf.writeBlockPos(this.sourcePos);
        buf.writeUtf(this.text);
        buf.writeUUID(this.target);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
                {
                    LOGGER.info("Received synthesis packet from server");
                    LMMChatController.synthesis(this.sourceId, this.sourcePos, this.text);
                }
        );
        ctx.get().setPacketHandled(true);

    }
}
