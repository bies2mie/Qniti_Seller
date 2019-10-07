package com.badrul.qnitiseller;

public class User {

    //private int userid;
    private int sellerID;
    private String sellerUserName;
    private String sellerName;
    private String sellerPhone;
    private String sellerLocation;

    public User(int sellerID, String sellerUserName, String sellerName, String sellerPhone, String sellerLocation) {

        this.sellerID = sellerID;
        this.sellerUserName = sellerUserName;
        this.sellerName = sellerName;
        this.sellerPhone = sellerPhone;
        this.sellerLocation = sellerLocation;

    }

    public int getSellerID() {
        return sellerID;
    }

    public String getSellerUserName() {
        return sellerUserName;
    }

    public String getSellerName() {
        return sellerName;
    }

    public String getSellerPhone() {
        return sellerPhone;
    }

    public String getSellerLocation() {
        return sellerLocation;
    }

}
