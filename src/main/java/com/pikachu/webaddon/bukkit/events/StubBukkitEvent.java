package com.pikachu.webaddon.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * A bukkit event that won't ever be called,
 * for registration purposes.
 */
public final class StubBukkitEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return handlerList;
    }

    private StubBukkitEvent() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
