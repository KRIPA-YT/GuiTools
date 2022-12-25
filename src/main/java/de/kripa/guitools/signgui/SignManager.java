package de.kripa.guitools.signgui;

import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPipeline;
import lombok.AccessLevel;
import lombok.Getter;
import net.minecraft.core.BlockPosition;
import net.minecraft.network.protocol.game.PacketPlayInUpdateSign;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_19_R1.entity.CraftPlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.beans.ConstructorProperties;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class SignManager {

    private final Plugin plugin;
    @Getter(AccessLevel.PROTECTED)
    private final Map<UUID, SignGUI> guiMap;
    private final PluginManager pluginManager;

    @ConstructorProperties({"plugin"})
    public SignManager(Plugin plugin) {
        this.plugin = plugin;
        this.guiMap = new HashMap<>();
        this.pluginManager = Bukkit.getPluginManager();
    }

    public void init(PluginManager pm) {
        pm.registerEvents(new SignManager.SignListener(), this.plugin);
    }

    private class SignListener implements Listener {
        @EventHandler()
        public void onPlayerJoin(PlayerJoinEvent event) {
            final var player = event.getPlayer();
            ChannelDuplexHandler channelDuplexHandler = new ChannelDuplexHandler() {
                @Override
                public void channelRead(ChannelHandlerContext ctx, Object packet) throws Exception {
                    if (packet instanceof PacketPlayInUpdateSign inUpdateSign) {
                        if (guiMap.containsKey(player.getUniqueId())) {
                            var signGUI = guiMap.get(player.getUniqueId());

                            BlockPosition blockPosition = SignReflection.getValue(inUpdateSign, "b");
                            String[] lines = SignReflection.getValue(inUpdateSign, "c");

                            signGUI.getCompleteHandler().onSignComplete(new SignCompleteEvent(player, blockPosition, lines));
                            guiMap.remove(player.getUniqueId());
                        }
                    }
                    super.channelRead(ctx, packet);
                }
            };
            final ChannelPipeline pipeline = ((CraftPlayer) player).getHandle().b.a().m.pipeline();
            pipeline.addBefore("packet_handler", player.getName(), channelDuplexHandler);
        }

        @EventHandler()
        public void onPlayerQuit(PlayerQuitEvent event) {
            final Channel channel = ((CraftPlayer) event.getPlayer()).getHandle().b.a().m;
            channel.eventLoop().submit(() -> channel.pipeline().remove(event.getPlayer().getName()));
            guiMap.remove(event.getPlayer().getUniqueId());
        }
    }

    /**
     * Add new SignGui
     *
     * @param uuid    - UUID of the player
     * @param signGUI - {@link SignGUI} instance
     */
    void addGui(UUID uuid, SignGUI signGUI) {
        this.guiMap.put(uuid, signGUI);
    }
}
