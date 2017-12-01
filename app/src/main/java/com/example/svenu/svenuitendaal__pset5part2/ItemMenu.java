package com.example.svenu.svenuitendaal__pset5part2;

/**
 * Created by svenu on 30-11-2017.
 */

public class ItemMenu {
    private String name;
    private String description;
    private float price;
    private String image;
    ItemMenu(String aName, String aDescription, float aPrice, String anImage) {
        name = aName;
        description = aDescription;
        price = aPrice;
        image = anImage;
    }

    public void setName(String aName) {
        name = aName;
    }
    public String getName() {
        return name;
    }

    public void setDescription(String aDescription) {
        description = aDescription;
    }
    public String getDescription() {
        return description;
    }

    public void setPrice(float aPrice) {
        price = aPrice;
    }
    public float getPrice() {
        return price;
    }

    public void setImage(String anImageUrl) {
        image = anImageUrl;
    }
    public String getImage() {
        return image;
    }
}
