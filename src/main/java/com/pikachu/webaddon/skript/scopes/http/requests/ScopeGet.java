package com.pikachu.webaddon.skript.scopes.http.requests;

import org.bukkit.event.Event;

public class ScopeGet extends SimpleRequestScope {

	static {
		register(ScopeGet.class, "get");
	}

	@Override
	public void start(Event e) {
		getServer().get(getPath(), this::run);
	}

}
