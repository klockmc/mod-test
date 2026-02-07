package com.chronosmp.events;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public final class AbilityFlightTracker {
    private static final Map<UUID, Integer> FLIGHT_TICKS = new HashMap<>();

    private AbilityFlightTracker() {
    }

    public static void enableFlight(ServerPlayerEntity player, int durationTicks) {
        player.getAbilities().allowFlying = true;
        player.sendAbilitiesUpdate();
        FLIGHT_TICKS.put(player.getUuid(), durationTicks);
    }

    public static void tick(MinecraftServer server) {
        Iterator<Map.Entry<UUID, Integer>> iterator = FLIGHT_TICKS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, Integer> entry = iterator.next();
            int remaining = entry.getValue() - 1;
            if (remaining <= 0) {
                ServerPlayerEntity player = server.getPlayerManager().getPlayer(entry.getKey());
                if (player != null && !player.isCreative() && !player.isSpectator()) {
                    player.getAbilities().allowFlying = false;
                    player.getAbilities().flying = false;
                    player.sendAbilitiesUpdate();
                }
                iterator.remove();
            } else {
                entry.setValue(remaining);
            }
        }
    }
}
