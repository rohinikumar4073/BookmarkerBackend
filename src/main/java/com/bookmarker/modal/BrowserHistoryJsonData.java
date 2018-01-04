package com.bookmarker.modal;

import java.util.List;

public class BrowserHistoryJsonData {

    private Boolean sparse;
    private double weight;
    private String[] value;

    public BrowserHistoryJsonData(Boolean sparse, double weight, String[] value) {
        this.sparse = sparse;
        this.weight = weight;
        this.value = value;
    }

    public Boolean getSparse() {
        return sparse;
    }

    public void setSparse(Boolean sparse) {
        this.sparse = sparse;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }
}
