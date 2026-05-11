package io.github.defalt.autotorch.client.integration;

import io.github.defalt.autotorch.client.config.AutoTorchConfig;
import io.github.defalt.autotorch.client.config.AutoTorchConfigManager;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public final class AutoTorchConfigScreen {

    private AutoTorchConfigScreen() {
        // TODO: not yet implemented
    }

    public static Screen create(Screen parent) {
        ConfigBuilder configBuilder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.literal("Defalt's AutoTorch Reforged"));
        ConfigCategory configCategory = configBuilder.getOrCreateCategory(Component.literal("AutoTorch"));
        ConfigEntryBuilder configEntryBuilder = configBuilder.entryBuilder();
        AutoTorchConfig autoTorchConfig = AutoTorchConfigManager.getAutoTorchConfig();
        configCategory.addEntry(configEntryBuilder
                .startBooleanToggle(Component.literal("Enable AutoTorch"), autoTorchConfig.enabled)
                .setDefaultValue(true)
                .setSaveConsumer(value -> autoTorchConfig.enabled = value)
                .build());
        configCategory.addEntry(configEntryBuilder
                .startIntSlider(Component.literal("Minimum light Level"), autoTorchConfig.lightLevel, 1, 14)
                .setDefaultValue(4)
                .setSaveConsumer(value -> autoTorchConfig.lightLevel = value)
                .build());
        configCategory.addEntry(configEntryBuilder
                .startBooleanToggle(Component.literal("Enable accurate torch placement"), autoTorchConfig.accuratePlacement)
                .setDefaultValue(false)
                .setSaveConsumer(value -> autoTorchConfig.accuratePlacement = value)
                .build());
        configCategory.addEntry(configEntryBuilder
                .startBooleanToggle(Component.literal("Show status"), autoTorchConfig.hudVisible)
                .setDefaultValue(true)
                .setSaveConsumer(value -> autoTorchConfig.hudVisible = value)
                .build());
        configCategory.addEntry(configEntryBuilder
                .startIntSlider(Component.literal("Status X position"), autoTorchConfig.hudX, 0, 1000)
                .setDefaultValue(4)
                .setSaveConsumer(value -> autoTorchConfig.hudX = value)
                .build());
        configCategory.addEntry(configEntryBuilder
                .startIntSlider(Component.literal("Status Y position"), autoTorchConfig.hudY, 0, 1000)
                .setDefaultValue(4)
                .setSaveConsumer(value -> autoTorchConfig.hudY = value)
                .build());
        configBuilder.setSavingRunnable(AutoTorchConfigManager::save);
        return configBuilder.build();
    }

}