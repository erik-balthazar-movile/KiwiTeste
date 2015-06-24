package com.movile.subscription.bean;

/**
 * @author Heitor Hack Canabal (heitor.canabal@movile.com)
 */
public class Phone {

    private String ddd;
    private String number;
    private long carrierId;

    public String getDDD() {
        return ddd;
    }

    public void setDDD(String ddd) {
        this.ddd = ddd;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public long getCarrierId() {
        return carrierId;
    }

    public void setCarrierId(long carrierId) {
        this.carrierId = carrierId;
    }
}
