package com.pikachu.webaddon.skript.scopes.http.requests;

import org.bukkit.event.Event;

public class ScopeHead extends SimpleRequestScope {

	static {
		register(ScopeHead.class, "head");
	}

	@Override
	public void start(Event e) {
		getServer().head(getPath(), this::run);
	}

}
