package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Date;
import spark.Session;

@Name("Creation Date")
@Description("The date a session was created")
@Examples("if difference between now and creation date of event-request's session is less than 5 minutes:")
public class ExprCreationDate extends SimplePropertyExpression<Session, Date> {

	static {
		PropertyExpression.register(ExprCreationDate.class, Date.class, "creation date", "sessions");
	}

	@Override
	public Date convert(Session session) {
		return new Date(session.creationTime());
	}

	@Override
	protected String getPropertyName() {
		return "creation date";
	}

	@Override
	public Class<? extends Date> getReturnType() {
		return Date.class;
	}

}
