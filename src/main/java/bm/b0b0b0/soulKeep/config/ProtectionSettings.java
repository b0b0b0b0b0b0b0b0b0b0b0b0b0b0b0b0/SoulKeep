package bm.b0b0b0.soulKeep.config;

public final class ProtectionSettings {

    private final boolean allowStacks;

    public ProtectionSettings(SoulKeepSettings settings) {
        this.allowStacks = settings.settings.allowStacks;
    }

    public boolean isAllowStacks() {
        return allowStacks;
    }
}
