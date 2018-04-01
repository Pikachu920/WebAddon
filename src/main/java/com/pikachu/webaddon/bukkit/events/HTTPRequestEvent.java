package com.pikachu.webaddon.bukkit.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import spark.Request;
import spark.Response;

public class HTTPRequestEvent extends Event {

    private static HandlerList handlerList = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlerList;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }

    private Request request;
    private Response response;

    public HTTPRequestEvent(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public Request getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}
