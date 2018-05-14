package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.event.Event;
import spark.Response;

@Name("Status Code")
@Description("The status code of a response (e.g. 404)")
@Examples("set status code of event-response to 404")
public class ExprStatusCode extends SimplePropertyExpression<Response, Integer> {

	static {
		register(ExprStatusCode.class, Integer.class, "status code[s]", "responses");
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
			return new Class[]{Number.class}; // Integer causes weirdness in skript
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
