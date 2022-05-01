package net.wevboy.alkasi.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.wevboy.alkasi.block.entity.JarPlainBlockEntity;
import net.wevboy.alkasi.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

public class JarPlainBlock extends BlockWithEntity implements BlockEntityProvider
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

	@Override
	public ActionResult onUse(
			BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (!world.isClient)
		{
			NamedScreenHandlerFactory screenHandlerFactory =  state.createScreenHandlerFactory(world, pos);

			if (screenHandlerFactory != null) {
				player.openHandledScreen(screenHandlerFactory);
			}
		}
		return ActionResult.SUCCESS;
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
	{
		if (state.getBlock() != newState.getBlock())
		{
			BlockEntity blockEntity = world.getBlockEntity(pos);
			if (blockEntity instanceof  JarPlainBlockEntity)
			{
				ItemScatterer.spawn(world, pos, (JarPlainBlockEntity)blockEntity);
				world.updateComparators(pos, this);
			}
			super.onStateReplaced(state, world, pos, newState, moved);
		}
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new JarPlainBlockEntity(pos, state);
	}

	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type)
	{
        return checkType(type, ModBlockEntities.JAR_PLAIN, JarPlainBlockEntity::tick);
    }
}