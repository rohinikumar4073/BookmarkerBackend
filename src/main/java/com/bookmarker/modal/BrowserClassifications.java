package com.bookmarker.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "BrowserClassifications")

public class BrowserClassifications {
    @Id
    private String browserId;
    private String fileId;

    public String getBrowserId() {
        return browserId;
    }

    public void setBrowserId(String browserId) {
        this.browserId = browserId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public BrowserClassifications(String browserId, String fileId) {

        this.browserId = browserId;
        this.fileId = fileId;
    }
}
