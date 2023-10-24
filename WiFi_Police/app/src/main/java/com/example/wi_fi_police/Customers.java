package com.example.wi_fi_police;

import android.graphics.Bitmap;
import android.media.Image;

public class Customers {

    String firstname, surname, email, cellNumber,location, buildingType, siteImage;


    public Customers() {

    }

    public Customers(String firstname, String surname, String email, String cellNumber, String location, String buildingType, String siteImage) {
        this.firstname = firstname;
        this.surname = surname;
        this.email = email;
        this.cellNumber = cellNumber;
        this.location = location;
        this.buildingType = buildingType;
        this.siteImage = siteImage;

    }


    public String getSiteImage() {
        return siteImage;
    }

    public void setSiteImage(String siteImage) {
        this.siteImage = siteImage;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCellNumber() {
        return cellNumber;
    }

    public void setCellNumber(String cellNumber) {
        this.cellNumber = cellNumber;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(String buildingType) {
        this.buildingType = buildingType;
    }


}
