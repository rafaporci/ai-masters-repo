package org.engcia.engine.tradelanesdiscovery.model;

public class LogisticPointBlockage
{
    private final LogisticPoint logisticPoint;

    private final String reason;

    public LogisticPointBlockage(LogisticPoint logisticPoint, String reason) {
        this.logisticPoint = logisticPoint;
        this.reason = reason;
    }

    public LogisticPoint getLogisticPoint() {
        return logisticPoint;
    }

    public String getReason() {
        return reason;
    }

    public String toString() {
        return logisticPoint.toString() + " due to " + reason;
    }
}
