package com.amee.climatecraft;

import java.util.Arrays;
import java.util.HashMap;
import java.io.*;
import java.util.Properties;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.NotePlayEvent;

import com.amee.client.AmeeException;
import com.amee.client.service.*;
import com.amee.client.model.profile.*;

public class CarbonCounter implements Listener {

	private static AmeeProfile profile;

	private static HashMap<String,Calculation> calculations = new HashMap<String,Calculation>(); 

	CarbonCounter(String server, String username, String password)
	{
		// Create mappings
		calculations.put("coal", new Calculation("coal", 
			"business/energy/stationaryCombustion", 
			Arrays.asList("fuel", "anthracite", "context", "residential"),
			Arrays.asList("mass", "2.0", "massUnit", "kg"))
			);
		calculations.put("stick", new Calculation("stick", 
			"business/energy/stationaryCombustion", 
			Arrays.asList("fuel", "wood or wood waste", "context", "residential"),
			Arrays.asList("mass", "1.0", "massUnit", "kg"))
			);
		calculations.put("plank", new Calculation("plank", 
			"business/energy/stationaryCombustion", 
			Arrays.asList("fuel", "wood or wood waste", "context", "residential"),
			Arrays.asList("mass", "2.0", "massUnit", "kg"))
			);
		calculations.put("wood", new Calculation("wood", 
			"business/energy/stationaryCombustion", 
			Arrays.asList("fuel", "wood or wood waste", "context", "residential"),
			Arrays.asList("mass", "8.0", "massUnit", "kg"))
			);
		calculations.put("leaf", new Calculation("leaf", 
			"business/energy/stationaryCombustion", 
			Arrays.asList("fuel", "other primary solid biomass fuels", "context", "residential"),
			Arrays.asList("mass", "1.0", "massUnit", "kg"))
			);
		calculations.put("wood-", new Calculation("wood", 
			"business/energy/stationaryCombustion", 
			Arrays.asList("fuel", "wood or wood waste", "context", "residential"),
			Arrays.asList("mass", "-8.0", "massUnit", "kg"))
			);
		calculations.put("leaf-", new Calculation("leaf", 
			"business/energy/stationaryCombustion", 
			Arrays.asList("fuel", "other primary solid biomass fuels", "context", "residential"),
			Arrays.asList("mass", "-1.0", "massUnit", "kg"))
			);
		calculations.put("crops-", new Calculation("crops", 
			"business/energy/stationaryCombustion", 
			Arrays.asList("fuel", "other primary solid biomass fuels", "context", "residential"),
			Arrays.asList("mass", "-0.25", "massUnit", "kg"))
			);
		calculations.put("cactus", new Calculation("cactus", 
			"business/energy/stationaryCombustion", 
			Arrays.asList("fuel", "other primary solid biomass fuels", "context", "residential"),
			Arrays.asList("mass", "4.0", "massUnit", "kg"))
			);
		calculations.put("cactus-", new Calculation("cactus", 
			"business/energy/stationaryCombustion", 
			Arrays.asList("fuel", "other primary solid biomass fuels", "context", "residential"),
			Arrays.asList("mass", "-4.0", "massUnit", "kg"))
			);
		calculations.put("pig", new Calculation("pig", 
			"business/waste/combustion/municipal", 
			Arrays.asList("type", "food waste", "method", "open burning", "technology", "none"),
			Arrays.asList("mass", "50.0", "massUnit", "kg", "isWetWeight", "true"))
			);
		calculations.put("cow", new Calculation("cow", 
			"business/waste/combustion/municipal", 
			Arrays.asList("type", "food waste", "method", "open burning", "technology", "none"),
			Arrays.asList("mass", "600.0", "massUnit", "kg", "isWetWeight", "true"))
			);
		calculations.put("sheep", new Calculation("sheep", 
			"business/waste/combustion/municipal", 
			Arrays.asList("type", "food waste", "method", "open burning", "technology", "none"),
			Arrays.asList("mass", "50", "massUnit", "kg", "isWetWeight", "true"))
			);
		calculations.put("chicken", new Calculation("chicken", 
			"business/waste/combustion/municipal", 
			Arrays.asList("type", "food waste", "method", "open burning", "technology", "none"),
			Arrays.asList("mass", "2.5", "massUnit", "kg", "isWetWeight", "true"))
			);
		calculations.put("creeper", new Calculation("creeper", 
			"business/waste/combustion/municipal", 
			Arrays.asList("type", "food waste", "method", "open burning", "technology", "none"),
			Arrays.asList("mass", "50.0", "massUnit", "kg", "isWetWeight", "true"))
			);
		calculations.put("zombie", new Calculation("zombie", 
			"business/waste/combustion/municipal", 
			Arrays.asList("type", "food waste", "method", "open burning", "technology", "none"),
			Arrays.asList("mass", "75.0", "massUnit", "kg", "isWetWeight", "true"))
			);
		calculations.put("piston", new Calculation("piston", 
			"home/energy/electricity/realTimeElectricity", 
			Arrays.asList("country", "GB"),
			Arrays.asList("energyUsed", "2.0", "energyUsedUnit", "kWh"))
			);
		calculations.put("note", new Calculation("note", 
			"home/energy/electricity/realTimeElectricity", 
			Arrays.asList("country", "GB"),
			Arrays.asList("energyUsed", "0.5", "energyUsedUnit", "kWh"))
			);
		calculations.put("dispenser", new Calculation("dispenser", 
			"home/energy/electricity/realTimeElectricity", 
			Arrays.asList("country", "GB"),
			Arrays.asList("energyUsed", "1.0", "energyUsedUnit", "kWh"))
			);
		// Initialise members
		//player = _player;
		Atmosphere.init();
		// Connect to AMEE
		AmeeContext.getInstance().setUsername(username);
		AmeeContext.getInstance().setPassword(password);
		AmeeContext.getInstance().setBaseUrl("http://" + server);
		// Create a profile - later on this will be stored with the world somehow
		try {
			profile = AmeeObjectFactory.getInstance().addProfile();
		} catch (AmeeException e) {
			throw new RuntimeException("Problem connecting to AMEE");
		}
	}

	public static AmeeProfile profile() {
		return profile;
	}

	// Entities release carbon when they are set on fire
	public static void releaseFromEntity(/* Entity entity */)
	{
		// Get item
		Calculation calculation = null;
		calculation = calculations.get("");//entity.getEntityString().toLowerCase());
		// Do it
		if (calculation != null) 
		{
			calculation.calculate();
		}
	}

	// Trees can absorb carbon when they grow
	public static void absorbIntoBlock(Integer blockID) 
	{
		// Get item
		Calculation calculation = null;
//		if 		(blockID == Block.wood.blockID) 	calculation = calculations.get("wood-");
//		else if (blockID == Block.leaves.blockID) 	calculation = calculations.get("leaf-");
//		else if (blockID == Block.cactus.blockID) 	calculation = calculations.get("cactus-");
//		else if (blockID == Block.crops.blockID) 	calculation = calculations.get("crops-");
		// Do it
		if (calculation != null) 
		{
			calculation.calculate();
		}
	}

  private void emit(String calculationName) {
    Calculation calc = calculations.get(calculationName);
    if (calc)
      calc.calculate();
  }

  private void absorb(String calculationName) {
    Calculation calc = calculations.get(calculationName+'-');
    if (calc)
      calc.calculate();
  }

	@EventHandler
  public void onBlockBurn(BlockBurnEvent event) {
    switch (event.getBlock().getType()) {
      case LOG:
        emit("wood"); break;
      case LEAVES:
        emit("leaf"); break;
      case CACTUS:
        emit("cactus"); break;
      case WOOD:
        emit("plank"); break;
    }
  }	

	@EventHandler
  public void onBlockDispense(BlockDispenseEvent event) {
    emit("dispenser");
  }	

	@EventHandler
  public void onNotePlay(NotePlayEvent event) {
    emit("note");
  }	

	@EventHandler
  public void onBlockPistonExtend(BlockPistonExtendEvent event) {
    emit("piston");
  }	

	@EventHandler
  public void onBlockPistonRetract(BlockPistonRetractEvent event) {
    emit("piston");
  }

	@EventHandler
  public void OnFurnaceBurn(FurnaceBurnEvent event) {
    switch (event.getFuel().getType()) {
      case WOOD:
        emit("plank"); break;
      case SAPLING:
        emit("leaf"); break;
      case LOG:
        emit("wood"); break;
      case STICK:
        emit("stick"); break;
      case COAL:
        emit("coal"); break;
    }
  }

}
