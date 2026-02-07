package com.chronosmp.commands;

import com.chronosmp.ChronoSmpMod;
import com.chronosmp.ability.Ability;
import com.chronosmp.config.ConfigManager;
import com.chronosmp.data.AbilityManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public final class AbilityCommands {
    private static final SuggestionProvider<ServerCommandSource> ABILITY_SUGGESTIONS = (context, builder) -> {
        Ability.ids().forEach(builder::suggest);
        return builder.buildFuture();
    };

    private AbilityCommands() {
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess, CommandRegistrationCallback.CommandSelection selection) {
        dispatcher.register(CommandManager.literal("ability")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.literal("give")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .then(CommandManager.argument("ability", StringArgumentType.word())
                        .suggests(ABILITY_SUGGESTIONS)
                        .executes(context -> {
                            ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                            String abilityId = StringArgumentType.getString(context, "ability");
                            Ability ability = Ability.fromId(abilityId).orElse(null);
                            if (ability == null) {
                                context.getSource().sendError(Text.literal("Unknown ability."));
                                return 0;
                            }
                            if (AbilityManager.hasAbility(player.getUuid())) {
                                context.getSource().sendError(Text.literal("Player already has an ability. Remove it first."));
                                return 0;
                            }
                            AbilityManager.setAbility(player.getUuid(), ability);
                            player.giveItemStack(new net.minecraft.item.ItemStack(ChronoSmpMod.ABILITY_ITEMS.get(ability)));
                            context.getSource().sendFeedback(() -> Text.literal("Granted " + ability.displayName() + " to " + player.getName().getString()).formatted(Formatting.GREEN), true);
                            return 1;
                        }))))
            .then(CommandManager.literal("remove")
                .then(CommandManager.argument("player", EntityArgumentType.player())
                    .executes(context -> {
                        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
                        AbilityManager.removeAbility(player.getUuid());
                        context.getSource().sendFeedback(() -> Text.literal("Removed ability from " + player.getName().getString()).formatted(Formatting.YELLOW), true);
                        return 1;
                    })))
            .then(CommandManager.literal("list")
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.literal("Abilities: " + String.join(", ", Ability.ids())).formatted(Formatting.AQUA), false);
                    return 1;
                }))
            .then(CommandManager.literal("reload")
                .executes(context -> {
                    ConfigManager.load();
                    context.getSource().sendFeedback(() -> Text.literal("ChronoSMP config reloaded.").formatted(Formatting.GREEN), true);
                    return 1;
                })));

        dispatcher.register(CommandManager.literal("abilities")
            .executes(context -> {
                ServerCommandSource source = context.getSource();
                if (source.getEntity() instanceof ServerPlayerEntity player) {
                    Ability ability = AbilityManager.getAbility(player.getUuid());
                    if (ability == null) {
                        player.sendMessage(Text.literal("You have no ability assigned.").formatted(Formatting.GRAY), false);
                    } else {
                        player.sendMessage(Text.literal("Your ability: " + ability.displayName()).formatted(Formatting.GOLD), false);
                    }
                    return 1;
                }
                source.sendError(Text.literal("Only players can run this command."));
                return 0;
            }));
    }
}
