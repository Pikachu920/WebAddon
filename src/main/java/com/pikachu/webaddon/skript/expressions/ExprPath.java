package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import spark.Request;

public class ExprPath extends SimplePropertyExpression<Request, String> {

	static {
		register(ExprPath.class, String.class, "path", "requests");
	}

	@Override
	public String convert(Request request) {
		return request.pathInfo();
	}

	@Override
	protected String getPropertyName() {
		return "path";
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}
