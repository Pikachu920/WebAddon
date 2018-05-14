package com.pikachu.webaddon.skript.effects;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import spark.Response;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@Name("Send File")
@Description("Sends a file (e.g. an image)")
@Examples({"open a web server on port 8080:",
		"\tget /images/%{_image}%:",
		"\t\tsend back file \"website/images/%{_image}%\""
})
public class EffSendBackFile extends Effect {

	static {
		Skript.registerEffect(EffSendBackFile.class, "send back file %string%");
	}

	private Expression<String> file;

	@Override
	public boolean init(Expression<?>[] exprs, int i, Kleenean kleenean, SkriptParser.ParseResult parseResult) {
		if (ScriptLoader.getCurrentEvents() != null && Arrays.stream(ScriptLoader.getCurrentEvents())
				.anyMatch(event -> EventValues.getEventValueGetter(event, Response.class, 0) != null)) {
			file = (Expression<String>) exprs[0];
			return true;
		}
		Skript.error("You may only use 'send back' in events with a response");
		return false;
	}

	@Override
	protected void execute(Event e) {
		Response response = EventValues.getEventValue(e, Response.class, 0);
		String file = this.file.getSingle(e);
		if (response != null && file != null) {
			try {
				byte[] fileData = Files.readAllBytes(Paths.get(file));
				HttpServletResponse raw = response.raw();
				raw.getOutputStream().write(fileData);
				raw.getOutputStream().flush();
				raw.getOutputStream().close();
			} catch (IOException ignored) {
			}
		}
	}

	@Override
	public String toString(Event e, boolean debug) {
		return "send back file" + file.toString(e, debug);
	}

}
