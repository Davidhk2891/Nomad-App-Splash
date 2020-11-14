package com.nomadapp.splash.model.objects;

/**
 * Created by David on 6/5/2018 for Splash.
 */
public class MySplasher {

    private String splasherUsername;
    private String splasherActualName;
    private String splasherPrice;
    private String carOwnerPrice;
    private String splasherNumOfWashes;
    private int splasherAvgRating;
    private String splasherProfPic;
    private int splasherCardColor;
    private int splasherTxtColor;

    public int getSplasherTxtColor() {
        return splasherTxtColor;
    }

    public void setSplasherTxtColor(int splasherTxtColor) {
        this.splasherTxtColor = splasherTxtColor;
    }

    public int getSplasherCardColor() {
        return splasherCardColor;
    }

    public void setSplasherCardColor(int splasherCardColor) {
        this.splasherCardColor = splasherCardColor;
    }

    public String getSplasherActualName() {
        return splasherActualName;
    }

    public void setSplasherActualName(String splasherActualName) {
        this.splasherActualName = splasherActualName;
    }

    public String getSplasherProfPic() {
        return splasherProfPic;
    }

    public void setSplasherProfPic(String splasherProfPic) {
        this.splasherProfPic = splasherProfPic;
    }

    public String getSplasherUsername() {
        return splasherUsername;
    }

    public void setSplasherUsername(String splasherUsername) {
        this.splasherUsername = splasherUsername;
    }

    public String getSplasherPrice() {
        return splasherPrice;
    }

    public void setSplasherPrice(String splasherPrice) {
        this.splasherPrice = splasherPrice;
    }

    public String getCarOwnerPrice() {
        return carOwnerPrice;
    }

    public void setCarOwnerPrice(String carOwnerPrice) {
        this.carOwnerPrice = carOwnerPrice;
    }

    public String getSplasherNumOfWashes() {
        return splasherNumOfWashes;
    }

    public void setSplasherNumOfWashes(String splasherNumOfWashes) {
        this.splasherNumOfWashes = splasherNumOfWashes;
    }

    public int getSplasherAvgRating() {
        return splasherAvgRating;
    }

    public void setSplasherAvgRating(int splasherAvgRating) {
        this.splasherAvgRating = splasherAvgRating;
    }
}
