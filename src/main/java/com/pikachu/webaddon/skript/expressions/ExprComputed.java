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
import com.pikachu.webaddon.skript.effects.EffCompute;
import com.pikachu.webaddon.util.ConvertibleSimpleExpression;
import com.pikachu.webaddon.util.Util;
import org.bukkit.event.Event;
import spark.Request;

@Name("Computation Result")
@Description("Holds the result of the compute expression effect")
@Examples({"compute body of event-request",
		"broadcast \"%computed expression%\""})
public class ExprComputed<T> extends ConvertibleSimpleExpression<T> {

	static {
		Skript.registerExpression(ExprComputed.class, Object.class, ExpressionType.SIMPLE,
				"[last[ly]] computed (expr[ession]|result)");
	}

	private Expression<String> attribute;
	private Expression<Request> requests;
	private ExprComputed<?> source;
	private Class<T> superType;

	public ExprComputed() {
		this(null, (Class<? extends T>) Object.class);
	}

	private ExprComputed(ExprComputed<?> source, Class<? extends T>... types) {
		this.source = source;
		if (source != null) {
			this.attribute = source.attribute;
			this.requests = source.requests;
		}
		this.superType = (Class<T>) Utils.getSuperType(types);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		return true;
	}

	@Override
	protected T[] get(Event e) {
		try {
			return Util.convertStrictly(EffCompute.result, superType);
		} catch (ClassCastException e1) {
			return null;
		}
	}

	@Override
	public void change(Event e, Object[] delta, Changer.ChangeMode mode) {
		EffCompute.result = null;
	}

	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Class<? extends T> getReturnType() {
		return superType;
	}

	@Override
	public <R> Expression<? extends R> getConvertedExpression(Class<R>... to) {
		return new ExprComputed<>(this, to);
	}

	@Override
	public Expression<?> getSource() {
		return source == null ? this : source;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "computed expression";
	}

}
