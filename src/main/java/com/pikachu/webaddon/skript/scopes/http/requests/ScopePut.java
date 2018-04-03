package com.pikachu.webaddon.skript.scopes.http.requests;

import org.bukkit.event.Event;

public class ScopePut extends SimpleRequestScope {

	static {
		register(ScopePut.class, "put");
	}

	@Override
	public void start(Event e) {
		getServer().put(getPath(), this::run);
	}

}
