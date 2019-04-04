package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import spark.Request;

@Name("IP address")
@Description("The IP address of a request")
@Examples("send back \"Woah bro! Your IP address is %request-IP of event-request%\"")
public class ExprIP extends SimplePropertyExpression<Request, String> {

	static {
		register(ExprIP.class, String.class, "request(-| )IP", "requests");
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
