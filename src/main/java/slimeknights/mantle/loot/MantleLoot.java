package slimeknights.mantle.loot;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.core.Registry;
import slimeknights.mantle.Mantle;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MantleLoot {
  static LootItemFunctionType RETEXTURED_FUNCTION;

  /**
   * Called during serializer registration to register any relevant loot logic
   */
  public static void register() {
    RETEXTURED_FUNCTION = registerFunction("fill_retextured_block", new RetexturedLootFunction.Serializer());
  }

  /**
   * Registers a loot function
   * @param name        Loot function name
   * @param serializer  Loot function serializer
   * @return  Registered loot function
   */
  private static LootItemFunctionType registerFunction(String name, Serializer<? extends LootItemFunction> serializer) {
    return Registry.register(Registry.LOOT_FUNCTION_TYPE, Mantle.getResource(name), new LootItemFunctionType(serializer));
  }
}
