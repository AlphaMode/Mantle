package slimeknights.mantle.recipe;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

import net.minecraftforge.registries.ForgeRegistryEntry;

/**
 * This class simply exists because every recipe serializer has to extend the forge registry entry and implement the interface. Easier to just extend this class
 * @param <T> Recipe type
 */
public abstract class AbstractRecipeSerializer<T extends Recipe<?>> extends ForgeRegistryEntry<AbstractRecipeSerializer<?>> implements RecipeSerializer<T> {}
