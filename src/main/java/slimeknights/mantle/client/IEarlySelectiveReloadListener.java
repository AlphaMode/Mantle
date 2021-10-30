package slimeknights.mantle.client;

import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.resource.IResourceType;
import net.minecraftforge.resource.ReloadRequirements;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;

/**
 * This interface is similar to {@link net.minecraftforge.resource.ISelectiveResourceReloadListener}, except it runs during {@link net.minecraft.client.resources.ReloadListener}'s prepare phase.
 * This is used mainly as models load during the prepare phase, so it ensures they are loaded soon enough.
 */
public interface IEarlySelectiveReloadListener extends PreparableReloadListener {
  @Override
  default CompletableFuture<Void> reload(PreparationBarrier stage, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
    return CompletableFuture.runAsync(() -> {
      this.onResourceManagerReload(resourceManager, ReloadRequirements.all());
    }, backgroundExecutor).thenCompose(stage::wait);
  }

  /**
   * @param resourceManager the resource manager being reloaded
   * @param resourcePredicate predicate to test whether any given resource type should be reloaded
   */
  void onResourceManagerReload(ResourceManager resourceManager, Predicate<IResourceType> resourcePredicate);
}
