package org.engcia.engine.premiumservicessimulator.model;

public class TradeLane {
    private TransportationLane[] lanes;

    public TradeLane(TransportationLane[] lanes) {
        this.lanes = lanes;
    }

    public TransportationLane[] getLanes() {
        return lanes;
    }

    public TradeLaneType getType() {
        return this.lanes.length == 1 ? TradeLaneType.Direct : TradeLaneType.Indirect;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (TransportationLane lane : this.lanes)
        {
            stringBuilder.append(lane.toString());
            stringBuilder.append("  ->   ");
        }
        return stringBuilder.toString();
    }
}

