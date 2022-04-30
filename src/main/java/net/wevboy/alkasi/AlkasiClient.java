package net.wevboy.alkasi;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.minecraft.client.render.RenderLayer;
import net.wevboy.alkasi.block.ModBlocks;

public class AlkasiClient implements ClientModInitializer
{
	@Override
	public void onInitializeClient()
	{
		BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.JAR_PLAIN, RenderLayer.getTranslucent());
	}
}
