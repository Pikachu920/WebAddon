package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import spark.Request;

public class ExprUserAgent extends SimplePropertyExpression<Request, String> {

	static {
		SimplePropertyExpression.register(ExprUserAgent.class, String.class,
				"user[ ]agent", "requests");
	}

	@Override
	protected String getPropertyName() {
		return "user agent";
	}

	@Override
	public String convert(Request request) {
		return request.userAgent();
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

}
