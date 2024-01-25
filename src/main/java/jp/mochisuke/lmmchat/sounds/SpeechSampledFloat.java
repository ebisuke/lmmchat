package jp.mochisuke.lmmchat.sounds;

import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.SampledFloat;

public class SpeechSampledFloat implements SampledFloat {

    private final float fixedValue;

    public SpeechSampledFloat(float fixedValue) {
        this.fixedValue = fixedValue;
    }

    @Override
    public float sample(RandomSource randomSource) {
        return fixedValue;
    }
}
