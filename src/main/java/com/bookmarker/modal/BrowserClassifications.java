package com.bookmarker.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "BrowserClassifications")

public class BrowserClassifications {
    @Id
    private String browserId;
    private String fileId;

    public String getTrainFileId() {
        return trainFileId;
    }

    public void setTrainFileId(String trainFileId) {
        this.trainFileId = trainFileId;
    }

    private String trainFileId;

    public String getBrowserId() {
        return browserId;
    }

    public void setBrowserId(String browserId) {
        this.browserId = browserId;
    }

    public String getFileId() {
        return fileId;
    }

    public BrowserClassifications(String browserId, String fileId, String trainFileId) {
        this.browserId = browserId;
        this.fileId = fileId;
        this.trainFileId = trainFileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }


}
