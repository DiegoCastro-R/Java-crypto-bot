package services;

import models.TickerResponse;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpClient;
import java.util.concurrent.CompletableFuture;

public class GetTickerService {

    public CompletableFuture<TickerResponse> executeAsync(String market) {
        String url = "https://tradeogre.com/api/v1/ticker/" + market;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .method("GET", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient client = HttpClient.newHttpClient();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> parseJsonResponse(response.body()));
    }

    private TickerResponse parseJsonResponse(String responseBody) {
        JSONObject json = new JSONObject(responseBody);
        TickerResponse tickerResponse = new TickerResponse();
        tickerResponse.setSuccess(json.getBoolean("success"));
        tickerResponse.setInitialprice(json.getString("initialprice"));
        tickerResponse.setPrice(json.getString("price"));
        tickerResponse.setHigh(json.getString("high"));
        tickerResponse.setLow(json.getString("low"));
        tickerResponse.setVolume(json.getString("volume"));
        tickerResponse.setBid(json.getString("bid"));
        tickerResponse.setAsk(json.getString("ask"));
        return tickerResponse;
    }

}
