package net.reimaden.insomniatracker;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;

public class InsomniaTracker implements ModInitializer {

    // Vanilla behavior
    private static int ticksUntilPhantoms = 72000;

    public static int getTicksUntilPhantoms() {
        return ticksUntilPhantoms;
    }

    public static void setTicksUntilPhantoms(int ticks) {
        ticksUntilPhantoms = ticks;
    }

    @Override
    public void onInitialize() {
        // Change timer based on other mods
        // I sure hope there aren't too many of these mods, otherwise I'm going to have to implement something better
        if (FabricLoader.getInstance().isModLoaded("phantomredux")) {
            setTicksUntilPhantoms(96000);
        }
    }
}
