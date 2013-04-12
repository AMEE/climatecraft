package com.amee.climatecraft;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import java.util.Random;

class WeatherController extends BukkitRunnable implements Listener {
  
  private final JavaPlugin plugin;
  private World world;
  private Random random = new Random();
 
  public WeatherController(JavaPlugin plugin) {
    this.plugin = plugin;
  }

  @EventHandler
  public void onWeatherChange(WeatherChangeEvent event) {
    // If it's changing to clear
    if (event.toWeatherState() == false) {
      // Work out a time to next storm that decreases with pollution ratio.
      world = event.getWorld();
      int ticks = world.getWeatherDuration();
      ticks *= 1.0f - Atmosphere.getPollutionRatio();
      plugin.getServer().getScheduler().runTaskLater(plugin, this, ticks);
      plugin.getServer().getLogger().info("Firing off another storm in " + Integer.toString(ticks));
    }
  }
  
  public void run() {
    plugin.getServer().getLogger().info("Starting a storm!");
    // Fire off a storm!
    world.setStorm(true);
    if (Atmosphere.getPollutionRatio() > 0.5)
      world.setThundering(true);
  }
  
}