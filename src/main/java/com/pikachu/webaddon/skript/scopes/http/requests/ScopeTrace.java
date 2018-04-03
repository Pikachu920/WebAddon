package com.pikachu.webaddon.skript.scopes.http.requests;

import org.bukkit.event.Event;

public class ScopeTrace extends SimpleRequestScope {

	static {
		register(ScopeTrace.class, "trace");
	}

	@Override
	public void start(Event e) {
		getServer().trace(getPath(), this::run);
	}

}
