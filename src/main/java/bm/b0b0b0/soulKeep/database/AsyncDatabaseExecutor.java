package bm.b0b0b0.soulKeep.database;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

public final class AsyncDatabaseExecutor {

    private final JavaPlugin plugin;

    public AsyncDatabaseExecutor(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void run(Runnable task) {
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> execute(task));
    }

    public <T> CompletableFuture<T> supply(Callable<T> callable) {
        CompletableFuture<T> future = new CompletableFuture<>();
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                future.complete(callable.call());
            } catch (Exception exception) {
                future.completeExceptionally(exception);
            }
        });
        return future;
    }

    private void execute(Runnable task) {
        try {
            task.run();
        } catch (Exception exception) {
            plugin.getLogger().log(Level.SEVERE, "Database task failed", exception);
        }
    }
}
