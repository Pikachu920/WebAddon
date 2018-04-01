package com.pikachu.webaddon.skript.scopes.http;

import ch.njol.skript.ScriptLoader;
import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.skript.lang.TriggerSection;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import ch.njol.skript.variables.Variables;
import com.pikachu.webaddon.bukkit.events.HTTPRequestEvent;
import com.pikachu.webaddon.bukkit.events.StubBukkitEvent;
import com.pikachu.webaddon.skript.scopes.http.requests.RequestScope;
import com.pikachu.webaddon.util.Util;
import com.pikachu.webaddon.util.scope.EffectSection;
import com.pikachu.webaddon.util.scope.EventScope;
import org.bukkit.event.Event;
import spark.Request;
import spark.Response;
import spark.Service;
import spark.servlet.SparkApplication;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ScopeHTTPServer extends EventScope {

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
    private static Condition cond;
    private List<TriggerSection> triggers = new ArrayList<>();
    private Service server;

    @Override
    public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
        SectionNode sectionNode = (SectionNode) SkriptLogger.getNode();
        stringRep = sectionNode.getKey();
        String stringPort = parseResult.regexes.get(0).group();
        if (stringPort.matches("\\d{1,5}")) {
            port = Integer.parseInt(parseResult.regexes.get(0).group());
        } else {
            Skript.error("'" + stringPort + "' is not a valid port");
            return false;
        }
        if (stringRep.startsWith("on")) {
            Skript.error("You can't use 'on' with a web server!");
            return false;
        }

        for (Node node : sectionNode) {
            Util.setKey(node, ScriptLoader.replaceOptions(node.getKey()));
            if (!(node instanceof SectionNode)) {
                Skript.error("A web server can only contain request scopes");
                return false;
            }
            if (RequestScope.getPatterns().stream().noneMatch(p -> node.getKey().matches(p))) {
                Skript.error("'" + node.getKey() + "' is not a request scope (e.g. 'get /index:')");
                return false;
            }
            rawNodes.add((SectionNode) node);
        }
        Util.clearSectionNode(sectionNode);
        return true;
    }

    @Override
    public void load() {
        server = Service.ignite().port(port);
        for (SectionNode node : rawNodes) {
            RequestScope scope = (RequestScope) Condition.parse(node.getKey(), "Can't understand this scope: '" + node.getKey() + "'");
            if (scope != null) {
                TriggerSection trigger = Util.loadSectionNode(node, node.getKey(), true, "http event", HTTPRequestEvent.class);
                scope.setTrigger(trigger);
                scope.setServer(server);
                scope.check(null);
                triggers.add(trigger);
            }
        }
    }

    @Override
    public void unregister(Trigger t) {
        server.stop();
    }

    @Override
    public void unregisterAll() {
    }

    @Override
    public String toString(Event e, boolean debug) {
        return stringRep;
    }

}
