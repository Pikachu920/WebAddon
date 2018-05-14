package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import spark.Request;
import spark.Response;

import java.util.Arrays;

@Name("Header")
@Description("A request/response's header")
@Examples({"set header \"Content-Type\" of event-response to \"text/css\"",
		"broadcast header \"Referrer\" of event-request"
})
public class ExprHeader extends SimpleExpression<String> {

	static {
		PropertyExpression.register(ExprHeader.class, String.class, "header %string%", "requests/responses");
	}

	private Expression<String> header;
	private Expression<Object> web;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		web = (Expression<Object>) exprs[matchedPattern ^ 1];
		header = (Expression<String>) exprs[matchedPattern];
		return true;
	}

	@Override
	protected String[] get(Event e) {
		String header = this.header.getSingle(e);
		return header == null ? new String[0] :
				Arrays.stream(web.getArray(e))
						.map(o -> o instanceof Request ?
								((Request) o).headers(header) : ((Response) o).raw().getHeader(header))
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
			Skript.error("Only response headers may be changed");
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
		String header = this.header.getSingle(e);
		if (header != null) {
			Arrays.stream(web.getArray(e))
					.filter(Response.class::isInstance)
					.map(o -> (Response) o)
					.forEach(response -> {
						switch (mode) {
							case SET:
								response.raw().setHeader(header, (String) delta[0]);
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
