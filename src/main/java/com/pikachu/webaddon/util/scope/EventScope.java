package com.pikachu.webaddon.util.scope;

import ch.njol.skript.config.Config;
import ch.njol.skript.lang.SelfRegisteringSkriptEvent;
import ch.njol.skript.lang.Trigger;

public abstract class EventScope extends SelfRegisteringSkriptEvent {

    private boolean loaded;

    @Override
    public final void register(Trigger t) {
        if (!loaded) {
            load();
            loaded = true;
        }
    }

    @Override
    public final void afterParse(Config config) {
        register(null);
    }

    public abstract void load();

}
