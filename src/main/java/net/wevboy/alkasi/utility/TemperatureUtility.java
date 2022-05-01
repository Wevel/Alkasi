package net.wevboy.alkasi.utility;

public class TemperatureUtility
{
	public static final double TEMPERATURE_WATER_FREEZE = CelsiusToKelvin(0.0);
	public static final double TEMPERATURE_AMBIENT = CelsiusToKelvin(20.0);
	public static final double TEMPERATURE_WARM = CelsiusToKelvin(50.0);
	public static final double TEMPERATURE_WATER_BOILING = CelsiusToKelvin(100.0);
	public static final double TEMPERATURE_MAX = CelsiusToKelvin(1399.0);

	public static final int TEMPERATURE_LEVEL_AMBIENT = CalculateTemperatureLevel(TEMPERATURE_AMBIENT);
	public static final int DEFAULT_TEMPERATURE_LEVEL = TEMPERATURE_LEVEL_AMBIENT;
	public static final double DEFAULT_TEMPERATURE = TEMPERATURE_AMBIENT;

	public static final double HEATER_FALLOFF_SCALE = 10;
	public static final double HEATER_TEMPERATURE_CAMPFIRE = CelsiusToKelvin(120.0);

	public static double CelsiusToKelvin(double celsius)
	{
		return celsius + 273.15;
	}

	public static double KelvinToCelsius(double kelvin)
	{
		return kelvin - 273.15;
	}

	public static int CalculateTemperatureLevel(double kelvin)
	{
		double celsius = KelvinToCelsius(kelvin);

		if (celsius < TEMPERATURE_WATER_FREEZE) return 0;
		else if (celsius < TEMPERATURE_WARM) return 1;
		else if (celsius < TEMPERATURE_WATER_BOILING) return 2;
		else if (celsius < TEMPERATURE_MAX) return (int) Math.max(Math.min(Math.floor((celsius * 0.01) + 2), 15), 0);
		else return 15;
	}

	public static double ClampTemperature(double kelvin)
	{
		return Math.max(0, Math.min(kelvin, TEMPERATURE_MAX));
	}

	public static double CalculateHeaterTemperature(double baseTemperatureKelvin, double distance)
	{
		if (Math.abs(baseTemperatureKelvin) > 0)
		{
			distance = Math.max(0.0, distance - 1);
			return Math.max(TEMPERATURE_AMBIENT, baseTemperatureKelvin - (distance * distance * HEATER_FALLOFF_SCALE));
		}
		else return baseTemperatureKelvin;
	}
}
