package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import spark.Request;
import spark.Response;

import java.util.Arrays;

public class ExprCookie extends SimpleExpression<String> {

	static {
		PropertyExpression.register(ExprCookie.class, String.class, "cookie %string%", "requests/responses");
	}

	private Expression<String> cookie;
	private Expression<Object> web;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		web = (Expression<Object>) exprs[matchedPattern ^ 1];
		cookie = (Expression<String>) exprs[matchedPattern];
		return true;
	}

	@Override
	protected String[] get(Event e) {
		String cookie = this.cookie.getSingle(e);
		return cookie == null ? new String[0] :
				Arrays.stream(web.getArray(e))
						.filter(Request.class::isInstance)
						.map(o -> ((Request) o).cookie(cookie))
						.toArray(String[]::new);
	}

	@Override
	public boolean isSingle() {
		return web.isSingle();
	}

	@Override
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		Class returnType = web.getReturnType();
		if (returnType != Response.class && returnType != Object.class) {
			Skript.error("Only response cookies may be changed");
			return null;
		}
		if (mode == Changer.ChangeMode.SET ||
				mode == Changer.ChangeMode.DELETE) {
			return new Class<?>[]{String.class};
		}
		return null;
	}

	@Override
	public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
		String cookie = this.cookie.getSingle(e);
		if (cookie != null) {
			Arrays.stream(web.getArray(e))
					.filter(Response.class::isInstance)
					.map(o -> (Response) o)
					.forEach(response -> {
						switch (mode) {
							case SET:
								response.cookie(cookie, (String) delta[0]);
								break;
							case DELETE:
								response.raw().setHeader(cookie, null);
						}
					});
		}
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "cookie " + cookie.toString(e, debug) + " of " + web.toString(e, debug);
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}
