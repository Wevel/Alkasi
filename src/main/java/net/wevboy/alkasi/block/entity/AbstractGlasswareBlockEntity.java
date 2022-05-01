package net.wevboy.alkasi.block.entity;

import java.util.Optional;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.wevboy.alkasi.Alkasi;
import net.wevboy.alkasi.block.custom.AbstractGlasswareBlock;
import net.wevboy.alkasi.item.inventory.ImplementedInventory;
import net.wevboy.alkasi.recipe.GlasswareRecipe;
import net.wevboy.alkasi.utility.TemperatureUtility;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGlasswareBlockEntity extends BlockEntity implements ImplementedInventory
{
	private static final int HEATER_RANGE = 4;

	// Should take about 20s to get to 100C when campfire placed directly below
	private static final double SECONDS_PER_TICK = 0.05;
	private static final double TEMPERATURE_RATE_OF_CHANGE_SCALE = SECONDS_PER_TICK * 0.06;

	// TODO: Implement fluid input, output, and use in recipes
	//private static final int FLUID_INPUT_SLOT = 0;
	private static final int ITEM_INPUT_SLOT = 1;
	//private static final int FLUID_OUTPUT_SLOT = 2;
	private static final int ITEM_OUTPUT_SLOT = 3;
	private static final int[] TOP_SLOTS = new int[] { ITEM_INPUT_SLOT };
	private static final int[] BOTTOM_SLOTS = new int[] { ITEM_OUTPUT_SLOT };
	private static final int[] SIDE_SLOTS = new int[] { ITEM_INPUT_SLOT };

	protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

	private double heaterTemperature = TemperatureUtility.DEFAULT_TEMPERATURE;
	private double temperature = TemperatureUtility.DEFAULT_TEMPERATURE;
	private int progress = 0;
	private int maxProgress = 100;

	protected final PropertyDelegate propertyDelegate = new PropertyDelegate()
	{
		public int get(int index)
		{
			switch (index)
			{
				case 0:
					return (int) TemperatureUtility.ClampTemperature(AbstractGlasswareBlockEntity.this.temperature);
				case 1:
					return AbstractGlasswareBlockEntity.this.progress;
				case 2:
					return AbstractGlasswareBlockEntity.this.maxProgress;
				default:
					return 0;
			}
		}

		public void set(int index, int value)
		{
			switch (index)
			{
				case 0:
					AbstractGlasswareBlockEntity.this.temperature = value;
					break;
				case 1:
					AbstractGlasswareBlockEntity.this.progress = value;
					break;
				case 2:
					AbstractGlasswareBlockEntity.this.maxProgress = value;
					break;
			}
		}

		public int size()
		{
			return 3;
		}
	};

	private final RecipeType<? extends GlasswareRecipe> recipeType;

	protected AbstractGlasswareBlockEntity(
			BlockEntityType<?> blockEntityType,
			BlockPos pos,
			BlockState state,
			RecipeType<? extends GlasswareRecipe> recipeType)
	{
		super(blockEntityType, pos, state);
		this.recipeType = recipeType;
	}

	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.readNbt(nbt, this.inventory);
		this.temperature = nbt.getDouble("Temperature");
		this.progress = nbt.getInt("Progress");
		this.maxProgress = nbt.getInt("MaxProgress");
	}

	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, this.inventory);
		nbt.putDouble("Temperature", this.temperature);
		nbt.putInt("Progress", this.progress);
		nbt.putInt("MaxProgress", this.maxProgress);
	}

	public static void tick(World world, BlockPos pos, BlockState state, AbstractGlasswareBlockEntity blockEntity)
	{
		boolean oldHasFluid = hasFluid(blockEntity);
		boolean oldIsBoiling = isBoiling(blockEntity);
		boolean oldHasLid = hasLid(blockEntity);

		blockEntity.heaterTemperature = getHeatingTemperature(world, pos);
		updateTemperature(blockEntity);

		if (hasRecipe(blockEntity))
		{
			if (isBoiling(blockEntity))
			{
				blockEntity.progress++;
				if (blockEntity.progress > blockEntity.maxProgress) craftItem(blockEntity);
			}
		}
		else
		{
			resetProgress(blockEntity);
		}

		boolean newHasFluid = hasFluid(blockEntity);
		boolean newIsBoiling = isBoiling(blockEntity);
		boolean newHasLid = hasLid(blockEntity);
		if (oldHasFluid != newHasFluid || oldIsBoiling != newIsBoiling || oldHasLid != newHasLid)
		{
			state = state.with(AbstractGlasswareBlock.HAS_FLUID, newHasFluid)
						 .with(AbstractGlasswareBlock.BOILING, newIsBoiling)
						 .with(AbstractGlasswareBlock.HAS_LID, newHasLid);
			world.setBlockState(pos, state, Block.NOTIFY_ALL);
		}
	}

	private static SimpleInventory getRecipeInventory(AbstractGlasswareBlockEntity blockEntity)
	{
		SimpleInventory inventory = new SimpleInventory(2);
		inventory.setStack(0, blockEntity.getStack(ITEM_INPUT_SLOT));
		//inventory.setStack(1, blockEntity.getStack(ITEM_INPUT_SLOT));
		return inventory;
	}

	private static Optional<? extends GlasswareRecipe> findValidRecipe(AbstractGlasswareBlockEntity blockEntity)
	{
		World world = blockEntity.world;
		assert world != null;

		SimpleInventory inventory = getRecipeInventory(blockEntity);
		return world.getRecipeManager().getFirstMatch(blockEntity.recipeType, inventory, world);
	}

	private static boolean hasRecipe(AbstractGlasswareBlockEntity blockEntity)
	{
		Optional<? extends GlasswareRecipe> recipe = findValidRecipe(blockEntity);
		return recipe.isPresent() && canInsertIntoOutputSlot(blockEntity, recipe.get().getOutput());
	}

	private static void craftItem(AbstractGlasswareBlockEntity blockEntity)
	{
		Optional<? extends GlasswareRecipe> recipe = findValidRecipe(blockEntity);
		if (recipe.isPresent())
		{
			//ItemStack fluidInputSlot = blockEntity.getStack(FLUID_INPUT_SLOT);
			ItemStack itemInputSlot = blockEntity.getStack(ITEM_INPUT_SLOT);

			//ItemStack fluidOutputSlot = blockEntity.getStack(FLUID_OUTPUT_SLOT);
			ItemStack itemOutputSlot = blockEntity.getStack(ITEM_OUTPUT_SLOT);

			//ItemStack recipeFluidOutput = match.get().getOutput();
			ItemStack recipeItemOutput = recipe.get().getOutput();

			//blockEntity.removeStack(FLUID_INPUT_SLOT, 1);
			blockEntity.removeStack(ITEM_INPUT_SLOT, 1);

			//if (fluidOutputSlot.isEmpty()) blockEntity.setStack(FLUID_OUTPUT_SLOT, recipeFluidOutput.copy());
			//else if (fluidOutputSlot.isOf(recipeFluidOutput.getItem())) fluidOutputSlot.increment(1);

			if (itemOutputSlot.isEmpty())
				blockEntity.setStack(ITEM_OUTPUT_SLOT, new ItemStack(recipeItemOutput.getItem(), 1));
			else if (itemOutputSlot.isOf(recipeItemOutput.getItem())) itemOutputSlot.increment(1);

			resetProgress(blockEntity);
		}
	}

	private static boolean canInsertIntoOutputSlot(AbstractGlasswareBlockEntity blockEntity, ItemStack output)
	{
		if (blockEntity.getStack(ITEM_OUTPUT_SLOT).isEmpty()) return output.getCount() <= output.getMaxCount();
		else if (blockEntity.getStack(ITEM_OUTPUT_SLOT).getItem() == output.getItem())
			return blockEntity.getStack(ITEM_OUTPUT_SLOT).getCount() + output.getCount() <= output.getMaxCount();
		else return false;
	}

	private static void resetProgress(AbstractGlasswareBlockEntity blockEntity)
	{
		blockEntity.progress = 0;
	}

	public static boolean isRoomTemperature(AbstractGlasswareBlockEntity blockEntity)
	{
		return blockEntity.temperature >= TemperatureUtility.TEMPERATURE_AMBIENT;
	}

	public static boolean isWarm(AbstractGlasswareBlockEntity blockEntity)
	{
		return blockEntity.temperature >= TemperatureUtility.TEMPERATURE_WARM;
	}

	public static boolean isBoiling(AbstractGlasswareBlockEntity blockEntity)
	{
		return blockEntity.temperature >= TemperatureUtility.TEMPERATURE_WATER_BOILING;
	}

	public static boolean hasFluid(AbstractGlasswareBlockEntity blockEntity)
	{
		return false;
	}

	public static boolean hasLid(AbstractGlasswareBlockEntity blockEntity)
	{
		return false;
	}

	private static double getHeatingTemperature(World world, BlockPos pos)
	{
		BlockPos blockPos;
		BlockState blockState;
		for (int i = 1; i <= HEATER_RANGE; ++i)
		{
			blockPos = pos.down(i);
			blockState = world.getBlockState(blockPos);
			if (CampfireBlock.isLitCampfire(blockState))
				return TemperatureUtility.CalculateHeaterTemperature(TemperatureUtility.HEATER_TEMPERATURE_CAMPFIRE, i);
		}

		return TemperatureUtility.TEMPERATURE_AMBIENT;
	}

	private static void updateTemperature(AbstractGlasswareBlockEntity blockEntity)
	{
		double delta = blockEntity.heaterTemperature - blockEntity.temperature;
		blockEntity.temperature += delta * TEMPERATURE_RATE_OF_CHANGE_SCALE;
		blockEntity.temperature = TemperatureUtility.ClampTemperature(blockEntity.temperature);
	}

	@Override
	public int[] getAvailableSlots(Direction side)
	{
		if (side == Direction.DOWN) return BOTTOM_SLOTS;
		else if (side == Direction.UP) return TOP_SLOTS;
		else return SIDE_SLOTS;
	}

	@Override
	public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir)
	{
		return this.isValid(slot, stack);
	}

	@Override
	public boolean canExtract(int slot, ItemStack stack, Direction dir)
	{
		return slot == ITEM_OUTPUT_SLOT;
	}

	@Override
	public boolean canPlayerUse(PlayerEntity player)
	{
		assert this.world != null;
		if (this.world.getBlockEntity(this.pos) != this) return false;
		return player.squaredDistanceTo((double) this.pos.getX() + 0.5,
				(double) this.pos.getY() + 0.5,
				(double) this.pos.getZ() + 0.5) <= 64.0;
	}

	@Override
	public boolean isValid(int slot, ItemStack stack)
	{
		// TODO: Only allow valid items into the input
		return slot == ITEM_INPUT_SLOT;
		//		if (slot == ITEM || slot == 2 || slot == 3) return false;
		//		else if (slot == ITEM_INPUT_SLOT)
		//		{
		//			// TODO: Only allow valid items into the first slot
		//
		//			//ItemStack itemStack = this.inventory.get(1);
		//			//return AbstractFurnaceBlockEntity.canUseAsFuel(stack) || stack.isOf(Items.BUCKET) && !itemStack.isOf(Items.BUCKET);
		//
		//			return true;
		//		}
		//
		//		return false;
	}

	public boolean TryInsertItem(ItemStack itemStack, Direction dir)
	{
		if (this.canInsert(ITEM_INPUT_SLOT, itemStack, dir))
		{
			ItemStack itemInputSlot = this.inventory.get(ITEM_INPUT_SLOT);
			if (itemInputSlot.isEmpty()) this.inventory.set(ITEM_OUTPUT_SLOT, new ItemStack(itemStack.getItem(), 1));
			else if (itemInputSlot.isOf(itemStack.getItem())) itemInputSlot.increment(1);
			else return false;

			itemStack.decrement(1);

			return true;
		}

		return false;
	}

	@Override
	public DefaultedList<ItemStack> getItems()
	{
		return inventory;
	}
}

