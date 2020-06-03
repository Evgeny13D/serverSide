package com.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RequestInfo {

    private static final String HTML_START = "<html><body><h1>Tech Talk server_side of network!</h1><br><br>";
    private static final String HTML_END = "</body></html>";

    private static final Logger log = LoggerFactory.getLogger(RequestInfo.class);

    public static void getRequestInformation(Socket socket) throws IOException {

        OutputStream os = socket.getOutputStream();

        String responseHtml = HTML_START + generateRequestHtml(socket) + HTML_END;

        String result = getResponse(responseHtml) + responseHtml;
        os.write(result.getBytes());
        os.flush();
    }

    private static Map<String, String> getRequestHeaders(BufferedReader bufferedReader)
            throws IOException {
        Map<String, String> headers = new HashMap<>();

        String readLine = bufferedReader.readLine();
        headers.put("Request Method", readLine.split(" /")[0]);

        while (!(readLine = bufferedReader.readLine()).equals("")) {
            String[] header = readLine.split(":");
            if (header.length > 1) {
                headers.put(header[0], header[1]);
                log.info("Add header " + header[0]);
            }
        }

        return headers;
    }

    private static String generateRequestHtml(Socket socket) throws IOException {

        InputStream inputStreamReader = socket.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStreamReader));

        Map<String, String> requestHeaders = getRequestHeaders(reader);

        StringBuffer generatedRequestHtml = new StringBuffer();

        if (requestHeaders.get("Request Method").equals("POST")) {

        } else {
            requestHeaders.forEach(
                    (key, value) ->
                            generatedRequestHtml.append(
                                    "<p style='margin: 0; padding: 0;'><b>" + key + ": </b>" + value + "</p>"));
        }

        return String.valueOf(generatedRequestHtml);
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
}
