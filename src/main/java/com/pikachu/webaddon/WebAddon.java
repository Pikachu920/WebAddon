package com.pikachu.webaddon;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.pikachu.webaddon.skript.classinfos.Classinfos;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class WebAddon extends JavaPlugin {

    private static SkriptAddon addonInstance;
    private static WebAddon instance;

    @Override
    public void onEnable() {
        instance = this;
        try {
            getAddonInstance().loadClasses("com.pikachu.webaddon", "skript");
            Classinfos.register();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static SkriptAddon getAddonInstance() {
        if (addonInstance == null) {
            addonInstance = Skript.registerAddon(getInstance());
        }
        return addonInstance;
    }

    public static WebAddon getInstance() {
        if (instance == null) {
            instance = new WebAddon();
        }
        return instance;
    }

}