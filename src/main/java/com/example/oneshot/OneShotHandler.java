package com.example.oneshot;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;

/**
 * OneShotHandler: standalone handler for the sword's effect so the Item class doesn't run
 * any game-initialization code during static init.
 */
public class OneShotHandler {
    private static final int EFFECT_AMPLIFIER = 254;
    private static final int EFFECT_DURATION = 20 * 60 * 60; // 1 hour in ticks

    public static void onAttack(LivingEntity target, LivingEntity attacker, ItemStack stack) {
        // Defensive runtime guards: only run server-side and ensure attacker is a LivingEntity
        net.minecraft.world.World world = target.getEntityWorld();
        if (world == null || world.isClient()) {
            return;
        }

        net.minecraft.entity.LivingEntity livingAttacker = null;
        if (attacker instanceof net.minecraft.entity.LivingEntity) {
            livingAttacker = (net.minecraft.entity.LivingEntity) attacker;
        }

        // For players, wipe inventory before kill so nothing drops
        if (target instanceof PlayerEntity playerTarget) {
            try {
                playerTarget.getInventory().clear();
            } catch (Throwable ignored) {}
        }

        // Attempt a proper instant-kill using DamageSources when attacker exists
        if (livingAttacker != null) {
            try {
                net.minecraft.entity.damage.DamageSource ds = target.getDamageSources().mobAttack(livingAttacker);
                try {
                    target.sidedDamage(ds, Float.MAX_VALUE);
                } catch (Throwable inner) {
                    target.setHealth(0.0F);
                }
            } catch (Throwable t) {
                try { target.setHealth(0.0F); } catch (Throwable ignored) {}
            }
        } else {
            try { target.setHealth(0.0F); } catch (Throwable ignored) {}
        }

        try { attacker.heal(99999.0F); } catch (Throwable ignored) {}

        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true, true));
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true, true));
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true, true));
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true, true));
        // Speed level 9 -> amplifier 8 (doubled)
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, EFFECT_DURATION, 8, false, true, true));
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.HASTE, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true, true));
        // Jump Boost level 5 -> amplifier 4 (doubled)
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.JUMP_BOOST, EFFECT_DURATION, 4, false, true, true));
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.SATURATION, EFFECT_DURATION, 5, false, true, true));
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, EFFECT_DURATION, 0, false, true, true));
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.INVISIBILITY, EFFECT_DURATION, 0, false, true, true));
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, EFFECT_DURATION, 2, false, true, true));
        attacker.addStatusEffect(new StatusEffectInstance(StatusEffects.NIGHT_VISION, EFFECT_DURATION, 0, false, true, true));

        if (target.isAlive()) {
            try {
                if (target instanceof PlayerEntity) {
                    ((PlayerEntity) target).getInventory().clear();
                }
            } catch (Throwable ignored) {}

            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true, true));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.POISON, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true, true));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true, true));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true, true));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, EFFECT_DURATION, EFFECT_AMPLIFIER, false, true, true));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_DAMAGE, 1, EFFECT_AMPLIFIER, false, true, true));
            target.addStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 60, 10, false, true, true));
        }
    }
}
