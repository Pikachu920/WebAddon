package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ExprHeader extends SimpleExpression<String> {

	static {
		PropertyExpression.register(ExprHeader.class, String.class, "header %string%", "requests/responses");
	}

	private Expression<String> header;
	private Expression<Object> web;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
			web = (Expression<Object>) (matchedPattern == 1 ? exprs[0] : exprs[1]);
			header = (Expression<String>) (matchedPattern == 1 ? exprs[1] : exprs[0]);
			return true;
	}

	@Override
	protected String[] get(Event e) {
		String header = this.header.getSingle(e);
		if (header == null) {
			return new String[0];
		}
		List<String> headers = new ArrayList<>();

		for (Object o : web.getArray(e)) {
			if (o instanceof Request)
				headers.add(((Request) o).headers(header));
			else
				headers.add(((Response) o).raw().getHeader(header));
		}

		return headers.toArray(new String[0]);
	}

	@Override
	public boolean isSingle() {
		return web.isSingle();
	}

	@Override
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		Class returnType = web.getReturnType();
		if (returnType != Response.class && returnType != Object.class) {
			Skript.error("Only response headers may be changed");
			return null;
		}
		if (mode == Changer.ChangeMode.SET ||
				mode == Changer.ChangeMode.DELETE) {
			return new Class<?>[]{ String.class };
		}
		return null;
	}

	@Override
	public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
		String header = this.header.getSingle(e);
		if (header != null) {
			Arrays.stream(web.getArray(e))
					.filter(Response.class::isInstance)
					.map(o -> (Response) o)
					.forEach(response -> {
						switch (mode) {
							case SET:
								response.header(header, (String) delta[0]);
								break;
							case DELETE:
								response.raw().setHeader(header, null);
						}
					});
		}
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "header " + header.toString(e, debug) + " of " + web.toString(e, debug);
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}
