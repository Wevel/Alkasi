package net.wevboy.alkasi.recipe;

import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.wevboy.alkasi.Alkasi;

public class ModRecipes
{
	public static final RecipeType<GlasswareRecipe> GLASSWARE = RecipeType.register("glassware");

	public static void RegisterRecipes()
	{
		Registry.register(
				Registry.RECIPE_SERIALIZER,
				new Identifier(Alkasi.MOD_ID, GlasswareRecipe.Serializer.ID),
				GlasswareRecipe.Serializer.INSTANCE);
		Registry.register(
				Registry.RECIPE_TYPE,
				new Identifier(Alkasi.MOD_ID, GlasswareRecipe.Type.ID),
				GlasswareRecipe.Type.INSTANCE);
	}
}
