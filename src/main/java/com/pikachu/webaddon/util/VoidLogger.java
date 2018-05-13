package com.pikachu.webaddon.util;


import org.eclipse.jetty.util.log.Logger;

public class VoidLogger implements Logger {

	@Override
	public String getName() {
		return "VoidLogger";
	}

	@Override
	public void warn(String msg, Object... args) {

	}

	@Override
	public void warn(Throwable thrown) {

	}

	@Override
	public void warn(String msg, Throwable thrown) {

	}

	@Override
	public void info(String msg, Object... args) {

	}

	@Override
	public void info(Throwable thrown) {

	}

	@Override
	public void info(String msg, Throwable thrown) {

	}

	@Override
	public boolean isDebugEnabled() {
		return false;
	}

	@Override
	public void setDebugEnabled(boolean enabled) {

	}

	@Override
	public void debug(String msg, Object... args) {

	}

	@Override
	public void debug(String msg, long value) {

	}

	@Override
	public void debug(Throwable thrown) {

	}

	@Override
	public void debug(String msg, Throwable thrown) {

	}

	@Override
	public Logger getLogger(String name) {
		return new VoidLogger();
	}

	@Override
	public void ignore(Throwable ignored) {

	}
}
