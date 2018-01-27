package com.bookmarker.modal;

import com.bol.secure.Encrypted;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;


@Document(collection = "browserHistory")
public class BrowserHistory {
    @Id
    public int id;
    @Field
    @Encrypted
    public double lastVisitTime;
    @Field
    @Encrypted
    public String title;
    @Field
    @Encrypted
    public String url;
    @Field
    @Encrypted
    public int typedCount;
    @Field
    @Encrypted
    public int visitCount;
    @Field
    @Encrypted
    public String browserId;

    public BrowserHistory(int id, double lastVisitTime, String title, String url, int typedCount, int visitCount, String browserId) {
        this.id = id;
        this.lastVisitTime = lastVisitTime;
        this.title = title;
        this.url = url;
        this.typedCount = typedCount;
        this.visitCount = visitCount;
        this.browserId = browserId;
    }

    public BrowserHistory() {

    }

    public String getBrowserId() {
        return browserId;
    }

    public void setBrowserId(String browserId) {
        this.browserId = browserId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLastVisitTime() {
        return lastVisitTime;
    }

    public void setLastVisitTime(double lastVisitTime) {
        this.lastVisitTime = lastVisitTime;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getTypedCount() {
        return typedCount;
    }

    public void setTypedCount(int typedCount) {
        this.typedCount = typedCount;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }
}
