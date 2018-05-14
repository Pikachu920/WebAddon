package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import spark.Request;

@Name("User agent")
@Description("The user agent of a request")
@Examples("broadcast user agent of event-request")
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
