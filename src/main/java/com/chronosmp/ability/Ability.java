package com.chronosmp.ability;

import com.chronosmp.config.AbilityConfig;
import com.chronosmp.config.ConfigManager;
import com.chronosmp.events.AbilityFlightTracker;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.SkeletonEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Arrays;

public enum Ability {
    SHADOW_WALKER("shadow_walker", "Shadow Walker", List.of("Invisibility and night vision", "Stealthy particle trail")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, config.durationTicks(), 0));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, config.durationTicks(), 0));
            world.spawnParticles(net.minecraft.particle.ParticleTypes.SMOKE, player.getX(), player.getY() + 1.0, player.getZ(), 20, 0.4, 0.5, 0.4, 0.0);
        }
    },
    PYROMANIAC("pyromaniac", "Pyromaniac", List.of("Fire immunity", "Ignite targets")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, config.durationTicks(), 0));
        }
    },
    TITAN_MIGHT("titan_might", "Titan's Might", List.of("Strength + resistance", "Ground slam knockback")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, config.durationTicks(), 1));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, config.durationTicks(), 1));
            Box area = player.getBoundingBox().expand(4.0);
            for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, area, entity -> entity != player)) {
                Vec3d knockback = target.getPos().subtract(player.getPos()).normalize().multiply(1.2);
                target.takeKnockback(1.0, -knockback.x, -knockback.z);
                target.damage(world.getDamageSources().playerAttack(player), 4.0f);
            }
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_IRON_GOLEM_ATTACK, SoundCategory.PLAYERS, 1.0f, 0.8f);
        }
    },
    VOID_BENDER("void_bender", "Void Bender", List.of("Teleport to where you look")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            Vec3d eye = player.getCameraPosVec(1.0f);
            Vec3d look = player.getRotationVec(1.0f);
            Vec3d reach = eye.add(look.multiply(config.range()));
            RaycastContext context = new RaycastContext(eye, reach, RaycastContext.ShapeType.OUTLINE, RaycastContext.FluidHandling.NONE, player);
            var result = world.raycast(context);
            BlockPos hit = result.getBlockPos();
            BlockPos target = hit.offset(result.getSide());
            player.teleport(world, target.getX() + 0.5, target.getY(), target.getZ() + 0.5, player.getYaw(), player.getPitch());
            world.playSound(null, target, SoundEvents.ENTITY_ENDERMAN_TELEPORT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    },
    STORM_CALLER("storm_caller", "Storm Caller", List.of("Summon lightning")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            BlockPos target = player.getBlockPos().offset(player.getHorizontalFacing(), config.range());
            for (int i = 0; i < config.power(); i++) {
                Entity lightning = EntityTypeBuilder.lightning(world, target);
                if (lightning != null) {
                    world.spawnEntity(lightning);
                }
            }
        }
    },
    NATURES_ALLY("natures_ally", "Nature's Ally", List.of("Summon loyal wolves")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            for (int i = 0; i < config.power(); i++) {
                WolfEntity wolf = net.minecraft.entity.EntityType.WOLF.create(world);
                if (wolf == null) {
                    continue;
                }
                wolf.setTamed(true);
                wolf.setOwner(player);
                wolf.refreshPositionAndAngles(player.getX() + world.random.nextInt(2), player.getY(), player.getZ() + world.random.nextInt(2), player.getYaw(), 0.0f);
                world.spawnEntity(wolf);
            }
        }
    },
    BLOODLUST("bloodlust", "Bloodlust", List.of("Lifesteal on attacks")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, config.durationTicks(), 0));
        }
    },
    PHASE_SHIFT("phase_shift", "Phase Shift", List.of("Phased movement and speed")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, config.durationTicks(), 2));
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, config.durationTicks(), 0));
            player.sendMessage(Text.literal("You feel ethereal and can slip past threats.").formatted(Formatting.AQUA), true);
        }
    },
    TIME_WARP("time_warp", "Time Warp", List.of("Slow nearby enemies")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            Box area = player.getBoundingBox().expand(config.range());
            for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, area, entity -> entity != player)) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, config.durationTicks(), 1));
            }
        }
    },
    DRAGON_HEART("dragon_heart", "Dragon Heart", List.of("Temporary flight")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            AbilityFlightTracker.enableFlight(player, config.durationTicks());
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_ENDER_DRAGON_GROWL, SoundCategory.PLAYERS, 0.6f, 1.2f);
        }
    },
    ALCHEMIST("alchemist", "Alchemist", List.of("Create random powerful potion")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            List<ItemStack> potions = List.of(
                PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.STRONG_HEALING),
                PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.STRONG_STRENGTH),
                PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.LONG_REGENERATION),
                PotionUtil.setPotion(new ItemStack(Items.SPLASH_POTION), Potions.LONG_SWIFTNESS)
            );
            ItemStack potion = potions.get(world.random.nextInt(potions.size()));
            player.giveItemStack(potion);
            world.playSound(null, player.getBlockPos(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.PLAYERS, 0.8f, 1.2f);
        }
    },
    MIDAS_TOUCH("midas_touch", "Midas Touch", List.of("Bonus ore drops")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, config.durationTicks(), 1));
        }
    },
    BERSERKER("berserker", "Berserker", List.of("More damage at lower health")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            player.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, config.durationTicks(), 1));
        }
    },
    NECROMANCER("necromancer", "Necromancer", List.of("Summon skeleton allies")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            for (int i = 0; i < config.power(); i++) {
                SkeletonEntity skeleton = net.minecraft.entity.EntityType.SKELETON.create(world);
                if (skeleton == null) {
                    continue;
                }
                skeleton.refreshPositionAndAngles(player.getX() + world.random.nextInt(3), player.getY(), player.getZ() + world.random.nextInt(3), player.getYaw(), 0.0f);
                skeleton.initialize(world, world.getLocalDifficulty(player.getBlockPos()), SpawnReason.MOB_SUMMONED, null);
                skeleton.equipStack(net.minecraft.entity.EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));
                world.spawnEntity(skeleton);
            }
            world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_SKELETON_AMBIENT, SoundCategory.PLAYERS, 0.8f, 0.8f);
        }
    },
    FROST_WEAVER("frost_weaver", "Frost Weaver", List.of("Freeze enemies", "Create ice platforms")) {
        @Override
        public void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config) {
            Box area = player.getBoundingBox().expand(config.range());
            for (LivingEntity target : world.getEntitiesByClass(LivingEntity.class, area, entity -> entity != player)) {
                target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, config.durationTicks(), 3));
                target.setFrozenTicks(config.durationTicks());
            }
            BlockPos under = player.getBlockPos().down();
            if (world.getBlockState(under).isAir()) {
                world.setBlockState(under, Blocks.ICE.getDefaultState());
            }
        }
    };

    private final String id;
    private final String displayName;
    private final List<String> lore;

    Ability(String id, String displayName, List<String> lore) {
        this.id = id;
        this.displayName = displayName;
        this.lore = lore;
    }

    public String id() {
        return id;
    }

    public String displayName() {
        return displayName;
    }

    public List<String> lore() {
        return lore;
    }

    public AbilityConfig config() {
        return ConfigManager.getConfig().abilityConfig(id);
    }

    public abstract void activate(ServerWorld world, ServerPlayerEntity player, AbilityConfig config);

    public static Optional<Ability> fromId(String id) {
        String key = id.toLowerCase(Locale.ROOT);
        for (Ability ability : values()) {
            if (ability.id.equals(key)) {
                return Optional.of(ability);
            }
        }
        return Optional.empty();
    }

    public static List<String> ids() {
        return Arrays.stream(values()).map(Ability::id).toList();
    }

    public void sendActivationMessage(ServerPlayerEntity player) {
        player.sendMessage(Text.literal("Activated " + displayName + "!").formatted(Formatting.GOLD), true);
    }

    private static final class EntityTypeBuilder {
        private EntityTypeBuilder() {
        }

        static Entity lightning(ServerWorld world, BlockPos pos) {
            net.minecraft.entity.LightningEntity lightning = net.minecraft.entity.EntityType.LIGHTNING_BOLT.create(world);
            if (lightning != null) {
                lightning.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(pos));
            }
            return lightning;
        }
    }
}
