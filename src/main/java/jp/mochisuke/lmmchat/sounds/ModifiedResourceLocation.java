package jp.mochisuke.lmmchat.sounds;

import net.minecraft.resources.ResourceLocation;

import java.util.function.UnaryOperator;

public class ModifiedResourceLocation extends ResourceLocation {

    public ModifiedResourceLocation(String p_135809_) {
        super("", p_135809_, null);
    }


    public ResourceLocation withPath(String p_251088_) {
        return new ModifiedResourceLocation( p_251088_);
    }

    public ResourceLocation withPath(UnaryOperator<String> p_250342_) {
        return this.withPath(p_250342_.apply(this.getPath()));
    }

    public String toString() {
        return  this.getPath();
    }

}
