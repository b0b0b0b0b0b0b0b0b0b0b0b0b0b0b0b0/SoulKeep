package bm.b0b0b0.soulKeep.config;

import net.elytrium.serializer.custom.ClassSerializer;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unchecked")
public final class ItemOverridesSerializer extends ClassSerializer<ItemOverridesSection, Map<String, Object>> {

    private static final String ENABLED_KEY = "enabled";

    public ItemOverridesSerializer() {
        super(ItemOverridesSection.class, (Class<Map<String, Object>>) (Class<?>) Map.class);
    }

    @Override
    public Map<String, Object> serialize(ItemOverridesSection from) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put(ENABLED_KEY, from.enabled);
        map.putAll(from.entries);
        return map;
    }

    @Override
    public ItemOverridesSection deserialize(Map<String, Object> from) {
        ItemOverridesSection section = new ItemOverridesSection();
        if (from == null || from.isEmpty()) {
            return section;
        }
        Object enabledValue = from.get(ENABLED_KEY);
        if (enabledValue instanceof Boolean enabled) {
            section.enabled = enabled;
        } else if (enabledValue instanceof String enabledText) {
            section.enabled = Boolean.parseBoolean(enabledText);
        }
        for (Map.Entry<String, Object> entry : from.entrySet()) {
            if (ENABLED_KEY.equalsIgnoreCase(entry.getKey())) {
                continue;
            }
            if (entry.getValue() instanceof Number number) {
                section.entries.put(entry.getKey(), number.doubleValue());
            }
        }
        return section;
    }
}
