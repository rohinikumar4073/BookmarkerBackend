package com.bookmarker.modal;

import com.google.gson.annotations.SerializedName;

import java.util.List;

class BrowserHistoryJsonHeaderAttribute {
    private String name;
    private String type;
    @SerializedName("class")
    private Boolean attributeClass;
    private double weight;
    private List<String> labels;

    public BrowserHistoryJsonHeaderAttribute(String name, String type, Boolean attributeClass, double weight, List<String> labels) {
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

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }


}

class BrowserHistoryJsonHeader {
    private String relation;
    private List<BrowserHistoryJsonHeaderAttribute> attribute;
    public BrowserHistoryJsonHeader(String relation, List<BrowserHistoryJsonHeaderAttribute> attribute) {
        this.relation = relation;
        this.attribute = attribute;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

}

class BrowserHistoryJsonData {

    private Boolean sparse;
    private double weight;
    private List<String> value;

    public BrowserHistoryJsonData(Boolean sparse, double weight, List<String> value) {
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

    public List<String> getValue() {
        return value;
    }

    public void setValue(List<String> value) {
        this.value = value;
    }

}

public class BrowserHistoryJson {
    private BrowserHistoryJsonHeader header;
    private List<BrowserHistoryJsonData> data;

    public BrowserHistoryJson(BrowserHistoryJsonHeader header, List<BrowserHistoryJsonData> data) {
        this.header = header;
        this.data = data;
    }

    public BrowserHistoryJsonHeader getHeader() {
        return header;
    }

    public void setHeader(BrowserHistoryJsonHeader header) {
        this.header = header;
    }

    public List<BrowserHistoryJsonData> getData() {
        return data;
    }

    public void setData(List<BrowserHistoryJsonData> data) {
        this.data = data;
    }


}
