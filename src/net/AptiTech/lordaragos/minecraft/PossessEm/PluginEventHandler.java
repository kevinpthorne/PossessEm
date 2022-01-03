package net.AptiTech.lordaragos.minecraft.PossessEm;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * TODO Put here a description of what this class does.
 *
 * @author kevint.
 *         Created Jan 18, 2013.
 */
@SuppressWarnings({ "javadoc" })
public class PluginEventHandler implements Listener {
	public static Main plugin;
	public PluginEventHandler(Main instance){
		plugin = instance;	
	}
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event){
		if(plugin.possessedlist.containsKey(event.getPlayer())) {
			event.setCancelled(true);
		}
	}
	@EventHandler
	public void onBlockPlaceEvent(BlockPlaceEvent event){
		if(plugin.possessedlist.containsKey(event.getPlayer())) {
			event.setCancelled(true);
			event.setBuild(false);
		}
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
		if(plugin.possessedlist.containsKey(event.getPlayer())){
			event.setCancelled(true);
		}
	}
}
