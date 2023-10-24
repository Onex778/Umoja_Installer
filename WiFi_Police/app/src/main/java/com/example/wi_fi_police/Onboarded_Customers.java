package com.example.wi_fi_police;

public class Onboarded_Customers {
    String Date, customerNumber,customer,MAC,SN,IMSI, Installer, ImageUri,siteType;

    public String getDate() {
        return Date;
    }

    public String getSiteType() {
        return siteType;
    }

    public void setSiteType(String siteType) {
        this.siteType = siteType;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String getMAC() {
        return MAC;
    }

    public void setMAC(String MAC) {
        this.MAC = MAC;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getIMSI() {
        return IMSI;
    }

    public void setIMSI(String IMSI) {
        this.IMSI = IMSI;
    }

    public String getInstaller() {
        return Installer;
    }

    public void setInstaller(String installer) {
        Installer = installer;
    }

    public String getImageUri() {
        return ImageUri;
    }

    public void setImageUri(String imageUri) {
        ImageUri = imageUri;
    }

    public Onboarded_Customers(String date, String customerNumber, String customer, String MAC, String SN, String IMSI, String installer, String imageUri,String sitetype) {
        Date = date;
        this.customerNumber = customerNumber;
        this.customer = customer;
        this.MAC = MAC;
        this.SN = SN;
        this.IMSI = IMSI;
        Installer = installer;
        ImageUri = imageUri;
        siteType = sitetype;
    }
}
