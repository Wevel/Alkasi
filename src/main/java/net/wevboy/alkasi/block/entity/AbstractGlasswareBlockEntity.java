package net.wevboy.alkasi.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.AbstractCookingRecipe;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeInputProvider;
import net.minecraft.recipe.RecipeMatcher;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.RecipeUnlocker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.wevboy.alkasi.block.custom.AbstractGlasswareBlock;
import net.wevboy.alkasi.item.ModItems;
import net.wevboy.alkasi.item.inventory.ImplementedInventory;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractGlasswareBlockEntity extends BlockEntity implements ImplementedInventory
{
	// TODO: Implement fluid input, output, and use in recipes
	//private static final int FLUID_INPUT_SLOT = 0;
	private static final int ITEM_INPUT_SLOT = 1;
	//private static final int FLUID_OUTPUT_SLOT = 2;
	private static final int ITEM_OUTPUT_SLOT = 3;
	private static final int[] TOP_SLOTS = new int[] { ITEM_INPUT_SLOT };
	private static final int[] BOTTOM_SLOTS = new int[] { ITEM_OUTPUT_SLOT };
	private static final int[] SIDE_SLOTS = new int[] { ITEM_INPUT_SLOT };

	protected DefaultedList<ItemStack> inventory = DefaultedList.ofSize(4, ItemStack.EMPTY);

	private int temperature;
	private int lastNotifyTemperature;
	private int cookTime;
	private int cookTimeTotal;

	private final Object2IntOpenHashMap<Identifier> recipesUsed = new Object2IntOpenHashMap<>();
	private final RecipeType<? extends AbstractCookingRecipe> recipeType;

	protected AbstractGlasswareBlockEntity(
			BlockEntityType<?> blockEntityType,
			BlockPos pos,
			BlockState state,
			RecipeType<? extends AbstractCookingRecipe> recipeType)
	{
		super(blockEntityType, pos, state);
		this.recipeType = recipeType;
		this.temperature = 0;
	}

	public boolean isRoomTemperature()
	{
		return this.temperature >= 20;
	}

	public boolean isWarm()
	{
		return this.temperature >= 50;
	}

	public boolean isBoiling()
	{
		return true;
		//return this.temperature >= 100;
	}

	@Override
	public void readNbt(NbtCompound nbt)
	{
		super.readNbt(nbt);
		this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
		Inventories.readNbt(nbt, this.inventory);
		this.cookTime = nbt.getShort("CookTime");
		this.cookTimeTotal = nbt.getShort("CookTimeTotal");
	}

	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		nbt.putShort("CookTime", (short) this.cookTime);
		nbt.putShort("CookTimeTotal", (short) this.cookTimeTotal);
		Inventories.writeNbt(nbt, this.inventory);
	}

	public static void tick(World world, BlockPos pos, BlockState state, AbstractGlasswareBlockEntity blockEntity)
	{
		ItemStack itemInputSlot = blockEntity.inventory.get(ITEM_INPUT_SLOT);
		ItemStack itemOutputSlot = blockEntity.inventory.get(ITEM_OUTPUT_SLOT);

		if (itemInputSlot != null)
		{
			if (itemOutputSlot.isEmpty())
				blockEntity.inventory.set(ITEM_OUTPUT_SLOT, new ItemStack(ModItems.DUST_IRON, 1));
			else itemOutputSlot.increment(1);

			blockEntity.removeStack(ITEM_INPUT_SLOT, 1);
		}

		//		updateTemperature(blockEntity);
		//		//if (hasRecipe(blockEntity) && hasNotReachedStackLimit(blockEntity)) craftItem(blockEntity, blockEntity.inventory, maxStackSize);
		//
		//		boolean stateChanged = false;
		//		if (blockEntity.isBoiling() && !blockEntity.inventory.get(ITEM_INPUT_SLOT).isEmpty())
		//		{
		//
		//
		////			Recipe<?> recipe = world.getRecipeManager()
		////									.getFirstMatch(blockEntity.recipeType, blockEntity, world)
		////									.orElse(null);
		////			int maxStackSize = blockEntity.getMaxCountPerStack();
		////			if (AbstractGlasswareBlockEntity.canAcceptRecipeOutput(recipe, blockEntity.inventory, maxStackSize))
		////			{
		////				blockEntity.cookTime++;
		////				if (blockEntity.cookTime == blockEntity.cookTimeTotal)
		////				{
		////					blockEntity.cookTime = 0;
		////					blockEntity.cookTimeTotal = AbstractGlasswareBlockEntity.getCookTime(world,
		////							blockEntity.recipeType,
		////							blockEntity);
		////					craftRecipe(recipe, blockEntity.inventory, maxStackSize);
		////					stateChanged = true;
		////				}
		////			}
		////			else
		////			{
		////				blockEntity.cookTime = 0;
		////			}
		//		}
		//
		//		// Notify the block if the state has changed
		//		BlockState newState = state;
		//		if (blockEntity.temperature != blockEntity.lastNotifyTemperature)
		//		{
		//			stateChanged = true;
		//			newState = state.with(AbstractGlasswareBlock.TEMPERATURE, blockEntity.temperature);
		//			world.setBlockState(pos, newState, Block.NOTIFY_ALL);
		//			blockEntity.lastNotifyTemperature = blockEntity.temperature;
		//		}
		//
		//		// Set the state to dirty if something has changed
		//		if (stateChanged) AbstractGlasswareBlockEntity.markDirty(world, pos, newState);
	}

	private static boolean canAcceptRecipeOutput(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count)
	{
		if (slots.get(0).isEmpty() || recipe == null) return false;

		ItemStack itemStack = recipe.getOutput();
		if (itemStack.isEmpty()) return false;

		ItemStack itemStack2 = slots.get(2);
		if (itemStack2.isEmpty()) return true;
		if (!itemStack2.isItemEqualIgnoreDamage(itemStack)) return false;
		if (itemStack2.getCount() < count && itemStack2.getCount() < itemStack2.getMaxCount()) return true;

		return itemStack2.getCount() < itemStack.getMaxCount();
	}

	private static void craftRecipe(@Nullable Recipe<?> recipe, DefaultedList<ItemStack> slots, int count)
	{
		if (recipe == null || !AbstractGlasswareBlockEntity.canAcceptRecipeOutput(recipe, slots, count)) return;

		//ItemStack fluidInputSlot = slots.get(FLUID_INPUT_SLOT);
		ItemStack itemInputSlot = slots.get(ITEM_INPUT_SLOT);

		//ItemStack fluidOutputSlot = slots.get(FLUID_OUTPUT_SLOT);
		ItemStack itemOutputSlot = slots.get(ITEM_OUTPUT_SLOT);

		//ItemStack recipeFluidOutput = recipe.getOutput();
		ItemStack recipeItemOutput = recipe.getOutput();

		//if (fluidOutputSlot.isEmpty()) slots.set(FLUID_OUTPUT_SLOT, recipeFluidOutput.copy());
		//else if (fluidOutputSlot.isOf(recipeFluidOutput.getItem())) fluidOutputSlot.increment(1);

		if (itemOutputSlot.isEmpty()) slots.set(ITEM_OUTPUT_SLOT, recipeItemOutput.copy());
		else if (itemOutputSlot.isOf(recipeItemOutput.getItem())) itemOutputSlot.increment(1);

		//fluidInputSlot.decrement(1);
		itemInputSlot.decrement(1);
	}

	private static int getCookTime(
			World world, RecipeType<? extends AbstractCookingRecipe> recipeType, Inventory inventory)
	{
		return world.getRecipeManager()
					.getFirstMatch(recipeType, inventory, world)
					.map(AbstractCookingRecipe::getCookTime)
					.orElse(200);
	}

	private static void updateTemperature(AbstractGlasswareBlockEntity blockEntity)
	{
		if (blockEntity.temperature == 15) blockEntity.temperature++;
		else blockEntity.temperature = 15;
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
	public void setStack(int slot, ItemStack stack)
	{
		ItemStack itemStack = this.inventory.get(slot);
		boolean bl = !stack.isEmpty() && stack.isItemEqualIgnoreDamage(itemStack) && ItemStack.areNbtEqual(stack,
				itemStack);
		this.inventory.set(slot, stack);
		if (stack.getCount() > this.getMaxCountPerStack()) stack.setCount(this.getMaxCountPerStack());
		if (slot == 0 && !bl)
		{
			assert this.world != null;
			this.cookTimeTotal = AbstractGlasswareBlockEntity.getCookTime(this.world, this.recipeType, this);
			this.cookTime = 0;
			this.markDirty();
		}
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
			if (itemInputSlot.isEmpty()) this.inventory.set(ITEM_OUTPUT_SLOT, itemStack.copy());
			else if (itemInputSlot.isOf(itemStack.getItem())) itemInputSlot.increment(1);
			itemStack.decrement(1);
		}

		return false;
	}

	public void dropAllExperience(ServerWorld world, Vec3d pos)
	{
		for (Object2IntMap.Entry<?> entry : this.recipesUsed.object2IntEntrySet())
		{
			world.getRecipeManager().get((Identifier) entry.getKey()).ifPresent(recipe -> {
				AbstractGlasswareBlockEntity.dropExperience(world,
						pos,
						entry.getIntValue(),
						((AbstractCookingRecipe) recipe).getExperience());
			});
		}
	}

	private static void dropExperience(ServerWorld world, Vec3d pos, int multiplier, float experience)
	{
		int i = MathHelper.floor((float) multiplier * experience);
		float f = MathHelper.fractionalPart((float) multiplier * experience);
		if (f != 0.0f && Math.random() < (double) f) i++;
		ExperienceOrbEntity.spawn(world, pos, i);
	}

	@Override
	public DefaultedList<ItemStack> getItems()
	{
		return inventory;
	}
}

