package com.pikachu.webaddon.skript.scopes;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pikachu.webaddon.skript.expressions.ExprMultilineText;
import com.pikachu.webaddon.util.scope.EffectSection;
import org.bukkit.event.Event;

import java.lang.reflect.Field;
import java.util.List;

public class ScopeText extends EffectSection {

	/*
	 * This is required because SectionNode's iterator does not include InvalidNodes
	 * Under normal circumstances the correct way to do this is just an enhanced for
	 * loop on the SectionNode like for (Node n : sectionNode)
	 */
	private static final Field NODE_LIST;

	static {
		Skript.registerCondition(ScopeText.class, "[multi[(-| )]line] (text|string)");

		Field _NODE_LIST = null;
		try {
			_NODE_LIST = SectionNode.class.getDeclaredField("nodes");
			_NODE_LIST.setAccessible(true);
		} catch (NoSuchFieldException e) {
			Skript.error("Couldn't resolve Skript's 'node list' field. The multiline text scope will not work");
		}
		NODE_LIST = _NODE_LIST;

	}

	private String text;

	private String nodeToString(SectionNode node, boolean appendKey) throws IllegalAccessException {
		StringBuilder builder = appendKey ? new StringBuilder(node.getKey()) : new StringBuilder();
		for (Node n : (List<Node>) NODE_LIST.get(node)) {
			builder.append("\n");
			builder.append(n.getKey());
			if (n instanceof SectionNode) {
				builder.append(nodeToString((SectionNode) n, true));
			}
		}
		return builder.length() == 0 ? null : builder.toString().substring(1);
	}

	@Override
	public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		if (checkIfCondition(false)) {
			Skript.error("You may not use 'if' or 'else if' with a multiline text scope");
			return false;
		} else if (!hasSection()) {
			Skript.error("A multiline text scope may not stand alone");
			return false;
		} else if (NODE_LIST == null) {
			Skript.error("Multiline text scopes are not compatible with this Skript version");
			return false;
		}

		try {
			text = nodeToString(getSectionNode(), false);
		} catch (IllegalAccessException e) {
			Skript.error("Failed to extract text from multiline scope");
			return false;
		}

		return true;
	}

	@Override
	protected void execute(Event e) {
		ExprMultilineText.lastText = text;
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "multiline string";
	}

}
