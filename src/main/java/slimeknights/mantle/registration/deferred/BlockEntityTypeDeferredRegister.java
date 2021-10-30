package slimeknights.mantle.registration.deferred;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.types.Type;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.Util;
import net.minecraft.util.datafix.fixes.References;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.registration.object.EnumObject;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Deferred register to register block entity instances
 */
public class BlockEntityTypeDeferredRegister extends DeferredRegisterWrapper<BlockEntityType<?>> {
  public BlockEntityTypeDeferredRegister(String modID) {
    super(ForgeRegistries.BLOCK_ENTITIES, modID);
  }

  /**
   * Gets the data fixer type for the block entity instance
   * @param name  Block entity name
   * @return  Data fixer type
   */
  @Nullable
  private Type<?> getType(String name) {
    return Util.fetchChoiceType(References.BLOCK_ENTITY, resourceName(name));
  }

  /**
   * Registers a block entity type for a single block
   * @param name     Block entity name
   * @param factory  Block entity factory
   * @param block    Single block to add
   * @param <T>      Block entity type
   * @return  Registry object instance
   */
  @SuppressWarnings("ConstantConditions")
  public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory, Supplier<? extends Block> block) {
    return register.register(name, () ->  BlockEntityType.Builder.<T>of(factory, block.get()).build(getType(name)));
  }

  /**
   * Registers a new block entity type using a block entity factory and a block supplier
   * @param name     Block entity name
   * @param factory  Block entity factory
   * @param blocks   Enum object
   * @param <T>      Block entity type
   * @return  Block entity type registry object
   */
  @SuppressWarnings("ConstantConditions")
  public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory, EnumObject<?, ? extends Block> blocks) {
    return register.register(name, () ->  new BlockEntityType<>(factory, ImmutableSet.copyOf(blocks.values()), getType(name)));
  }

  /**
   * Registers a new block entity type using a block entity factory and a block supplier
   * @param name             Block entity name
   * @param factory          Block entity factory
   * @param blockCollector   Function to get block list
   * @param <T>              Block entity type
   * @return  Block entity type registry object
   */
  @SuppressWarnings("ConstantConditions")
  public <T extends BlockEntity> RegistryObject<BlockEntityType<T>> register(String name, BlockEntityType.BlockEntitySupplier<? extends T> factory, Consumer<ImmutableSet.Builder<Block>> blockCollector) {
    return register.register(name, () ->  {
      ImmutableSet.Builder<Block> blocks = new ImmutableSet.Builder<>();
      blockCollector.accept(blocks);
      return new BlockEntityType<>(factory, blocks.build(), getType(name));
    });
  }
}
