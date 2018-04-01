package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import spark.Request;

public class ExprIP extends SimplePropertyExpression<Request, String> {

	static {
		register(ExprIP.class, String.class, "ip", "requests");
	}

	@Override
	public String convert(Request request) {
		return request.ip();
	}

	@Override
	protected String getPropertyName() {
		return "ip";
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}


}
