package com.pikachu.webaddon.skript.scopes.http.requests;

import org.bukkit.event.Event;

public class ScopePatch extends SimpleRequestScope {

	static {
		register(ScopePatch.class, "patch");
	}

	@Override
	public void start(Event e) {
		getServer().patch(getPath(), this::run);
	}

}
