package slimeknights.mantle.loot;

import com.google.gson.JsonObject;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.util.GsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import slimeknights.mantle.recipe.RecipeHelper;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/** Loot modifier to replace an item with another */
public class ReplaceItemLootModifier extends LootModifier {
  private final Item original;
  private final Item replacement;
  protected ReplaceItemLootModifier(LootItemCondition[] conditionsIn, Item original, Item replacement) {
    super(conditionsIn);
    this.original = original;
    this.replacement = replacement;
  }

  @Nonnull
  @Override
  protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
    return generatedLoot.stream().map(stack -> {
      if (stack.getItem() == original) {
        return new ItemStack(replacement, stack.getCount());
      }
      return stack;
    }).collect(Collectors.toList());
  }

  public static class Serializer extends GlobalLootModifierSerializer<ReplaceItemLootModifier> {
    @Override
    public ReplaceItemLootModifier read(ResourceLocation location, JsonObject object, LootItemCondition[] conditions) {
      Item original = RecipeHelper.deserializeItem(GsonHelper.getAsString(object, "original"), "original", Item.class);
      Item replacement = RecipeHelper.deserializeItem(GsonHelper.getAsString(object, "replacement"), "replacement", Item.class);
      return new ReplaceItemLootModifier(conditions, original, replacement);
    }

    @Override
    public JsonObject write(ReplaceItemLootModifier instance) {
      JsonObject object = makeConditions(instance.conditions);
      object.addProperty("original", Objects.requireNonNull(instance.original.getRegistryName()).toString());
      object.addProperty("replacement", Objects.requireNonNull(instance.replacement.getRegistryName()).toString());
      return object;
    }
  }
}
