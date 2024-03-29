package controllers;

import models.OrderResponse;
import org.json.JSONObject;
import services.GetTickerService;
import services.GetAccountInfosService;
import models.TickerResponse;
import models.BalanceResponse;

public class OperationController {
    private final GetTickerService tickerService;
    private final GetAccountInfosService accountInfosService;
    private static final String MARKET = "USDT-BTC";

    public OperationController(String apiKey, String apiSecret) {
        this.tickerService = new GetTickerService();
        this.accountInfosService = new GetAccountInfosService(apiKey, apiSecret);
    }

    public enum OperationSide {
        BUY,
        SELL
    }

    public void execute() {
        OperationSide operationSide = determineOperationSide();

        try {
            BalanceResponse accountBalance = accountInfosService.getBalancesAsync().join();
            System.out.println("Market: " + MARKET);

            if (accountBalance.isSuccess()) {
                JSONObject balancesJson = new JSONObject(accountBalance.getBalances());
                System.out.println("Base Currency Balance: " + balancesJson.opt(getBaseCurrency()) + " " + getBaseCurrency());
                System.out.println("Quote Currency Balance: " + balancesJson.opt(getQuoteCurrency()) + " " + getQuoteCurrency());
            } else {
                throw new Exception("Error in getting balance");
            }

            OrderResponse[] accountOrders = accountInfosService.getOpenOrdersAsync().join();
            boolean hasOrders = accountOrders.length > 0;

            if (!hasOrders) {
                operationSide = OperationSide.BUY;
            } else {
                operationSide = OperationSide.SELL;
            }

            System.out.println("Operation side: " + operationSide);

            TickerResponse tickerInfos = tickerService.executeAsync(MARKET).join();
            if (tickerInfos != null && tickerInfos.isSuccess()) {
                System.out.println("Actual Price: " + tickerInfos.getPrice());
                System.out.println("24 Hours - High: " + tickerInfos.getHigh());
                System.out.println("24 Hours - Low: " + tickerInfos.getLow());

                double currentPrice = Double.parseDouble(tickerInfos.getPrice());
                double highPrice = Double.parseDouble(tickerInfos.getHigh());

                if (currentPrice < highPrice && operationSide == OperationSide.BUY) {
                    System.out.println("Buying some BTC...");
                } else if (currentPrice >= highPrice && operationSide == OperationSide.SELL) {
                    System.out.println("Verifying if it is profitable to sell...");
                }
            } else {
                throw new Exception("Failed to fetch ticker data.");
            }
            System.out.println("_____________________________________________________");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private OperationSide determineOperationSide() {
        // Add logic here to determine the operation side based on additional criteria if needed
        return OperationSide.BUY; // Default to BUY
    }

    private String getBaseCurrency() {
        return MARKET.split("-")[0];
    }

    private String getQuoteCurrency() {
        return MARKET.split("-")[1];
    }
}
