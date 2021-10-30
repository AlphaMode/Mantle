package slimeknights.mantle.tileentity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.util.LazyLoadedValue;
import net.minecraftforge.client.model.data.IModelData;
import slimeknights.mantle.util.RetexturedHelper;

/**
 * Minimal implementation for {@link IRetexturedTileEntity}, use alongside {@link slimeknights.mantle.block.RetexturedBlock} and {@link slimeknights.mantle.item.RetexturedBlockItem}
 */
public class RetexturedTileEntity extends MantleTileEntity implements IRetexturedTileEntity {

  /** Lazy value of model data as it will not change after first fetch */
  private final LazyLoadedValue<IModelData> data = new LazyLoadedValue<>(this::getRetexturedModelData);
  public RetexturedTileEntity(BlockEntityType<?> type) {
    super(type);
  }

  @Override
  public IModelData getModelData() {
    return data.get();
  }

  @Override
  protected boolean shouldSyncOnUpdate() {
    return true;
  }

  @Override
  public void load(CompoundTag tags) {
    String oldName = getTextureName();
    super.load(tags);
    String newName = getTextureName();
    // if the texture name changed, mark the position for rerender
    if (!oldName.equals(newName) && level != null && level.isClientSide) {
      data.get().setData(RetexturedHelper.BLOCK_PROPERTY, getTexture());
      requestModelDataUpdate();
      level.sendBlockUpdated(worldPosition, blockState, blockState, 0);
    }
  }
}
