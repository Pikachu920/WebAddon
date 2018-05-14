package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import spark.Request;
import spark.Session;

@Name("Session")
@Description("The session of a request (something you can use to store data about a user, for example who they are)")
@Examples("broadcast \"%session of event-request%\"")
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
