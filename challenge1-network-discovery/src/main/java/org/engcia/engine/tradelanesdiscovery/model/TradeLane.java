package org.engcia.engine.tradelanesdiscovery.model;

public class TradeLane {
    private TransportationLane[] lanes;
    private Integer days;
    private Float cost;

    public TradeLane(TransportationLane[] lanes) {
        this.lanes = lanes;
    }

    public TradeLane(TransportationLane[] lanes, Integer days, Float cost) {
        this.lanes = lanes;
        this.days = days;
        this.cost = cost;
    }

    public TransportationLane[] getLanes() {
        return lanes;
    }

    public Integer getDays(){return days;}

    public Float getCost(){return cost;}

    public TradeLaneType getType() {
        return this.lanes.length == 1 ? TradeLaneType.Direct : TradeLaneType.Indirect;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();

        for (TransportationLane lane : this.lanes)
        {
            stringBuilder.append("- " + lane.toString() + "\n");
        }
        stringBuilder.append("Cost: " + getCost().toString() + "$\nDuration: " + getDays().toString());
        if (getDays() == 1) { stringBuilder.append(" day"); }
        else { stringBuilder.append(" days"); }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }
}

