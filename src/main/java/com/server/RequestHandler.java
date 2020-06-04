package com.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class RequestHandler {

    private static final String FREE_SPACE = "<br><br><br>";
    private static final String HTML_START = "<html><body>";
    private static final String HTML_END = "</body></html>";
    private static final String HTML_FORM = "<form method='POST'>" +
            "<input name='param' type='text'/>" +
            "<input type='submit'/>" +
            "</form>";
    private static final String DELEMITER = " ---------------------   ---------------------   ----------- \n";

    private static final String HOME_PAGE = "<h1>Tech Talk server_side of network!</h1>";
    private static final String MESSAGE_PAGE = "<h1>Welcome to MESSAGE PAGE</h1>";

    private static final List<String> HANDLED_REQUEST = Arrays.asList("/", "/msg");

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private static String payload = null;

    public static void handleRequest(Socket socket) throws IOException {

        OutputStream os = socket.getOutputStream();

        InputStream inputStream = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        Request request = new Request(reader);
        if (!request.parse()) {
            respond(500, "Unable to parse request", os);
            return;
        }

        log.info(DELEMITER);
        log.info("Request path: " + request.getPath());
        log.info("Request Query: " + request.getQueryParameters());
        log.info(DELEMITER);

        String responseHtml = null;

        switch (request.getPath()) {
            case "/msg":
                responseHtml = handleMessagePage(request);
                break;
            default:
                responseHtml = handleRequestMethod(request, HOME_PAGE);
        }

        String result = getResponse(responseHtml) + responseHtml;
        os.write(result.getBytes());
        os.flush();
        reader.close();
    }

    private static String getResponse(String responseHtml) {
        return "HTTP/1.1 200 OK\r\n"
                + "Server: Server\r\n"
                + "Content-Type: text/html\r\n"
                + "Content-Length: "
                + responseHtml.length()
                + "\r\n"
                + "Connection: close\r\n\r\n";
    }

    private static void respond(int statusCode, String msg, OutputStream out) throws IOException {
        String responseLine = "HTTP/1.1 " + statusCode + " " + msg + "\r\n\r\n";
        log.info(responseLine);
        out.write(responseLine.getBytes());
    }

    private static String handleMessagePage(Request request) throws IOException {

        return handleRequestMethod(request, MESSAGE_PAGE);
    }

    private static String handleRequestMethod(Request request, String pageTitle) throws IOException {
        StringBuffer generatedRequestHtml = new StringBuffer();

        generatedRequestHtml.append(HTML_START);

        if (request.getMethod().equals("POST")) {

            BufferedReader reader = request.getInputReader();

            String payload = "<div><h1>Payload DATA from POST = </h1>" + getPostPayloadData(reader) + "</div>" + FREE_SPACE;

            generatedRequestHtml.append(pageTitle + FREE_SPACE);
            generatedRequestHtml.append(payload);

            request.getHeaders().forEach(
                    (key, value) ->
                            generatedRequestHtml.append(
                                    "<p style='margin: 0; padding: 0;'><b>" + key + ": </b>" + value + "</p>"));
            generatedRequestHtml.append(HTML_END);
        } else {
            generatedRequestHtml.append(pageTitle + FREE_SPACE);
            request.getHeaders().forEach(
                    (key, value) ->
                            generatedRequestHtml.append(
                                    "<p style='margin: 0; padding: 0;'><b>" + key + ": </b>" + value + "</p>"));
            generatedRequestHtml.append(FREE_SPACE + HTML_FORM + HTML_END);
        }

        return String.valueOf(generatedRequestHtml);
    }

    private static String getPostPayloadData(BufferedReader bufferedReader) throws IOException {
        String body = null;
        StringBuilder stringBuilder = new StringBuilder();

        char[] charBuffer = new char[128];
        int bytesRead = -1;

        bytesRead = bufferedReader.read(charBuffer);
        stringBuilder.append(charBuffer, 0, bytesRead);

        body = stringBuilder.toString();
        return body;
    }
}