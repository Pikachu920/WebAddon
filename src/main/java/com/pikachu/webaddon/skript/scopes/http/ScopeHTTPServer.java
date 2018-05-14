package com.pikachu.webaddon.skript.scopes.http;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.pikachu.webaddon.bukkit.events.HTTPRequestEvent;
import com.pikachu.webaddon.bukkit.events.StubBukkitEvent;
import com.pikachu.webaddon.skript.scopes.http.requests.RequestScope;
import com.pikachu.webaddon.util.Util;
import com.pikachu.webaddon.util.scope.EventScope;
import org.bukkit.event.Event;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ScopeHTTPServer extends EventScope {

	private static boolean parsingRoute;
	private static Set<Integer> usedPorts = new HashSet<>();

	static {
		Skript.registerEvent("web server", ScopeHTTPServer.class, StubBukkitEvent.class,
				"[open a] (web|http) server on port <.+>");

		EventValues.registerEventValue(HTTPRequestEvent.class, Request.class, new Getter<Request, HTTPRequestEvent>() {
			@Override
			public Request get(HTTPRequestEvent arg) {
				return arg.getRequest();
			}
		}, 0);

		EventValues.registerEventValue(HTTPRequestEvent.class, Response.class, new Getter<Response, HTTPRequestEvent>() {
			@Override
			public Response get(HTTPRequestEvent arg) {
				return arg.getResponse();
			}
		}, 0);
	}

	private String stringRep;
	private int port;
	private List<SectionNode> rawNodes = new ArrayList<>();
	private Service server;

	public static boolean isParsingRoute() {
		return parsingRoute;
	}

	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
		SectionNode sectionNode = (SectionNode) SkriptLogger.getNode();
		stringRep = sectionNode.getKey();
		String stringPort = parseResult.regexes.get(0).group();
		if (stringPort.matches("\\d{1,5}")) {
			port = Integer.parseInt(stringPort);
			if (usedPorts.contains(port)) {
				Skript.error("There is already a web server running on port " + port);
				return false;
			}
		} else {
			Skript.error("'" + stringPort + "' is not a valid port");
			return false;
		}
		if (stringRep.startsWith("on")) {
			Skript.error("You may not use 'on' with a web server!");
			return false;
		}

		for (Node node : sectionNode) {
			Util.setKey(node, ScriptLoader.replaceOptions(node.getKey()));
			if (!(node instanceof SectionNode)) {
				Skript.error("A web server may only contain request scopes");
				return false;
			}
			if (RequestScope.getPatterns().stream().noneMatch(p -> node.getKey().matches(p))) {
				Skript.error("'" + node.getKey() + "' is not a request scope (e.g. 'get /index:')");
				return false;
			}
			rawNodes.add((SectionNode) node);
		}
		if (rawNodes.isEmpty()) {
			Skript.error("A web server without any routes is useless");
			return false;
		}
		Util.clearSectionNode(sectionNode);
		return true;
	}

	@Override
	public void load() {
		usedPorts.add(port);
		server = Service.ignite().port(port);
		for (SectionNode node : rawNodes) {
			RequestScope scope;
			try {
				parsingRoute = true;
				scope = (RequestScope) Condition.parse(node.getKey(), "Can't understand this scope: '" + node.getKey() + "'");
			} finally {
				parsingRoute = false;
			}
			if (scope != null) {
				TriggerSection trigger = Util.loadSectionNode(node, node.getKey(), true, "http event", HTTPRequestEvent.class);
				scope.setTrigger(trigger);
				scope.setServer(server);
				scope.check(null);
			}
		}
		rawNodes.clear();
	}

	@Override
	public void unregister(Trigger t) {
		// workaround for spark npe
		server.options("workaroundForSparkNpe" + UUID.randomUUID().toString(), (req, resp) -> "you shouldn't be seeing this");
		server.stop();
		usedPorts.remove(port);
	}

	@Override
	public void unregisterAll() {
		unregister(null);
	}

	@Override
	public String toString(Event e, boolean debug) {
		return stringRep;
	}

}
