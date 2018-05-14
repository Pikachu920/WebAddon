package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import org.bukkit.event.Event;
import spark.Request;
import spark.Response;

import java.util.Arrays;

@Name("Body")
@Description("The body of a request/response. Note that for large bodies, " +
		"you may want to compute them via the compute effect as large bodies (e.g. images) " +
		"could crash your server.")
public class ExprBody extends SimplePropertyExpression<Object, String> {

	static {
		register(ExprBody.class, String.class, "bod(y|ies)", "requests/responses");
	}

	@Override
	public String convert(Object o) {
		if (o instanceof Request) {
			return ((Request) o).body();
		}
		return ((Response) o).body();
	}

	@Override
	protected String getPropertyName() {
		return "body";
	}

	@Override
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		Class returnType = getExpr().getReturnType();
		if (returnType != Response.class && returnType != Object.class) {
			Skript.error("Only response bodies may be changed");
			return null;
		}
		if (mode == Changer.ChangeMode.SET ||
				mode == Changer.ChangeMode.ADD ||
				mode == Changer.ChangeMode.DELETE) {
			return new Class<?>[]{String.class};
		}
		return null;
	}

	@Override
	public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
		Arrays.stream(getExpr().getArray(e))
				.filter(Response.class::isInstance)
				.map(Response.class::cast)
				.forEach(response -> {
					switch (mode) {
						case ADD:
							if (response.body() == null)
								response.body((String) delta[0]);
							else
								response.body(response.body() + delta[0]);
							break;
						case SET:
							response.body((String) delta[0]);
							break;
						case DELETE:
							response.body(null);
					}
				});
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}
