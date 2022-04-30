package net.wevboy.alkasi.item;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;
import net.wevboy.alkasi.Alkasi;
import net.wevboy.alkasi.block.ModBlocks;

public class ModItemGroup
{
	public static final ItemGroup ALKASI = FabricItemGroupBuilder.build(new Identifier(Alkasi.MOD_ID, "alkasi"),
																		() -> new ItemStack(ModBlocks.CATIUM_ORE));
}
