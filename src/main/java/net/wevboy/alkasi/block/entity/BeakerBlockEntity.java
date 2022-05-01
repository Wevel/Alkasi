package net.wevboy.alkasi.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.wevboy.alkasi.recipe.ModRecipes;

public class BeakerBlockEntity extends AbstractGlasswareBlockEntity
{
	public BeakerBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.BEAKER_GLASS, pos, state, ModRecipes.GLASSWARE);
	}
}

