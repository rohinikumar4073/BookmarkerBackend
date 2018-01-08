package com.bookmarker.modal;

public class BrowserHistoryJsonData {

    private Boolean sparse;
    private double weight;
    private String[] values;

    public BrowserHistoryJsonData(Boolean sparse, double weight, String[] values) {
        this.sparse = sparse;
        this.weight = weight;
        this.values = values;
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

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] values) {
        this.values = values;
    }
}
