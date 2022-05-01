package net.wevboy.alkasi.block.custom;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.wevboy.alkasi.block.entity.AbstractGlasswareBlockEntity;
import net.wevboy.alkasi.state.property.ModProperties;
import net.wevboy.alkasi.utility.TemperatureUtility;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGlasswareBlock extends BlockWithEntity implements BlockEntityProvider
{
	public static final BooleanProperty HAS_FLUID = ModProperties.HAS_FLUID;
	public static final BooleanProperty BOILING = ModProperties.BOILING;
	public static final BooleanProperty HAS_LID = ModProperties.HAS_LID;

	protected AbstractGlasswareBlock(AbstractBlock.Settings settings)
	{
		super(settings);
		this.setDefaultState(this.stateManager.getDefaultState()
											  .with(HAS_FLUID, false)
											  .with(BOILING, false)
											  .with(HAS_LID, false));
	}

	@Override
	public ActionResult onUse(
			BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit)
	{
		if (world.isClient) return ActionResult.SUCCESS;

		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof AbstractGlasswareBlockEntity)
		{
			//this.openScreen(world, pos, player);
			// TODO: Add using items on block

			ItemStack itemStack = player.getStackInHand(hand);
			//CauldronBehavior cauldronBehavior = this.behaviorMap.get(itemStack.getItem());
			//return cauldronBehavior.interact(state, world, pos, player, hand, itemStack);
			if (itemStack != null)
			{
				if (((AbstractGlasswareBlockEntity) blockEntity).TryInsertItem(itemStack, Direction.UP))
					return ActionResult.CONSUME;
			}
		}

		return ActionResult.CONSUME;
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack itemStack)
	{
		// TODO: Change display to connect to adjacent blocks
	}

	@Override
	public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved)
	{
		if (state.isOf(newState.getBlock())) return;

		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (blockEntity instanceof AbstractGlasswareBlockEntity)
		{
			if (world instanceof ServerWorld) ItemScatterer.spawn(world, pos, (Inventory) blockEntity);

			world.updateComparators(pos, this);
		}

		super.onStateReplaced(state, world, pos, newState, moved);
	}

	@Override
	public boolean hasComparatorOutput(BlockState state)
	{
		return true;
	}

	@Override
	public int getComparatorOutput(BlockState state, World world, BlockPos pos)
	{
		return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
	}

	@Override
	public BlockRenderType getRenderType(BlockState state)
	{
		return BlockRenderType.MODEL;
	}

	@Override
	protected void appendProperties(StateManager.Builder<Block, BlockState> builder)
	{
		builder.add(HAS_FLUID, BOILING, HAS_LID);
	}

	@Nullable
	protected static <T extends BlockEntity> BlockEntityTicker<T> checkType(
			World world,
			BlockEntityType<T> givenType,
			BlockEntityType<? extends AbstractGlasswareBlockEntity> expectedType)
	{
		return world.isClient ? null : AbstractGlasswareBlock.checkType(givenType,
				expectedType,
				AbstractGlasswareBlockEntity::tick);
	}
}

