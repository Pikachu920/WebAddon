package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import spark.Request;
import spark.Session;

public class ExprSession extends SimplePropertyExpression<Request, Session> {

	static {
		SimplePropertyExpression.register(ExprSession.class, Session.class, "session", "requests");
	}

	@Override
	protected String getPropertyName() {
		return "session";
	}

	@Override
	public Session convert(Request request) {
		return request.session(true);
	}

	@Override
	public Class<? extends Session> getReturnType() {
		return Session.class;
	}
}
