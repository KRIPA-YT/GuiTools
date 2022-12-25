package de.kripa.guitools.signgui;

import com.google.common.base.Preconditions;
import lombok.Getter;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.PacketPlayOutBlockChange;
import net.minecraft.network.protocol.game.PacketPlayOutOpenSignEditor;
import net.minecraft.world.item.EnumColor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.TileEntitySign;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_19_R1.block.CraftSign;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_19_R1.util.CraftMagicNumbers;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;

public final class SignGUI {

    private final SignManager signManager;
    @Getter private final SignClickCompleteHandler completeHandler;
    private Player player;
    private String[] lines;

    @ConstructorProperties({"signManager", "completeHandler"})
    public SignGUI(SignManager signManager, SignClickCompleteHandler completeHandler) {
        this.signManager = signManager;
        this.completeHandler = completeHandler;
        this.lines = new String[4];
        this.player = null;
    }

    public SignGUI withLines(String... lines) {
        if (lines.length != 4) {
            throw new IllegalArgumentException("Must have at least 4 lines");
        }

        this.lines = lines;
        return this;
    }

    public void open(Player player) {
        open(player, Material.OAK_SIGN);
    }
    public void open(Player player, Material signType) {
        this.player = player;

        final var blockPosition = new BlockPosition(player.getLocation().getBlockX(), 1, player.getLocation().getBlockZ());

        var packet = new PacketPlayOutBlockChange(blockPosition, CraftMagicNumbers.getBlock(signType, (byte) 0));
        sendPacket(packet);

        IChatBaseComponent[] components = CraftSign.sanitizeLines(lines);
        var sign = new TileEntitySign(blockPosition, Blocks.cg.m());
        sign.a(EnumColor.p);

        for (var i = 0; i < components.length; i++)
            sign.a(i, components[i]);

        sendPacket(sign.c());

        var outOpenSignEditor = new PacketPlayOutOpenSignEditor(blockPosition);
        sendPacket(outOpenSignEditor);
        this.signManager.addGui(player.getUniqueId(), this);
    }

    private void sendPacket(Packet<?> packet) {
        Preconditions.checkNotNull(this.player);
        ((CraftPlayer) this.player).getHandle().b.a(packet);
    }
}
