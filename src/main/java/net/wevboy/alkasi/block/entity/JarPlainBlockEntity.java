package net.wevboy.alkasi.block.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wevboy.alkasi.block.custom.JarPlainBlock;
import net.wevboy.alkasi.item.ModItems;
import net.wevboy.alkasi.item.inventory.ImplementedInventory;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.CallbackI;
import oshi.driver.unix.aix.perfstat.PerfstatNetInterface;

public class JarPlainBlockEntity extends BlockEntity implements NamedScreenHandlerFactory, ImplementedInventory
{
	private final DefaultedList<ItemStack> inventory =
			DefaultedList.ofSize(4, ItemStack.EMPTY);

	public JarPlainBlockEntity(
			BlockPos pos, BlockState state)
	{
		super(ModBlockEntities.JAR_PLAIN, pos, state);
	}

	@Override
	public Text getDisplayName()
	{
		return new LiteralText("Jar");
	}

	@Nullable
	@Override
	public ScreenHandler createMenu(
			int syncId, PlayerInventory inv, PlayerEntity player)
	{
		return null;
	}

	@Override
	public DefaultedList<ItemStack> getItems()
	{
		return inventory;
	}

	@Override
	protected void writeNbt(NbtCompound nbt)
	{
		super.writeNbt(nbt);
		Inventories.writeNbt(nbt, inventory);
	}

	@Override
	public void readNbt(NbtCompound nbt)
	{
		Inventories.readNbt(nbt, inventory);
		super.readNbt(nbt);
	}

	public static void tick(World world, BlockPos pos, BlockState state, JarPlainBlockEntity entity)
	{
		if (hasRecipe(entity) && hasNotReachedStackLimit(entity))
		{
			craftItem(entity);
		}
	}

	private static void craftItem(JarPlainBlockEntity entity)
	{
		entity.removeStack(0, 1);
		entity.removeStack(1, 1);
		entity.removeStack(2, 1);

		entity.setStack(3, new ItemStack(ModItems.DUST_IRON, entity.getStack(3).getCount() + 1));
	}

	private static boolean hasRecipe(JarPlainBlockEntity entity)
	{
		boolean hasItemInFirstSlot = entity.getStack(0).getItem() == Items.RAW_IRON;
		boolean hasItemInSecondSlot = entity.getStack(1).getItem() == Items.REDSTONE;
		boolean hasItemInThirdSlot = entity.getStack(2).getItem() == Items.LAPIS_LAZULI;

		return hasItemInFirstSlot && hasItemInSecondSlot && hasItemInThirdSlot;
	}

	private static boolean hasNotReachedStackLimit(JarPlainBlockEntity entity)
	{
		return entity.getStack(3).getCount() < entity.getStack(3).getMaxCount();
	}
}
