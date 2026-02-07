package com.chronosmp.config;

public record AbilityConfig(int cooldownSeconds, int durationSeconds, int power, int range) {
    public int cooldownTicks() {
        return cooldownSeconds * 20;
    }

    public int durationTicks() {
        return durationSeconds * 20;
    }
}
