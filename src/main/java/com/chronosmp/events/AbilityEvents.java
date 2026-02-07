package com.chronosmp.events;

import com.chronosmp.ability.Ability;
import com.chronosmp.data.AbilityManager;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.block.entity.BlockEntity;

public final class AbilityEvents {
    private AbilityEvents() {
    }

    public static ActionResult onAttackEntity(PlayerEntity player, net.minecraft.world.World world, net.minecraft.util.Hand hand, Entity entity, net.minecraft.util.hit.EntityHitResult hitResult) {
        if (world.isClient || !(player instanceof ServerPlayerEntity serverPlayer) || !(entity instanceof LivingEntity target)) {
            return ActionResult.PASS;
        }

        Ability ability = AbilityManager.getAbility(serverPlayer.getUuid());
        if (ability == null) {
            return ActionResult.PASS;
        }

        if (ability == Ability.PYROMANIAC) {
            target.setOnFireFor(4);
        }

        if (ability == Ability.BLOODLUST) {
            float heal = 2.0f;
            serverPlayer.heal(heal);
        }

        if (ability == Ability.BERSERKER) {
            float missing = serverPlayer.getMaxHealth() - serverPlayer.getHealth();
            float bonus = Math.min(6.0f, missing * 0.5f);
            target.damage(world.getDamageSources().playerAttack(serverPlayer), bonus);
        }

        return ActionResult.PASS;
    }

    public static void onBlockBreak(ServerWorld world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity) {
        if (!(player instanceof ServerPlayerEntity serverPlayer)) {
            return;
        }

        Ability ability = AbilityManager.getAbility(serverPlayer.getUuid());
        if (ability != Ability.MIDAS_TOUCH) {
            return;
        }

        if (state.isIn(BlockTags.ORES)) {
            Block block = state.getBlock();
            ItemStack drop = new ItemStack(block.asItem());
            Block.dropStack(world, pos, drop);
        }
    }
}
