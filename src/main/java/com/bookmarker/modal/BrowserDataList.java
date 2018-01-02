package com.bookmarker.modal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BrowserDataList {
    public String browserId;
    private String browserLogs;

    public BrowserDataList( String browserLogs, String browserId) {
        this.browserId = browserId;
        this.browserLogs = browserLogs;
    }

    public BrowserDataList() {

    }

    public String getBrowserId() {
        return browserId;
    }

    public void setBrowserId(String browserId) {
        this.browserId = browserId;
    }

    public String getBrowserLogs() {
        return browserLogs;
    }

    public void setBrowserLogs(String browserLogs) {
        this.browserLogs = browserLogs;
    }
}
