package com.pikachu.webaddon.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.pikachu.webaddon.util.Util;
import org.bukkit.event.Event;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Arrays;

@Name("URL Encoded")
@Description("URL encodes a string")
@Examples("broadcast url safe \"cool this is url safe!\"")
public class ExprURLSafe extends SimpleExpression<String> {

	static {
		if (!Util.runningReqn()) {
			Skript.registerExpression(ExprURLSafe.class, String.class, ExpressionType.COMBINED,
					"(http|ur(i|l)) (safe|encoded|escaped) %strings%");
		}
	}

	private Expression<String> strings;

	public String encode(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}

	@Override
	protected String[] get(Event e) {
		return Arrays.stream(strings.getArray(e))
				.map(this::encode)
				.toArray(String[]::new);
	}

	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}

	@Override
	public boolean isSingle() {
		return strings.isSingle();
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "url safe " + strings.toString(e, debug);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		strings = (Expression<String>) exprs[0];
		return true;
	}

}
