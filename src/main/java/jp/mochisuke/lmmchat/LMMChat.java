package jp.mochisuke.lmmchat;

import com.mojang.logging.LogUtils;
import jp.mochisuke.lmmchat.chat.*;
import jp.mochisuke.lmmchat.commands.CommandDispatcher;
import jp.mochisuke.lmmchat.order.AIOrderDefinitions;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.util.Objects;
import java.util.UUID;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(LMMChat.MODID)
public class LMMChat {

    public  static ChatManager chatManager;
    static LMMChatController lmmChatController;
    // Define mod id in a common place for everything to reference
    public static final String MODID = "lmmchat";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "LMMChat" namespace
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "LMMChat" namespace
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);

    // Creates a new Block with the id "LMMChat:example_block", combining the namespace and path
    // Creates a new BlockItem with the id "LMMChat:example_block", combining the namespace and path
    private static MinecraftServer server;
    public static void addChatMessage(@Nullable LivingEntity caller,@Nullable LivingEntity callee, boolean callerIsAssistant,
                                      boolean calleeIsAssistant, String callerMessage, int conversationCount){
        // if caller and caller are same entity, do nothing
        if(Objects.equals(caller, callee)){
            return;
        }
        boolean friendly = false;
        if(caller!=null) {
            // check callee is friendly to caller

            if (callee instanceof TamableAnimal animal) {
                if (animal.isTame() && animal.isOwnedBy(caller)) {
                    friendly = true;
                }
            }
        }else{

            if (callee instanceof TamableAnimal animal) {
                if (animal.isTame() ) {
                    friendly = true;
                }
            }
        }

        IChatPreface preface;
        LivingEntity owner=null;
        if (callee instanceof TamableAnimal animal) {
            if (animal.isTame() ) {
                owner=animal.getOwner();
            }
        }
        if(!friendly){
            preface=new VariableChatPreface(LMMChatConfig.getNeutralPreface(),owner,caller,callee);
        }else{
            preface=new VariableChatPreface(LMMChatConfig.getPreface(),owner,caller,callee);
        }


        ChatGenerationRequest request = new ChatGenerationRequest(caller,callee,callerIsAssistant,
                calleeIsAssistant,callerMessage,caller!=null?caller.level().getGameTime():callee.level().getGameTime(),conversationCount,preface);
        chatManager.PushRequest(request);

    }
    public LMMChat() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        AIOrderDefinitions.initialize();
    }
    public static Player findPlayerByUUID(UUID uuid){
        return server.getPlayerList().getPlayer(uuid);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        //LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
        LMMChatConfig.loadConfig();
        //load config


    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
        //generate chat thread

        server = event.getServer();
        //register command
        CommandDispatcher.registerCommands(event.getServer());
    }
    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        chatManager = new ChatManager();
    }
    @SubscribeEvent
    public static long getServerTime(){
        return server.getLevel(net.minecraft.world.level.Level.OVERWORLD).getGameTime();
    }
    public static long getElapsedDays(){
        var day= (getServerTime())/24000;
        return day;
    }

    public static MinecraftServer getServer(){
        return server;
    }
    public static boolean isEnableComputerCraft(){
        return ModList.get().isLoaded("computercraft");
    }
    public static long getDayTime(){
        return getServerTime()%24000;
    }
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
