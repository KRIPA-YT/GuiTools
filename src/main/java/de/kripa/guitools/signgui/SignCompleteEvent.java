package de.kripa.guitools.signgui;

import net.minecraft.core.BlockPosition;
import org.bukkit.entity.Player;

import java.beans.ConstructorProperties;

public final class SignCompleteEvent {

	private final Player        player;
	private final BlockPosition location;
	private final String[]      lines;

	@ConstructorProperties({"player", "location", "lines"})
	public SignCompleteEvent(Player player, BlockPosition location, String[] lines)
	{
		this.player = player;
		this.location = location;
		this.lines = lines;
	}

	public final Player getPlayer()
	{
		return this.player;
	}

	public final BlockPosition getLocation()
	{
		return this.location;
	}

	public final String[] getLines()
	{
		return this.lines;
	}
}
