package com.pikachu.webaddon.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import spark.Response;

public class EffRedirect extends Effect {

	static {
		Skript.registerEffect(EffRedirect.class, "redirect %responses% to %string% [with [the] (response|status) code %number%]");
	}

	private Expression<Response> responses;
	private Expression<String> url;
	private Expression<Number> status;


	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		responses = (Expression<Response>) exprs[0];
		url = (Expression<String>) exprs[1];
		status = (Expression<Number>) exprs[2];
		return true;
	}

	@Override
	protected void execute(Event e) {
		String url = this.url.getSingle(e);
		if (url != null) {
			for (Response response : responses.getAll(e)) {
				if (status != null) {
					Number status = this.status.getSingle(e);
					if (status != null) {
						response.redirect(url, status.intValue());
					}
				} else {
					response.redirect(url);
				}
			}
		}
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "redirect " + responses.toString(e, debug) + " to " + url.toString(e, debug) + (status == null ? "" : " with the status code "  +status.toString(e, debug));
	}

}
