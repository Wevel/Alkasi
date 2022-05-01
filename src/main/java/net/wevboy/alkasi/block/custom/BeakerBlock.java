package net.wevboy.alkasi.block.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.wevboy.alkasi.block.entity.BeakerBlockEntity;
import net.wevboy.alkasi.block.entity.ModBlockEntities;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class BeakerBlock extends AbstractGlasswareBlock
{
	public BeakerBlock(Settings settings)
	{
		super(settings);
	}

	@Nullable
	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state)
	{
		return new BeakerBlockEntity(pos, state);
	}

	@Override
	@Nullable
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
			World world, BlockState state, BlockEntityType<T> type)
	{
		return checkType(world, type, ModBlockEntities.BEAKER_GLASS);
	}

	@Override
	public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random)
	{
		if (state.get(TEMPERATURE) > 90) return;

		double d = (double) pos.getX() + 0.5;
		double e = pos.getY();
		double f = (double) pos.getZ() + 0.5;

		// TODO: Play bubbling sounds
		//if (random.nextDouble() < 0.1)
		//	world.playSound(d, e, f, SoundEvents.BLOCK_FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0f, 1.0f, false);

		double i = random.nextDouble() * 0.6 - 0.3;
		double j = random.nextDouble() * 6.0 / 16.0;
		double k = random.nextDouble() * 0.6 - 0.3;
		world.addParticle(ParticleTypes.SMOKE, d + i, e + j, f + k, 0.0, 0.0, 0.0);
	}
}
