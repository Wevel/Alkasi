package net.wevboy.alkasi.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.wevboy.alkasi.Alkasi;
import net.wevboy.alkasi.item.ModItemGroup;

public class ModBlocks
{
	public static final Block CATIUM_ORE = registerBlock("catium_ore",
														 new Block(FabricBlockSettings.of(Material.STONE).strength(4.5f).requiresTool()),
														 ModItemGroup.ALKASI);
	public static final Block JAR_PLAIN= registerBlock("jar_plain",
														 new GlassBlock(FabricBlockSettings.copy(Blocks.GLASS).strength(1.0f).nonOpaque()),
														 ModItemGroup.ALKASI);

	public static void RegisterModBlocks()
	{
		Alkasi.LOGGER.info("Registering mod blocks for " + Alkasi.MOD_ID);
	}

	private static Item registerBlockItem(String name, Block block, ItemGroup group)
	{
		return Registry.register(Registry.ITEM, new Identifier(Alkasi.MOD_ID, name), new BlockItem(block, new FabricItemSettings().group(group)));
	}

	private static Block registerBlock(String name, Block block, ItemGroup group)
	{
		registerBlockItem(name, block, group);
		return Registry.register(Registry.BLOCK, new Identifier(Alkasi.MOD_ID, name), block);
	}
}
