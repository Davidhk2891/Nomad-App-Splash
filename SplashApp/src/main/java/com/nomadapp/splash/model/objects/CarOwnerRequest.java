package com.nomadapp.splash.model.objects;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by David on 5/30/2019 for Splash.
 */
public class CarOwnerRequest {

    private String address;
    private LatLng addressCoords;
    private String addressDetails;
    private String carBrand, carModel, carColor, CarPlateNum;
    private String carUntilTime;
    private String serviceType;
    private String initialSetPrice;
    private boolean temporalKeyActive;
    private String[] splashersWanted, pricesWanted, ogPricesWanted;
    private String splasherUsername, splasherShowingName;
    private String requestType;


    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LatLng getAddressCoords() {
        return addressCoords;
    }

    public void setAddressCoords(LatLng addressCoords) {
        this.addressCoords = addressCoords;
    }

    public String getAddressDetails() {
        return addressDetails;
    }

    public void setAddressDetails(String addressDetails) {
        this.addressDetails = addressDetails;
    }

    public String getCarBrand() {
        return carBrand;
    }

    public void setCarBrand(String carBrand) {
        this.carBrand = carBrand;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarColor() {
        return carColor;
    }

    public void setCarColor(String carColor) {
        this.carColor = carColor;
    }

    public String getCarPlateNum() {
        return CarPlateNum;
    }

    public void setCarPlateNum(String carPlateNum) {
        CarPlateNum = carPlateNum;
    }

    public String getCarUntilTime() {
        return carUntilTime;
    }

    public void setCarUntilTime(String carUntilTime) {
        this.carUntilTime = carUntilTime;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public String getInitialSetPrice() {
        return initialSetPrice;
    }

    public void setInitialSetPrice(String initialSetPrice) {
        this.initialSetPrice = initialSetPrice;
    }

    public boolean isTemporalKeyActive() {
        return temporalKeyActive;
    }

    public void setTemporalKeyActive(boolean temporalKeyActive) {
        this.temporalKeyActive = temporalKeyActive;
    }

    public String[] getSplashersWanted() {
        return splashersWanted;
    }

    public void setSplashersWanted(String[] splashersWanted) {
        this.splashersWanted = splashersWanted;
    }

    public String[] getPricesWanted() {
        return pricesWanted;
    }

    public void setPricesWanted(String[] pricesWanted) {
        this.pricesWanted = pricesWanted;
    }

    public String[] getOgPricesWanted() {
        return ogPricesWanted;
    }

    public void setOgPricesWanted(String[] ogPricesWanted) {
        this.ogPricesWanted = ogPricesWanted;
    }

    public String getSplasherUsername() {
        return splasherUsername;
    }

    public void setSplasherUsername(String splasherUsername) {
        this.splasherUsername = splasherUsername;
    }

    public String getSplasherShowingName() {
        return splasherShowingName;
    }

    public void setSplasherShowingName(String splasherShowingName) {
        this.splasherShowingName = splasherShowingName;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }
}
