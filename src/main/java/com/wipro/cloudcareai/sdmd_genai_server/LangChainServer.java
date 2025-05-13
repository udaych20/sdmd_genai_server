package com.wipro.cloudcareai.sdmd_genai_server;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import dev.langchain4j.model.language.LanguageModel;
import dev.langchain4j.model.ollama.OllamaLanguageModel;

public class LangChainServer {

    private static final int PORT = 8080;
    private static final LanguageModel model = OllamaLanguageModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("llama3.2")
            .build();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/ask", new AskHandler());
        server.setExecutor(null);
        System.out.println("Server started on port " + PORT);
        server.start();
    }

    static class AskHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if (!"POST".equals(exchange.getRequestMethod())) {
                sendResponse(exchange, 405, "Only POST method is allowed");
                return;
            }

            InputStream requestBody = exchange.getRequestBody();
            String prompt = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);

            String answer;
            try {
                answer = model.generate(prompt).content(); // ✅ Correct usage
            } catch (Exception e) {
                e.printStackTrace();
                answer = "Error: " + e.getMessage();
            }

            sendResponse(exchange, 200, answer);
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
            byte[] responseBytes = responseText.getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=UTF-8");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
    }
}
