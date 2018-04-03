package com.pikachu.webaddon.skript.scopes.http.requests;

import org.bukkit.event.Event;

public class ScopeConnect extends SimpleRequestScope {

	static {
		register(ScopeConnect.class, "connect");
	}

	@Override
	public void start(Event e) {
		getServer().connect(getPath(), this::run);
	}

}
