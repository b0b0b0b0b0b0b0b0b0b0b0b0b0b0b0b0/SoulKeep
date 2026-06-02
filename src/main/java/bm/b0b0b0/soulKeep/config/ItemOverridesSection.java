package bm.b0b0b0.soulKeep.config;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ItemOverridesSection {

    public boolean enabled = true;
    public Map<String, Double> entries = defaultEntries();

    static Map<String, Double> defaultEntries() {
        Map<String, Double> values = new LinkedHashMap<>();
        values.put("TOTEM_OF_UNDYING", 5.0);
        return values;
    }
}
