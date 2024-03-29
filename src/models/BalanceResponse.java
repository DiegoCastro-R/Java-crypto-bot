package models;

import java.util.Map;

public class BalanceResponse {
    private boolean success;
    private Map<String, Object> balances;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public Map<String, Object> getBalances() {
        return balances;
    }

    public void setBalances(Map<String, Object> balances) {
        this.balances = balances;
    }
}
