package jp.mochisuke.lmmchat.sounds;

import com.mojang.blaze3d.audio.OggAudioStream;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.client.sounds.AudioStream;
import net.minecraft.client.sounds.SoundBufferLibrary;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.client.sounds.WeighedSoundEvents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

@OnlyIn(net.minecraftforge.api.distmarker.Dist.CLIENT)
public class SpeechSoundInstance implements net.minecraft.client.resources.sounds.SoundInstance{


    private final Sound sound;
    private final float volume;
    private final float pitch;
    private final float x;
    private final float y;
    private final float z;
    private final Attenuation attenuation;
    private final String text;
    private final ResourceLocation fileloc;

    public SpeechSoundInstance(Sound sound,String text,ResourceLocation fileloc, float volume, float pitch, float x, float y, float z, Attenuation attenuation) {

        this.sound = sound;
        this.text = text;
        this.fileloc = fileloc;

        this.volume = volume;
        this.pitch = pitch;
        this.x = x;
        this.y = y;
        this.z = z;
        this.attenuation = attenuation;
    }

    @Override
    public ResourceLocation getLocation() {
        return this.sound.getLocation();
    }

    @Nullable
    @Override
    public WeighedSoundEvents resolve(SoundManager soundManager) {
        return  new WeighedSoundEvents(fileloc, text);
    }

    @Override
    public Sound getSound() {
        return this.sound;
    }

    @Override
    public SoundSource getSource() {
        return SoundSource.NEUTRAL;
    }

    @Override
    public boolean isLooping() {
        return false;
    }

    @Override
    public boolean isRelative() {
        return false;
    }

    @Override
    public int getDelay() {
        return 0;
    }

    @Override
    public float getVolume() {
        return this.volume;
    }

    @Override
    public float getPitch() {
        return this.pitch;
    }

    @Override
    public double getX() {
        return this.x;
    }

    @Override
    public double getY() {
        return this.y;
    }

    @Override
    public double getZ() {
        return this.z;
    }


    public CompletableFuture<AudioStream> getStream(SoundBufferLibrary soundBuffers, Sound sound, boolean looping) {
        String path= sound.getLocation().getPath();
        try {



            FileInputStream inputstream=new FileInputStream(path);

            AudioStream as=new OggAudioStream(inputstream);
            return CompletableFuture.completedFuture(as);


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public Attenuation getAttenuation() {
        return this.attenuation;
    }
}
