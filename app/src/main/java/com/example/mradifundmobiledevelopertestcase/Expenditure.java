package com.example.mradifundmobiledevelopertestcase;

public class Expenditure {
    private String received;
    private Double spent;
    private String balance;
    private String date;

    public Expenditure(){

    }

    public Expenditure(String received, Double spent, String balance, String date) {
        this.received = received;
        this.spent = spent;
        this.balance = balance;
        this.date = date;
    }

    public String getReceived() {
        return received;
    }

    public void setReceived(String received) {
        this.received = received;
    }

    public Double getSpent() {
        return spent;
    }

    public void setSpent(Double spent) {
        this.spent = spent;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
