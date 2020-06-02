package com.server;

import java.io.*;
import java.net.Socket;

public class ServerProcess extends Thread{
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;

    public ServerProcess(Socket socket) throws IOException {
        this.socket = socket;
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        ServerSide.messageHistory.printStory(out);
        start();
    }
    @Override
    public void run() {
        String word;
        try {
            word = in.readLine();
            try {
                out.write(word + "\n");
                out.flush();
            } catch (IOException ignored) {}
            try {
                while (true) {
                    word = in.readLine();
                    if(word.equals("stop")) {
                        this.downService();
                        break;
                    }
                    System.out.println("Echoing: " + word);
                    ServerSide.messageHistory.addStoryMessage(word);
                    for (ServerProcess vr : ServerSide.serverList) {
                        vr.send(word);
                    }
                }
            } catch (NullPointerException ignored) {}


        } catch (IOException e) {
            this.downService();
        }
    }

    private void send(String msg) {
        try {
            out.write(msg + "\n");
            out.flush();
        } catch (IOException ignored) {}

    }

    private void downService() {
        try {
            if(!socket.isClosed()) {
                socket.close();
                in.close();
                out.close();
                for (ServerProcess vr : ServerSide.serverList) {
                    if(vr.equals(this)) vr.interrupt();
                    ServerSide.serverList.remove(this);
                }
            }
        } catch (IOException ignored) {}
    }
}
