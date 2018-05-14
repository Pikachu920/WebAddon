package com.pikachu.webaddon.skript.scopes.http.requests;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pikachu.webaddon.skript.scopes.http.ScopeHTTPServer;
import com.pikachu.webaddon.util.Util;
import org.bukkit.event.Event;

public abstract class SimpleRequestScope extends RequestScope {

	private String path;
	private boolean started;

	public static void register(Class<? extends SimpleRequestScope> clazz, String type) {
		RequestScope.register(clazz, type + " <.+>");
	}

	@Override
	public final boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		if (parseResult.regexes.size() != 1) {
			throw new UnsupportedOperationException("A " + getClass().getSimpleName() + " must have only one regex!");
		} else if (!ScopeHTTPServer.isParsingRoute()) {
			return false;
		}
		path = Util.parsePath(parseResult.regexes.get(0).group());
		return path != null;
	}

	@Override
	public final void execute(Event e) {
		if (!started) {
			start(e);
			started = true;
		}
	}

	public final String getPath() {
		return path;
	}

	public abstract void start(Event e);

	@Override
	public final String toString(Event e, boolean debug) {
		return getInScript();
	}

}
