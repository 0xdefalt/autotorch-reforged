package io.github.defalt.autotorch.client;

import io.github.defalt.autotorch.client.config.AutoTorchConfig;
import io.github.defalt.autotorch.client.config.AutoTorchConfigManager;
import io.github.defalt.autotorch.client.render.AutoTorchHudRenderer;
import io.github.defalt.autotorch.client.torch.AutoTorchPlacement;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class AutoTorchClient implements ClientModInitializer {

    private static final KeyBinding.Category AUTOTORCH_CATEGORY = KeyBinding.Category.create(Identifier.of("autotorch", "main"));
    private static final KeyBinding AUTO_PLACE_BINDING = KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                    "autotorch.autotorch.toggle",
                    InputUtil.Type.KEYSYM,
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

    private void tick(MinecraftClient client) {
        if (client.player == null || client.world == null) {
            return;
        }
        AutoTorchConfig config = AutoTorchConfigManager.getAutoTorchConfig();
        if (AUTO_PLACE_BINDING.wasPressed()) {
            config.enabled = !config.enabled;
            AutoTorchConfigManager.save();
        }
        if (config.enabled) {
            AutoTorchPlacement.tryPlace(client, config);
        }
    }

}