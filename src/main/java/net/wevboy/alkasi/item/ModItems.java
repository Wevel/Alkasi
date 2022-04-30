package net.wevboy.alkasi.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.wevboy.alkasi.Alkasi;

public class ModItems
{
	public static final Item GLASS_TUBE = registerItem("glass_tube", new Item(new FabricItemSettings().group(ItemGroup.MISC)));

	public static void RegisterModItems()
	{
		Alkasi.LOGGER.info("Registering mod items for " + Alkasi.MOD_ID);
	}

	private static Item registerItem(String name, Item item)
	{
		return Registry.register(Registry.ITEM, new Identifier(Alkasi.MOD_ID, name), item);
	}
}
