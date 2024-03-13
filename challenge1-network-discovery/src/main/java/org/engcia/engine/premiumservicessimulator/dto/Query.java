package org.engcia.engine.premiumservicessimulator.dto;

public class Query {
    private boolean vipCustomer;

    private String destinationCountry;

    private double orderValue;

    private int standardShippingInDays;

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public void setDestinationCountry(String destinationCountry) {
        this.destinationCountry = destinationCountry;
    }

    public boolean isVipCustomer() {
        return vipCustomer;
    }

    public void setVipCustomer(boolean vipCustomer) {
        this.vipCustomer = vipCustomer;
    }

    public double getOrderValue() {
        return orderValue;
    }

    public void setOrderValue(double orderValue) {
        this.orderValue = orderValue;
    }

    public int getStandardShippingInDays() {
        return standardShippingInDays;
    }

    public void setStandardShippingInDays(int standardShippingInDays) {
        this.standardShippingInDays = standardShippingInDays;
    }

}