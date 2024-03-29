package services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;
import java.util.concurrent.CompletableFuture;
import models.BalanceResponse;
import models.OrderResponse;
import org.json.JSONArray;
import org.json.JSONObject;


public class GetAccountInfosService {
        private final String apiKey;
        private final String apiSecret;

        public GetAccountInfosService(String apiKey, String apiSecret) {
            this.apiKey = apiKey;
            this.apiSecret = apiSecret;
        }

        // Method to retrieve balances asynchronously
        public CompletableFuture<BalanceResponse> getBalancesAsync() {
            String url = "https://tradeogre.com/api/v1/account/balances";
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Basic " + encodeCredentials(apiKey, apiSecret))
                    .method("GET", HttpRequest.BodyPublishers.noBody())
                    .build();

            HttpClient client = HttpClient.newHttpClient();
            return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> {
                        JSONObject jsonResponse = new JSONObject(response.body());
                        boolean success = jsonResponse.getBoolean("success");
                        JSONObject balancesObject = jsonResponse.optJSONObject("balances");
                        BalanceResponse balanceResponse = new BalanceResponse();
                        balanceResponse.setSuccess(success);
                        if (balancesObject != null) {
                            balanceResponse.setBalances(balancesObject.toMap());
                        }
                        return balanceResponse;
                    });
        }

    // Method to retrieve open orders asynchronously
    public CompletableFuture<OrderResponse[]> getOpenOrdersAsync() {
        String url = "https://tradeogre.com/api/v1/account/orders";
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Basic " + encodeCredentials(apiKey, apiSecret))
                .method("POST", HttpRequest.BodyPublishers.noBody())
                .build();

        HttpClient client = HttpClient.newHttpClient();
        return client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(response -> {
                    if (response.body().isEmpty()) {
                        return new OrderResponse[0];
                    } else {
                        JSONArray jsonResponse = new JSONArray(response.body());
                        OrderResponse[] orderResponses = new OrderResponse[jsonResponse.length()];
                        for (int i = 0; i < jsonResponse.length(); i++) {
                            JSONObject orderObject = jsonResponse.getJSONObject(i);
                            OrderResponse orderResponse = new OrderResponse();
                            orderResponse.setUuid(orderObject.getString("uuid"));
                            orderResponse.setDate(orderObject.getLong("date"));
                            orderResponse.setType(orderObject.getString("type"));
                            orderResponse.setPrice(orderObject.getString("price"));
                            orderResponse.setQuantity(orderObject.getString("quantity"));
                            orderResponse.setMarket(orderObject.getString("market"));
                            orderResponses[i] = orderResponse;
                        }
                        return orderResponses;
                    }
                });
    }

    // Method to encode API key and API secret for basic authentication
    private String encodeCredentials(String apiKey, String apiSecret) {
        String credentials = apiKey + ":" + apiSecret;
        return Base64.getEncoder().encodeToString(credentials.getBytes());
    }
}
