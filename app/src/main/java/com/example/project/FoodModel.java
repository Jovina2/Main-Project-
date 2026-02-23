package com.example.project;

public class FoodModel {

    String foodName;
    String barcode;

    public FoodModel(String foodName, String barcode) {
        this.foodName = foodName;
        this.barcode = barcode;
    }

    public String getFoodName() {
        return foodName;
    }

    public String getBarcode() {
        return barcode;
    }
}
