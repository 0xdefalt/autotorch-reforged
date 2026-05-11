package io.github.defalt.autotorch.client;

import io.github.defalt.autotorch.client.config.AutoTorchConfig;
import io.github.defalt.autotorch.client.config.AutoTorchConfigManager;
import io.github.defalt.autotorch.client.render.AutoTorchHudRenderer;
import io.github.defalt.autotorch.client.torch.AutoTorchPlacement;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keymapping.v1.KeyMappingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AutoTorchClient implements ClientModInitializer {

    private static final KeyMapping.Category AUTOTORCH_CATEGORY = KeyMapping.Category.register(Identifier.fromNamespaceAndPath("autotorch", "main"));
    private static final KeyMapping AUTO_PLACE_MAPPING = KeyMappingHelper.registerKeyMapping(
            new KeyMapping(
                    "autotorch.autotorch.toggle",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_LEFT_ALT,
                    AUTOTORCH_CATEGORY
            )
    );

    @Override
    public void onInitializeClient() {
        AutoTorchConfigManager.load();
        AutoTorchHudRenderer.register();
        ClientTickEvents.END_CLIENT_TICK.register(this::tick);
    }

    private void tick(Minecraft client) {
        if (client.player == null || client.level == null) {
            return;
        }
        AutoTorchConfig config = AutoTorchConfigManager.getAutoTorchConfig();
        if (AUTO_PLACE_MAPPING.consumeClick()) {
            config.enabled = !config.enabled;
            AutoTorchConfigManager.save();
        }
        if (config.enabled) {
            AutoTorchPlacement.tryPlace(client, config);
        }
    }

}