package com.pikachu.webaddon.skript.scopes.http.requests;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.pikachu.webaddon.bukkit.events.HTTPRequestEvent;
import com.pikachu.webaddon.util.Util;
import org.bukkit.event.Event;

public class ScopeGet extends SimpleRequestScope {

    static {
        register(ScopeGet.class,  "get");
    }

    @Override
    public void start(Event e) {
        getServer().get(getPath(), this::run);
    }

}
