package com.pikachu.webaddon.skript.scopes.http.requests;

import org.bukkit.event.Event;

public class ScopeDelete extends SimpleRequestScope {

	static {
		register(ScopeDelete.class, "delete");
	}

	@Override
	public void start(Event e) {
		getServer().delete(getPath(), this::run);
	}

}
