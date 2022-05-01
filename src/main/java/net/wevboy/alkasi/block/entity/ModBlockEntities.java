package net.wevboy.alkasi.block.entity;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.wevboy.alkasi.Alkasi;
import net.wevboy.alkasi.block.ModBlocks;

public class ModBlockEntities
{
	public static final BlockEntityType<BeakerBlockEntity> BEAKER_GLASS = Registry.register(
			Registry.BLOCK_ENTITY_TYPE,
			new Identifier(Alkasi.MOD_ID, "beaker_glass"),
			FabricBlockEntityTypeBuilder.create(BeakerBlockEntity::new, ModBlocks.BEAKER_GLASS).build(null));

	public static void RegisterBlockEntities()
	{
		Alkasi.LOGGER.info("Registering mod block entities for " + Alkasi.MOD_ID);
	}
}
