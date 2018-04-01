package com.pikachu.webaddon.skript.scopes.http.requests;

import org.bukkit.event.Event;

public class ScopePost extends SimpleRequestScope {

	static {
		register(ScopePost.class, "post");
	}

	@Override
	public void start(Event e) {
		getServer().post(getPath(), this::run);
	}

}
