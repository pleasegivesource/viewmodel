package net.cyberflame.viewmodel;

import net.cyberflame.viewmodel.config.LoadConfig;
import net.cyberflame.viewmodel.config.SaveConfig;
import net.cyberflame.viewmodel.gui.ViewmodelScreen;
import net.cyberflame.viewmodel.settings.Setting;
import net.cyberflame.viewmodel.settings.SettingType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFW;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Runtime.getRuntime;

public class Viewmodel implements ModInitializer {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final List<Setting<?>> SETTINGS = Arrays.stream(SettingType.values())
            .map(SettingType::getSetting)
            .collect(Collectors.toList());
    private static KeyBinding keyBinding;

    @Contract(value = " -> new", pure = true)
    public static @NotNull List<Setting<?>> getSettings() {
        // Return a copy of the SETTINGS list to prevent direct modification
        return new ArrayList<>(SETTINGS);
    }

    @NonNls
    public static final String VIEWMODEL_JSON = "Viewmodel.json";


    @Override
    public final void onInitialize() {
        LOGGER.info("Loading Viewmodel!");
        try {
            new LoadConfig();
            new SaveConfig();
        } catch (IOException e) {
            LOGGER.error("Failed to load settings!", e);
        }
        keyBinding = KeyBindingHelper.registerKeyBinding(
                new KeyBinding("key.viewmodel.open", InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_BACKSLASH, "category.viewmodel.menu"));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (keyBinding.wasPressed()) {
                if (client.world != null) {
                    client.setScreen(new ViewmodelScreen());
                }
            }
        });
        getRuntime().addShutdownHook(new Thread(SaveConfig::saveAllSettings));
    }

}
