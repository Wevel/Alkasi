package net.wevboy.alkasi;

import net.fabricmc.api.ModInitializer;
import net.wevboy.alkasi.block.ModBlocks;
import net.wevboy.alkasi.block.entity.ModBlockEntities;
import net.wevboy.alkasi.item.ModItems;
import net.wevboy.alkasi.world.feature.ModConfiguredFeatures;
import net.wevboy.alkasi.world.gen.ModWorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Alkasi implements ModInitializer
{
	public static final String MOD_ID = "alkasi";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize()
	{
		ModConfiguredFeatures.RegisterConfiguredFeatures();

		ModItems.RegisterModItems();
		ModBlocks.RegisterModBlocks();

		ModWorldGeneration.GenerateWorld();

		ModBlockEntities.RegisterBlockEntities();
	}
}
