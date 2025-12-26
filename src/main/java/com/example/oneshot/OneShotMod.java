package com.example.oneshot;

import com.example.oneshot.OneShotHandler;
import net.fabricmc.api.ModInitializer;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Settings;
import net.minecraft.item.ItemGroups;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;

public class OneShotMod implements ModInitializer {
    public static final String MOD_ID = "oneshot";

    // Avoid calling Settings.group(...) because the signature can vary between mappings.
    // Defer creating the Item instance until onInitialize to prevent Minecraft code from
    // running during static class initialization (fixes "Item id not set" NPE on startup).
    public static Item ONE_SHOT_SWORD;

    @Override
    public void onInitialize() {
        // Register the item inline. Avoid wrapping in a broad try/catch because creating an
        // Item before a failing register() call can leave an intrusive holder unregistered.
        Identifier swordId = Identifier.of(MOD_ID, "oneshot_sword");

        // Safety: if the ID is already present, skip creating a second Item instance.
        if (Registries.ITEM.containsId(swordId)) {
            System.err.println("[OneShotMod] Item ID already registered: " + swordId + " — skipping duplicate registration.");
            ONE_SHOT_SWORD = Registries.ITEM.get(swordId);
        } else {
            System.out.println("[OneShotMod][Diag] About to construct Item settings (assigning registry key early); classloader=" + OneShotMod.class.getClassLoader());
            // In modern 1.21.x, Items expect their registry key to be pre-associated in Settings
            // before construction, otherwise the constructor will throw the 'Item id not set' NPE.
            RegistryKey<Item> swordKey = RegistryKey.of(RegistryKeys.ITEM, swordId);
            Settings settings = new Settings().registryKey(swordKey).maxCount(1);
            System.out.println("[OneShotMod][Diag] Settings built: " + settings);
            System.out.println("[OneShotMod][Diag] Calling Registry.register for id=" + swordId);
            ONE_SHOT_SWORD = Registry.register(Registries.ITEM, swordId, new Item(settings));
            System.out.println("[OneShotMod][Diag] Registry.register returned instance=" + ONE_SHOT_SWORD + " class=" + ONE_SHOT_SWORD.getClass().getName());
            Identifier fetched = Registries.ITEM.getId(ONE_SHOT_SWORD);
            System.out.println("[OneShotMod][Diag] Post-registration fetched id=" + fetched);
            if (fetched == null) {
                throw new IllegalStateException("[OneShotMod][Diag] Fetched null id for sword AFTER registration; registry corruption?");
            }
        }

        // Only register callbacks if the sword successfully registered
        if (ONE_SHOT_SWORD != null) {
            // Add to Combat creative tab for visibility
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.COMBAT).register(entries -> entries.add(ONE_SHOT_SWORD.getDefaultStack()));
            net.fabricmc.fabric.api.event.player.AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
                if (world == null || world.isClient()) return net.minecraft.util.ActionResult.PASS;
                if (player.getMainHandStack() == null) return net.minecraft.util.ActionResult.PASS;
                if (player.getMainHandStack().getItem() != ONE_SHOT_SWORD) return net.minecraft.util.ActionResult.PASS;
                if (entity instanceof net.minecraft.entity.LivingEntity) {
                    OneShotHandler.onAttack((net.minecraft.entity.LivingEntity) entity, player, player.getMainHandStack());
                }
                return net.minecraft.util.ActionResult.PASS;
            });
        }

        System.out.println("[OneShotMod][Diag] onInitialize complete.");
    }
}
