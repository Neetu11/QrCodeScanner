package com.efficientindia.securescan.model;

import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("status")
    String status;

    @SerializedName("bName")
    String bName;

    @SerializedName("pName")
    String pName;

    @SerializedName("details1")
    String details1;

    @SerializedName("details2")
    String details2;

    @SerializedName("mNumber")
    String mNumber;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getbName() {
        return bName;
    }

    public void setbName(String bName) {
        this.bName = bName;
    }

    public String getpName() {
        return pName;
    }

    public void setpName(String pName) {
        this.pName = pName;
    }

    public String getDetails1() {
        return details1;
    }

    public void setDetails1(String details1) {
        this.details1 = details1;
    }

    public String getDetails2() {
        return details2;
    }

    public void setDetails2(String details2) {
        this.details2 = details2;
    }

    public String getmNumber() {
        return mNumber;
    }

    public void setmNumber(String mNumber) {
        this.mNumber = mNumber;
    }
}
