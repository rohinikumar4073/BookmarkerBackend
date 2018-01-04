package com.bookmarker.modal;

import java.util.List;

public class BrowserHistoryJsonHeader {
    private String relation;
    private List<BrowserHistoryJsonHeaderAttribute> attributes;
    public BrowserHistoryJsonHeader(String relation, List<BrowserHistoryJsonHeaderAttribute> attribute) {
        this.relation = relation;
        this.attributes = attribute;
    }

    public BrowserHistoryJsonHeader() {

    }

    public void setAttributes(List<BrowserHistoryJsonHeaderAttribute> attributes) {
        this.attributes = attributes;
    }

    public List<BrowserHistoryJsonHeaderAttribute> getAttributes() {

        return attributes;
    }

    public String getRelation() {
        return relation;
    }

    public void setRelation(String relation) {
        this.relation = relation;
    }

}
