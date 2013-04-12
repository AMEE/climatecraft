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
    // Register carbon counter listener
		getServer().getPluginManager().registerEvents(new CarbonCounter(server, username, password), this);
    // Set co2 value
    Atmosphere.setTotal((float)getConfig().getDouble("co2"));
    // Setup listener for weather control
    getServer().getPluginManager().registerEvents(new WeatherController(this), this);
	}
	 
	@Override
	public void onDisable() {
    // Store co2 value
    getConfig().set("co2", Atmosphere.getTotal());
    saveConfig();
	}

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
		if(cmd.getName().equalsIgnoreCase("co2")){
			sender.sendMessage(Atmosphere.totalAsString());
			return true;
		}
		else if(cmd.getName().equalsIgnoreCase("setco2")){
			Float value = Float.parseFloat(args[0]);
      if (value != null)
        Atmosphere.setTotal(value);
      sender.sendMessage(Atmosphere.totalAsString());
			return true;
		}
		return false; 
	}

}
