package com.chronosmp;

import com.chronosmp.ability.Ability;
import com.chronosmp.ability.AbilityItem;
import com.chronosmp.config.ConfigManager;
import com.chronosmp.data.AbilityManager;
import com.chronosmp.events.AbilityEvents;
import com.chronosmp.events.AbilityFlightTracker;
import com.chronosmp.commands.AbilityCommands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

public class ChronoSmpMod implements ModInitializer {
    public static final String MOD_ID = "chronosmp";
    public static final Map<Ability, Item> ABILITY_ITEMS = new EnumMap<>(Ability.class);

    @Override
    public void onInitialize() {
        ConfigManager.init();
        AbilityManager.init();

        registerItems();

        CommandRegistrationCallback.EVENT.register(AbilityCommands::register);

        AttackEntityCallback.EVENT.register(AbilityEvents::onAttackEntity);
        PlayerBlockBreakEvents.AFTER.register(AbilityEvents::onBlockBreak);
        ServerTickEvents.END_SERVER_TICK.register(AbilityFlightTracker::tick);

        ServerLifecycleEvents.SERVER_STOPPING.register(server -> AbilityManager.save());
    }

    private void registerItems() {
        for (Ability ability : Ability.values()) {
            Identifier id = new Identifier(MOD_ID, ability.id());
            Item item = new AbilityItem(new Item.Settings().maxCount(1), ability);
            ABILITY_ITEMS.put(ability, item);
            Registry.register(Registries.ITEM, id, item);
        }
    }
}
