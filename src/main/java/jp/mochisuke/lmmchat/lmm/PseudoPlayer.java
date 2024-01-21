package jp.mochisuke.lmmchat.lmm;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PseudoPlayer extends Player {
    public Inventory inventory;
    public PseudoPlayer(Level level,BlockPos pos ){
        super(level, pos,1, new GameProfile(null,"lmm"));
        inventory=new Inventory(this);
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }
}
