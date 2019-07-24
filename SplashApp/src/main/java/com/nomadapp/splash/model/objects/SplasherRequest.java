package com.nomadapp.splash.model.objects;

import android.net.Uri;

/**
 * Created by David on 1/10/2018.
 */

public class SplasherRequest {

    public String distance;
    public String untilTime;
    public String service;
    public String tomorrow;
    public String bikeService;
    public String requestType;
    public Uri profPicFbUri;
    public Uri profPicUri;
    public int numBadge;
    public int itemId;

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getUntilTime() {
        return untilTime;
    }

    public void setUntilTime(String untilTime) {
        this.untilTime = untilTime;
    }

    public String getService() {
        return service;
    }

    public void setService(String price) {
        this.service = price;
    }

    public Uri getProfPicFbUri() {
        return profPicFbUri;
    }

    public void setProfPicFbUri(Uri profPicFbUri) {
        this.profPicFbUri = profPicFbUri;
    }

    public Uri getProfPicUri() {
        return profPicUri;
    }

    public void setProfPicUri(Uri profPicUri) {
        this.profPicUri = profPicUri;
    }

    public int getNumBadge() {
        return numBadge;
    }

    public void setNumBadge(int numBadge) {
        this.numBadge = numBadge;
    }

    public String getTomorrow() {
        return tomorrow;
    }

    public void setTomorrow(String tomorrow) {
        this.tomorrow = tomorrow;
    }

    public String getBikeService() {
        return bikeService;
    }

    public void setBikeService(String bikeService) {
        this.bikeService = bikeService;
    }

    public String getRequestType() {
        return requestType;
    }

    public void setRequestType(String requestType) {
        this.requestType = requestType;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
