package com.example.stammaskool.Models;

public class Notification {
    String title;
    String body;
    String media;
    String click_action;

    public Notification(String title, String body,
                        String media, String click_action) {
        this.title = title;
        this.body = body;
        this.media = media;
        this.click_action = click_action;
    }
}