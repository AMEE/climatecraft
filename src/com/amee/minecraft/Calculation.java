package com.amee.minecraft;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


import com.amee.client.AmeeException;
import com.amee.client.model.data.*;
import com.amee.client.model.profile.*;
import com.amee.client.service.AmeeObjectFactory;
import com.amee.client.util.Choice;

public class Calculation {

	private AmeeDataItem dataItem;
	private AmeeProfileCategory profileCategory;

	private String path;
	private List<String> drills;
	private Float cachedResult;
	private List<String> parameters;
	private String name;

	public Calculation(String _name, String _path, List<String> _drills, List<String> _parameters)
	{
		name = _name;
		parameters = _parameters;
		path = _path;
		drills = _drills;
	}

	public AmeeDataItem dataItem() {
		if (dataItem == null)
		{
			try {
				AmeeDrillDown drillDown;
				drillDown = AmeeObjectFactory.getInstance().getDrillDown(path + "/drill");
				for (int i=0; i<drills.size(); i+=2)
				{
					drillDown.addSelection(drills.get(i), drills.get(i+1));
				}
				drillDown.fetch();
				dataItem = drillDown.getDataItem();
			} catch (AmeeException e) {
				throw new RuntimeException("Couldn't fetch data item! " + e.getMessage());
			}
		}
		return dataItem;
	}

	public AmeeProfileCategory profileCategory() {
		if (profileCategory == null)
		{
			try {
				profileCategory = AmeeObjectFactory.getInstance().getProfileCategory(CarbonCounter.profile(), path);
			} catch (AmeeException e) {
				throw new RuntimeException("Couldn't fetch profile category! " + e.getMessage());
			}
		}
		return profileCategory;
	}

	public void calculate()
	{
		new CalculationThread(this).run(); 
	}

	public void blockingCalculate() {
		if (cachedResult == null) {
			List<Choice> values = new ArrayList<Choice>();
			for (int i=0; i<parameters.size(); i+=2)
			{
				values.add(new Choice(parameters.get(i), parameters.get(i+1)));
			}
			values.add(new Choice("name", name + "-" + UUID.randomUUID().toString()));
			try {
				// Get category
				AmeeProfileItem profileItem = profileCategory().addProfileItem(dataItem().getUid(), values);
				cachedResult = new Float(profileItem.getAmount().floatValue()); 
			} catch (AmeeException e) {
				throw new RuntimeException("Problem creating profile item!");
			}		
		}
		Atmosphere.addToTotal(cachedResult);
	}

	public String name()
	{
		return name;
	}

}
