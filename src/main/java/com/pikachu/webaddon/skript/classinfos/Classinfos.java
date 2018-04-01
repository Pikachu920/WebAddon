package com.pikachu.webaddon.skript.classinfos;

import ch.njol.skript.lang.ParseContext;
import spark.Request;
import spark.Response;

public class Classinfos {

	public static void register() {
		new SimpleType<Request>(Request.class, "request", "requests?") {
			@Override
			public Request parse(String arg0, ParseContext arg1) {
				return null;
			}

			@Override
			public boolean canParse(ParseContext pc) {
				return false;
			}

			@Override
			public String toString(Request request, int context) {
				return request.pathInfo();
			}

			@Override
			public String toVariableNameString(Request request) {
				return request.pathInfo();
			}

		};

		new SimpleType<Response>(Response.class, "response", "responses?") {
			@Override
			public Response parse(String arg0, ParseContext arg1) {
				return null;
			}

			@Override
			public boolean canParse(ParseContext pc) {
				return false;
			}

			@Override
			public String toString(Response response, int context) {
				return response.body();
			}

			@Override
			public String toVariableNameString(Response response) {
				return response.body();
			}

		};
	}

}
