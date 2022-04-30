package net.wevboy.alkasi.world.feature;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreConfiguredFeatures;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.wevboy.alkasi.Alkasi;
import net.wevboy.alkasi.block.ModBlocks;

import java.util.List;

public class ModConfiguredFeatures
{
	public static final List<OreFeatureConfig.Target> OVERWORLD_CATIUM_ORES = List.of(
			OreFeatureConfig.createTarget(OreConfiguredFeatures.STONE_ORE_REPLACEABLES,
					ModBlocks.CATIUM_ORE.getDefaultState()),
			OreFeatureConfig.createTarget(OreConfiguredFeatures.DEEPSLATE_ORE_REPLACEABLES,
					ModBlocks.CATIUM_ORE.getDefaultState()));

	public static final RegistryEntry<ConfiguredFeature<OreFeatureConfig, ?>> CATIUM_ORE = ConfiguredFeatures.register(
			"catium_ore",
			Feature.ORE,
			new OreFeatureConfig(OVERWORLD_CATIUM_ORES, 6));

	public static void RegisterConfiguredFeatures()
	{
		System.out.println("Registering ModConfiguredFeatures for " + Alkasi.MOD_ID);
	}
}
