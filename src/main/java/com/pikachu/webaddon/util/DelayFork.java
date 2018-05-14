package com.pikachu.webaddon.util;

import ch.njol.skript.effects.Delay;
import org.bukkit.event.Event;

public abstract class DelayFork extends Delay {

	public static void addDelayedEvent(Event event) {
		delayed.add(event);
	}

}
