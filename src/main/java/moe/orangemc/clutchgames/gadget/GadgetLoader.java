package moe.orangemc.clutchgames.gadget;

import moe.orangemc.clutchgames.util.Logger;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class GadgetLoader {
    private final YamlConfiguration configuration;

    public GadgetLoader(File file) {
        configuration = YamlConfiguration.loadConfiguration(file);
    }

    public Map<String, Gadget> readConfiguration(GadgetType type) {
        Set<String> keys = configuration.getKeys(false);
        Map<String, Gadget> result = new LinkedHashMap<>();
        for (String key : keys) {
            result.put(key, new Gadget(type, configuration.getConfigurationSection(key)));
        }
        return result;
    }
}
