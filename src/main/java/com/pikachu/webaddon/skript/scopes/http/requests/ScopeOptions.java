package com.pikachu.webaddon.skript.scopes.http.requests;

import org.bukkit.event.Event;

public class ScopeOptions extends SimpleRequestScope {

	static {
		register(ScopeOptions.class, "options");
	}

	@Override
	public void start(Event e) {
		getServer().options(getPath(), this::run);
	}

}
