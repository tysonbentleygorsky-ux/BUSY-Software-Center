package ca.busyschool.desktop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

final class BusyApiClient implements AutoCloseable {
    private final ExecutorService executor = Executors.newFixedThreadPool(3, r -> {
        Thread t = new Thread(r, "busy-api-client"); t.setDaemon(true); return t;
    });
    private final HttpClient http = HttpClient.newBuilder().executor(executor)
        .connectTimeout(Duration.ofSeconds(5)).build();
    private final ObjectMapper json = new ObjectMapper();
    private volatile String baseUrl = "http://localhost:8080";

    void setBaseUrl(String value) { baseUrl = value; }

    CompletableFuture<Boolean> health() {
        return get("/api/health").thenApply(node -> node.path("status").asText().equals("ok"));
    }

    CompletableFuture<List<JsonNode>> catalog(String query) {
        String encoded = URLEncoder.encode(query == null ? "" : query, StandardCharsets.UTF_8);
        return get("/api/catalog?q=" + encoded).thenApply(node -> {
            List<JsonNode> rows = new ArrayList<>();
            if (node.isArray()) node.forEach(rows::add);
            return rows;
        });
    }

    private CompletableFuture<JsonNode> get(String path) {
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + path))
            .timeout(Duration.ofSeconds(12)).header("Accept", "application/json").GET().build();
        return http.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(response -> {
                if (response.statusCode() < 200 || response.statusCode() >= 300)
                    throw new IllegalStateException("BUSY API returned HTTP " + response.statusCode());
                try { return json.readTree(response.body()); }
                catch (Exception e) { throw new IllegalStateException("BUSY API returned invalid JSON", e); }
            });
    }

    @Override public void close() { executor.shutdownNow(); }
}
