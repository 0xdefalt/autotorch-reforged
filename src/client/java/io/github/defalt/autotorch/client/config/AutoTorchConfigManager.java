package io.github.defalt.autotorch.client.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public final class AutoTorchConfigManager {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve("autotorch.json");
    private static AutoTorchConfig autoTorchConfig = new AutoTorchConfig();

    private AutoTorchConfigManager() {
        // TODO: not yet implemented
    }

    public static void load() {
        if (Files.notExists(PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(PATH)) {
            JsonElement root = JsonParser.parseReader(reader);
            AutoTorchConfig loadedConfig = GSON.fromJson(root, AutoTorchConfig.class);
            if (loadedConfig != null) {
                if (root != null && root.isJsonObject()) {
                    applyLegacyMigration(loadedConfig, root.getAsJsonObject());
                }
                autoTorchConfig = sanitize(loadedConfig);
            } else {
                autoTorchConfig = new AutoTorchConfig();
            }
        } catch (Exception exception) {
            autoTorchConfig = new AutoTorchConfig();
        }
    }

    public static void save() {
        autoTorchConfig = sanitize(autoTorchConfig);
        try {
            Files.createDirectories(PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(PATH)) {
                GSON.toJson(autoTorchConfig, writer);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public static AutoTorchConfig getAutoTorchConfig() {
        return autoTorchConfig;
    }

    private static void applyLegacyMigration(AutoTorchConfig loadedConfig, JsonObject root) {
        if (!root.has("accuratePlacement") && root.has("accurateTorchPlacement") && root.get("accurateTorchPlacement").isJsonPrimitive()) {
            loadedConfig.accuratePlacement = root.get("accurateTorchPlacement").getAsBoolean();
        }
    }

    private static AutoTorchConfig sanitize(AutoTorchConfig autoTorchConfig) {
        autoTorchConfig.lightLevel = clamp(autoTorchConfig.lightLevel, 1, 14);
        autoTorchConfig.hudX = clamp(autoTorchConfig.hudX, 0, 1000);
        autoTorchConfig.hudY = clamp(autoTorchConfig.hudY, 0, 1000);
        return autoTorchConfig;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

}