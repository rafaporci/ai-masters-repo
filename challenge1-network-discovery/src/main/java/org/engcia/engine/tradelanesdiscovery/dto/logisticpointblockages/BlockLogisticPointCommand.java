package org.engcia.engine.tradelanesdiscovery.dto.logisticpointblockages;

public class BlockLogisticPointCommand {
    private String logisticPointName;
    private String reason;

    public String getLogisticPointName() {
        return this.logisticPointName;
    }

    public String getReason() {
        return reason;
    }

    public void setLogisticPointName(String value) {
        this.logisticPointName = value;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
