package org.engcia.engine.tradelanesdiscovery.dto;

public class Query {

    private String deliveryType;
    private String origin;

    private String originState;

    private String destinationCountry;

    private String destinationState;

    private String destinationCity;
    private String collectionPoint;

    public String getDeliveryType(){return deliveryType;}
    public void setDeliveryType(String value){deliveryType = value;}

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String value) {
        origin = value;
    }

    public String getOriginState() {
        return originState;
    }

    public void setOriginState(String value) {
        originState = value;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String value) {
        destinationCity = value;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String value) { destinationCountry = value; }

    public String getDestinationState() {
        return destinationState;
    }

    public void setDestinationState(String value) {
        destinationState = value;
    }

    public String getColletionPoint(){return collectionPoint;}
    public void setCollectionPoint(String value){ collectionPoint = value;}
}

