package com.amee.climatecraft;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ClimateCraft extends JavaPlugin {

	@Override
	public void onEnable(){
		// TODO Insert logic to be performed when the plugin is enabled
	}
	 
	@Override
	public void onDisable() {
		// TODO Insert logic to be performed when the plugin is disabled
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("co2")){
			sender.sendMessage(Atmosphere.totalAsString());
			return true;
		}
		return false; 
	}

}
