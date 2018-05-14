package com.pikachu.webaddon.skript.classinfos;

import spark.Request;
import spark.Response;
import spark.Session;

public class Classinfos {

	static {
		new SimpleType<Request>(Request.class, "request", "requests?") {

			@Override
			public String toString(Request request, int context) {
				return "request";
			}

		};

		new SimpleType<Response>(Response.class, "response", "responses?") {

			@Override
			public String toString(Response response, int context) {
				return "response";
			}

		};

		new SimpleType<Session>(Session.class, "session", "sessions?") {

			@Override
			public String toString(Session session, int arg1) {
				return session.id();
			}

		};

	}

}
