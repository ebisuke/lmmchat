package jp.mochisuke.lmmchat.sounds;

import jp.mochisuke.lmmchat.LMMChat;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SoundProvider {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, LMMChat.MODID);


    private static RegistryObject<SoundEvent> registerSound(String name) {

        return SOUNDS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(LMMChat.MODID, name)));
    }

    public static void init(IEventBus bus) {
        SOUNDS.register(bus);
    }
}
