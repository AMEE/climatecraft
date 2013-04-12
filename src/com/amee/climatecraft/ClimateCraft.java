package com.amee.climatecraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ClimateCraft extends JavaPlugin {

	@Override
	public void onEnable(){
    // Save default configuration file
    this.saveDefaultConfig();
    // Load config
    String server = getConfig().getString("server");
    String username = getConfig().getString("username");
    String password = getConfig().getString("password");
	}
	 
	@Override
	public void onDisable() {
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("co2")){
			sender.sendMessage(Atmosphere.totalAsString());
			return true;
		}
		return false; 
	}

}
