package com.server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;

public class MessageHistory {


    private LinkedList<String> story = new LinkedList<>();

    public void addStoryMessage(String el) {
        if (story.size() >= 10) {
            story.removeFirst();
            story.add(el);
        } else {
            story.add(el);
        }
    }

    public void printStory(BufferedWriter writer) {
        if (story.size() > 0) {
            try {
                writer.write("History messages" + "\n");
                for (String vr : story) {
                    writer.write(vr + "\n");
                }
                writer.write("/...." + "\n");
                writer.flush();
            } catch (IOException ignored) {
            }
        }
    }
}
