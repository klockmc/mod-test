package com.chronosmp.ability;

import com.chronosmp.data.AbilityManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.TooltipContext;
import net.minecraft.item.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.List;

public class AbilityItem extends Item {
    private final Ability ability;

    public AbilityItem(Settings settings, Ability ability) {
        super(settings);
        this.ability = ability;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) {
            return TypedActionResult.success(stack);
        }

        if (!(user instanceof ServerPlayerEntity player)) {
            return TypedActionResult.fail(stack);
        }

        Ability assigned = AbilityManager.getAbility(player.getUuid());
        if (assigned != ability) {
            player.sendMessage(Text.literal("You are not bonded to this ability.").formatted(Formatting.RED), true);
            return TypedActionResult.fail(stack);
        }

        int cooldown = ability.config().cooldownTicks();
        if (player.getItemCooldownManager().isCoolingDown(this)) {
            return TypedActionResult.fail(stack);
        }

        ability.activate(player.getServerWorld(), player, ability.config());
        ability.sendActivationMessage(player);
        player.getItemCooldownManager().set(this, cooldown);
        return TypedActionResult.success(stack);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.literal(ability.displayName()).formatted(Formatting.GOLD));
        for (String line : ability.lore()) {
            tooltip.add(Text.literal(line).formatted(Formatting.GRAY));
        }
        tooltip.add(Text.literal("Right-click to activate").formatted(Formatting.AQUA));
    }
}
