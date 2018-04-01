package com.pikachu.webaddon.skript.effects;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import spark.Response;

import java.util.Arrays;

public class EffSendBack extends Effect {

	static {
		Skript.registerEffect(EffSendBack.class, "send back %string%", "send %string% back");
	}

	private Expression<String> response;

	@Override
	public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (ScriptLoader.getCurrentEvents() != null && Arrays.stream(ScriptLoader.getCurrentEvents())
				.anyMatch(event -> EventValues.getEventValueGetter(event, Response.class, 0) != null)) {
			response = (Expression<String>) exprs[0];
			return true;
		}
		Skript.error("You may only use 'send back' in events with a response");
		return false;
	}

	@Override
	protected void execute(Event e) {
		Response response = EventValues.getEventValue(e, Response.class, 0);
		String body = this.response.getSingle(e);
		if (response != null && body != null) {
			response.body(body);
		}
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "send back " + response.toString(e, debug);
	}
}
