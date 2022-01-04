package net.AptiTech.lordaragos.minecraft.PossessEm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

/**
 * TODO Put here a description of what this class does.
 *
 * @author kevint.
 *         Created Jan 18, 2013.
 */
@SuppressWarnings("javadoc")
public class Main extends JavaPlugin{
	public static Main plugin;
	public final Logger logger = Logger.getLogger("Minecraft");
	public final PluginEventHandler eventhandler = new PluginEventHandler(this);
	public String name;
	private Player player;
	public BukkitTask possessor;
	// Possessed, Possessor
	public Map<Player, Player> possessedlist = new HashMap<Player, Player>();
	// Possessor, hidden or not
	public Map<Player, Boolean> hidelist = new HashMap<Player, Boolean>();
	public ArrayList<Player> oplist = new ArrayList<Player>();
	
	@Override
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		this.logger.info("[" + pdfFile.getName() +"']" +" Plugin is now disabled.");
	}
	
	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(this.eventhandler, this);
		this.name = ChatColor.AQUA + "["+pdfFile.getName()+"']" + ChatColor.WHITE;
		this.logger.info(this.name +" Plugin is now enabled.");
		
		this.possessor = this.getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
		    @Override  
		    public void run() {
		        for(Player player:Main.this.getServer().getOnlinePlayers()) {
		        	if(player.isOp() || player.hasPermission("PossessEm.possess")) {
		        		if(Main.this.oplist.contains(player)) {
		        			//pass
		        		} else {
		        			Main.this.oplist.add(player);
		        		}
		        	} else {
		        		Main.this.oplist.remove(player);
		        	}
		        	if(Main.this.possessedlist.containsKey(player)) {
		        		if(player.isOnline()) {
			        		try
			        		{
			        			Player possessor = getValueByKey(Main.this.possessedlist, player);
			        			player.teleport(possessor.getLocation());
			        		}
			        		catch (NullPointerException e) {
			        			Main.this.possessedlist.remove(player);
			        		}
		        		} else {
		        			Main.this.possessedlist.remove(player);
		        		}
		        	} else {
		        		//pass
		        	}
		        }
		    }
		}, 60L, 1L);
	}
	@SuppressWarnings("unused")
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, final String[] args) {
		if (sender instanceof Player) {
			this.player = (Player) sender;
			World world = this.player.getWorld();
		} else {
			onConsoleCommand((ConsoleCommandSender) sender, cmd, commandLabel, args);
		}
		
		
		String command = commandLabel;
		if(command.equalsIgnoreCase("possess")) {
			if(this.player.hasPermission("PossessEm.possess")){
				if(args.length == 0){
					this.player.sendMessage(ChatColor.RED + "Error: You need to have a player specified. /possess <player>");
				} else if (args.length == 1) {
					Player targetp = this.getServer().getPlayer(args[0]);
					if(targetp.isOnline()) {
						if(this.possessedlist.containsKey(targetp)) {
							//show possessor
							for(Player other : getServer().getOnlinePlayers()){
								other.showPlayer(this.player);
							}
							this.player.showPlayer(targetp);
							getServer().broadcastMessage(ChatColor.YELLOW + this.player.getDisplayName() + " has joined the game.");
							this.player.sendMessage("You are now shown.");
							this.possessedlist.remove(targetp);
							this.player.sendMessage("Possesion Released");
							for(Player ops:this.oplist){
								ops.sendMessage(this.player.getDisplayName() + " has unhidden from the other players. Possession Released.");
							}
							this.player.showPlayer(targetp);
						} else {
							//hide possessor and possessed (to possessor)
							for(Player other : getServer().getOnlinePlayers()){
								other.hidePlayer(this.player);
							}
							this.player.hidePlayer(targetp);
							getServer().broadcastMessage(ChatColor.YELLOW + this.player.getDisplayName() + " has left the game.");
							this.player.sendMessage("You are now hidden.");
							for(Player ops:this.oplist){
								ops.sendMessage(this.player.getDisplayName() + " has hidden from the other players. Possessing a player...");
							}
							
							//start possessing }:)
							this.player.teleport(targetp.getLocation());
							this.possessedlist.put(targetp, this.player);
							
						}
					}
				} else {
					this.player.sendMessage(ChatColor.RED + "Error: Too many arguments. /possess <player>");
				}
			} else {
				this.player.sendMessage(ChatColor.RED + "Error: You have no permission to do this.");
			}
		}
		
		return false;
		
	}
	
	@SuppressWarnings("unused")
	public boolean onConsoleCommand(ConsoleCommandSender sender, Command cmd, String commandLabel, final String[] args){
		if(commandLabel.equalsIgnoreCase("possess") ) {
			sender.sendMessage(ChatColor.YELLOW + "Notice: There's nothing to do in console silly");
		}
		return false;
	}
	
	public static <E, T> T getValueByKey(Map<E, T> map, E key) {
	    for (Entry<E, T> entry : map.entrySet()) {
	        if (key.equals(entry.getKey())) {
	            return entry.getValue();
	        }
	    }
	    return null;
	}
}