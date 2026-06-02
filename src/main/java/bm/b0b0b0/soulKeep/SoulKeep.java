package bm.b0b0b0.soulKeep;

import bm.b0b0b0.soulKeep.bootstrap.PluginContext;
import bm.b0b0b0.soulKeep.bootstrap.PluginLifecycle;
import org.bukkit.plugin.java.JavaPlugin;

public final class SoulKeep extends JavaPlugin {

    private PluginLifecycle lifecycle;

    @Override
    public void onEnable() {
        lifecycle = new PluginContext(this).getLifecycle();
    }

    @Override
    public void onDisable() {
        if (lifecycle != null) {
            lifecycle.shutdown();
            lifecycle = null;
        }
    }
}
