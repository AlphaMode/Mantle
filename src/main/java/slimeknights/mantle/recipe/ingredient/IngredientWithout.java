package slimeknights.mantle.recipe.ingredient;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntComparators;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import slimeknights.mantle.Mantle;
import slimeknights.mantle.util.JsonHelper;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.stream.Stream;

/**
 * Ingredient that matches everything from another ingredient, without a second
 */
public class IngredientWithout extends Ingredient {
  public static final ResourceLocation ID = Mantle.getResource("without");
  public static final IIngredientSerializer<IngredientWithout> SERIALIZER = new Serializer();

  private final Ingredient base;
  private final Ingredient without;
  private ItemStack[] filteredMatchingStacks;
  private IntList packedMatchingStacks;
  public IngredientWithout(Ingredient base, Ingredient without) {
    super(Stream.empty());
    this.base = base;
    this.without = without;
  }

  @Override
  public boolean test(@Nullable ItemStack stack) {
    if (stack == null || stack.isEmpty()) {
      return false;
    }
    return base.test(stack) && !without.test(stack);
  }

  @Override
  public ItemStack[] getItems() {
    if (this.filteredMatchingStacks == null) {
      this.filteredMatchingStacks = Arrays.stream(base.getItems())
                                          .filter(stack -> !without.test(stack))
                                          .toArray(ItemStack[]::new);
    }
    return filteredMatchingStacks;
  }

  @Override
  public boolean isEmpty() {
    return getItems().length == 0;
  }

  @Override
  public boolean isSimple() {
    return base.isSimple() && without.isSimple();
  }

  @Override
  protected void invalidate() {
    super.invalidate();
    this.filteredMatchingStacks = null;
    this.packedMatchingStacks = null;
  }

  @Override
  public IntList getStackingIds() {
    if (this.packedMatchingStacks == null) {
      ItemStack[] matchingStacks = getItems();
      this.packedMatchingStacks = new IntArrayList(matchingStacks.length);
      for(ItemStack stack : matchingStacks) {
        this.packedMatchingStacks.add(StackedContents.getStackingIndex(stack));
      }
      this.packedMatchingStacks.sort(IntComparators.NATURAL_COMPARATOR);
    }
    return packedMatchingStacks;
  }

  @Override
  public JsonElement toJson() {
    JsonObject json = new JsonObject();
    json.addProperty("type", ID.toString());
    json.add("base", base.toJson());
    json.add("without", without.toJson());
    return json;
  }

  @Override
  public IIngredientSerializer<IngredientWithout> getSerializer() {
    return SERIALIZER;
  }

  private static class Serializer implements IIngredientSerializer<IngredientWithout> {
    @Override
    public IngredientWithout parse(JsonObject json) {
      Ingredient base = Ingredient.fromJson(JsonHelper.getElement(json, "base"));
      Ingredient without = Ingredient.fromJson(JsonHelper.getElement(json, "without"));
      return new IngredientWithout(base, without);
    }

    @Override
    public IngredientWithout parse(FriendlyByteBuf buffer) {
      Ingredient base = Ingredient.fromNetwork(buffer);
      Ingredient without = Ingredient.fromNetwork(buffer);
      return new IngredientWithout(base, without);
    }

    @Override
    public void write(FriendlyByteBuf buffer, IngredientWithout ingredient) {
      CraftingHelper.write(buffer, ingredient.base);
      CraftingHelper.write(buffer, ingredient.without);
    }
  }
}
