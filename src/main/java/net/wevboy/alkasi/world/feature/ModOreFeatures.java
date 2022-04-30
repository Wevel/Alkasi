package net.wevboy.alkasi.world.feature;

import net.minecraft.world.gen.placementmodifier.*;

import java.util.List;

public class ModOreFeatures
{
	public static List<PlacementModifier> Modifiers(PlacementModifier countModifier, PlacementModifier heightModifier) {
		return List.of(countModifier, SquarePlacementModifier.of(), heightModifier, BiomePlacementModifier.of());
	}

	public static List<PlacementModifier> ModifiersWithCount(int count, PlacementModifier heightModifier) {
		return Modifiers(CountPlacementModifier.of(count), heightModifier);
	}

	public static List<PlacementModifier> ModifiersWithRarity(int chance, PlacementModifier heightModifier) {
		return Modifiers(RarityFilterPlacementModifier.of(chance), heightModifier);
	}
}
