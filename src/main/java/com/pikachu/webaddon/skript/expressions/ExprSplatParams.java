package com.pikachu.webaddon.skript.expressions;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Name("Splat Parameters")
@Description("The \"splat\" parameters (e.g. if you had scope 'get /user/*:' the splat parameter" +
		"would be the text represented by the *.")
@Examples("set {_l::*} to splat parameters")
public class ExprSplatParams extends SimpleExpression<String> {

	static {
		PropertyExpression.register(ExprSplatParams.class, String.class,
				"splat param[eter][s]", "requests");
	}

	private Expression<Request> requests;

	@Override
	protected String[] get(Event e) {
		List<String> splat = new ArrayList<>();
		for (Request request : requests.getArray(e)) {
			Collections.addAll(splat, request.splat());
		}
		return splat.toArray(new String[0]);
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "splat parameters of " + requests.toString(e, debug);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		requests = (Expression<Request>) exprs[0];
		return true;
	}

}
