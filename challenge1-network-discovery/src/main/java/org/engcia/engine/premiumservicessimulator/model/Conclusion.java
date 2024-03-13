package org.engcia.engine.premiumservicessimulator.model;

public class Conclusion extends Fact{
    private TradeLane tradeLane;

    public Conclusion(TradeLane tradeLane) {
        this.tradeLane = tradeLane;
    }

    public TradeLane getTradeLane() {
        return tradeLane;
    }

    public String toString() {
        return ("Conclusion: " + tradeLane.toString());
    }

}