package slimeknights.mantle.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public interface TickableBlockEntity {
  void tick();

  static void tickBlockEntity(Level world, BlockPos pos, BlockState state, BlockEntity be) {
    if(be instanceof TickableBlockEntity tickableBE) {
      tickableBE.tick();
    }
  };
}

