package net.wevboy.alkasi.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;

public class BeakerBlockEntity extends AbstractGlasswareBlockEntity
{
	public BeakerBlockEntity(BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.BEAKER_GLASS, pos, state, RecipeType.SMELTING);
	}
}

