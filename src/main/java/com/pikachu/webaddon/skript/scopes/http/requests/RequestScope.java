package com.pikachu.webaddon.skript.scopes.http.requests;

import ch.njol.skript.Skript;
import ch.njol.skript.variables.Variables;
import com.pikachu.webaddon.bukkit.events.HTTPRequestEvent;
import com.pikachu.webaddon.util.scope.EffectSection;
import org.bukkit.event.Event;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public abstract class RequestScope extends EffectSection {

	private static Set<String> patterns = new HashSet<>();
	private Service server;

	public static Set<String> getPatterns() {
		return Collections.unmodifiableSet(patterns);
	}

	public static void register(Class<? extends RequestScope> clazz, String... patterns) {
		Skript.registerCondition(clazz, patterns);
		for (int i = 0; i < patterns.length; i++) {
			RequestScope.patterns.add(patterns[i]
					.replaceAll("%(number|integer)%", "(\\d+)")
					.replaceAll("<(.+?)>", "($1)")
			);
		}
	}

	public String run(Request request, Response response) {
		HTTPRequestEvent event = new HTTPRequestEvent(request, response);
		setParamVars(request, event);
		runSection(event);
		return event.getResponse().body();
	}

	public Service getServer() {
		return server;
	}

	public void setServer(Service server) {
		this.server = server;
	}

	public void setParamVars(Request request, Event event) {
		request.params()
				.forEach(
						(param, value) -> Variables.setVariable(param.substring(1), value, event, true)
				);
	}

}
