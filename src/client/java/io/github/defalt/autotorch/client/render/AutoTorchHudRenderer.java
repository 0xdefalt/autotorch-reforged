package io.github.defalt.autotorch.client.render;

import io.github.defalt.autotorch.client.config.AutoTorchConfig;
import io.github.defalt.autotorch.client.config.AutoTorchConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class AutoTorchHudRenderer {

    private static final Identifier STATUS_HUD_ID = Identifier.fromNamespaceAndPath("autotorch", "status");
    private static final int STATUS_ON_COLOR = 0xFF55FF55;
    private static final int STATUS_OFF_COLOR = 0xFFFF5555;

    private AutoTorchHudRenderer() {
        // TODO: not yet implemented
    }

    public static void register() {
        HudElementRegistry.addLast(STATUS_HUD_ID, AutoTorchHudRenderer::render);
    }

    private static void render(GuiGraphicsExtractor context, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();
        AutoTorchConfig config = AutoTorchConfigManager.getAutoTorchConfig();
        if (client.player == null || client.level == null || !config.hudVisible) {
            return;
        }

        Component status = Component.translatable(config.enabled ? "autotorch.status.on" : "autotorch.status.off");
        int color = config.enabled ? STATUS_ON_COLOR : STATUS_OFF_COLOR;
        context.text(client.font, status, config.hudX, config.hudY, color, true);
    }

}