package com.auth0.samples;

/**
 * Created by Major on 10/7/17.
 */

public class Transaction {
    String counterParty;
    String id;
    String type;
    String status;
    int amount;
    String description;
    String date;

    public String getCounterParty() {
        return counterParty;
    }

    public int getAmount() {
        return amount;
    }

    public String getDescription() {
        return description;
    }
}
