package com.pikachu.webaddon.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pikachu.webaddon.util.AsyncEffect;
import com.pikachu.webaddon.util.LiteralUtils;
import org.bukkit.event.Event;

@Name("Compute Expression")
@Description("Computes an expression on another thread (aka in a way that doesn't lag your server). " +
		"This is useful for requests which have bodies that take a long time to compute (e.g. images).")
@Examples({"compute body of event-request",
		"broadcast \"%computed expression%\""})
public class EffCompute extends AsyncEffect {

	public static Object[] result;

	static {
		Skript.registerEffect(EffCompute.class, "compute %objects%");
	}

	private Expression<Object> objects;

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		objects = LiteralUtils.defendExpression(exprs[0]);
		return LiteralUtils.canInitSafely(objects);
	}

	@Override
	protected void execute(Event e) {
		result = objects.getArray(e);
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "compute " + objects.toString(e, debug);
	}
}
