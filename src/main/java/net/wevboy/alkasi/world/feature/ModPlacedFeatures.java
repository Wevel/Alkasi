package net.wevboy.alkasi.world.feature;

import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;

public class ModPlacedFeatures
{
	// ModifiersWithCount: Veins per chunk
	// HeightRangePlacementModifier.trapezoid: Spawns most between min and max
	public static final RegistryEntry<PlacedFeature> CATIUM_ORE_PLACED = PlacedFeatures.register("catium_ore_placed",
			ModConfiguredFeatures.CATIUM_ORE, ModOreFeatures.ModifiersWithCount(7,
					HeightRangePlacementModifier.trapezoid(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))));
}
