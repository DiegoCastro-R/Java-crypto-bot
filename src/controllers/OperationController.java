package controllers;

import services.GetTickerService;
import services.GetAccountInfosService;
import models.TickerResponse;
import models.BalanceResponse;

public class OperationController {
    private final GetTickerService tickerService;
    private final GetAccountInfosService accountInfosService;

    public OperationController(String apiKey, String apiSecret) {
        this.tickerService = new GetTickerService();
        this.accountInfosService = new GetAccountInfosService(apiKey, apiSecret);
    }

    public void execute() {
        String market = "USDT-BTC";
        try {
            BalanceResponse accountBalance = accountInfosService.getBalancesAsync()
                    .exceptionally(ex -> {
                        System.err.println("Error occurred while fetching balances: " + ex.getMessage());
                        return new BalanceResponse(); // return a default BalanceResponse
                    })
                    .join();

            if (accountBalance.isSuccess()) {
                System.out.println(accountBalance.getBalances());
            } else {
                throw new Exception("Error in getting balance");
            }
            // Call executeAsync and wait for its completion
            TickerResponse response = tickerService.executeAsync(market).join();
            // Handle the response as needed
            if (response != null && response.isSuccess()) {
                System.out.println("Market: " + market);
                System.out.println("Price: " + response.getPrice());
                System.out.println("High: " + response.getHigh());
                System.out.println("Low: " + response.getLow());
            } else {
                throw new Exception("Failed to fetch ticker data.");
            }
        } catch (Exception ex) {
            // Handle any exception thrown
            ex.printStackTrace();
        }
    }
}
