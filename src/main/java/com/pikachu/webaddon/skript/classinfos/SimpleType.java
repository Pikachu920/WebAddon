package com.pikachu.webaddon.skript.classinfos;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.localization.Language;
import ch.njol.skript.registrations.Classes;
import com.pikachu.webaddon.util.ReflectionUtils;

import java.util.HashMap;
import java.util.Locale;

public abstract class SimpleType<T> extends ClassInfo<T> implements Changer<T> {

	/*
	 * Skript's language map, access reflectively to
	 * automate turning types.typename into just typename
	 */
	private static final HashMap<String, String> LANG_MAP =
			ReflectionUtils.getField(Language.class, null, "english");

	private String variableName;
	private String name;
	private String pattern;
	private Class<T> clz;

	public SimpleType(Class<T> clz, String name) {
		this(clz, name, name, ".+");
	}

	public SimpleType(Class<T> clz, String name, String pattern) {
		this(clz, name, pattern, ".+");
	}

	public SimpleType(Class<T> clz, String name, String pattern, String variableName) {
		super(clz, name.toLowerCase().replaceAll("\\s+", ""));
		this.clz = clz;
		this.name = name;
		this.pattern = pattern;
		this.variableName = variableName;
		LANG_MAP.put("types." + clz.getSimpleName().toLowerCase(Locale.ENGLISH), name);
		register();
	}

	public abstract String toString(T type, int flags);

	public String toVariableNameString(T type) {
		return toString(type, 0);
	}

	public T parse(String pattern, ParseContext context) {
		return null;
	}

	public boolean canParse(ParseContext context) {
		return false;
	}

	@Override
	public Class<?>[] acceptChange(ChangeMode mode) {
		return null;
	}

	@Override
	public void change(T[] source, Object[] set, ChangeMode mode) {

	}

	private void register() {
		try {
			Classes.registerClass(user(pattern)
					.defaultExpression(new EventValueExpression<>(clz))
					.parser(new Parser<T>() {
						@Override
						public String getVariableNamePattern() {
							return variableName;
						}

						@Override
						public boolean canParse(ParseContext context) {
							return SimpleType.this.canParse(context);
						}

						@Override
						public T parse(String pattern, ParseContext context) {
							return SimpleType.this.parse(pattern, context);
						}

						@Override
						public String toString(T type, int flags) {
							return SimpleType.this.toString(type, flags);
						}

						@Override
						public String toVariableNameString(T type) {
							return SimpleType.this.toVariableNameString(type);
						}
					}));
		} catch (Exception e) {
			Skript.warning("Couldn't register the type '" + name + "'. Due to: " + (e.getMessage() != null && !e.getMessage().isEmpty() ? e.getMessage() : "unknown"));
		}

	}

}