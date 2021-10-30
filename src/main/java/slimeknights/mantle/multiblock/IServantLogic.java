package slimeknights.mantle.multiblock;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

/**
 * @deprecated  Slated for removal in 1.17. If you used this, talk to one of the devs and we can pull the updated verson from Tinkers Construct back
 */
@Deprecated
public interface IServantLogic {

  BlockPos getMasterPosition();

  /** The block should already have a valid master */
  void notifyMasterOfChange();

  /**
   * Checks if this block can be tied to this master
   *
   * @param world the world of master
   * @param pos   position of master
   * @return whether the servant can be tied to this master
   */

  boolean setPotentialMaster(IMasterLogic master, Level world, BlockPos pos);

  /**
   * Used to set and verify that this is the block's master
   *
   * @param pos position of master
   * @return Is this block tied to this master?
   */

  boolean verifyMaster(IMasterLogic master, Level world, BlockPos pos);

  /**
   * Exactly what it says on the tin
   *
   * @param pos position of master
   */

  void invalidateMaster(IMasterLogic master, Level world, BlockPos pos);
}
