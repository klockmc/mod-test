package com.chronosmp.config;

import com.chronosmp.ability.Ability;
import net.fabricmc.loader.api.FabricLoader;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ConfigManager {
    private static final String CONFIG_NAME = "chronosmp.yml";
    private static ChronoConfig config;

    private ConfigManager() {
    }

    public static void init() {
        load();
    }

    public static void load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME);
        if (Files.notExists(configPath)) {
            config = defaultConfig();
            save(configPath, config);
            return;
        }

        try (InputStream input = Files.newInputStream(configPath)) {
            Yaml yaml = new Yaml(new Constructor(ChronoConfig.class));
            config = yaml.loadAs(input, ChronoConfig.class);
        } catch (IOException exception) {
            config = defaultConfig();
        }
    }

    public static void save() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(CONFIG_NAME);
        save(configPath, config);
    }

    private static void save(Path path, ChronoConfig config) {
        try {
            Files.createDirectories(path.getParent());
            DumperOptions options = new DumperOptions();
            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
            options.setPrettyFlow(true);
            Yaml yaml = new Yaml(options);
            try (OutputStreamWriter writer = new OutputStreamWriter(Files.newOutputStream(path))) {
                yaml.dump(config, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public static ChronoConfig getConfig() {
        if (config == null) {
            config = defaultConfig();
        }
        return config;
    }

    public static ChronoConfig defaultConfig() {
        ChronoConfig chronoConfig = new ChronoConfig();
        Map<String, AbilityConfig> abilities = new LinkedHashMap<>();
        for (Ability ability : Ability.values()) {
            abilities.put(ability.id(), defaultAbilityConfig(ability));
        }
        chronoConfig.setAbilities(abilities);
        return chronoConfig;
    }

    private static AbilityConfig defaultAbilityConfig(Ability ability) {
        return switch (ability) {
            case SHADOW_WALKER -> new AbilityConfig(45, 20, 2, 6);
            case PYROMANIAC -> new AbilityConfig(60, 25, 2, 6);
            case TITAN_MIGHT -> new AbilityConfig(60, 15, 2, 4);
            case VOID_BENDER -> new AbilityConfig(20, 5, 1, 12);
            case STORM_CALLER -> new AbilityConfig(90, 0, 3, 6);
            case NATURES_ALLY -> new AbilityConfig(90, 0, 3, 0);
            case BLOODLUST -> new AbilityConfig(40, 15, 1, 0);
            case PHASE_SHIFT -> new AbilityConfig(50, 12, 1, 0);
            case TIME_WARP -> new AbilityConfig(75, 10, 1, 6);
            case DRAGON_HEART -> new AbilityConfig(120, 10, 1, 0);
            case ALCHEMIST -> new AbilityConfig(45, 0, 1, 0);
            case MIDAS_TOUCH -> new AbilityConfig(60, 20, 1, 0);
            case BERSERKER -> new AbilityConfig(30, 15, 1, 0);
            case NECROMANCER -> new AbilityConfig(120, 0, 3, 0);
            case FROST_WEAVER -> new AbilityConfig(80, 12, 1, 5);
        };
    }
}
