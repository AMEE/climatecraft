package com.amee.climatecraft;

import java.text.DecimalFormat;

public class Atmosphere {

	private static float total = 0.0f;
	private static float pollutionRatio = 0.0f;

	public static void init() {
		total = 0.0f;
		pollutionRatio = 0.0f;
	}

	public static void addToTotal(float value)
	{
		total += value;
		updatePollutionRatio();
	}

	private static void updatePollutionRatio() {
		pollutionRatio = total / 1000.0f;
		if (pollutionRatio < 0.0)
		{
			pollutionRatio = 0.0f;
		}
		if (pollutionRatio > 1.0)
		{
			pollutionRatio = 1.0f;
		}
	}

	public static String totalAsString() 
	{
		return new DecimalFormat("#0.00").format(total) + "kg";
	}

	public static float cloudColourScale() {
		return 1.0f - pollutionRatio;
	}

	public static float fogColourScale() {
		return 1.0f - (pollutionRatio / 2.0f);
	}

	public static float cloudHeightOffset() {
		return -(pollutionRatio*40.0f);    	
	}

}
