package com.chronosmp.data;

import com.chronosmp.ability.Ability;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public final class AbilityManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Type DATA_TYPE = new TypeToken<Map<String, String>>() { } .getType();
    private static final String DATA_FILE = "chronosmp-abilities.json";
    private static final Map<UUID, Ability> ABILITIES = new HashMap<>();

    private AbilityManager() {
    }

    public static void init() {
        load();
    }

    public static void load() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(DATA_FILE);
        if (Files.notExists(path)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(path)) {
            Map<String, String> data = GSON.fromJson(reader, DATA_TYPE);
            ABILITIES.clear();
            if (data != null) {
                data.forEach((uuid, id) -> Ability.fromId(id).ifPresent(ability -> ABILITIES.put(UUID.fromString(uuid), ability)));
            }
        } catch (IOException ignored) {
        }
    }

    public static void save() {
        Path path = FabricLoader.getInstance().getConfigDir().resolve(DATA_FILE);
        Map<String, String> data = new HashMap<>();
        ABILITIES.forEach((uuid, ability) -> data.put(uuid.toString(), ability.id()));
        try {
            Files.createDirectories(path.getParent());
            try (Writer writer = Files.newBufferedWriter(path)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException ignored) {
        }
    }

    public static Ability getAbility(UUID uuid) {
        return ABILITIES.get(uuid);
    }

    public static Optional<Ability> findAbility(UUID uuid) {
        return Optional.ofNullable(ABILITIES.get(uuid));
    }

    public static void setAbility(UUID uuid, Ability ability) {
        ABILITIES.put(uuid, ability);
        save();
    }

    public static void removeAbility(UUID uuid) {
        ABILITIES.remove(uuid);
        save();
    }

    public static boolean hasAbility(UUID uuid) {
        return ABILITIES.containsKey(uuid);
    }
}
