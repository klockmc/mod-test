package com.chronosmp.config;

import java.util.LinkedHashMap;
import java.util.Map;

public class ChronoConfig {
    private Map<String, AbilityConfig> abilities = new LinkedHashMap<>();

    public Map<String, AbilityConfig> abilities() {
        return abilities;
    }

    public AbilityConfig abilityConfig(String id) {
        return abilities.getOrDefault(id, new AbilityConfig(60, 15, 3, 6));
    }

    public void setAbilities(Map<String, AbilityConfig> abilities) {
        this.abilities = abilities;
    }
}
