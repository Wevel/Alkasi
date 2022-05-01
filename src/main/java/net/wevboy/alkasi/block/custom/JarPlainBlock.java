package net.wevboy.alkasi.block.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.GlassBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class JarPlainBlock extends GlassBlock
{
	public JarPlainBlock(Settings settings)
	{
		super(settings);
	}

	private static final VoxelShape SHAPE = VoxelShapes.combineAndSimplify(Block.createCuboidShape(4, 9, 4, 12, 14, 12), Block.createCuboidShape(2, 0, 2, 14, 9, 14), BooleanBiFunction.OR);

	@Override
	public VoxelShape getOutlineShape(
			BlockState state, BlockView world, BlockPos pos, ShapeContext context)
	{
		return SHAPE;
	}
}