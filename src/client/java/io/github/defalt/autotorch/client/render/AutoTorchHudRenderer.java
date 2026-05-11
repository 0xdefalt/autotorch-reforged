package io.github.defalt.autotorch.client.render;

import io.github.defalt.autotorch.client.config.AutoTorchConfig;
import io.github.defalt.autotorch.client.config.AutoTorchConfigManager;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public final class AutoTorchHudRenderer {

    private static final Identifier STATUS_HUD_ID = Identifier.of("autotorch", "status");
    private static final int STATUS_ON_COLOR = 0xFF55FF55;
    private static final int STATUS_OFF_COLOR = 0xFFFF5555;

    private AutoTorchHudRenderer() {
        // TODO: not yet implemented
    }

    public static void register() {
        HudElementRegistry.addLast(STATUS_HUD_ID, AutoTorchHudRenderer::render);
    }

    private static void render(DrawContext context, RenderTickCounter tickCounter) {
        MinecraftClient client = MinecraftClient.getInstance();
        AutoTorchConfig config = AutoTorchConfigManager.getAutoTorchConfig();
        if (client.player == null || client.world == null || !config.hudVisible) {
            return;
        }

        Text status = Text.translatable(config.enabled ? "autotorch.status.on" : "autotorch.status.off");
        int color = config.enabled ? STATUS_ON_COLOR : STATUS_OFF_COLOR;
        context.drawTextWithShadow(client.textRenderer, status, config.hudX, config.hudY, color);
    }

}