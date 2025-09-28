package com.moclg.elytramace.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.Item;
import net.minecraft.util.Hand;

public class ElytramaceClient implements ClientModInitializer {
    // Store the starting Y position when the player begins to fall/glide
    private double startFallY = Double.NaN;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null || client.world == null) return;
            if (!client.isInSingleplayer()) return;

            ItemStack chest = client.player.getEquippedStack(EquipmentSlot.CHEST);
            if (chest.getItem() != Items.ELYTRA) {
                startFallY = Double.NaN; // Reset if not wearing elytra
                return;
            }

            ItemStack mainHand = client.player.getStackInHand(Hand.MAIN_HAND);
            Item item = mainHand.getItem();
            if (!(item == Items.MACE)) {
                startFallY = Double.NaN; // Reset if not holding mace
                return;
            }

            double velocityY = client.player.getVelocity().y;
            double currentY = client.player.getY();

            // If player just started falling/gliding, record the starting Y
            if (velocityY < 0) {
                if (Double.isNaN(startFallY)) {
                    startFallY = currentY;
                }
                // Set fallDistance to the difference between start Y and current Y
                float simulatedFallDistance = (float) (startFallY - currentY);
                if (simulatedFallDistance > 0) {
                    client.player.fallDistance = simulatedFallDistance;
                }
            } else {
                // Not falling, reset
                startFallY = Double.NaN;
            }
        });
    }
}
