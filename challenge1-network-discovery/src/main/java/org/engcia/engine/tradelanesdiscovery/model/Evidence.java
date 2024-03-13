package org.engcia.engine.tradelanesdiscovery.model;

public class Evidence extends Fact{

    public static final String DELIVERY_TYPE = "Delivery type";
    public static final String ORIGIN_COUNTRY = "Origin country";
    public static final String ORIGIN_STATE = "Origin state or city";
    public static final String DESTINATION_COUNTRY = "Destination country";
    public static final String DESTINATION_STATE = "Destination state or city";
    public static final String COLLECTION_POINT = "Collect location";
    private String evidence;
    private String value;

    public Evidence(String ev, String v) {
        evidence = ev;
        value = v;
    }
    public String getEvidence() {
        return evidence;
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return (evidence + " = " + value);
    }

}

