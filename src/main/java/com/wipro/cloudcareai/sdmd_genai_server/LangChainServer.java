package com.wipro.cloudcareai.sdmd_genai_server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static final Logger logger = LoggerFactory.getLogger(LangChainServer.class);
    private static final Logger queryLogger = LoggerFactory.getLogger("QUERY_LOGGER");

    private static final int PORT = 8080;

    private static final LanguageModel model = OllamaLanguageModel.builder()
            .baseUrl("http://localhost:11434")
            .modelName("llama3.2")
            .build();

    public static void main(String[] args) throws IOException {
        logger.info("Initializing LangChain4j server...");

        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/ask", new AskHandler());
        server.setExecutor(null); // creates a default executor

        logger.info("Server started and listening on port {}", PORT);
        server.start();
    }

    static class AskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            logger.debug("Received request: {}", exchange.getRequestMethod());

            if (!"POST".equals(exchange.getRequestMethod())) {
                logger.warn("Rejected non-POST request from {}", exchange.getRemoteAddress());
                sendResponse(exchange, 405, "Only POST method is allowed");
                return;
            }

            String prompt = "";
            String answer = "";

            try (InputStream requestBody = exchange.getRequestBody()) {
                prompt = new String(requestBody.readAllBytes(), StandardCharsets.UTF_8);
                logger.info("Received prompt: {}", prompt);
            } catch (IOException e) {
                logger.error("Failed to read request body", e);
                sendResponse(exchange, 400, "Invalid request body");
                return;
            }

            try {
                answer = model.generate(prompt).content();
                queryLogger.info("Prompt: {}\nResponse: {}", prompt, answer);
            } catch (Exception e) {
                logger.error("Error while generating response for prompt: {}", prompt, e);
                answer = "Error: " + e.getMessage();
            }

            try {
                sendResponse(exchange, 200, answer);
                logger.debug("Sent response successfully.");
            } catch (IOException e) {
                logger.error("Failed to send response", e);
            }
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
