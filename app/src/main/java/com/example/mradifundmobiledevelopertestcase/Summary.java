package com.example.mradifundmobiledevelopertestcase;

public class Summary {
    private String transactionType;
    private Double received;
    private Double spent;

    public Summary() {

    }

    public Summary(String transactionType, Double received, Double spent){
        this.transactionType = transactionType;
        this.received = received;
        this.spent = spent;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public Double getReceived() {
        return received;
    }

    public void setReceived(Double received) {
        this.received = received;
    }

    public Double getSpent() {
        return spent;
    }

    public void setSpent(Double spent) {
        this.spent = spent;
    }
}
