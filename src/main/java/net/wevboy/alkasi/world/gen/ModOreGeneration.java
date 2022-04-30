package net.wevboy.alkasi.world.gen;

import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.world.gen.GenerationStep;
import net.wevboy.alkasi.world.feature.ModPlacedFeatures;

public class ModOreGeneration
{
	public static void GenerateOres()
	{
		BiomeModifications.addFeature(BiomeSelectors.foundInOverworld(), GenerationStep.Feature.UNDERGROUND_ORES,
				ModPlacedFeatures.CATIUM_ORE_PLACED.getKey().get());
	}
}
