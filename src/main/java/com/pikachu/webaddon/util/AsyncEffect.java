package com.pikachu.webaddon.util;

import ch.njol.skript.lang.TriggerItem;
import org.bukkit.event.Event;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Effects that extend this class are ran asynchronously. Next trigger item will
 * be ran in main server thread, as if there had been a delay before.
 * <p>
 * Majority of Skript and Minecraft APIs are not thread-safe, so be careful.
 */
public abstract class AsyncEffect extends DelayFork {

	private static final ReentrantLock SCRIPT_EXECUTION = new ReentrantLock(true);
	private static final ExecutorService THREADS = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

	@Override
	protected TriggerItem walk(Event e) {
		debug(e, true);
		DelayFork.addDelayedEvent(e);
		CompletableFuture<Void> run = CompletableFuture.runAsync(() -> execute(e), THREADS);
		run.whenComplete((r, err) -> {
			if (err != null) {
				err.printStackTrace();
			}
			SCRIPT_EXECUTION.lock();
			try {
				if (getNext() != null) {
					walk(getNext(), e);
				}
			} finally {
				SCRIPT_EXECUTION.unlock();
			}
		});
		return null;
	}
}