package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.pikachu.webaddon.util.ConvertibleSimpleExpression;
import com.pikachu.webaddon.util.Util;
import org.bukkit.event.Event;
import spark.Request;

import java.util.stream.Stream;

@Name("Attribute")
@Description("An attribute of a request")
@Examples("set attribute \"handler\" of event-request to \"CoolServer2\"")
public class ExprAttribute<T> extends ConvertibleSimpleExpression<T> {

	static {
		Skript.registerExpression(ExprAttribute.class, Object.class, ExpressionType.PROPERTY,
				"attribute %string% of %requests%",
				"%requests%'[s] attribute %string%"
		);
	}

	private Expression<String> attribute;
	private Expression<Request> requests;
	private ExprAttribute<?> source;
	private Class<T> superType;

	public ExprAttribute() {
		this(null, (Class<? extends T>) Object.class);
	}

	private ExprAttribute(ExprAttribute<?> source, Class<? extends T>... types) {
		this.source = source;
		if (source != null) {
			this.attribute = source.attribute;
			this.requests = source.requests;
		}
		this.superType = (Class<T>) Utils.getSuperType(types);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		requests = (Expression<Request>) exprs[matchedPattern ^ 1];
		attribute = (Expression<String>) exprs[matchedPattern];
		return true;
	}

	@Override
	protected T[] get(Event e) {
		String attribute = this.attribute.getSingle(e);
		if (attribute == null) {
			return null;
		}
		try {
			return Util.convertStrictly(
					Stream.of(requests.getArray(e)).map(r -> r.attribute(attribute)).toArray(),
					superType
			);
		} catch (ClassCastException e1) {
			return null;
		}
	}

	@Override
	public Class<?>[] acceptChange(Changer.ChangeMode mode) {
		if (mode == Changer.ChangeMode.DELETE || mode == Changer.ChangeMode.SET) {
			return CollectionUtils.array(Object.class);
		}
		return null;
	}

	@Override
	public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
		String attribute = this.attribute.getSingle(e);
		if (attribute == null) {
			return;
		}
		Stream.of(requests.getArray(e)).forEach(r -> {
			switch (mode) {
				case DELETE:
					r.attribute(attribute, null);
					break;
				case SET:
					r.attribute(attribute, delta[0]);
			}
		});
	}

	@Override
	public boolean isSingle() {
		return requests.isSingle();
	}

	@Override
	public Class<? extends T> getReturnType() {
		return superType;
	}

	@Override
	public <R> Expression<? extends R> getConvertedExpression(Class<R>... to) {
		return new ExprAttribute<>(this, to);
	}

	@Override
	public Expression<?> getSource() {
		return source == null ? this : source;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "attribute " + attribute.toString(e, debug) + " of " + requests.toString(e, debug);
	}

}
