package com.nomadapp.splash.model.objects;

/**
 * Created by David on 6/25/2017.
 */

public class MyCar {

    private String brand;
    public String model;
    private String colorz;
    private String plateNum;
    private int itemId;

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getColorz() {
        return colorz;
    }

    public void setColorz(String colorz) {
        this.colorz = colorz;
    }

    public String getPlateNum() {
        return plateNum;
    }

    public void setPlateNum(String plateNum) {
        this.plateNum = plateNum;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }
}
