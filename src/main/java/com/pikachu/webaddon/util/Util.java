package com.pikachu.webaddon.util;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.lang.Variable;
import ch.njol.skript.lang.VariableString;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.registrations.Converters;
import ch.njol.skript.util.StringMode;
import ch.njol.util.Kleenean;
import com.pikachu.webaddon.util.scope.EffectSection;
import org.bukkit.Bukkit;
import org.bukkit.event.Event;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Util {

	public static final Field VARIABLE_NAME;
	private static final Field KEY;
	private static final Field VARIABLE_STRING_DATA;
	public static boolean variableNameGetterExists = Skript.methodExists(Variable.class, "getName");

	static {
		Field _KEY = null;
		try {
			_KEY = Node.class.getDeclaredField("key");
			_KEY.setAccessible(true);
		} catch (NoSuchFieldException e) {
			Skript.warning("Skript's node key field could not be resolved.");
		}
		KEY = _KEY;
	}

	static {
		Field _VARIABLE_STRING_DATA = null;
		try {
			_VARIABLE_STRING_DATA = VariableString.class.getDeclaredField("string");
			_VARIABLE_STRING_DATA.setAccessible(true);
		} catch (NoSuchFieldException e) {
			Skript.warning("Skript's variable string data field could not be resolved.");
		}
		VARIABLE_STRING_DATA = _VARIABLE_STRING_DATA;
	}

	static {
		if (!variableNameGetterExists) {
			Field _VARIABLE_NAME = null;
			try {
				_VARIABLE_NAME = Variable.class.getDeclaredField("name");
				_VARIABLE_NAME.setAccessible(true);
			} catch (NoSuchFieldException e) {
				e.printStackTrace();
				Skript.error("Skript's variable name field could not be resolved.");
			}
			VARIABLE_NAME = _VARIABLE_NAME;
		} else {
			VARIABLE_NAME = null;
		}
	}

	public static VariableString getVariableName(Variable<?> var) {
		if (variableNameGetterExists) {
			return var.getName();
		} else {
			try {
				return (VariableString) VARIABLE_NAME.get(var);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void setKey(Node node, String key) {
		try {
			KEY.set(node, key);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}

	public static void clearSectionNode(SectionNode sectionNode) {
		List<Node> nodes = new ArrayList<>();
		for (Iterator<Node> iterator = sectionNode.iterator(); iterator.hasNext(); ) {
			nodes.add(iterator.next());
		}
		for (Node n : nodes) {
			sectionNode.remove(n);
		}
	}

	public static TriggerSection loadSectionNode(SectionNode node, String stringrep,
												 boolean printErrors, String eventName,
												 Class<? extends Event>... events) {

		Class<? extends Event>[] originalEvents = ScriptLoader.getCurrentEvents();
		String originalName = ScriptLoader.getCurrentEventName();
		Kleenean originalDelay = ScriptLoader.hasDelayBefore;

		ScriptLoader.hasDelayBefore = Kleenean.FALSE;
		ScriptLoader.setCurrentEvent(eventName, events);

		TriggerSection triggerSection = loadSectionNode(node, stringrep, printErrors);

		ScriptLoader.setCurrentEvent(originalName, originalEvents);
		ScriptLoader.hasDelayBefore = originalDelay;

		return triggerSection;

	}

	public static TriggerSection loadSectionNode(SectionNode node, String stringRep, boolean printErrors) {
		TriggerSection trigger;
		RetainingLogHandler errors = null;
		if (printErrors) {
			errors = SkriptLogger.startRetainingLog();
		}
		try {
			trigger = new TriggerSection(node) {
				@Override
				public String toString(Event event, boolean b) {
					return stringRep;
				}

				@Override
				public TriggerItem walk(Event event) {
					return walk(event, true);
				}
			};
		} finally {
			if (printErrors) {
				EffectSection.stopLog(errors);
			}
		}
		return trigger;
	}

	/**
	 * Strictly converts an array to a non-null array of the specified class.
	 * Uses registered {@link ch.njol.skript.registrations.Converters} to convert.
	 *
	 * @param original The array to convert
	 * @param to       What to convert {@code original} to
	 * @return {@code original} converted to an array of {@code to}
	 * @throws ClassCastException if one of {@code original}'s
	 *                            elements cannot be converted to a {@code to}
	 */
	@SuppressWarnings("unchecked")
	public static <T> T[] convertStrictly(Object[] original, Class<T> to) throws ClassCastException {
		if (original == null) {
			return (T[]) Array.newInstance(to, 0);
		}
		T[] end = (T[]) Array.newInstance(to, original.length);
		for (int i = 0; i < original.length; i++) {
			T converted = Converters.convert(original[i], to);
			if (converted != null) {
				end[i] = converted;
			} else {
				throw new ClassCastException();
			}
		}
		return end;
	}

	/**
	 * Strictly converts an object to the specified class
	 *
	 * @param original The object to convert
	 * @param to       What to convert {@code original} to
	 * @return {@code original} converted to a {@code to}
	 * @throws ClassCastException if {@code original} could not be converted to a {@code to}
	 */
	public static <T> T convertStrictly(Object original, Class<T> to) throws ClassCastException {
		T converted = Converters.convert(original, to);
		if (converted != null) {
			return converted;
		} else {
			throw new ClassCastException();
		}
	}

	public static String parsePath(String path) {
		VariableString varString = VariableString.newInstance(path, StringMode.MESSAGE);
		if (!varString.isSimple()) {
			RetainingLogHandler errors = SkriptLogger.startRetainingLog();
			try {
				Object[] data = (Object[]) VARIABLE_STRING_DATA.get(varString);
				for (int i = 0; i < data.length; i++) {
					if (!(data[i] instanceof String)) {
						Expression<?> expr = ReflectionUtils.getField(data[i].getClass(), data[i], "expr");
						if (!(expr instanceof Variable<?>)) {
							Skript.error("A path may only contain variables");
							return null;
						}
						Variable variable = (Variable<?>) expr;
						if (!variable.isLocal()) {
							Skript.error("Path variable must be local");
							return null;
						}
						VariableString name = getVariableName(variable);
						if (!name.isSimple()) {
							Skript.error("A path variable may not contain expressions");
							return null;
						}
						data[i] = ":" + name.toString(null);
					}
				}
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} finally {
				EffectSection.stopLog(errors);
			}
		}
		return varString.toString(null);
	}

	public static boolean runningReqn() {
		return Bukkit.getServer().getPluginManager().getPlugin("Reqn") != null;
	}

}
