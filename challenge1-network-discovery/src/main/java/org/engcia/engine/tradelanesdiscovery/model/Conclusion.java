package org.engcia.engine.tradelanesdiscovery.model;

import org.engcia.network.NetworkDiscovery;

public class Conclusion extends Fact{
    private TradeLane[] tradeLanes;

    public Conclusion(TradeLane tradeLane) {
        this.tradeLanes = new TradeLane[] { tradeLane };
    }

    public Conclusion(TradeLane[] tradeLane) {
        this.tradeLanes = tradeLane;
    }

    public TradeLane[] getTradeLanes() {
        return tradeLanes;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Conclusion: \n");
        int x = 1;
        for (TradeLane tradeLane : tradeLanes) {
            stringBuilder.append("TradeLane " + Integer.toString(x) + ": \n");

            stringBuilder.append(tradeLane.toString());
            stringBuilder.append("\n");
            stringBuilder.append("\n");

            x += 1;
        }
        return stringBuilder.toString();
    }

}
