package com.bookmarker.modal;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "browserHistory")

public class Browser {
    @Id
    public int id;
    public double lastSyncedUpTime;

    public Browser(int id, double lastSyncedUpTime) {
        this.id = id;
        this.lastSyncedUpTime = lastSyncedUpTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;

    }

    public double getLastSyncedUpTime() {
        return lastSyncedUpTime;
    }

    public void setLastSyncedUpTime(double lastSyncedUpTime) {
        this.lastSyncedUpTime = lastSyncedUpTime;
    }
}
