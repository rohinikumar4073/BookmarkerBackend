package com.bookmarker.modal;

import java.util.List;

public class BrowserHistoryJson {
    private BrowserHistoryJsonHeader header;
    private List<BrowserHistoryJsonData> data;

    public BrowserHistoryJson(BrowserHistoryJsonHeader header, List<BrowserHistoryJsonData> data) {
        this.header = header;
        this.data = data;
    }

    public BrowserHistoryJson() {

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
