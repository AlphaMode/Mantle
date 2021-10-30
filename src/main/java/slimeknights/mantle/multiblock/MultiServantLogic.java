package slimeknights.mantle.multiblock;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import slimeknights.mantle.tileentity.MantleTileEntity;

import java.util.Objects;

/**
 * @deprecated  Slated for removal in 1.17. If you used this, talk to one of the devs and we can pull the updated verson from Tinkers Construct back
 */
@Deprecated
public class MultiServantLogic extends MantleTileEntity implements IServantLogic {

  boolean hasMaster;
  BlockPos master;
  Block masterBlock;
  BlockState state;

  public MultiServantLogic(BlockEntityType<?> blockEntityTypeIn, BlockPos pos, BlockState state) {
    super(blockEntityTypeIn, pos, state);
  }

  public boolean canUpdate() {
    return false;
  }

  public boolean getHasMaster() {
    return this.hasMaster;
  }

  public boolean hasValidMaster() {
    if (!this.hasMaster) {
      return false;
    }

    assert this.level != null;
    if (this.level.getBlockState(this.master).getBlock() == this.masterBlock && this.level.getBlockState(this.master) == this.state) {
      return true;
    }
    else {
      this.hasMaster = false;
      this.master = null;
      return false;
    }
  }

  @Override
  public BlockPos getMasterPosition() {
    return this.master;
  }

  public void overrideMaster(BlockPos pos) {
    assert this.level != null;
    this.hasMaster = true;
    this.master = pos;
    this.state = this.level.getBlockState(this.master);
    this.masterBlock = this.state.getBlock();
    this.markDirtyFast();
  }

  public void removeMaster() {
    this.hasMaster = false;
    this.master = null;
    this.masterBlock = null;
    this.state = null;
    this.markDirtyFast();
  }

  @Override
  public boolean setPotentialMaster(IMasterLogic master, Level w, BlockPos pos) {
    return !this.hasMaster;
  }

  @Deprecated
  public boolean verifyMaster(IMasterLogic logic, BlockPos pos) {
    assert this.level != null;
    return this.master.equals(pos) && this.level.getBlockState(pos) == this.state
           && this.level.getBlockState(pos).getBlock() == this.masterBlock;
  }

  @Override
  public boolean verifyMaster(IMasterLogic logic, Level world, BlockPos pos) {
    if (this.hasMaster) {
      return this.hasValidMaster();
    }
    else {
      this.overrideMaster(pos);
      return true;
    }
  }

  @Override
  public void invalidateMaster(IMasterLogic master, Level w, BlockPos pos) {
    this.removeMaster();
  }

  @Override
  public void notifyMasterOfChange() {
    if (this.hasValidMaster()) {
      assert this.level != null;
      IMasterLogic logic = (IMasterLogic) this.level.getBlockEntity(this.master);
      logic.notifyChange(this, this.worldPosition);
    }
  }

  public void readCustomNBT(CompoundTag tags) {
    this.hasMaster = tags.getBoolean("hasMaster");
    if (this.hasMaster) {
      int xCenter = tags.getInt("xCenter");
      int yCenter = tags.getInt("yCenter");
      int zCenter = tags.getInt("zCenter");
      this.master = new BlockPos(xCenter, yCenter, zCenter);
      this.masterBlock = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(tags.getString("MasterBlockName")));
      this.state = Block.stateById(tags.getInt("masterState"));
    }
  }

  public CompoundTag writeCustomNBT(CompoundTag tags) {
    tags.putBoolean("hasMaster", this.hasMaster);
    if (this.hasMaster) {
      tags.putInt("xCenter", this.master.getX());
      tags.putInt("yCenter", this.master.getY());
      tags.putInt("zCenter", this.master.getZ());
      tags.putString("MasterBlockName", Objects.requireNonNull(this.masterBlock.getRegistryName()).toString());
      tags.putInt("masterState", Block.getId(this.state));
    }
    return tags;
  }

  @Override
  public void load(CompoundTag tags) {
    super.load(tags);
    this.readCustomNBT(tags);
  }

  @Override
  public CompoundTag save(CompoundTag tags) {
    tags = super.save(tags);
    return this.writeCustomNBT(tags);
  }

  /* Packets */
  @Override
  public CompoundTag getUpdateTag() {
    CompoundTag tag = new CompoundTag();
    this.writeCustomNBT(tag);
    return tag;
  }

  @Override
  public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket packet) {
    this.readCustomNBT(packet.getTag());
    //this.world.notifyLightSet(this.pos);
    assert level != null;
    BlockState state = level.getBlockState(this.worldPosition);
    this.level.sendBlockUpdated(this.worldPosition, state, state, 3);
  }

  @Deprecated
  public boolean setMaster(BlockPos pos) {
    assert this.level != null;
    if (!this.hasMaster || this.level.getBlockState(this.master) != this.state || (this.level.getBlockState(this.master).getBlock() != this.masterBlock)) {
      this.overrideMaster(pos);
      return true;
    }
    else {
      return false;
    }
  }

}
