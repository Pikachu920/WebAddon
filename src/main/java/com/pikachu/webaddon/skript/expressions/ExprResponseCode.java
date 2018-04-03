package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.event.Event;
import spark.Request;
import spark.Response;

public class ExprResponseCode extends SimplePropertyExpression<Response, Integer> {

	static {
		register(ExprResponseCode.class, Integer.class, "[(status|response)] code[s]", "responses");
	}

	@Override
	public Integer convert(Response response) {
		return response.status();
	}

	@Override
	protected String getPropertyName() {
		return "status code";
	}

	@Override
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.SET) {
			return new Class[] { Number.class }; // Integer causes weirdness in skript
		}
		return null;
	}

	@Override
	public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
		for (Response response : getExpr().getArray(e))
			response.status(((Number) delta[0]).intValue());
	}

	@Override
	public Class<? extends Integer> getReturnType() {
		return Integer.class;
	}

}
