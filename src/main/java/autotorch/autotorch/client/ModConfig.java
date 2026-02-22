package autotorch.autotorch.client;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.*;

@Config(name = "autotorch")
public class ModConfig implements ConfigData {

    @ConfigEntry.Gui.Tooltip
    boolean enabled = true;

    @ConfigEntry.Gui.Tooltip
    boolean accuratePlacement = false;

    @ConfigEntry.Gui.Tooltip
    @ConfigEntry.BoundedDiscrete(min = 1, max = 14)
    int lightLevel = 4;

}