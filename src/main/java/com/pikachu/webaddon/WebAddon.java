package com.pikachu.webaddon;

import ch.njol.skript.Skript;
import ch.njol.skript.SkriptAddon;
import com.pikachu.webaddon.util.VoidLogger;
import org.bukkit.plugin.java.JavaPlugin;
import org.eclipse.jetty.util.log.Log;

import java.io.IOException;

public class WebAddon extends JavaPlugin {

	private static SkriptAddon addonInstance;
	private static WebAddon instance;

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

	@Override
	public void onEnable() {
		// disable all logging with various methods
		Log.setLog(new VoidLogger());
		System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
		System.setProperty("org.eclipse.jetty.LEVEL", "OFF");
		instance = this;
		try {
			getAddonInstance().loadClasses("com.pikachu.webaddon", "skript");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}