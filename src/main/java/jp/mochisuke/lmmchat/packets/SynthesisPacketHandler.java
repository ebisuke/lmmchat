package jp.mochisuke.lmmchat.packets;

import jp.mochisuke.lmmchat.LMMChat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

public class SynthesisPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(LMMChat.MODID, "main"),
            () -> PROTOCOL_VERSION,PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    private static int index = 0;

    public static void register() {
        INSTANCE.messageBuilder(SynthesisPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SynthesisPacket::new)
                .encoder(SynthesisPacket::encode)
                .consumerMainThread(SynthesisPacket::handle)
                .add();
    }
    public static void sendToAllClients(SynthesisPacket msg ) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }
    public static void sendToClient(SynthesisPacket msg, ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }
}