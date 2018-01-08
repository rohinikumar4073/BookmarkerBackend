package com.bookmarker.modal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class BrowserHistoryJsonHeaderAttribute {
    private String name;
    private String type;
    @SerializedName("class")
    private Boolean attributeClass;
    private float weight;
    private String[] labels;

    public BrowserHistoryJsonHeaderAttribute(String name, String type, Boolean attributeClass, float weight, String[] labels) {
        this.name = name;
        this.type = type;
        this.attributeClass = attributeClass;
        this.weight = weight;
        this.labels = labels;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getAttributeClass() {
        return attributeClass;
    }

    public void setAttributeClass(Boolean attributeClass) {
        this.attributeClass = attributeClass;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public String[] getLabels() {
        return labels;
    }

    public void setLabels(String[] labels) {
        this.labels = labels;
    }
}
